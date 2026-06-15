package ui

import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import model.Item

class MainController {

    @FXML
    private lateinit var machineContainer: HBox

    @FXML
    private lateinit var itemContainer: HBox

    @FXML
    fun initialize() {

        val machines = listOf(
            "Machine 1",
            "Machine 2",
            "Machine 3"
        )

        val items = listOf(
            Item("Soda", 150, "assets/soda.png"),
            Item("Chips", 250, "assets/chips.png"),
            Item("Candy", 200, "assets/candy.png")
        )

        for (machine in machines) {
            val card = createMachineCard(machine)
            machineContainer.children.add(card)
        }

        machineContainer.children.add(createAddCard("Create Machine"))

        for (item in items) {
            val card = createItemCard(item)
            itemContainer.children.add(card)
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
        val iconLabel = Label("Icon: ${item.iconPath}")

        title.style =
            "-fx-text-fill: white;" +
            "-fx-font-size: 20px;" +
            "-fx-font-weight: bold;"

        val button = Button("Open")

        val card = VBox(10.0)
        card.children.addAll(title, calories, iconLabel, button)
        card.prefWidth = 220.0
        card.style =
            "-fx-background-color: #2b2b2b;" +
            "-fx-background-radius: 15;" +
            "-fx-padding: 20;"

        return card
    }

    private fun createAddCard(labelText: String): VBox {
        val plusButton = Button("+")

        plusButton.prefWidth = 80.0
        plusButton.prefHeight = 80.0

        val label = Label(labelText)
        label.style = "-fx-text-fill: white;"

        val card = VBox(15.0)
        card.children.addAll(plusButton, label)
        card.prefWidth = 220.0
        card.style =
            "-fx-background-color: #343434;" +
            "-fx-background-radius: 15;" +
            "-fx-padding: 20;"

        return card
    }
}
