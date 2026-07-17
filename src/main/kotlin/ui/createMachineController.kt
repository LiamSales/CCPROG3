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
import model.CashRegister
import java.io.File

// lets not save in a csv anymore, just the folders for each, so we only save in folders, we just get the latest folder name (machine_xxxx) then increment the name of the folder
// if the machine is not a special machine the info is basically "slot limit, item limit" thats it thats all there is,

// if the machine is a special machine, then slot limit, item limit, addon limit, basically another line in the text, thats all

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

        val factory =
            SpinnerValueFactory
                .IntegerSpinnerValueFactory(
                    8,
                    Int.MAX_VALUE,
                    8
                )

        slotSpinner.valueFactory = factory
    }

    private fun configureItemLimitSpinner() {

        val factory =
            SpinnerValueFactory
                .IntegerSpinnerValueFactory(
                    10,
                    Int.MAX_VALUE,
                    10
                )

        itemLimitSpinner.valueFactory =
            factory
    }

    private fun configureAddonSpinner() {

        val factory =
            SpinnerValueFactory
                .IntegerSpinnerValueFactory(
                    1,
                    Int.MAX_VALUE,
                    1
                )

        addonSpinner.valueFactory =
            factory
    }

    private fun updateAddonSectionVisibility(
        visible: Boolean
    ) {

        addonSection.isVisible = visible
        addonSection.isManaged = visible
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

        val addonSlots =
            if (special)
                addonSpinner.editor.text.toIntOrNull()
            else
                0

        if (
            special &&
            (addonSlots == null || addonSlots < 1)
        ) {

            showError(
                "Special machines need at least one add-on slot."
            )

            return
        }

        val machine: VendingMachine =

            if (special) {

                SpecialMachine(
                    slots,
                    itemLimit,
                    addonSlots!!
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

        val dataFolder =
            File("data")

        dataFolder.mkdirs()

        val machineRoot =
            File(dataFolder, "machines")

        machineRoot.mkdirs()

        val nextId =
            getNextMachineId(machineRoot)

        val special = machine is SpecialMachine

        val addonSlots =
            if (special)
                (machine as SpecialMachine)
                    .getAddOnSlots()
                    .size
            else
                0

        val folder =

            File(
                machineRoot,
                "machine_%04d".format(nextId)
            )

        folder.mkdirs()

        createInfoFile(
            folder,
            machine,
            nextId,
            special,
            addonSlots
        )

        createInventoryFile(
            folder,
            machine
        )

        createRegisterFile(
            folder
        )

        createTransactionFile(
            folder
        )

        println(
            "Created machine_${
                "%04d".format(nextId)
            }"
        )
    }

    private fun getNextMachineId(
        machineRoot: File
    ): Int {

        if (!machineRoot.exists())
            return 1

        val ids =
            machineRoot.listFiles()
                ?.filter { it.isDirectory && it.name.startsWith("machine_") }
                ?.mapNotNull { dir ->

                    dir.name.removePrefix("machine_")
                        .toIntOrNull()

                } ?: emptyList()

        return (ids.maxOrNull() ?: 0) + 1
    }

    private fun createInfoFile(

        folder: File,
        machine: VendingMachine,
        id: Int,
        special: Boolean,
        addonSlots: Int

    ) {

        File(
            folder,
            "info.csv"
        ).writeText(

            buildString {

                appendLine("field,value")
                appendLine("id,$id")
                appendLine("slotLimit,${machine.slotLimit}")
                appendLine("itemLimit,${machine.itemLimit}")
                appendLine("special,$special")
                appendLine("addonSlots,$addonSlots")
                appendLine("totalSales,0")

            }

        )
    }

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

private fun createRegisterFile(
    folder: File
) {

    val file =
        File(
            folder,
            "register.csv"
        )

    val register =
        CashRegister()

    file.printWriter().use { out ->

        out.println(
            "denomination,quantity"
        )

        register
            .getContents()
            .toSortedMap()
            .forEach {

                (denomination, quantity) ->

                out.println(
                    "$denomination,$quantity"
                )

            }

    }

}

private fun createTransactionFile(
    folder: File
) {

        val file =
            File(
                folder,
                "transactions.csv"
            )

        file.printWriter().use { out ->

            out.println(
                "id,timestamp,slot,quantity,amount"
            )

        }
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


