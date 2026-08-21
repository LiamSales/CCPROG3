package ui

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.Stage
import model.Cash
import model.Slot
import model.SpecialMachine
import model.VendingMachine

class TestController {

    @FXML
    private lateinit var balanceLabel: Label

    @FXML
    private lateinit var displayLabel: Label

    @FXML
    private lateinit var slotGrid: GridPane

    @FXML
    private lateinit var addOnContainer: HBox

    @FXML
    private lateinit var addOnScroll: ScrollPane

    @FXML
    private lateinit var addOnTitle: Label

    /*
     * ==========================================================
     * CUSTOMER TRANSACTION STATE
     * ==========================================================
     */

    private val deposit: Cash =
        mutableMapOf()

    private var balance = 0f

    /*
     * Total price of everything purchased
     * during this transaction.
     *
     * The customer's deposited cash remains
     * untouched until Dispense Change is pressed.
     */
    private var pendingPurchaseTotal = 0f

    /*
     * IMPORTANT:
     *
     * This is the SAME machine object stored
     * inside SelectedMachine and MachineManager.
     */
    private lateinit var machine: VendingMachine

    private val itemButtons =
        mutableListOf<Button>()

    /*
     * ==========================================================
     * INITIALIZE
     * ==========================================================
     */

    @FXML
    fun initialize() {

        machine =
            SelectedMachine.machine
                ?: error("No machine selected.")

        createSlotCards(machine)

        if (machine is SpecialMachine) {

            createAddOnCards(
                machine as SpecialMachine
            )

        } else {

            addOnTitle.isVisible = false
            addOnTitle.isManaged = false

            addOnScroll.isVisible = false
            addOnScroll.isManaged = false
        }

        updateBalance()
        updateItemButtonsEnabledState()
    }

    /*
     * ==========================================================
     * SAVE CURRENT MACHINE
     * ==========================================================
     *
     * The machine itself is NOT replaced.
     *
     * MachineManager already contains this exact object.
     * We simply save its current state to disk.
     */

    private fun saveCurrentMachine() {

        val entry =
            MachineManager.machines.find {

                it.machine === machine

            } ?: return

        MachineManager.saveMachine(entry)
    }

    /*
     * ==========================================================
     * REGULAR MACHINE SLOTS
     * ==========================================================
     */

    private fun createSlotCards(
        machine: VendingMachine
    ) {

        slotGrid.children.clear()
        itemButtons.clear()

        for (i in machine.slots.indices) {

            val slot =
                machine.slots[i]

            val button =
                Button()

            button.prefWidth = 140.0
            button.prefHeight = 40.0

            updateSlotButton(
                button,
                slot
            )

            button.setOnAction {

                purchaseSlot(
                    i,
                    button
                )

            }

            val label =
                Label("Slot ${i + 1}")

            val card =
                VBox(10.0)

            card.alignment =
                Pos.CENTER

            card.children.addAll(
                label,
                button
            )

            slotGrid.add(
                card,
                i % 4,
                i / 4
            )

            itemButtons.add(button)
        }
    }

    /*
     * ==========================================================
     * SPECIAL MACHINE ADD-ONS
     * ==========================================================
     */

    private fun createAddOnCards(
        machine: SpecialMachine
    ) {

        addOnContainer.children.clear()

        for (
            i in 0 until machine.getAddOnSlotCount()
        ) {

            val slot =
                machine.getAddOnSlot(i)

            val button =
                Button()

            button.prefWidth = 140.0
            button.prefHeight = 40.0

            if (slot != null) {

                updateSlotButton(
                    button,
                    slot
                )

            } else {

                button.text =
                    "Unavailable"

                button.isDisable =
                    true
            }

            button.setOnAction {

                purchaseAddOn(
                    i,
                    button
                )

            }

            val card =
                VBox(10.0)

            card.alignment =
                Pos.CENTER

            card.children.addAll(

                Label(
                    "Add-on ${i + 1}"
                ),

                button

            )

            addOnContainer.children.add(card)

            itemButtons.add(button)
        }
    }

    /*
     * ==========================================================
     * UPDATE SLOT BUTTON
     * ==========================================================
     */

    private fun updateSlotButton(
        button: Button,
        slot: Slot
    ) {

        if (
            slot.item == null ||
            slot.quantity <= 0
        ) {

            button.text =
                "Unavailable"

            button.isDisable =
                true

            return
        }

        button.text =
            "₱%.2f".format(
                slot.price
            )
    }

    /*
     * ==========================================================
     * INSERT CASH
     * ==========================================================
     */

    @FXML
    fun addCash(
        event: ActionEvent
    ) {

        val button =
            event.source as Button

        val denomination =
            button.text
                .replace("₱", "")
                .toFloatOrNull()
                ?: return

        deposit[denomination] =
            deposit.getOrDefault(
                denomination,
                0
            ) + 1

        balance += denomination

        balance =
            roundMoney(balance)

        updateBalance()
        updateItemButtonsEnabledState()

        displayLabel.text =
            "Inserted ₱%.2f".format(
                denomination
            )

        log(
            "Inserted ₱%.2f".format(
                denomination
            )
        )

        logCurrentState()
    }

