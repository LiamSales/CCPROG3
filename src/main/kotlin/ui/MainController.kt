package ui

import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox

class MainController {

    @FXML
    private lateinit var machineContainer: HBox

    @FXML
    fun initialize() {

        val machines = listOf(
            "Machine 1",
            "Machine 2",
            "Machine 3"
        ) //backend data which well update
        //would be simply a list of strings?

        val items = listOf(
            "Item 1",
            "Item 2",
            "Item 3",

        ) //strings, and png

        /*
            Loop through backend data
            and generate UI cards.
        */

        //this is all handled in the xml file, 
        for (machine in machines) {

            val card = createMachineCard(machine)
            machineContainer.children.add(card)
        }

        machineContainer.children.add(createAddCard())
    }

            for (item in items) {

            val card = createItemCard(item)
            itemContainer.children.add(card)
        }

        itemContainer.children.add(createAddCard())
    }


    private fun createMachineCard(name: String): VBox {

        val title = Label(name)

        title.style =
            "-fx-text-fill: white;" +
            "-fx-font-size: 20px;" +
            "-fx-font-weight: bold;"


        val button = Button("Open")


        val card = VBox(10.0)

        card.children.addAll(
            title,
            button
        )

        card.prefWidth = 220.0

        card.style =
            "-fx-background-color: #2b2b2b;" +
            "-fx-background-radius: 15;" +
            "-fx-padding: 20;"


        return card
    }

    private fun createItemCard(name: String, icon: String): VBox {

        val title = Label(name)

        //then we add the picture as well

        title.style =
            "-fx-text-fill: white;" +
            "-fx-font-size: 20px;" +
            "-fx-font-weight: bold;"


        val button = Button("Open")


        val card = VBox(10.0)

        card.children.addAll(
            title,
            button
        )

        card.prefWidth = 220.0

        card.style =
            "-fx-background-color: #2b2b2b;" +
            "-fx-background-radius: 15;" +
            "-fx-padding: 20;"


        return card
    }


    private fun createAddCard(): VBox {

        val plusButton = Button("+")

        plusButton.prefWidth = 80.0
        plusButton.prefHeight = 80.0


        val label = Label("Create Machine")

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
}