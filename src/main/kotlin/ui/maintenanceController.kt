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
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.Stage
import java.io.File
import model.VendingMachine

class MaintenanceController {

    @FXML
    private lateinit var slotGrid: GridPane

    private lateinit var machineFolder: File

    @FXML
    fun initialize() {

        machineFolder =
        SelectedMachine.folder
            ?: error("No machine selected.")

        for (i in 1..8) {

            val card = VBox(10.0)

            card.alignment = Pos.CENTER
            card.prefWidth = 220.0
            card.prefHeight = 220.0

            card.style =
                "-fx-background-color: white;" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: lightgray;" +
                "-fx-border-radius: 12;" +
                "-fx-padding: 15;"

            val slotLabel = Label("Slot $i")

            slotLabel.style =
                "-fx-font-size:18px;" +
                "-fx-font-weight:bold;"

            val itemLabel = Label("Item: Empty")
            val qtyLabel = Label("Quantity: 0")
            val priceLabel = Label("Price: ₱0")

            val setButton = Button("Set")
            val clearButton = Button("Clear")
            val restockButton = Button("Restock")
            val priceButton = Button("Price")

            setButton.setOnAction {
                popup("Set Slot", "Set Slot $i")
            }

            clearButton.setOnAction {
                popup("Clear Slot", "Clear Slot $i")
            }

            restockButton.setOnAction {

                val root: Parent =
                    FXMLLoader.load(
                        javaClass.getResource("/fxml/restock.fxml")
                    )

                val stage =
                    slotGrid.scene.window as Stage

                stage.scene = Scene(root)
            }

            priceButton.setOnAction {
                popup("Change Price", "Change price for Slot $i")
            }

            val row1 = HBox(10.0)
            row1.alignment = Pos.CENTER
            row1.children.addAll(setButton, clearButton)

            val row2 = HBox(10.0)
            row2.alignment = Pos.CENTER
            row2.children.addAll(restockButton, priceButton)

            card.children.addAll(
                slotLabel,
                itemLabel,
                qtyLabel,
                priceLabel,
                row1,
                row2
            )

            slotGrid.add(
                card,
                (i - 1) % 4,
                (i - 1) / 4
            )
        }
    }

        private fun loadMachine(): VendingMachine {

        val infoFile = File(machineFolder, "info.csv")

        val lines = infoFile.readLines()

        val slotCount = lines[0].trim().toInt()
        val slotCapacity = lines[1].trim().toInt()

        return VendingMachine(
            slotCount,
            slotCapacity
        )
    }


    @FXML
    fun replenishCash() {
        popup("Replenish Cash", "Cash replenished.")
    }

    @FXML
    fun collectBalance() {
        popup("Collect Balance", "Balance collected.")
    }

    @FXML
    fun displaySummary() {
        popup("Summary", "Summary page coming soon.")
    }

@FXML
fun removeMachine(event: ActionEvent) {

    val alert =
        Alert(Alert.AlertType.CONFIRMATION)

    alert.title =
        "Remove Machine"

    alert.headerText =
        "Remove Machine?"

    alert.contentText =
        "This action cannot be undone."

    val result =
        alert.showAndWait()

    if (

        result.isPresent &&
        result.get() == ButtonType.OK

    ) {

        deleteFolder(machineFolder)

        backToMainPage(event)

    }

}

private fun deleteFolder(
    folder: File
) {

    folder.listFiles()?.forEach {

        if (it.isDirectory) {

            deleteFolder(it)

        }

        else {

            it.delete()

        }

    }

    folder.delete()

}

    @FXML
    fun backToMainPage(event: ActionEvent) {

        val root: Parent =
            FXMLLoader.load(
                javaClass.getResource("/fxml/main.fxml")
            )

        val stage =
            (event.source as javafx.scene.Node)
                .scene
                .window as Stage

        stage.scene = Scene(root)
    }

    private fun popup(title: String, message: String) {

        val alert = Alert(Alert.AlertType.INFORMATION)

        alert.title = title
        alert.headerText = null
        alert.contentText = message

        alert.showAndWait()
    }
}