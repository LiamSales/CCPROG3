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

class MainController {

    @FXML
    private lateinit var machineContainer: HBox

    @FXML
    private lateinit var itemContainer: HBox

    private val items = mutableListOf<Item>()

    @FXML
    fun initialize() {

        val machines = listOf(
            "Machine 1",
            "Machine 2",
            "Machine 3"
        )

        machines.forEach {
            machineContainer.children.add(
                createMachineCard(it)
            )
        }

        machineContainer.children.add(
            createAddCard("Create Machine")
        )

        loadItems()

        renderItems()
    }

    /*
     * ==========================================================
     * ITEM CSV
     * ==========================================================
     */

    private fun loadItems() {

        items.clear()

        val file =
            File("data/items.csv")

        if (!file.exists())
            return

        file.readLines().forEach { line ->

            if (line.isBlank())
                return@forEach

            val parts =
                line.split(",")

            if (parts.size < 3)
                return@forEach

            items.add(

                Item(

                    name = parts[0],

                    calories = parts[1].toInt(),

                    iconPath = parts[2]

                )

            )

        }

    }

    private fun saveItems() {

        val dataFolder =
            File("data")

        dataFolder.mkdirs()

        val file =
            File(dataFolder, "items.csv")

        file.printWriter().use { out ->

            items.forEach {

                out.println(

                    "${it.name}," +
                    "${it.calories}," +
                    it.iconPath

                )

            }

        }

    }

    /*
     * ==========================================================
     * UI Rendering
     * ==========================================================
     */

    private fun renderItems() {

        itemContainer.children.clear()

        items.forEach {

            itemContainer.children.add(

                createItemCard(it)

            )

        }

        itemContainer.children.add(

            createAddCard("Create Item")

        )
    }

        private fun createMachineCard(name: String): VBox {

        val title = Label(name)

        title.style =
            "-fx-text-fill:white;" +
            "-fx-font-size:20;" +
            "-fx-font-weight:bold;"

        val testButton = Button("Test")

        testButton.setOnAction {
            openTestPage()
        }

        val maintenanceButton = Button("Maintenance")

        maintenanceButton.setOnAction {
            openMaintenancePage()
        }

        val buttons = VBox(8.0)

        buttons.children.addAll(
            testButton,
            maintenanceButton
        )

        val card = VBox(10.0)

        card.children.addAll(
            title,
            buttons
        )

        card.prefWidth = 220.0

        card.style =
            "-fx-background-color:#1A1A1A;" +
            "-fx-background-radius:15;" +
            "-fx-padding:20;"

        return card
    }

    private fun createItemCard(item: Item): VBox {

        val image = ImageView()

        try {

            image.image = Image(
                File(item.iconPath)
                    .toURI()
                    .toString()
            )

        } catch (_: Exception) {

        }

        image.fitWidth = 120.0
        image.fitHeight = 120.0
        image.isPreserveRatio = true

        val title = Label(item.name)

        title.style =
            "-fx-text-fill:white;" +
            "-fx-font-size:20;" +
            "-fx-font-weight:bold;"

        val calories =
            Label("${item.calories} kcal")

        calories.style =
            "-fx-text-fill:white;"

        val removeButton =
            Button("Remove")

        removeButton.setOnAction {

            confirmRemoveItem(item)

        }

        val card = VBox(10.0)

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

    private fun confirmRemoveItem(item: Item) {

        val alert =
            Alert(Alert.AlertType.CONFIRMATION)

        alert.title = "Remove Item"

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

            items.remove(item)

            saveItems()

            renderItems()

        }
    }

    private fun createAddCard(labelText: String): VBox {

        val plusButton = Button("+")

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

        val label = Label(labelText)

        label.style =
            "-fx-text-fill:white;"

        val card = VBox(15.0)

        card.children.addAll(
            plusButton,
            label
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

            val root: Parent =
                FXMLLoader.load(resource)

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