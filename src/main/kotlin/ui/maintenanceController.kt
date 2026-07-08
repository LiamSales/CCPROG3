package ui

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.scene.control.ButtonType
import javafx.scene.control.Label
import javafx.scene.layout.GridPane
import javafx.scene.layout.VBox
import javafx.stage.Stage

class MaintenanceController {

    @FXML
    private lateinit var slotGrid: GridPane

    @FXML
    private lateinit var selectedSlotLabel: Label

    private var selectedSlot = -1

    @FXML
    fun initialize() {

        for (i in 1..8) {

            val card = VBox(8.0)

            card.alignment = Pos.CENTER
            card.prefWidth = 120.0
            card.prefHeight = 120.0

            card.style =
                "-fx-background-color:white;" +
                "-fx-border-color:gray;" +
                "-fx-background-radius:10;" +
                "-fx-border-radius:10;"

            val slot = Label("Slot $i")
            val item = Label("Empty")

            val button = Button("Select")

            button.setOnAction {

                selectedSlot = i

                selectedSlotLabel.text =
                    "Selected Slot: $i"

            }

            card.children.addAll(
                slot,
                item,
                button
            )

            slotGrid.add(
                card,
                (i - 1) % 4,
                (i - 1) / 4
            )
        }
    }

    @FXML
    fun setSlot() {
        popup("Set Slot", "Set Slot for Slot $selectedSlot")
    }

    @FXML
    fun clearSlot() {
        popup("Clear Slot", "Clear Slot $selectedSlot")
    }

    @FXML
    fun restockSlot(event: ActionEvent) {

        val root: Parent =
            FXMLLoader.load(
                javaClass.getResource("/fxml/restock.fxml")
            )

        val stage =
            (event.source as javafx.scene.Node)
                .scene.window as Stage

        stage.scene = Scene(root)
    }

    @FXML
    fun changePrice() {
        popup("Change Price", "Change price for Slot $selectedSlot")
    }

    @FXML
    fun replenishCash() {
        popup("Cash", "Cash replenished.")
    }

    @FXML
    fun collectBalance() {
        popup("Balance", "Balance collected.")
    }

    @FXML
    fun displaySummary() {
        popup("Summary", "Summary page coming soon.")
    }

    @FXML
    fun removeMachine(event: ActionEvent) {

        val alert =
            Alert(Alert.AlertType.CONFIRMATION)

        alert.title = "Remove Machine"
        alert.headerText = "Remove Machine?"
        alert.contentText =
            "This action cannot be undone."

        if (alert.showAndWait().get() == ButtonType.OK) {

            backToMainPage(event)

        }
    }

    @FXML
    fun backToMainPage(event: ActionEvent) {

        val root: Parent =
            FXMLLoader.load(
                javaClass.getResource("/fxml/main.fxml")
            )

        val stage =
            (event.source as javafx.scene.Node)
                .scene.window as Stage

        stage.scene = Scene(root)
    }

    private fun popup(title: String, message: String) {

        Alert(Alert.AlertType.INFORMATION).apply {

            this.title = title
            headerText = null
            contentText = message

        }.showAndWait()
    }
}