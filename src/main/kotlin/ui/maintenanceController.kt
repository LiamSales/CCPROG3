package ui

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.scene.control.ButtonType
import javafx.scene.control.Label
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.Stage
import java.io.File
import model.VendingMachine
import javafx.scene.control.ScrollPane
import model.SpecialMachine
import javafx.geometry.Insets
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.stage.Modality
import model.Item

class MaintenanceController {

    @FXML
    private lateinit var slotGrid: GridPane

    private lateinit var machineFolder: File

    private lateinit var machine: VendingMachine

    @FXML
    private lateinit var addOnGrid: GridPane

    @FXML
    private lateinit var addOnScroll: ScrollPane

    @FXML
    private lateinit var addOnLabel: Label

    @FXML
    fun initialize() {

    machineFolder =
        SelectedMachine.folder
            ?: error("No machine selected.")

    machine =
        SelectedMachine.machine
            ?: error("No machine loaded.")

    val special =
    machine as? SpecialMachine

    if (special == null) {

        addOnGrid.isVisible = false
        addOnGrid.isManaged = false

        addOnScroll.isVisible = false
        addOnScroll.isManaged = false

        addOnLabel.isVisible = false
        addOnLabel.isManaged = false

    } else {

    for (i in 1..special.getAddOnSlotCount()) {

    val card = VBox(10.0)

    card.alignment = Pos.CENTER
    card.prefWidth = 220.0
    card.prefHeight = 220.0

    card.style =
        "-fx-background-color: white;" +
        "-fx-background-radius: 12;" +
        "-fx-border-color: lightgray;" +
        "-fx-border-radius: 12;" +
        "-fx-padding: 15;"

    val slotLabel = Label("Add-On $i")

    val image =
    ImageView()

    image.fitWidth = 90.0
    image.fitHeight = 90.0
    image.isPreserveRatio = true

    slotLabel.style =
        "-fx-font-size:18px;" +
        "-fx-font-weight:bold;"

    val itemLabel = Label("Item: Empty")
    val qtyLabel = Label("Quantity: 0")
    val priceLabel = Label("Price: ₱0")

    val setButton = Button("Set")
    val clearButton = Button("Clear")
    val restockButton = Button("Restock")
    val priceButton = Button("Price")

setButton.setOnAction {

showItemPicker { item ->

    slotLabel.text =
        item.name

    itemLabel.text =
        "Calories: ${item.calories}"

    qtyLabel.text =
        "Quantity: 0"

    priceLabel.text =
        "Price: ₱0"

    image.image =
        Image(
            File(item.iconPath)
                .toURI()
                .toString()
        )

}

}

clearButton.setOnAction {

    slotLabel.text =
        "Slot $i"

    itemLabel.text =
        "Item: Empty"

    qtyLabel.text =
        "Quantity: 0"

    priceLabel.text =
        "Price: ₱0"

    image.image = null

        popup("Clear Add-on", "Clear Add-on $i")

}

restockButton.setOnAction {

    showRestockDialog(
        itemLabel,
        qtyLabel
    )

}

priceButton.setOnAction {

    showPriceDialog(priceLabel)

}
    val row1 = HBox(10.0)
    row1.alignment = Pos.CENTER
    row1.children.addAll(setButton, clearButton)

    val row2 = HBox(10.0)
    row2.alignment = Pos.CENTER
    row2.children.addAll(restockButton, priceButton)

card.children.addAll(

    slotLabel,
    image,
    itemLabel,
    qtyLabel,
    priceLabel,
    row1,
    row2

)

    addOnGrid.add(
        card,
        (i - 1) % 4,
        (i - 1) / 4
    )
}
   
}

    val slotCount =
        machine.slotLimit

    for (i in 1..slotCount) {

            val card = VBox(10.0)

            card.alignment = Pos.CENTER
            card.prefWidth = 220.0
            card.prefHeight = 220.0

            card.style =
                "-fx-background-color: white;" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: lightgray;" +
                "-fx-border-radius: 12;" +
                "-fx-padding: 15;"

            val slotLabel = Label("Slot $i")

            val image = ImageView()

image.fitWidth = 90.0
image.fitHeight = 90.0
image.isPreserveRatio = true

            slotLabel.style =
                "-fx-font-size:18px;" +
                "-fx-font-weight:bold;"

            val itemLabel = Label("Item: Empty")
            val qtyLabel = Label("Quantity: 0")
            val priceLabel = Label("Price: ₱0")

            val setButton = Button("Set")
            val clearButton = Button("Clear")
            val restockButton = Button("Restock")
            val priceButton = Button("Price")

setButton.setOnAction {

showItemPicker { item ->

    slotLabel.text =
        item.name

    itemLabel.text =
        "Calories: ${item.calories}"

    qtyLabel.text =
        "Quantity: 0"

    priceLabel.text =
        "Price: ₱0"

    image.image =
        Image(
            File(item.iconPath)
                .toURI()
                .toString()
        )

}

}

clearButton.setOnAction {

    slotLabel.text =
        "Slot $i"

    itemLabel.text =
        "Item: Empty"

    qtyLabel.text =
        "Quantity: 0"

    priceLabel.text =
        "Price: ₱0"

    image.image = null

    popup("Clear Slot", "Clear Slot $i")

} 

restockButton.setOnAction {

    showRestockDialog(
        itemLabel,
        qtyLabel
    )

}

priceButton.setOnAction {

    showPriceDialog(priceLabel)

}

            val row1 = HBox(10.0)
            row1.alignment = Pos.CENTER
            row1.children.addAll(setButton, clearButton)

            val row2 = HBox(10.0)
            row2.alignment = Pos.CENTER
            row2.children.addAll(restockButton, priceButton)

card.children.addAll(

    slotLabel,
    image,
    itemLabel,
    qtyLabel,
    priceLabel,
    row1,
    row2

)

            slotGrid.add(
                card,
                (i - 1) % 4,
                (i - 1) / 4
            )
        }
    }

