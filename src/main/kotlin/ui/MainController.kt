package ui

import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.Modality
import javafx.stage.Stage

class MainController {

    /*
        Injected from FXML.

        This variable becomes a reference
        to the HBox in the XML.
    */
    @FXML
    private lateinit var machineContainer: HBox
    @FXML
    private lateinit var itemContainer: HBox


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

        // --- items section: example data and dynamic add card
        val items = listOf(
            Triple("https://via.placeholder.com/150", "Soda", "120"),
            Triple("https://via.placeholder.com/150", "Chips", "250")
        )

        for (item in items) {
            itemContainer.children.add(createItemCard(item.first, item.second, item.third))
        }

        itemContainer.children.add(createAddItemCard())
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
        Creates one item card dynamically.
        imageUrl, name, calories
    */
    private fun createItemCard(imageUrl: String, name: String, calories: String): VBox {

        val title = Label(name)

        title.style =
            "-fx-text-fill: white;" +
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;"

        val subtitle = Label("${calories} cal")
        subtitle.style = "-fx-text-fill: lightgray;"

        val button = Button("Open")

        val card = VBox(8.0)

        card.children.addAll(
            title,
            subtitle,
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

        // open the Create Machine page when clicked
        plusButton.setOnAction {
            try {
                val loader = FXMLLoader(javaClass.getResource("/fxml/create-machine.fxml"))
                val root = loader.load<Parent>()

                val stage = Stage()
                stage.title = "Create Vending Machine"
                stage.initModality(Modality.APPLICATION_MODAL)
                stage.scene = Scene(root)
                stage.showAndWait()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }


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


    private fun createAddItemCard(): VBox {

        val plusButton = Button("+")

        plusButton.prefWidth = 80.0
        plusButton.prefHeight = 80.0

        // open Create Item modal
        plusButton.setOnAction {
            try {
                val loader = FXMLLoader(javaClass.getResource("/fxml/create-item.fxml"))
                val root = loader.load<Parent>()

                val stage = Stage()
                stage.title = "Create Item"
                stage.initModality(Modality.APPLICATION_MODAL)
                stage.scene = Scene(root)
                stage.showAndWait()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        val label = Label("Create Item")
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