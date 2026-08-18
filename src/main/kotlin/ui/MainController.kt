package ui

import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.scene.control.ButtonType
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.Stage
import model.Item
import model.SpecialMachine
import java.io.File

class MainController {

    @FXML
    private lateinit var machineContainer: HBox

    @FXML
    private lateinit var itemContainer: HBox


    /*
     * ==========================================================
     * INITIALIZE
     * ==========================================================
     *
     * Items are loaded first because inventory.csv stores
     * item names.
     *
     * Machines are then loaded once into MachineManager.
     *
     * From this point onward, the machine objects inside
     * MachineManager.machines are the objects used by the UI.
     */

    @FXML
    fun initialize() {

        ItemManager.loadItems()

        MachineManager.loadMachines()

        renderMachines()
        renderItems()
    }


    /*
     * ==========================================================
     * RENDER MACHINES
     * ==========================================================
     */

    private fun renderMachines() {

        machineContainer.children.clear()

        MachineManager.machines.forEach { entry ->

            machineContainer.children.add(
                createMachineCard(entry)
            )
        }

        machineContainer.children.add(
            createAddCard("Create Machine")
        )
    }


    /*
     * ==========================================================
     * RENDER ITEMS
     * ==========================================================
     */

    private fun renderItems() {

        itemContainer.children.clear()

        ItemManager.items.forEach { item ->

            itemContainer.children.add(
                createItemCard(item)
            )
        }

        itemContainer.children.add(
            createAddCard("Create Item")
        )
    }


    /*
     * ==========================================================
     * MACHINE CARD
     * ==========================================================
     */

    private fun createMachineCard(
        entry: MachineManager.MachineEntry
    ): VBox {

        val title =
            Label(entry.folder.name)

        title.style =
            "-fx-text-fill:white;" +
            "-fx-font-size:20;" +
            "-fx-font-weight:bold;"


        val type =
            Label(
                if (entry.machine is SpecialMachine)
                    "Special Machine"
                else
                    "Regular Machine"
            )

        type.style =
            "-fx-text-fill:#CFCFCF;"


        /*
         * ======================================================
         * TEST
         * ======================================================
         *
         * IMPORTANT:
         *
         * We use entry.machine directly.
         *
         * We DO NOT create another VendingMachine.
         *
         * We DO NOT reload the machine from CSV.
         *
         * Therefore TestController receives the exact same
         * machine object currently stored in MachineManager.
         */

        val testButton =
            Button("Test")

        testButton.setOnAction {

            selectMachine(entry)

            openTestPage()
        }


        /*
         * ======================================================
         * MAINTENANCE
         * ======================================================
         *
         * Same object is passed to MaintenanceController.
         */

        val maintenanceButton =
            Button("Maintenance")

        maintenanceButton.setOnAction {

            selectMachine(entry)

            openMaintenancePage()
        }


        val buttons =
            VBox(8.0)

        buttons.children.addAll(
            testButton,
            maintenanceButton
        )


        val card =
            VBox(10.0)

        card.children.addAll(
            title,
            type,
            buttons
        )

        card.prefWidth = 220.0

        card.style =
            "-fx-background-color:#1A1A1A;" +
            "-fx-background-radius:15;" +
            "-fx-padding:20;"


        return card
    }


    /*
     * ==========================================================
     * SELECT MACHINE
     * ==========================================================
     *
     * This is the important part.
     *
     * SelectedMachine.machine points to the SAME object as:
     *
     * MachineManager.machines[index].machine
     *
     * No copy is made.
     */

    private fun selectMachine(
        entry: MachineManager.MachineEntry
    ) {

        SelectedMachine.folder =
            entry.folder

        SelectedMachine.machine =
            entry.machine
    }


    /*
     * ==========================================================
     * ITEM CARD
     * ==========================================================
     */

