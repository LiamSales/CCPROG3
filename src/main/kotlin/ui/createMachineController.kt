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
    fun initialize() {

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

        // Allow free typing (including temporary values < min) — only commit valid integers on focus lost
        slotSpinner.editor.focusedProperty().addListener { _, _, focused ->
            if (!focused) {
                val parsed = slotSpinner.editor.text.toIntOrNull()

                if (parsed != null && parsed >= min) {
                    factory.value = parsed
                }
                // otherwise leave editor text as-is so submit can validate and show errors
            }
        }
    }

    private fun configureItemLimitSpinner() {

        val min = 10
        val factory = SpinnerValueFactory.IntegerSpinnerValueFactory(min, Int.MAX_VALUE, min)
        itemLimitSpinner.valueFactory = factory

        // Allow free typing (including temporary values < min) — only commit valid integers on focus lost
        itemLimitSpinner.editor.focusedProperty().addListener { _, _, focused ->
            if (!focused) {
                val parsed = itemLimitSpinner.editor.text.toIntOrNull()

                if (parsed != null && parsed >= min) {
                    factory.value = parsed
                }
                // otherwise leave editor text as-is so submit can validate and show errors
            }
        }
    }

    private fun configureAddonSpinner() {

        val min = 1
        val factory = SpinnerValueFactory.IntegerSpinnerValueFactory(min, Int.MAX_VALUE, min)
        addonSpinner.valueFactory = factory

        // Allow free typing — commit only valid integers when editor loses focus
        addonSpinner.editor.focusedProperty().addListener { _, _, focused ->
            if (!focused) {
                val parsedValue = addonSpinner.editor.text.toIntOrNull()

                if (parsedValue != null && parsedValue >= min) {
                    factory.value = parsedValue
                }
                // otherwise leave editor text as-is so submit can validate and show errors
            }
        }
    }

    private fun updateAddonSectionVisibility(isSelected: Boolean) {

        addonSection.isVisible = isSelected
        addonSection.isManaged = isSelected

        // keep the spinner value at the minimum when hidden or shown
        try {
            addonSpinner.valueFactory?.value = 1
        } catch (e: Exception) {
            // if valueFactory isn't ready yet, ensure editor shows a sensible default
            addonSpinner.editor.text = "1"
        }
    }

    @FXML
    fun back(event: ActionEvent) {

        openMainPage(event)

    }

    @FXML
    fun submitMachine(event: ActionEvent) {

        // Validate editor contents explicitly so in-progress typing is checked on submit
        val slotsParsed = slotSpinner.editor.text.toIntOrNull()
        if (slotsParsed == null || slotsParsed <= 7) {
            showError("Number of slots must be an integer of at least 8.")
            return
        }

        val itemLimitParsed = itemLimitSpinner.editor.text.toIntOrNull()
        if (itemLimitParsed == null || itemLimitParsed <= 9) {
            showError("Maximum items per slot must be an integer of at least 10.")
            return
        }

        val slots = slotsParsed
        val itemLimit = itemLimitParsed

        val addOnItems = addonSpinner.editor.text.toIntOrNull() ?: (addonSpinner.value ?: 1)


        val special =
            specialCheckBox.isSelected

        if (special && addOnItems < 1) {
            showError("A special machine must have at least one add-on slot.")
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

        val root = File("data/machines")
        root.mkdirs()

        val nextNumber = getNextMachineNumber(root)

        val folder = File(
            root,
            "machine_%04d".format(nextNumber)
        )

        folder.mkdirs()

        createInfoFile(folder, machine)
        createInventoryFile(folder, machine)
        createTransactionHistoryFile(folder)
        createRegisterFile(folder)

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

        private fun getNextMachineNumber(
        root: File
    ): Int {

        var largest = 0

        root.listFiles()?.forEach { file ->

            if (
                file.isDirectory &&
                file.name.startsWith("machine_")
            ) {

                val number =
                    file.name
                        .removePrefix("machine_")
                        .toIntOrNull()

                if (
                    number != null &&
                    number > largest
                ) {
                    largest = number
                }
            }
        }

        return largest + 1
    }


        private fun createInfoFile(

        folder: File,
        machine: VendingMachine

    ) {

        val file =
            File(folder, "info.csv")

        if (machine is SpecialMachine) {

            file.writeText(

                "${machine.slotLimit}\n" +
                "${machine.itemLimit}\n" +
                "${machine.getAddOnSlots().size}"

            )

        }

        else {

            file.writeText(

                "${machine.slotLimit}\n" +
                machine.itemLimit

            )

        }

    }

    private fun createInventoryFile(

        folder: File,
        machine: VendingMachine

    ) {

        val file =
            File(folder, "inventory.csv")

        file.printWriter().use {

            out ->

            for (

                i in 0 until machine.slotLimit

            ) {

                out.println(
                    "${i + 1},,0,0,0"
                )

            }

        }

    }

    private fun createTransactionHistoryFile(

        folder: File

    ) {

        File(
            folder,
            "transactionhistory.csv"
        ).writeText(""
        )

    }

        private fun createRegisterFile(

        folder: File

    ) {

        File(
            folder,
            "register.csv"
        ).writeText(

            "0\n0\n0\n0\n0\n0\n0\n0\n0"

        )

    }

    private fun showError(message: String) {

        Alert(Alert.AlertType.ERROR).apply {

            title = "Invalid Input"
            headerText = null
            contentText = message

        }.showAndWait()
    }


}