    @FXML
    fun replenishCash() {
        popup("Replenish Cash", "Cash replenished.")
    }

    @FXML
    fun collectBalance() {
        popup("Collect Balance", "Balance collected.")
    }

    @FXML
    fun displaySummary() {
        popup("Summary", "Summary page coming soon.")
    }

@FXML
fun removeMachine(event: ActionEvent) {

    val alert =
        Alert(Alert.AlertType.CONFIRMATION)

    alert.title =
        "Remove Machine"

    alert.headerText =
        "Remove Machine?"

    alert.contentText =
        "This action cannot be undone."

    val result =
        alert.showAndWait()

    if (

        result.isPresent &&
        result.get() == ButtonType.OK

    ) {

        deleteFolder(machineFolder)

        backToMainPage(event)

    }

}

private fun deleteFolder(
    folder: File
) {

    folder.listFiles()?.forEach {

        if (it.isDirectory) {

            deleteFolder(it)

        }

        else {

            it.delete()

        }

    }

    folder.delete()

}

    @FXML
    fun backToMainPage(event: ActionEvent) {

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

    private fun popup(title: String, message: String) {

        val alert = Alert(Alert.AlertType.INFORMATION)

        alert.title = title
        alert.headerText = null
        alert.contentText = message

        alert.showAndWait()
    }

    private fun showItemPicker(
    onSelected: (Item) -> Unit
) {

    val stage = Stage()

    stage.title = "Select Item"

    stage.initOwner(
        slotGrid.scene.window
    )

    stage.initModality(
        Modality.APPLICATION_MODAL
    )

    val list =
        VBox(10.0)

    list.padding =
        Insets(15.0)

    ItemManager.items.forEach { item ->

        val row =
            HBox(15.0)

        row.alignment =
            Pos.CENTER_LEFT

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
        }

        image.fitWidth = 50.0
        image.fitHeight = 50.0
        image.isPreserveRatio = true

        val info =
            VBox(5.0)

        val name =
            Label(item.name)

        name.style =
            "-fx-font-size:16;" +
            "-fx-font-weight:bold;"

        val calories =
            Label(
                "${item.calories} kcal"
            )

        info.children.addAll(
            name,
            calories
        )

        val selectButton =
            Button("Select")

        selectButton.setOnAction {

            onSelected(item)

            stage.close()

        }

        row.children.addAll(

            image,
            info,
            selectButton

        )

        list.children.add(row)

    }

    val scroll =
        ScrollPane(list)

    scroll.isFitToWidth = true

    val root =
        VBox(scroll)

    val scene =
        Scene(root, 450.0, 500.0)

    stage.scene = scene

    stage.showAndWait()

}

private fun showPriceDialog(
    priceLabel: Label
) {

    val stage = Stage()

    stage.title = "Set Price"

    val textField =
        javafx.scene.control.TextField()

    textField.promptText = "Enter price"

    val setButton =
        Button("Set")

    val cancelButton =
        Button("Cancel")

    val buttonRow =
        HBox(10.0)

    buttonRow.alignment = Pos.CENTER
    buttonRow.children.addAll(
        setButton,
        cancelButton
    )

    val root =
        VBox(15.0)

    root.alignment = Pos.CENTER
    root.style =
        "-fx-padding:20;" +
        "-fx-background-color:white;"

    root.children.addAll(

        Label("Enter Item Price"),

        textField,

        buttonRow

    )

    setButton.setOnAction {

        val value =
            textField.text.toFloatOrNull()

        if (value == null || value < 0f) {

            Alert(Alert.AlertType.ERROR).apply {

                title = "Invalid Price"
                headerText = null
                contentText = "Please enter a valid price."

            }.showAndWait()

            return@setOnAction

        }

        priceLabel.text =
            "Price: ₱%.2f".format(value)

        stage.close()

    }

    cancelButton.setOnAction {

        stage.close()

    }

    stage.scene =
        Scene(root, 300.0, 170.0)

    stage.showAndWait()

}

private fun showRestockDialog(
    itemLabel: Label,
    qtyLabel: Label
) {

    if (itemLabel.text == "Item: Empty") {

        Alert(Alert.AlertType.WARNING).apply {

            title = "No Item"
            headerText = null
            contentText =
                "Please assign an item before restocking."

        }.showAndWait()

        return

    }

    val stage = Stage()

    stage.title = "Restock"

    val textField =
        javafx.scene.control.TextField()

    textField.promptText =
        "Quantity"

    val setButton =
        Button("Set")

    val cancelButton =
        Button("Cancel")

    val row =
        HBox(10.0)

    row.alignment = Pos.CENTER
    row.children.addAll(
        setButton,
        cancelButton
    )

    val root =
        VBox(15.0)

    root.alignment = Pos.CENTER

    root.style =
        "-fx-padding:20;" +
        "-fx-background-color:white;"

    root.children.addAll(

        Label(
            "Enter quantity (0-${machine.itemLimit})"
        ),

        textField,

        row

    )

    setButton.setOnAction {

        val quantity =
            textField.text.toIntOrNull()

        if (

            quantity == null ||
            quantity < 0 ||
            quantity > machine.itemLimit

        ) {

            Alert(Alert.AlertType.ERROR).apply {

                title = "Invalid Quantity"
                headerText = null
                contentText =
                    "Quantity must be between 0 and ${machine.itemLimit}."

            }.showAndWait()

            return@setOnAction

        }

        qtyLabel.text =
            "Quantity: $quantity"

        stage.close()

    }

    cancelButton.setOnAction {

        stage.close()

    }

    stage.scene =
        Scene(root, 320.0, 180.0)

    stage.showAndWait()

}


}