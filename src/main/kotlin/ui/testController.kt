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

class TestController {

    @FXML
    private lateinit var balanceLabel: Label

    @FXML
    private lateinit var displayLabel: Label

    @FXML
    private lateinit var slotButton0: Button

    @FXML
    private lateinit var slotButton1: Button

    @FXML
    private lateinit var slotButton2: Button

    @FXML
    private lateinit var slotButton3: Button

    @FXML
    private lateinit var slotButton4: Button

    @FXML
    private lateinit var slotButton5: Button

    @FXML
    private lateinit var slotButton6: Button

    @FXML
    private lateinit var slotButton7: Button

    @FXML
    private lateinit var addOnButton0: Button

    @FXML
    private lateinit var addOnButton1: Button

    @FXML
    private lateinit var addOnButton2: Button

    private val deposit: Cash = mutableMapOf()
    private val denominations = listOf(1000f, 500f, 200f, 100f, 50f, 20f, 10f, 5f, 1f)
    private var balance = 0f
    private val itemButtons = mutableListOf<Button>()

    @FXML
    fun initialize() {
        itemButtons.addAll(
            listOf(
                slotButton0,
                slotButton1,
                slotButton2,
                slotButton3,
                slotButton4,
                slotButton5,
                slotButton6,
                slotButton7,
                addOnButton0,
                addOnButton1,
                addOnButton2
            )
        )
        updateBalance()
        updateItemButtonsEnabledState()
        log("TestController initialized. Starting balance: ₱%.2f".format(balance))
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
    fun selectItem(event: ActionEvent) {
        val button = event.source as Button
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
