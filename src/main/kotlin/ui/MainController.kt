package ui

import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.scene.control.ButtonType
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.Stage
import model.Item

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

        items.clear()
        items.addAll(
            listOf(
                Item("Soda", 150, "assets/soda.png"),
                Item("Chips", 250, "assets/chips.png"),
                Item("Candy", 200, "assets/candy.png")
            )
        )

        machines.forEach {
            machineContainer.children.add(createMachineCard(it))
        }

        machineContainer.children.add(createAddCard("Create Machine"))

        renderItems()
    }

    private fun renderItems() {
        itemContainer.children.clear()
        items.forEach {
            itemContainer.children.add(createItemCard(it))
        }
        itemContainer.children.add(createAddCard("Create Item"))
    }

    private fun createMachineCard(name: String): VBox {

        val title = Label(name)

        title.style =
            "-fx-text-fill: white;" +
            "-fx-font-size: 20px;" +
            "-fx-font-weight: bold;"

        val button = Button("Open")

        val card = VBox(10.0)

        card.children.addAll(title, button)

        card.prefWidth = 220.0

        card.style =
            "-fx-background-color: #2b2b2b;" +
            "-fx-background-radius: 15;" +
            "-fx-padding: 20;"

        return card
    }

    private fun createItemCard(item: Item): VBox {

        val title = Label(item.name)
        val calories = Label("${item.calories} kcal")
        val path = Label(item.iconPath)

        title.style =
            "-fx-text-fill: white;" +
            "-fx-font-size: 20px;" +
            "-fx-font-weight: bold;"

        calories.style = "-fx-text-fill: white;"
        path.style = "-fx-text-fill: lightgray;"

        val button = Button("Remove")
        button.setOnAction {
            confirmRemoveItem(item)
        }

        val card = VBox(10.0)

        card.children.addAll(
            title,
            calories,
            path,
            button
        )

        card.prefWidth = 220.0

        card.style =
            "-fx-background-color: #2b2b2b;" +
            "-fx-background-radius: 15;" +
            "-fx-padding: 20;"

        return card
    }

    private fun confirmRemoveItem(item: Item) {
        val alert = Alert(Alert.AlertType.CONFIRMATION)
        alert.title = "Remove Item"
        alert.headerText = "Delete ${item.name}?"
        alert.contentText = "This item will be removed from the main page."

        val result = alert.showAndWait()
        if (result.isPresent && result.get() == ButtonType.OK) {
            items.remove(item)
            renderItems()
        }
    }

    private fun createAddCard(labelText: String): VBox {

        val plusButton = Button("+")

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

        val label = Label(labelText)

        label.style = "-fx-text-fill: white;"

        val card = VBox(15.0)

        card.children.addAll(
            plusButton,
            label
        )

        card.prefWidth = 220.0

        card.style =
            "-fx-background-color: #343434;" +
            "-fx-background-radius: 15;" +
            "-fx-padding: 20;"

        return card
    }

    private fun openCreateMachinePage() {
        changeScene("/fxml/create-machine.fxml")
    }

    private fun openCreateItemPage() {
        changeScene("/fxml/create-item.fxml")
    }

    private fun changeScene(fxmlPath: String) {

        val resource = javaClass.getResource(fxmlPath)
            ?: error("Cannot find FXML: $fxmlPath")

        val root: Parent = FXMLLoader.load(resource)

        val stage = machineContainer.scene.window as Stage

        stage.scene = Scene(root)
    }
}