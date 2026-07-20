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

        updateAddonSectionVisibility(
            specialCheckBox.isSelected
        )

        specialCheckBox.selectedProperty().addListener {

            _, _, selected ->

            updateAddonSectionVisibility(
                selected
            )

        }
    }

    /*
     * ==========================================================
     * Spinner Configuration
     * ==========================================================
     */

    private fun configureSlotSpinner() {

        val minimum = 8

        val factory =

            SpinnerValueFactory
                .IntegerSpinnerValueFactory(
                    minimum,
                    Int.MAX_VALUE,
                    minimum
                )

        slotSpinner.valueFactory = factory
    }

    private fun configureItemLimitSpinner() {

        val minimum = 10

        val factory =

            SpinnerValueFactory
                .IntegerSpinnerValueFactory(
                    minimum,
                    Int.MAX_VALUE,
                    minimum
                )

        itemLimitSpinner.valueFactory =
            factory
    }

    private fun configureAddonSpinner() {

        val minimum = 1

        val factory =

            SpinnerValueFactory
                .IntegerSpinnerValueFactory(
                    minimum,
                    Int.MAX_VALUE,
                    minimum
                )

        addonSpinner.valueFactory =
            factory
    }

    private fun updateAddonSectionVisibility(
        visible: Boolean
    ) {

        addonSection.isVisible = visible
        addonSection.isManaged = visible

        addonSpinner.valueFactory?.value = 1
    }

    /*
     * ==========================================================
     * Buttons
     * ==========================================================
     */

    @FXML
    fun back(event: ActionEvent) {

        openMainPage(event)

    }

    @FXML
    fun submitMachine(event: ActionEvent) {

        val slots =
            slotSpinner.editor.text.toIntOrNull()

        if (slots == null || slots < 8) {

            showError(
                "A machine must have at least 8 slots."
            )

            return
        }

        val itemLimit =
            itemLimitSpinner.editor.text.toIntOrNull()

        if (itemLimit == null || itemLimit < 10) {

            showError(
                "Each slot must hold at least 10 items."
            )

            return
        }

        val special =
            specialCheckBox.isSelected

        val addOnSlots =

            if (special)

                addonSpinner
                    .editor
                    .text
                    .toIntOrNull()

            else

                0

        if (

            special &&
            (addOnSlots == null || addOnSlots < 1)

        ) {

            showError(
                "A special machine requires at least one add-on slot."
            )

            return
        }

        val machine: VendingMachine =

            if (special) {

                SpecialMachine(

                    slots,
                    itemLimit,
                    addOnSlots!!

                )

            }

            else {

                VendingMachine(

                    slots,
                    itemLimit

                )

            }

        saveMachine(machine)

        println("Machine successfully created.")

        openMainPage(event)
    }

        /*
     * ==========================================================
     * Save Machine
     * ==========================================================
     */

    private fun saveMachine(
        machine: VendingMachine
    ) {

        val machineRoot =
            File("data/machines")

        machineRoot.mkdirs()

        val nextId =
            getNextMachineId(machineRoot)

        val machineFolder =

            File(

                machineRoot,

                "machine_%04d".format(
                    nextId
                )

            )

        machineFolder.mkdirs()

        createInfoFile(

            machineFolder,
            machine

        )

        createInventoryFile(

            machineFolder,
            machine

        )

        createTransactionHistoryFile(

            machineFolder

        )

        println(
            "Created ${machineFolder.name}"
        )
    }

    /*
     * ==========================================================
     * Machine Numbering
     * ==========================================================
     */

    private fun getNextMachineId(

        machineRoot: File

    ): Int {

        val ids =

            machineRoot
                .listFiles()
                ?.filter {

                    it.isDirectory &&
                    it.name.startsWith(
                        "machine_"
                    )

                }
                ?.mapNotNull {

                    it.name
                        .removePrefix(
                            "machine_"
                        )
                        .toIntOrNull()

                }

                ?: emptyList()

        return

            (ids.maxOrNull() ?: 0) + 1
    }

    /*
     * ==========================================================
     * Create info.csv
     * ==========================================================
     */

    private fun createInfoFile(

        folder: File,
        machine: VendingMachine

    ) {

        val file =

            File(
                folder,
                "info.csv"
            )

        if (

            machine is SpecialMachine

        ) {

            file.writeText(

                "${machine.slotLimit}," +
                "${machine.itemLimit}," +
                machine
                    .getAddOnSlots()
                    .size

            )

        }

        else {

            file.writeText(

                "${machine.slotLimit}," +
                machine.itemLimit

            )

        }

    }

    /*
     * ==========================================================
     * Create inventory.csv
     * ==========================================================
     */

    private fun createInventoryFile(

        folder: File,
        machine: VendingMachine

    ) {

        val file =

            File(
                folder,
                "inventory.csv"
            )

        file.printWriter().use {

            out ->

            out.println(
                "slot,item,price,quantity,sold"
            )

            for (

                i in 0 until machine.slotLimit

            ) {

                out.println(

                    "${i + 1},,0,0,0"

                )

            }

        }

    }

    /*
     * ==========================================================
     * Create transactionhistory.csv
     * ==========================================================
     */

    private fun createTransactionHistoryFile(

        folder: File

    ) {

        val file =

            File(
                folder,
                "transactionhistory.csv"
            )

        file.printWriter().use {

            out ->

            out.println(

                "timestamp,item,quantity,total"

            )

        }

    }

    /*
     * ==========================================================
     * Navigation
     * ==========================================================
     */

    private fun openMainPage(
        event: ActionEvent
    ) {

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

        stage.scene = Scene(root)

    }

    /*
     * ==========================================================
     * Error Dialog
     * ==========================================================
     */

    private fun showError(
        message: String
    ) {

        Alert(
            Alert.AlertType.ERROR
        ).apply {

            title = "Invalid Input"

            headerText = null

            contentText = message

        }.showAndWait()

    }

}