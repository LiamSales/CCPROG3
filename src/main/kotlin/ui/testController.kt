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
     * MACHINE
     * ==========================================================
     */

    private lateinit var machine: VendingMachine

    /*
     * ==========================================================
     * CUSTOMER DEPOSIT
     *
     * This is cash currently inserted by the customer.
     *
     * It is NOT part of the machine register yet.
     * It only becomes part of the register after a
     * successful purchase.
     * ==========================================================
     */

    private val deposit: Cash =
        mutableMapOf()

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
            1f,
            0.25f,
            0.10f,
            0.05f
        )

    private var balance =
        0f

    /*
     * Buttons for normal machine slots.
     */
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
                ?: error(
                    "No machine selected."
                )

        /*
         * This is the SAME machine object already stored
         * inside MachineManager and SelectedMachine.
         *
         * We do NOT create or load another machine here.
         */

        createSlotCards()

        if (machine is SpecialMachine) {

            createAddOnCards()

        } else {

            addOnTitle.isVisible =
                false

            addOnTitle.isManaged =
                false

            addOnScroll.isVisible =
                false

            addOnScroll.isManaged =
                false
        }

        updateBalance()

        updateItemButtonsEnabledState()
    }

    /*
     * ==========================================================
     * NORMAL SLOTS
     * ==========================================================
     */

    private fun createSlotCards() {

        slotGrid.children.clear()

        itemButtons.clear()

        for (index in machine.slots.indices) {

            val slot =
                machine.slots[index]

            val button =
                Button()

            button.prefWidth =
                140.0

            button.prefHeight =
                40.0

            updateSlotButton(
                button,
                index
            )

            button.setOnAction {

                purchaseSlot(index)

            }

            val label =
                Label(
                    "Slot ${index + 1}"
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
                index % 4,
                index / 4
            )

            itemButtons.add(
                button
            )
        }
    }

    /*
     * ==========================================================
     * UPDATE NORMAL SLOT BUTTON
     * ==========================================================
     */

    private fun updateSlotButton(
        button: Button,
        index: Int
    ) {

        val slot =
            machine.slots[index]

        val item =
            slot.item

        if (item == null) {

            button.text =
                "Empty"

            button.isDisable =
                true

            return
        }

        button.text =
            "${item.name} ₱%.2f".format(
                slot.price
            )

        button.isDisable =
            !canPurchase(slot)
    }

    /*
     * ==========================================================
     * SPECIAL MACHINE ADD-ONS
     * ==========================================================
     */

    private fun createAddOnCards() {

        val special =
            machine as SpecialMachine

        addOnContainer.children.clear()

        for (index in
            special.getAddOnSlots().indices
        ) {

            val button =
                Button()

            button.prefWidth =
                140.0

            button.prefHeight =
                40.0

            updateAddOnButton(
                button,
                index
            )

            button.setOnAction {

                purchaseAddOn(index)

            }

            val label =
                Label(
                    "Add-on ${index + 1}"
                )

            val card =
                VBox(10.0)

            card.alignment =
                Pos.CENTER

            card.children.addAll(
                label,
                button
            )

            addOnContainer.children.add(
                card
            )
        }
    }

    /*
     * ==========================================================
     * UPDATE ADD-ON BUTTON
     * ==========================================================
     */

    private fun updateAddOnButton(
        button: Button,
        index: Int
    ) {

        val special =
            machine as SpecialMachine

        val slot =
            special.getAddOnSlot(index)
                ?: return

        val item =
            slot.item

        if (item == null) {

            button.text =
                "Empty"

            button.isDisable =
                true

            return
        }

        button.text =
            "${item.name} ₱%.2f".format(
                slot.price
            )

        button.isDisable =
            !canPurchase(slot)
    }

    /*
     * ==========================================================
     * ADD CASH
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
                .trim()
                .toFloatOrNull()

        if (denomination == null) {

            displayLabel.text =
                "Invalid denomination"

            return
        }

        /*
         * Add the denomination to the customer's
         * temporary deposit.
         */

        deposit[denomination] =
            deposit.getOrDefault(
                denomination,
                0
            ) + 1

        balance +=
            denomination

        updateBalance()

        updateItemButtonsEnabledState()

        displayLabel.text =
            "Inserted ₱%.2f".format(
                denomination
            )
    }

    /*
     * ==========================================================
     * PURCHASE NORMAL SLOT
     * ==========================================================
     */

    private fun purchaseSlot(
        index: Int
    ) {

        val slot =
            machine.slots[index]

        if (slot.item == null) {

            displayLabel.text =
                "Slot Empty"

            return
        }

        if (slot.quantity <= 0) {

            displayLabel.text =
                "Out of Stock"

            return
        }

        purchase(
            slot
        )
    }

    /*
     * ==========================================================
     * PURCHASE ADD-ON
     * ==========================================================
     */

    private fun purchaseAddOn(
        index: Int
    ) {

        val special =
            machine as SpecialMachine

        val slot =
            special.getAddOnSlot(index)
                ?: return

        if (slot.item == null) {

            displayLabel.text =
                "Add-on Empty"

            return
        }

        if (slot.quantity <= 0) {

            displayLabel.text =
                "Add-on Out of Stock"

            return
        }

        purchase(
            slot
        )
    }

    /*
     * ==========================================================
     * PURCHASE
     *
     * This is the important part.
     *
     * We use VendingMachine.dispenseChange()
     * to determine whether the machine can actually
     * produce the required change.
     * ==========================================================
     */

    private fun purchase(
        slot: model.Slot
    ) {

        /*
         * Make sure the customer has enough money.
         */

        if (balance < slot.price) {

            displayLabel.text =
                "Insufficient Funds"

            return
        }

        /*
         * Ask the actual machine logic whether
         * the required change can be produced.
         *
         * dispenseChange() considers:
         *
         * 1. Cash already in the machine
         * 2. Cash currently inserted by the customer
         * 3. The item's price
         */

        val change =
            machine.dispenseChange(
                deposit,
                slot.price
            )

        /*
         * If change == null, the machine cannot
         * make the exact required change.
         *
         * Therefore the purchase MUST NOT happen.
         */

        if (change == null) {

            displayLabel.text =
                "Cannot Make Exact Change"

            return
        }

        /*
         * ======================================================
         * TRANSACTION CAN NOW BE COMMITTED
         * ======================================================
         */

        /*
         * First put the customer's inserted cash
         * into the machine register.
         */

        machine.updateRegister(
            deposit
        )

        /*
         * Then remove the change from the register.
         */

        change.forEach {

            (denomination, quantity) ->

            machine.register.removeCash(
                denomination,
                quantity
            )
        }

        /*
         * Decrease inventory.
         */

        slot.quantity--

        /*
         * Increase sold count.
         */

        slot.sold++

        /*
         * Calculate the customer's new balance.
         */

        balance = 0f

        /*
         * Customer's inserted money has now been
         * consumed by the transaction.
         */

        deposit.clear()

        /*
         * Update UI.
         */

        updateBalance()

        updateAllButtons()

        displayLabel.text =
            if (change.isEmpty()) {

                "Dispensed ${slot.item?.name}"

            } else {

                "Dispensed ${slot.item?.name}, " +
                "Change ₱%.2f".format(
                    change.entries.sumOf {
                        (denomination, quantity) ->
                        (denomination * quantity).toDouble()
                    }
                )
            }

        /*
         * Immediately save the updated machine.
         */

        saveMachine()
    }

    /*
     * ==========================================================
     * CAN PURCHASE?
     *
     * This checks BOTH:
     *
     * - enough customer balance
     * - machine can actually make change
     * ==========================================================
     */

    private fun canPurchase(
        slot: model.Slot
    ): Boolean {

        if (slot.item == null)
            return false

        if (slot.quantity <= 0)
            return false

        if (balance < slot.price)
            return false

        return machine.dispenseChange(
            deposit,
            slot.price
        ) != null
    }

    /*
     * ==========================================================
     * UPDATE ALL BUTTONS
     * ==========================================================
     */

    private fun updateAllButtons() {

        /*
         * Normal slots.
         */

        itemButtons.forEachIndexed { index, button ->

            updateSlotButton(
                button,
                index
            )
        }

        /*
         * Add-ons.
         */

        if (machine is SpecialMachine) {

            val special =
                machine as SpecialMachine

            addOnContainer.children
                .forEachIndexed { index, node ->

                    val card =
                        node as? VBox
                            ?: return@forEachIndexed

                    val button =
                        card.children
                            .filterIsInstance<Button>()
                            .firstOrNull()
                            ?: return@forEachIndexed

                    updateAddOnButton(
                        button,
                        index
                    )
                }
        }
    }

    /*
     * ==========================================================
     * UPDATE ENABLED STATE
     * ==========================================================
     */

    private fun updateItemButtonsEnabledState() {

        updateAllButtons()
    }

    /*
     * ==========================================================
     * GET CHANGE
     * ==========================================================
     *
     * This does NOT modify the machine register because
     * the deposited money has not entered the register yet.
     * ==========================================================
     */

    @FXML
    fun getChange() {

        if (deposit.isEmpty()) {

            displayLabel.text =
                "No Cash Inserted"

            return
        }

        val change =
            deposit.toMap()

        val amount =
            balance

        deposit.clear()

        balance = 0f

        updateBalance()

        updateItemButtonsEnabledState()

        displayLabel.text =
            "Returned ₱%.2f".format(
                amount
            )

        println(
            "[TestController] Returned change: $change"
        )
    }

    /*
     * ==========================================================
     * SAVE MACHINE
     * ==========================================================
     *
     * Find the existing MachineEntry in memory and save
     * that exact machine object.
     * ==========================================================
     */

    private fun saveMachine() {

        val entry =
            MachineManager.machines.find {

                it.folder == SelectedMachine.folder

            }

        if (entry != null) {

            MachineManager.saveMachine(
                entry
            )
        }
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
     * BACK TO MAIN
     * ==========================================================
     */

    @FXML
    fun backToMainPage(
        event: ActionEvent
    ) {

        /*
         * If the user leaves with money inserted,
         * automatically return it.
         *
         * It was never placed into the machine register.
         */

        deposit.clear()

        balance = 0f

        val root: Parent =
            FXMLLoader.load(
                javaClass.getResource(
                    "/fxml/main.fxml"
                )
            )

        val stage =
            (event.source as javafx.scene.Node)
                .scene
                .window as Stage

        stage.scene =
            Scene(root)
    }
}