package ui

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.scene.control.CheckBox
import javafx.scene.control.Spinner
import javafx.stage.Stage

class CreateMachineController {

    @FXML
    private lateinit var slotSpinner: Spinner<Int>

    @FXML
    private lateinit var itemLimitSpinner: Spinner<Int>

    @FXML
    private lateinit var cashCapacitySpinner: Spinner<Int>

    @FXML
    private lateinit var specialCheckBox: CheckBox

    @FXML
    private lateinit var addonSpinner: Spinner<Int>

    @FXML
    fun back(event: ActionEvent) {

        openMainPage(event)

    }

    @FXML
    fun submitMachine(event: ActionEvent) {

        val slots =
            slotSpinner.editor.text.toIntOrNull()

        val itemLimit =
            itemLimitSpinner.editor.text.toIntOrNull()

        val cashCapacity =
            cashCapacitySpinner.editor.text.toIntOrNull()

        val addOnItems =
            addonSpinner.editor.text.toIntOrNull()

        if (slots == null || slots < 8) {

            showError(
                "A vending machine must have at least 8 slots."
            )

            return
        }

        if (itemLimit == null || itemLimit < 10) {

            showError(
                "Each slot must hold at least 10 items."
            )

            return
        }

        if (cashCapacity == null || cashCapacity < 100) {

            showError(
                "Cash register capacity must be at least 100."
            )

            return
        }

        if (addOnItems == null || addOnItems < 1) {

            showError(
                "There must be at least one add-on item."
            )

            return
        }

        val special =
            specialCheckBox.isSelected

        // Saving will be added later.

        println(special)

        openMainPage(event)
    }

    private fun openMainPage(event: ActionEvent) {

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

    private fun showError(message: String) {

        Alert(Alert.AlertType.ERROR).apply {

            title = "Invalid Input"
            headerText = null
            contentText = message

        }.showAndWait()
    }
}