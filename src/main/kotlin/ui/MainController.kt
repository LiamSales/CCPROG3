package ui

import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox

class MainController {

    /*
        Injected from FXML.

        This variable becomes a reference
        to the HBox in the XML.
    */
    @FXML
    private lateinit var machineContainer: HBox


    /*
        Called automatically after FXML loads.
    */
    @FXML
    fun initialize() {

        /*
            Example backend data.

            Later this becomes:
            your real machines list.
        */
        val machines = listOf(
            "Machine 1",
            "Machine 2",
            "Machine 3"
        )


        /*
            Loop through backend data
            and generate UI cards.
        */
        for (machine in machines) {

            val card = createMachineCard(machine)

            /*
                Add generated card
                into horizontal container.
            */
            machineContainer.children.add(card)
        }


        /*
            Add "+" create button LAST.
        */
        machineContainer.children.add(createAddCard())
    }


    /*
        Creates one machine card dynamically.
    */
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


    /*
        Creates "+" button card.
    */
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