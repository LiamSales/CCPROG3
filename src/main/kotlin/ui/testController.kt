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
     *
     * IMPORTANT:
     *
     * This is the SAME machine object that was selected
     * in MainController.
     *
     * We do NOT call MachineManager.loadMachines() here.
     */

    private lateinit var machine: VendingMachine

    /*
     * ==========================================================
     * CUSTOMER DEPOSIT
     * ==========================================================
     */

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

        /*
         * Build the UI directly from the machine currently
         * stored in memory.
         */

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
     * Saves the SAME machine object currently being used.
     */

    private fun saveCurrentMachine() {

        val entry =
            MachineManager.machines.find {
                it.folder == SelectedMachine.folder
            }

        if (entry != null) {

            MachineManager.saveMachine(
                entry.copy(
                    machine = machine
                )
            )

        }
    }

    /*
     * ==========================================================
     * SLOT CARDS
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

            button.prefWidth = 160.0
            button.prefHeight = 45.0

            /*
             * Button displays the actual item and price.
             */

            updateSlotButton(
                button,
                slot
            )

            button.setOnAction {

                selectItem(
                    i,
                    button
                )

            }

            val label =
                Label("Slot ${i + 1}")

            label.style =
                "-fx-font-size:16;" +
                "-fx-font-weight:bold;"

            val quantityLabel =
                Label()

            updateQuantityLabel(
                quantityLabel,
                slot
            )

            val card =
                VBox(10.0)

            card.alignment =
                Pos.CENTER

            card.children.addAll(
                label,
                button,
                quantityLabel
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
     * UPDATE SLOT BUTTON
     * ==========================================================
     */

    private fun updateSlotButton(
        button: Button,
        slot: model.Slot
    ) {

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
            "${item.name} - ₱%.2f".format(
                slot.price
            )

        button.isDisable =
            slot.quantity <= 0 ||
            balance < slot.price
    }

    /*
     * ==========================================================
     * UPDATE QUANTITY LABEL
     * ==========================================================
     */

    private fun updateQuantityLabel(
        label: Label,
        slot: model.Slot
    ) {

        if (slot.item == null) {

            label.text =
                "Quantity: 0"

        } else {

            label.text =
                "Quantity: ${slot.quantity}"
        }
    }

    /*
     * ==========================================================
     * PURCHASE ITEM
     * ==========================================================
     */

    private fun selectItem(
        slotIndex: Int,
        button: Button
    ) {

        val slot =
            machine.slots[slotIndex]

        val item =
            slot.item

        /*
         * Empty slot.
         */

        if (item == null) {

            displayLabel.text =
                "Item unavailable"

            return
        }

        /*
         * Empty inventory.
         */

        if (slot.quantity <= 0) {

            displayLabel.text =
                "Out of Stock"

            return
        }

        val price =
            slot.price

        /*
         * Not enough money.
         */

        if (balance < price) {

            displayLabel.text =
                "Insufficient Funds"

            log(
                "Purchase failed: insufficient funds for ₱$price"
            )

            return
        }

        /*
         * ======================================================
         * COMPLETE PURCHASE
         * ======================================================
         */

        /*
         * Remove the item from inventory.
         */

        slot.quantity--

        /*
         * Record the sale.
         */

        slot.sold++

        /*
         * Reduce customer balance.
         */

        balance -= price

        balance =
            "%.2f".format(balance).toFloat()

        /*
         * Add the customer's inserted cash to the
         * machine register.
         */

        deposit.forEach {
            (denomination, quantity) ->

            if (quantity > 0) {

                machine.register.addCash(
                    denomination,
                    quantity
                )
            }
        }

        /*
         * The customer's deposit has now become
         * machine cash.
         */

        deposit.clear()

        /*
         * Update UI.
         */

        updateBalance()

        updateSlotButton(
            button,
            slot
        )

        /*
         * Find the quantity label inside the card.
         */

        val card =
            button.parent as VBox

        val quantityLabel =
            card.children
                .filterIsInstance<Label>()
                .lastOrNull()

        if (quantityLabel != null) {

            updateQuantityLabel(
                quantityLabel,
                slot
            )
        }

        updateItemButtonsEnabledState()

        displayLabel.text =
            "Dispensed ${item.name}"

        log(
            "Purchased ${item.name} for ₱%.2f"
                .format(price)
        )

        /*
         * SAVE IMMEDIATELY.
         */

        saveCurrentMachine()
    }

    /*
     * ==========================================================
     * ADD-ON CARDS
     * ==========================================================
     *
     * SpecialMachine currently does not expose add-on Slot
     * objects through the API used by this project.
     *
     * Therefore these remain display-only until the
     * SpecialMachine model provides actual add-on slots.
     */

    private fun createAddOnCards(
        machine: SpecialMachine
    ) {

        addOnContainer.children.clear()

        for (i in 0 until machine.getAddOnSlotCount()) {

            val button =
                Button("Unavailable")

            button.prefWidth =
                160.0

            button.prefHeight =
                45.0

            button.isDisable =
                true

            val card =
                VBox(10.0)

            card.alignment =
                Pos.CENTER

            card.children.addAll(
                Label("Add-on ${i + 1}"),
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
         * Add the coin/bill to the customer's
         * temporary deposit.
         */

        deposit[denomination] =
            deposit.getOrDefault(
                denomination,
                0
            ) + 1

        balance += denomination

        balance =
            "%.2f".format(balance).toFloat()

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
     * GET CHANGE
     * ==========================================================
     */

    @FXML
    fun getChange() {

        log(
            "Get change clicked"
        )

        if (deposit.isEmpty()) {

            displayLabel.text =
                "No cash deposited"

            log(
                "No cash was deposited"
            )

            return
        }

        deposit.forEach {
            (denomination, quantity) ->

            log(
                "Returning ₱$denomination x$quantity"
            )
        }

        /*
         * Return customer's temporary deposit.
         *
         * It was NOT added to the machine register,
         * so no register changes are necessary.
         */

        deposit.clear()

        balance = 0f

        updateBalance()
        updateItemButtonsEnabledState()

        displayLabel.text =
            "Change Returned"

        log(
            "Balance reset to ₱0"
        )
    }

    /*
     * ==========================================================
     * ENABLE / DISABLE ITEMS
     * ==========================================================
     */

    private fun updateItemButtonsEnabledState() {

        /*
         * Rebuild state from the actual machine slots.
         */

        machine.slots.forEachIndexed {
            index,
            slot ->

            if (index >= itemButtons.size)
                return@forEachIndexed

            updateSlotButton(
                itemButtons[index],
                slot
            )
        }

        /*
         * Add-on buttons currently remain disabled.
         */

        if (machine is SpecialMachine) {

            for (
                i in machine.slots.size
                    until itemButtons.size
            ) {

                itemButtons[i].isDisable =
                    true
            }
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
     * LOGGING
     * ==========================================================
     */

    private fun logCurrentState() {

        log(
            "Current balance: ₱%.2f"
                .format(balance)
        )

        log(
            "Deposit map: $deposit"
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
         * Make absolutely sure the latest machine state
         * is written before leaving the test page.
         */

        saveCurrentMachine()

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