package ui

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.stage.Stage
import model.Cash
import javafx.geometry.Pos
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.control.ScrollPane
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
    private val denominations = listOf(1000f, 500f, 200f, 100f, 50f, 20f, 10f, 5f, 1f)
    private var balance = 0f
    private val itemButtons = mutableListOf<Button>()

@FXML
fun initialize() {

    val machine =
        SelectedMachine.machine
            ?: error("No machine selected.")

    createSlotCards(machine)

    if (machine is SpecialMachine) {

        createAddOnCards(machine)

    } else {

        addOnTitle.isVisible = false
        addOnTitle.isManaged = false

        addOnScroll.isVisible = false
        addOnScroll.isManaged = false
    }

    updateBalance()
    updateItemButtonsEnabledState()
}

private fun createSlotCards(
    machine: VendingMachine
) {

    slotGrid.children.clear()

    itemButtons.clear()

    for (i in 0 until machine.slotLimit) {

        val button =
            Button("₱50")

        button.prefWidth = 120.0
        button.prefHeight = 40.0

        button.setOnAction {

            selectItem(button)

        }

        val label =
            Label("Slot ${i + 1}")

        val card =
            VBox(10.0)

        card.alignment = Pos.CENTER

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

private fun createAddOnCards(
    machine: SpecialMachine
) {

    addOnContainer.children.clear()

    for (i in 0 until machine.getAddOnSlotCount()) {

        val button =
            Button("₱20")

        button.prefWidth = 120.0

        button.setOnAction {

            selectItem(button)

        }

        val card =
            VBox(10.0)

        card.alignment = Pos.CENTER

        card.children.addAll(

            Label("Add-on ${i + 1}"),

            button

        )

        addOnContainer.children.add(card)

        itemButtons.add(button)

    }

}

    @FXML
    fun addCash(event: ActionEvent) {
        val button = event.source as Button
        val denomination = button.text.replace("₱", "").toFloat()

        deposit[denomination] = deposit.getOrDefault(denomination, 0) + 1
        balance += denomination

        updateBalance()
        updateItemButtonsEnabledState()

        displayLabel.text = "Inserted ₱$denomination"
        log("Inserted ₱$denomination")
        logCurrentState()
    }

    @FXML
    fun selectItem( button: Button) {
        val price = button.text.replace("₱", "").toFloat()

        if (balance < price) {
            displayLabel.text = "Insufficient Funds"
            log("Purchase failed: insufficient funds for ₱$price")
            logCurrentState()
            return
        }

        balance -= price
        useDepositFor(price)
        updateBalance()
        updateItemButtonsEnabledState()

        displayLabel.text = "Dispensed Item"
        log("Purchased item for ₱$price")
        logCurrentState()
    }

    @FXML
    fun getChange() {
        log("Get change clicked")
        if (deposit.isEmpty()) {
            log("No cash was deposited")
        } else {
            deposit.forEach { (denomination, quantity) ->
                log("Returning ₱$denomination x$quantity")
            }
        }

        deposit.clear()
        balance = 0f
        updateBalance()
        updateItemButtonsEnabledState()

        displayLabel.text = "Change Returned"
        log("Balance reset to ₱0")
    }

    private fun useDepositFor(amount: Float) {
        var remaining = amount
        val sortedDenominations = denominations.sortedDescending()

        for (denomination in sortedDenominations) {
            while (remaining >= denomination && deposit.getOrDefault(denomination, 0) > 0) {
                deposit[denomination] = deposit.getOrDefault(denomination, 0) - 1
                if (deposit[denomination] == 0) {
                    deposit.remove(denomination)
                }
                remaining -= denomination
                remaining = String.format("%.2f", remaining).toFloat()
            }
        }

        if (remaining > 0f) {
            log("Warning: could not remove exact amount ₱$remaining from deposit")
        }
    }

    private fun updateItemButtonsEnabledState() {
        itemButtons.forEach { button ->
            val price = button.text.replace("₱", "").toFloatOrNull() ?: return@forEach
            button.isDisable = balance < price
        }
    }

    private fun updateBalance() {
        balanceLabel.text = "₱%.2f".format(balance)
    }

    private fun logCurrentState() {
        log("Current balance: ₱%.2f".format(balance))
        log("Deposit map: $deposit")
    }

    private fun log(message: String) {
        println("[TestController] $message")
        System.out.flush()
    }

    @FXML
    fun backToMainPage(event: ActionEvent) {
        val root: Parent = FXMLLoader.load(javaClass.getResource("/fxml/main.fxml"))
        val stage = (event.source as javafx.scene.Node).scene.window as Stage
        stage.scene = Scene(root)
    }
}