    private fun createItemCard(
        item: Item
    ): VBox {

        val image =
            ImageView()

        try {

            image.image =
                Image(
                    File(item.iconPath)
                        .toURI()
                        .toString()
                )

        } catch (_: Exception) {

            image.image = null
        }

        image.fitWidth = 120.0
        image.fitHeight = 120.0
        image.isPreserveRatio = true


        val title =
            Label(item.name)

        title.style =
            "-fx-text-fill:white;" +
            "-fx-font-size:20;" +
            "-fx-font-weight:bold;"


        val calories =
            Label(
                "${item.calories} kcal"
            )

        calories.style =
            "-fx-text-fill:white;"


        val removeButton =
            Button("Remove")

        removeButton.setOnAction {

            confirmRemoveItem(item)
        }


        val card =
            VBox(10.0)

        card.children.addAll(
            image,
            title,
            calories,
            removeButton
        )

        card.prefWidth = 220.0

        card.style =
            "-fx-background-color:#1A1A1A;" +
            "-fx-background-radius:15;" +
            "-fx-padding:20;"


        return card
    }


    /*
     * ==========================================================
     * REMOVE ITEM
     * ==========================================================
     */

    private fun confirmRemoveItem(
        item: Item
    ) {

        val alert =
            Alert(Alert.AlertType.CONFIRMATION)

        alert.title =
            "Remove Item"

        alert.headerText =
            "Delete ${item.name}?"

        alert.contentText =
            "This action cannot be undone."


        val result =
            alert.showAndWait()


        if (
            result.isPresent &&
            result.get() == ButtonType.OK
        ) {

            ItemManager.items.remove(item)

            ItemManager.saveItems()

            /*
             * If an item was removed from the item database,
             * remove references to that item from machine slots.
             *
             * This prevents inventory.csv from pointing to an
             * item that no longer exists.
             */

            MachineManager.machines.forEach { entry ->

                entry.machine.slots.forEach { slot ->

                    if (slot.item == item) {

                        slot.item = null
                        slot.quantity = 0
                        slot.price = 0f
                        slot.sold = 0
                    }
                }

                MachineManager.saveMachine(entry)
            }


            renderItems()
            renderMachines()
        }
    }


    /*
     * ==========================================================
     * ADD CARD
     * ==========================================================
     */

    private fun createAddCard(
        labelText: String
    ): VBox {

        val plusButton =
            Button("+")

        plusButton.prefWidth = 80.0
        plusButton.prefHeight = 80.0


        when (labelText) {

            "Create Machine" -> {

                plusButton.setOnAction {

                    openCreateMachinePage()
                }
            }


            "Create Item" -> {

                plusButton.setOnAction {

                    openCreateItemPage()
                }
            }
        }


        val label =
            Label(labelText)

        label.style =
            "-fx-text-fill:white;"


        val card =
            VBox(15.0)

        card.children.addAll(
            plusButton,
            label
        )

        card.prefWidth = 220.0

        card.style =
            "-fx-background-color:#343434;" +
            "-fx-background-radius:15;" +
            "-fx-padding:20;"


        return card
    }


    /*
     * ==========================================================
     * OPEN CREATE MACHINE
     * ==========================================================
     */

    private fun openCreateMachinePage() {

        changeScene(
            "/fxml/create-machine.fxml"
        )
    }


    /*
     * ==========================================================
     * OPEN CREATE ITEM
     * ==========================================================
     */

    private fun openCreateItemPage() {

        changeScene(
            "/fxml/create-item.fxml"
        )
    }


    /*
     * ==========================================================
     * OPEN TEST
     * ==========================================================
     */

    private fun openTestPage() {

        changeScene(
            "/fxml/test.fxml"
        )
    }


    /*
     * ==========================================================
     * OPEN MAINTENANCE
     * ==========================================================
     */

    private fun openMaintenancePage() {

        changeScene(
            "/fxml/maintenance.fxml"
        )
    }


    /*
     * ==========================================================
     * CHANGE SCENE
     * ==========================================================
     */

    private fun changeScene(
        fxmlPath: String
    ) {

        try {

            val resource =
                javaClass.getResource(fxmlPath)
                    ?: error(
                        "Cannot find FXML: $fxmlPath"
                    )


            val loader =
                FXMLLoader(resource)


            val root: Parent =
                loader.load()


            val stage =
                machineContainer
                    .scene
                    .window as Stage


            stage.scene =
                Scene(root)


        } catch (e: Exception) {

            e.printStackTrace()


            val alert =
                Alert(Alert.AlertType.ERROR)

            alert.title =
                "Navigation Error"

            alert.headerText =
                "Failed to open page"

            alert.contentText =
                "${e::class.simpleName}: ${e.message}"

            alert.showAndWait()
        }
    }
}