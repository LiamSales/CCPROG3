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
import java.io.File
import model.SpecialMachine

class MainController {

    @FXML
    private lateinit var machineContainer: HBox

    @FXML
    private lateinit var itemContainer: HBox
@FXML
fun initialize() {

    /*
     * Items MUST be loaded first because
     * inventory.csv stores item names.
     */

    ItemManager.loadItems()

    /*
     * Machines can now resolve those item names
     * when loading inventory.csv.
     */

    MachineManager.loadMachines()

    renderMachines()
    renderItems()

}

    /*
     * ==========================================================
     * Machine Loading
     * ==========================================================
     */

   private fun renderMachines() {

    machineContainer.children.clear()

    MachineManager.machines.forEach {

        machineContainer.children.add(

            createMachineCard(it)

        )

    }

    machineContainer.children.add(

        createAddCard(
            "Create Machine"
        )

    )

}

    /*
     * ==========================================================
     * UI Rendering
     * ==========================================================
     */

    private fun renderItems() {

        itemContainer.children.clear()

        ItemManager.items.forEach {

            itemContainer.children.add(

                createItemCard(it)

            )

        }

        itemContainer.children.add(

            createAddCard(
                "Create Item"
            )

        )

    }

    /*
     * ==========================================================
     * Machine Card
     * ==========================================================
     */

/*
 * ==========================================================
 * Machine Card
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

    val testButton =
        Button("Test")

    testButton.setOnAction {

        SelectedMachine.folder =
            entry.folder

        SelectedMachine.machine =
            entry.machine

        openTestPage()

    }

    val maintenanceButton =
        Button("Maintenance")

    maintenanceButton.setOnAction {

        SelectedMachine.folder =
            entry.folder

        SelectedMachine.machine =
            entry.machine

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
     * Item Card
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

        }

        catch (_: Exception) {

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
     * Remove Item
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

            renderItems()

        }

    }

    /*
     * ==========================================================
     * Add Card
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

            "Create Machine" ->

                plusButton.setOnAction {

                    openCreateMachinePage()

                }

            "Create Item" ->

                plusButton.setOnAction {

                    openCreateItemPage()

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
     * Navigation
     * ==========================================================
     */

    private fun openCreateMachinePage() {

        changeScene(
            "/fxml/create-machine.fxml"
        )

    }

    private fun openCreateItemPage() {

        changeScene(
            "/fxml/create-item.fxml"
        )

    }

    private fun openTestPage() {

        changeScene(
            "/fxml/test.fxml"
        )

    }

    private fun openMaintenancePage() {

        changeScene(
            "/fxml/maintenance.fxml"
        )

    }

    private fun changeScene(
        fxmlPath: String
    ) {

        try {

            val resource =
                javaClass.getResource(fxmlPath)
                    ?: error(
                        "Cannot find FXML: $fxmlPath"
                    )

            val loader = FXMLLoader(resource)

        val root: Parent =
            loader.load()

            val stage =
                machineContainer
                    .scene
                    .window as Stage

            stage.scene =
                Scene(root)

        }

        catch (e: Exception) {

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