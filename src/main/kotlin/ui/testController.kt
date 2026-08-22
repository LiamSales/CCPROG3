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
     * Total amount spent during the current transaction.
     */
    private var pendingPurchaseTotal = 0f

    /*
     * ==========================================================
     * SPECIAL MACHINE STATE
     * ==========================================================
     *
     * A special machine requires exactly one base item
     * before any add-ons can be purchased.
     */

    private var baseItemPurchased = false

    /*
     * Prevents another base item from being purchased
     * during the same transaction.
     */

    private var purchasedBaseSlotIndex: Int? = null

    /*
     * ==========================================================
     * MACHINE
     * ==========================================================
     */

    private lateinit var machine: VendingMachine

    /*
     * All purchase buttons.
     */
    private val itemButtons =
        mutableListOf<Button>()

    /*
     * ==========================================================
     * DENOMINATIONS
     * ==========================================================
     */

    private val denominations =
        listOf(
            1000f,
            500f,
            200f,
            100f,
            50f,
            20f,
            10f,
            5f,
            1f
        )

    /*
     * ==========================================================
     * INITIALIZE
     * ==========================================================
     */

    @FXML
    fun initialize() {

        machine =
            SelectedMachine.machine
                ?: error(
                    "No machine selected."
                )

        createSlotCards(
            machine
        )

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
     * This saves the SAME machine object.
     *
     * MachineManager already contains this object.
     */

    private fun saveCurrentMachine() {

        val entry =
            MachineManager.machines.find {

                it.machine === machine

            } ?: return

        MachineManager.saveMachine(
            entry
        )
    }

    /*
     * ==========================================================
     * CREATE BASE ITEM CARDS
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

            button.prefWidth =
                140.0

            button.prefHeight =
                40.0

            updateSlotButton(
                button,
                slot
            )

            button.setOnAction {

                purchaseBaseItem(
                    i,
                    button
                )
            }

            val label =
                Label(
                    "Slot ${i + 1}"
                )

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

            itemButtons.add(
                button
            )
        }
    }

    /*
     * ==========================================================
     * CREATE ADD-ON CARDS
     * ==========================================================
     */

    private fun createAddOnCards(
        machine: SpecialMachine
    ) {

        addOnContainer.children.clear()

        for (
            i in 0 until
                machine.getAddOnSlotCount()
        ) {

            val slot =
                machine.getAddOnSlot(i)

            val button =
                Button()

            button.prefWidth =
                140.0

            button.prefHeight =
                40.0

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

            addOnContainer.children.add(
                card
            )

            itemButtons.add(
                button
            )
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

        balance +=
            denomination

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
     * PURCHASE BASE ITEM
     * ==========================================================
     */

    private fun purchaseBaseItem(
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

        /*
         * SPECIAL MACHINE:
         *
         * Only one base item may be purchased
         * in a transaction.
         */

        if (
            machine is SpecialMachine &&
            baseItemPurchased
        ) {

            displayLabel.text =
                "Base Item Already Selected"

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
         * Check whether the machine can make exact
         * change for the ENTIRE transaction.
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

        val itemName =
            slot.item?.name
                ?: "Item"

        slot.quantity--

        slot.sold++

        pendingPurchaseTotal +=
            price

        balance -=
            price

        balance =
            roundMoney(balance)

        /*
         * Special machine now has its required
         * base item.
         */

        if (machine is SpecialMachine) {

            baseItemPurchased =
                true

            purchasedBaseSlotIndex =
                slotIndex
        }

        updateSlotButton(
            button,
            slot
        )

        updateBalance()
        updateItemButtonsEnabledState()

        displayLabel.text =
            "Dispensed $itemName"

        log(
            "Purchased $itemName for ₱%.2f"
                .format(price)
        )

        logCurrentState()

        /*
         * Inventory changes immediately.
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

        /*
         * IMPORTANT:
         *
         * A special-machine add-on CANNOT be
         * purchased before a base item.
         */

        if (!baseItemPurchased) {

            displayLabel.text =
                "Select a Base Item First"

            return
        }

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
         * Purchase add-on.
         */

        val itemName =
            slot.item?.name
                ?: "Add-on"

        slot.quantity--

        slot.sold++

        pendingPurchaseTotal +=
            price

        balance -=
            price

        balance =
            roundMoney(balance)

        updateSlotButton(
            button,
            slot
        )

        updateBalance()
        updateItemButtonsEnabledState()

        displayLabel.text =
            "Added $itemName"

        log(
            "Purchased add-on $itemName for ₱%.2f"
                .format(price)
        )

        logCurrentState()

        saveCurrentMachine()
    }

    /*
     * ==========================================================
     * ENABLE / DISABLE PURCHASE BUTTONS
     * ==========================================================
     */

    private fun updateItemButtonsEnabledState() {

        var buttonIndex = 0

        /*
         * ------------------------------------------------------
         * BASE ITEMS
         * ------------------------------------------------------
         */

        machine.slots.forEachIndexed { index, slot ->

            if (
                buttonIndex >=
                itemButtons.size
            ) {
                return@forEachIndexed
            }

            val button =
                itemButtons[
                    buttonIndex
                ]

            buttonIndex++

            /*
             * Empty / out of stock.
             */

            if (
                slot.item == null ||
                slot.quantity <= 0
            ) {

                button.isDisable =
                    true

                return@forEachIndexed
            }

            /*
             * Special machine:
             *
             * Once a base item has been purchased,
             * no other base item can be selected.
             */

            if (
                machine is SpecialMachine &&
                baseItemPurchased
            ) {

                button.isDisable =
                    true

                return@forEachIndexed
            }

            val price =
                slot.price

            /*
             * Not enough money.
             */

            if (
                balance < price
            ) {

                button.isDisable =
                    true

                return@forEachIndexed
            }

            /*
             * Machine must be capable of returning
             * exact change.
             */

            val possibleChange =
                machine.dispenseChange(
                    deposit,
                    pendingPurchaseTotal + price
                )

            button.isDisable =
                possibleChange == null
        }

        /*
         * ------------------------------------------------------
         * ADD-ONS
         * ------------------------------------------------------
         */

        val special =
            machine as? SpecialMachine

        if (special != null) {

            special.getAddOnSlots()
                .forEach { slot ->

                    if (
                        buttonIndex >=
                        itemButtons.size
                    ) {
                        return@forEach
                    }

                    val button =
                        itemButtons[
                            buttonIndex
                        ]

                    buttonIndex++

                    /*
                     * Add-ons are completely disabled
                     * until a base item exists.
                     */

                    if (!baseItemPurchased) {

                        button.isDisable =
                            true

                        return@forEach
                    }

                    if (
                        slot.item == null ||
                        slot.quantity <= 0
                    ) {

                        button.isDisable =
                            true

                        return@forEach
                    }

                    val price =
                        slot.price

                    if (
                        balance < price
                    ) {

                        button.isDisable =
                            true

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
         * Calculate exact change for the
         * complete transaction.
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
         * Put customer's money into register.
         */

        machine.updateRegister(
            deposit
        )

        /*
         * Remove the exact change that must
         * be returned.
         */

        change.forEach {

            (denomination, quantity) ->

            machine.register.removeCash(
                denomination,
                quantity
            )

            log(
                "Returning ₱%.2f x%d"
                    .format(
                        denomination,
                        quantity
                    )
            )
        }

        /*
         * Clear transaction.
         */

        deposit.clear()

        balance =
            0f

        pendingPurchaseTotal =
            0f

        /*
         * IMPORTANT:
         *
         * The special machine is now ready
         * for a completely new order.
         */

        baseItemPurchased =
            false

        purchasedBaseSlotIndex =
            null

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
     * CANCEL / RETURN REMAINING CASH
     * ==========================================================
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

        /*
         * Return only the customer's
         * remaining unspent cash.
         *
         * Purchases are NOT undone.
         */

        deposit.clear()

        balance =
            0f

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
            "Base item purchased: $baseItemPurchased"
        )

        log(
            "Base slot: $purchasedBaseSlotIndex"
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
         * Any unspent customer cash is simply
         * returned. It never entered the register.
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

        balance =
            0f

        pendingPurchaseTotal =
            0f

        baseItemPurchased =
            false

        purchasedBaseSlotIndex =
            null

        /*
         * Save any inventory changes made
         * during testing.
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
            FXMLLoader.load(
                resource
            )

        val stage =
            (event.source as javafx.scene.Node)
                .scene
                .window as Stage

        stage.scene =
            Scene(root)
    }
}