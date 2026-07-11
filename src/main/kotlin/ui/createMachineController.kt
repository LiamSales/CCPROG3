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
import model.SpecialMachine
import model.VendingMachine
import java.io.File

class CreateMachineController {

    @FXML
    private lateinit var slotSpinner: Spinner<Int>

    @FXML
    private lateinit var itemLimitSpinner: Spinner<Int>

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

        val addOnItems =
            addonSpinner.editor.text.toIntOrNull() ?: 0

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

        val special =
            specialCheckBox.isSelected

        if (special && addOnItems < 1) {

            showError(
                "A special machine must have at least one add-on slot."
            )

            return
        }

        val machine: VendingMachine =
            if (special) {

                SpecialMachine(
                    slots,
                    itemLimit,
                    addOnItems
                )

            } else {

                VendingMachine(
                    slots,
                    itemLimit
                )

            }

        saveMachine(machine)

        println("Machine created.")
        println(machine)

        openMainPage(event)
    }

    private fun saveMachine(machine: VendingMachine) {

        val file = File("machines.csv")

        val special =
            machine is SpecialMachine

        val addOnLimit =
            if (special)
                (machine as SpecialMachine).getAddOnSlots().size
            else
                0

        file.appendText(
            "${machine.slotLimit}," +
            "${machine.itemLimit}," +
            "$special," +
            "$addOnLimit\n"
        )
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