    /*
     * ==========================================================
     * PURCHASE REGULAR SLOT
     * ==========================================================
     */

    private fun purchaseSlot(
        slotIndex: Int,
        button: Button
    ) {

        val slot =
            machine.slots[slotIndex]

        if (slot.item == null) {

            displayLabel.text =
                "Item Unavailable"

            return
        }

        if (slot.quantity <= 0) {

            displayLabel.text =
                "Item Out of Stock"

            updateItemButtonsEnabledState()

            return
        }

        val price =
            slot.price

        if (balance < price) {

            displayLabel.text =
                "Insufficient Funds"

            return
        }

        /*
         * IMPORTANT:
         *
         * Check the ACTUAL denominations available
         * in the machine register.
         *
         * The customer's inserted denominations are also
         * available to the hypothetical change calculation.
         *
         * We calculate the change for ALL purchases made
         * during this transaction.
         */
        val possibleChange =
            machine.dispenseChange(
                deposit,
                pendingPurchaseTotal + price
            )

        if (possibleChange == null) {

            displayLabel.text =
                "Cannot Make Exact Change"

            return
        }

        /*
         * Purchase succeeds.
         *
         * Customer cash remains in deposit.
         *
         * It does NOT enter the machine register yet.
         */

        slot.quantity--
        slot.sold++

        pendingPurchaseTotal += price

        balance -= price

        balance =
            roundMoney(balance)

        updateSlotButton(
            button,
            slot
        )

        updateBalance()
        updateItemButtonsEnabledState()

        displayLabel.text =
            "Dispensed ${slot.item?.name ?: "Item"}"

        log(
            "Purchased ${slot.item?.name ?: "Item"} for ₱%.2f"
                .format(price)
        )

        logCurrentState()

        /*
         * Inventory changed immediately.
         */
        saveCurrentMachine()
    }

    /*
     * ==========================================================
     * PURCHASE ADD-ON
     * ==========================================================
     */

    private fun purchaseAddOn(
        addOnIndex: Int,
        button: Button
    ) {

        val special =
            machine as? SpecialMachine
                ?: return

        val slot =
            special.getAddOnSlot(
                addOnIndex
            )
                ?: return

        if (slot.item == null) {

            displayLabel.text =
                "Add-on Unavailable"

            return
        }

        if (slot.quantity <= 0) {

            displayLabel.text =
                "Add-on Out of Stock"

            updateItemButtonsEnabledState()

            return
        }

        val price =
            slot.price

        if (balance < price) {

            displayLabel.text =
                "Insufficient Funds"

            return
        }

        /*
         * Check exact change using the actual
         * machine denominations.
         */

        val possibleChange =
            machine.dispenseChange(
                deposit,
                pendingPurchaseTotal + price
            )

        if (possibleChange == null) {

            displayLabel.text =
                "Cannot Make Exact Change"

            return
        }

        /*
         * Purchase succeeds.
         */

        slot.quantity--
        slot.sold++

        pendingPurchaseTotal += price

        balance -= price

        balance =
            roundMoney(balance)

        updateSlotButton(
            button,
            slot
        )

        updateBalance()
        updateItemButtonsEnabledState()

        displayLabel.text =
            "Dispensed ${slot.item?.name ?: "Add-on"}"

        log(
            "Purchased ${slot.item?.name ?: "Add-on"} for ₱%.2f"
                .format(price)
        )

        logCurrentState()

        /*
         * Inventory changed immediately.
         */
        saveCurrentMachine()
    }

    /*
     * ==========================================================
     * ENABLE / DISABLE ITEM BUTTONS
     * ==========================================================
     *
     * A button is enabled ONLY when:
     *
     * 1. Item exists
     * 2. Item has stock
     * 3. Customer has enough remaining balance
     * 4. Machine can make exact change
     *
     * This is recalculated every time money or inventory changes.
     */

    private fun updateItemButtonsEnabledState() {

        var buttonIndex = 0

        /*
         * Regular slots
         */

        machine.slots.forEach { slot ->

            if (
                buttonIndex >= itemButtons.size
            ) {
                return@forEach
            }

            val button =
                itemButtons[buttonIndex]

            buttonIndex++

            if (
                slot.item == null ||
                slot.quantity <= 0
            ) {

                button.isDisable = true

                return@forEach
            }

            val price =
                slot.price

            if (balance < price) {

                button.isDisable = true

                return@forEach
            }

            val possibleChange =
                machine.dispenseChange(
                    deposit,
                    pendingPurchaseTotal + price
                )

            button.isDisable =
                possibleChange == null
        }

        /*
         * Special machine add-ons
         */

        val special =
            machine as? SpecialMachine

        if (special != null) {

            special.getAddOnSlots()
                .forEach { slot ->

                    if (
                        buttonIndex >= itemButtons.size
                    ) {
                        return@forEach
                    }

                    val button =
                        itemButtons[buttonIndex]

                    buttonIndex++

                    if (
                        slot.item == null ||
                        slot.quantity <= 0
                    ) {

                        button.isDisable = true

                        return@forEach
                    }

                    val price =
                        slot.price

                    if (balance < price) {

                        button.isDisable = true

                        return@forEach
                    }

                    val possibleChange =
                        machine.dispenseChange(
                            deposit,
                            pendingPurchaseTotal + price
                        )

                    button.isDisable =
                        possibleChange == null
                }
        }
    }

