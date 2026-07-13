package ui

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.scene.control.CheckBox
import javafx.scene.control.Spinner
import javafx.scene.control.SpinnerValueFactory
import javafx.scene.layout.VBox
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
    private lateinit var addonSection: VBox

    @FXML
    private fun initialize() {

        configureSlotSpinner()
        configureItemLimitSpinner()
        configureAddonSpinner()

        updateAddonSectionVisibility(specialCheckBox.isSelected)

        specialCheckBox.selectedProperty().addListener { _, _, isSelected ->
            updateAddonSectionVisibility(isSelected)
        }
    }

    private fun configureSlotSpinner() {

        val min = 8
        val factory = SpinnerValueFactory.IntegerSpinnerValueFactory(min, Int.MAX_VALUE, min)
        slotSpinner.valueFactory = factory

        slotSpinner.editor.textProperty().addListener { _, _, newValue ->
            val parsed = newValue.toIntOrNull()

            if (parsed == null || parsed < min) {
                slotSpinner.editor.text = min.toString()
                factory.value = min
            } else {
                factory.value = parsed
            }
        }
    }

    private fun configureItemLimitSpinner() {

        val min = 10
        val factory = SpinnerValueFactory.IntegerSpinnerValueFactory(min, Int.MAX_VALUE, min)
        itemLimitSpinner.valueFactory = factory

        itemLimitSpinner.editor.textProperty().addListener { _, _, newValue ->
            val parsed = newValue.toIntOrNull()

            if (parsed == null || parsed < min) {
                itemLimitSpinner.editor.text = min.toString()
                factory.value = min
            } else {
                factory.value = parsed
            }
        }
    }

    private fun configureAddonSpinner() {

        val factory = SpinnerValueFactory.IntegerSpinnerValueFactory(1, Int.MAX_VALUE, 1)
        addonSpinner.valueFactory = factory

        addonSpinner.editor.textProperty().addListener { _, _, newValue ->
            val parsedValue = newValue.toIntOrNull()

            if (parsedValue == null || parsedValue < 1) {
                addonSpinner.editor.text = "1"
                factory.value = 1
            } else {
                factory.value = parsedValue
            }
        }
    }

    private fun updateAddonSectionVisibility(isSelected: Boolean) {

        addonSection.isVisible = isSelected
        addonSection.isManaged = isSelected

        // keep the spinner value at the minimum when hidden or shown
        if (!isSelected) {
            addonSpinner.editor.text = "1"
        } else {
            addonSpinner.editor.text = "1"
        }
    }

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
            addonSpinner.value ?: 1

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
