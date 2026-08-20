package ui

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.Stage
import model.Cash
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

    private val deposit: Cash = mutableMapOf()

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

    private var balance = 0f

    /*
     * Total value of items already purchased
     * during the current transaction.
     *
     * The customer's cash remains in deposit
     * until "Dispense Change" is pressed.
     */
    private var pendingPurchaseTotal = 0f

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

        for (i in 0 until machine.getAddOnSlotCount()) {

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

                Label("Add-on ${i + 1}"),

                button

            )

            addOnContainer.children.add(card)

            itemButtons.add(button)
        }
    }

    /*
     * ==========================================================
     * UPDATE BUTTON DISPLAY
     * ==========================================================
     */

    private fun updateSlotButton(
        button: Button,
        slot: model.Slot
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
            "₱%.2f".format(slot.price)
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
         * Verify one more time that the machine
         * can make exact change after this purchase.
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
         * Cash remains inside deposit.
         * It is NOT transferred to the machine
         * register yet.
         */
        slot.quantity--
        slot.sold++

        pendingPurchaseTotal += price

        balance -= price

        updateBalance()

        updateItemButtonsEnabledState()

        displayLabel.text =
            "Dispensed ${slot.item?.name ?: "Item"}"

        log(
            "Purchased ${slot.item?.name ?: "Item"} for ₱%.2f"
                .format(price)
        )

        logCurrentState()
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
            special.getAddOnSlot(addOnIndex)
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
         * Check whether exact change is possible
         * after this additional purchase.
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
         * Cash remains pending until
         * "Dispense Change" is pressed.
         */
        slot.quantity--
        slot.sold++

        pendingPurchaseTotal += price

        balance -= price

        updateBalance()

        updateItemButtonsEnabledState()

        displayLabel.text =
            "Dispensed ${slot.item?.name ?: "Add-on"}"

        log(
            "Purchased ${slot.item?.name ?: "Add-on"} for ₱%.2f"
                .format(price)
        )

        logCurrentState()
    }

    /*
     * ==========================================================
     * ENABLE / DISABLE ITEM BUTTONS
     * ==========================================================
     *
     * A button is enabled ONLY when:
     *
     * 1. The item exists.
     * 2. The item has stock.
     * 3. The customer has enough remaining balance.
     * 4. The machine can make exact change after
     *    purchasing the item.
     */

    private fun updateItemButtonsEnabledState() {

        itemButtons.forEach { button ->

            val price =
                button.text
                    .replace("₱", "")
                    .toFloatOrNull()

            if (price == null) {

                button.isDisable = true

                return@forEach
            }

            /*
             * Customer must have enough
             * remaining balance.
             */
            if (balance < price) {

                button.isDisable = true

                return@forEach
            }

            /*
             * Check whether exact change can
             * actually be produced.
             *
             * The customer's entire deposit is
             * available to the hypothetical
             * change-making calculation.
             */
            val possibleChange =
                machine.dispenseChange(
                    deposit,
                    pendingPurchaseTotal + price
                )

            button.isDisable =
                possibleChange == null
        }
    }

    /*
     * ==========================================================
     * DISPENSE CHANGE
     * ==========================================================
     *
     * This is the ONLY point where the customer's
     * deposited cash enters the machine register.
     *
     * Purchases do NOT immediately dispense change.
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
         * Calculate the exact change required
         * for all purchases made in this transaction.
         */
        val change =
            machine.dispenseChange(
                deposit,
                pendingPurchaseTotal
            )

        if (change == null) {

            /*
             * This should normally be impossible
             * because purchases were blocked when
             * exact change could not be made.
             */
            displayLabel.text =
                "Cannot Make Exact Change"

            log(
                "ERROR: Exact change became unavailable."
            )

            return
        }

        /*
         * First put the customer's deposited cash
         * into the machine register.
         */
        machine.updateRegister(
            deposit
        )

        /*
         * Then remove the actual change from
         * the register.
         */
        change.forEach { (denomination, quantity) ->

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
    }

    /*
     * ==========================================================
     * CANCEL / RETURN CASH
     * ==========================================================
     */

    @FXML
    fun cancelTransaction() {

        if (deposit.isEmpty()) {

            displayLabel.text =
                "No Cash Inserted"

            return
        }

        /*
         * No purchase is cancelled here.
         *
         * This simply returns the currently
         * remaining balance.
         *
         * If purchases were already made,
         * the remaining deposit represents
         * the customer's change.
         */
        val remainingCash =
            deposit.toMutableMap()

        remainingCash.forEach {
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

        pendingPurchaseTotal = 0f

        updateBalance()

        updateItemButtonsEnabledState()

        displayLabel.text =
            "Cash Returned"

        log(
            "Cash returned."
        )
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
         * If there is still unreturned cash,
         * return it logically before leaving.
         *
         * The machine register is NOT modified,
         * because the money was never deposited
         * into the register yet.
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