    /*
     * ==========================================================
     * DISPENSE CHANGE
     * ==========================================================
     *
     * THIS is the only point where:
     *
     *     customer deposit -> machine register
     *
     * happens.
     *
     * Purchases do NOT immediately modify the register.
     */

    @FXML
    fun getChange() {

        if (deposit.isEmpty()) {

            displayLabel.text =
                "No Cash Inserted"

            log(
                "Get change clicked with no deposit."
            )

            return
        }

        /*
         * Calculate exact change for all purchases.
         */

        val change =
            machine.dispenseChange(
                deposit,
                pendingPurchaseTotal
            )

        if (change == null) {

            displayLabel.text =
                "Cannot Make Exact Change"

            log(
                "Cannot return exact change."
            )

            return
        }

        /*
         * First put customer's cash into
         * the machine register.
         */

        machine.updateRegister(
            deposit
        )

        /*
         * Then remove the actual change
         * that must be returned.
         */

        change.forEach {
            (denomination, quantity) ->

            machine.register.removeCash(
                denomination,
                quantity
            )
        }

        /*
         * Log returned change.
         */

        if (change.isEmpty()) {

            log(
                "No change required."
            )

        } else {

            change.forEach {
                (denomination, quantity) ->

                log(
                    "Returning ₱%.2f x%d"
                        .format(
                            denomination,
                            quantity
                        )
                )
            }
        }

        /*
         * Clear customer's transaction.
         */

        deposit.clear()

        balance = 0f

        pendingPurchaseTotal = 0f

        updateBalance()
        updateItemButtonsEnabledState()

        displayLabel.text =
            "Change Returned"

        log(
            "Transaction complete."
        )

        logCurrentState()

        /*
         * Register changed.
         */
        saveCurrentMachine()
    }

    /*
     * ==========================================================
     * CANCEL / RETURN CASH
     * ==========================================================
     *
     * This returns the customer's currently held cash
     * without putting it into the machine register.
     */

    @FXML
    fun cancelTransaction() {

        if (deposit.isEmpty()) {

            displayLabel.text =
                "No Cash Inserted"

            return
        }

        deposit.forEach {
            (denomination, quantity) ->

            log(
                "Returning ₱%.2f x%d"
                    .format(
                        denomination,
                        quantity
                    )
            )
        }

        deposit.clear()

        balance = 0f

        /*
         * Do NOT undo purchases.
         *
         * Purchases already happened.
         * This only returns the customer's remaining
         * unspent cash.
         */

        updateBalance()
        updateItemButtonsEnabledState()

        displayLabel.text =
            "Cash Returned"

        log(
            "Remaining cash returned."
        )

        logCurrentState()
    }

    /*
     * ==========================================================
     * BALANCE
     * ==========================================================
     */

    private fun updateBalance() {

        balanceLabel.text =
            "₱%.2f".format(
                balance
            )
    }

    /*
     * ==========================================================
     * MONEY ROUNDING
     * ==========================================================
     */

    private fun roundMoney(
        value: Float
    ): Float {

        return "%.2f"
            .format(value)
            .toFloat()
    }

    /*
     * ==========================================================
     * DEBUG STATE
     * ==========================================================
     */

    private fun logCurrentState() {

        log(
            "Current balance: ₱%.2f"
                .format(balance)
        )

        log(
            "Pending purchases: ₱%.2f"
                .format(
                    pendingPurchaseTotal
                )
        )

        log(
            "Deposit map: $deposit"
        )

        log(
            "Register: ${machine.register.getContents()}"
        )
    }

    private fun log(
        message: String
    ) {

        println(
            "[TestController] $message"
        )

        System.out.flush()
    }

    /*
     * ==========================================================
     * BACK TO MAIN
     * ==========================================================
     */

    @FXML
    fun backToMainPage(
        event: ActionEvent
    ) {

        /*
         * Any cash still in deposit has never entered
         * the machine register, so it is simply returned
         * to the customer when leaving.
         */

        if (deposit.isNotEmpty()) {

            deposit.forEach {
                (denomination, quantity) ->

                log(
                    "Returning ₱%.2f x%d before leaving."
                        .format(
                            denomination,
                            quantity
                        )
                )
            }

            deposit.clear()
        }

        balance = 0f
        pendingPurchaseTotal = 0f

        /*
         * Save any inventory changes made during testing.
         */
        saveCurrentMachine()

        val resource =
            javaClass.getResource(
                "/fxml/main.fxml"
            )
                ?: error(
                    "Cannot find /fxml/main.fxml"
                )

        val root: Parent =
            FXMLLoader.load(resource)

        val stage =
            (event.source as javafx.scene.Node)
                .scene
                .window as Stage

        stage.scene =
            Scene(root)
    }
}