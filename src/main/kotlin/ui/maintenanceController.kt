package ui

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.scene.control.ButtonType
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.control.Spinner
import javafx.scene.control.SpinnerValueFactory
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.Modality
import javafx.stage.Stage
import model.Item
import model.SpecialMachine
import model.VendingMachine
import java.io.File

class MaintenanceController {

    @FXML
    private lateinit var slotGrid: GridPane

    @FXML
    private lateinit var addOnGrid: GridPane

    @FXML
    private lateinit var addOnScroll: ScrollPane

    @FXML
    private lateinit var addOnLabel: Label

    @FXML
    private lateinit var cashLabel: Label

    private lateinit var machineFolder: File
    private lateinit var machine: VendingMachine

    @FXML
    fun initialize() {
        machineFolder = SelectedMachine.folder
            ?: error("No machine selected.")

        machine = SelectedMachine.machine
            ?: error("No machine loaded.")

        updateCashLabel()
        renderMachine()
    }

    private fun renderMachine() {
        slotGrid.children.clear()
        addOnGrid.children.clear()

        val special = machine as? SpecialMachine

        if (special == null) {
            addOnGrid.isVisible = false
            addOnGrid.isManaged = false
            addOnScroll.isVisible = false
            addOnScroll.isManaged = false
            addOnLabel.isVisible = false
            addOnLabel.isManaged = false
        } else {
            addOnGrid.isVisible = true
            addOnGrid.isManaged = true
            addOnScroll.isVisible = true
            addOnScroll.isManaged = true
            addOnLabel.isVisible = true
            addOnLabel.isManaged = true

            for (i in 1..special.getAddOnSlotCount()) {
                renderAddOnCard(i)
            }
        }

        for (i in machine.slots.indices) {
            renderSlotCard(i)
        }
    }

    private fun renderSlotCard(index: Int) {
        val card = VBox(10.0)
        card.alignment = Pos.CENTER
        card.prefWidth = 220.0
        card.prefHeight = 250.0
        card.style =
            "-fx-background-color:white;" +
            "-fx-background-radius:12;" +
            "-fx-border-color:lightgray;" +
            "-fx-border-radius:12;" +
            "-fx-padding:15;"

        val slotLabel = Label()
        slotLabel.style =
            "-fx-font-size:18px;" +
            "-fx-font-weight:bold;"

        val image = ImageView()
        image.fitWidth = 90.0
        image.fitHeight = 90.0
        image.isPreserveRatio = true

        val itemLabel = Label()
        val qtyLabel = Label()
        val priceLabel = Label()

        val setButton = Button("Set")
        val clearButton = Button("Clear")
        val restockButton = Button("Restock")
        val priceButton = Button("Price")

        setButton.setOnAction {
            showItemPicker { item ->
                machine.setSlot(index, item, 0f)
                refreshSlotCard(
                    index,
                    slotLabel,
                    itemLabel,
                    qtyLabel,
                    priceLabel,
                    image
                )
            }
        }

        clearButton.setOnAction {
            machine.clearSlot(index)

            refreshSlotCard(
                index,
                slotLabel,
                itemLabel,
                qtyLabel,
                priceLabel,
                image
            )
        }

        restockButton.setOnAction {
            showRestockDialog(
                index,
                qtyLabel
            )
        }

        priceButton.setOnAction {
            showPriceDialog(
                index,
                priceLabel
            )
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
            index % 4,
            index / 4
        )

        refreshSlotCard(
            index,
            slotLabel,
            itemLabel,
            qtyLabel,
            priceLabel,
            image
        )
    }

    private fun refreshSlotCard(
        index: Int,
        slotLabel: Label,
        itemLabel: Label,
        qtyLabel: Label,
        priceLabel: Label,
        image: ImageView
    ) {
        val slot = machine.slots[index]
        val item = slot.item

        if (item == null) {
            slotLabel.text = "Slot ${index + 1}"
            itemLabel.text = "Item: Empty"
            qtyLabel.text = "Quantity: 0"
            priceLabel.text = "Price: ₱0"
            image.image = null
            return
        }

        slotLabel.text = item.name
        itemLabel.text = "Calories: ${item.calories}"
        qtyLabel.text = "Quantity: ${slot.quantity}"
        priceLabel.text = "Price: ₱%.2f".format(slot.price)

        image.image = try {
            Image(
                File(item.iconPath)
                    .toURI()
                    .toString()
            )
        } catch (_: Exception) {
            null
        }
    }

    private fun renderAddOnCard(index: Int) {
        val card = VBox(10.0)
        card.alignment = Pos.CENTER
        card.prefWidth = 220.0
        card.prefHeight = 250.0
        card.style =
            "-fx-background-color:white;" +
            "-fx-background-radius:12;" +
            "-fx-border-color:lightgray;" +
            "-fx-border-radius:12;" +
            "-fx-padding:15;"

        val slotLabel = Label("Add-On $index")
        slotLabel.style =
            "-fx-font-size:18px;" +
            "-fx-font-weight:bold;"

        val image = ImageView()
        image.fitWidth = 90.0
        image.fitHeight = 90.0
        image.isPreserveRatio = true

        val itemLabel = Label("Item: Empty")
        val qtyLabel = Label("Quantity: 0")
        val priceLabel = Label("Price: ₱0")

        val setButton = Button("Set")
        val clearButton = Button("Clear")
        val restockButton = Button("Restock")
        val priceButton = Button("Price")

        /*
         * The current SpecialMachine class/API supplied to this controller
         * does not expose add-on Slot objects. Therefore these controls
         * update the visible card only. Regular machine slots use the real
         * VendingMachine slot API above.
         */
        setButton.setOnAction {
            showItemPicker { item ->
                slotLabel.text = item.name
                itemLabel.text = "Calories: ${item.calories}"
                qtyLabel.text = "Quantity: 0"
                priceLabel.text = "Price: ₱0"
                image.image = try {
                    Image(
                        File(item.iconPath)
                            .toURI()
                            .toString()
                    )
                } catch (_: Exception) {
                    null
                }
            }
        }

        clearButton.setOnAction {
            slotLabel.text = "Add-On $index"
            itemLabel.text = "Item: Empty"
            qtyLabel.text = "Quantity: 0"
            priceLabel.text = "Price: ₱0"
            image.image = null
        }

        restockButton.setOnAction {
            if (itemLabel.text == "Item: Empty") {
                showWarning(
                    "No Item",
                    "Please assign an item before restocking."
                )
                return@setOnAction
            }

            showSimpleQuantityDialog(qtyLabel)
        }

        priceButton.setOnAction {
            showSimplePriceDialog(priceLabel)
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
            (index - 1) % 4,
            (index - 1) / 4
        )
    }

    private fun showItemPicker(
        onSelected: (Item) -> Unit
    ) {
        val stage = Stage()

        stage.title = "Select Item"
        stage.initOwner(slotGrid.scene.window)
        stage.initModality(Modality.APPLICATION_MODAL)

        val list = VBox(10.0)
        list.padding = Insets(15.0)

        if (ItemManager.items.isEmpty()) {
            list.children.add(
                Label("No items available.")
            )
        }

        ItemManager.items.forEach { item ->
            val row = HBox(15.0)
            row.alignment = Pos.CENTER_LEFT

            val image = ImageView()

            image.image = try {
                Image(
                    File(item.iconPath)
                        .toURI()
                        .toString()
                )
            } catch (_: Exception) {
                null
            }

            image.fitWidth = 50.0
            image.fitHeight = 50.0
            image.isPreserveRatio = true

            val info = VBox(5.0)

            val name = Label(item.name)
            name.style =
                "-fx-font-size:16;" +
                "-fx-font-weight:bold;"

            val calories =
                Label("${item.calories} kcal")

            info.children.addAll(
                name,
                calories
            )

            val selectButton = Button("Select")

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

        val scroll = ScrollPane(list)
        scroll.isFitToWidth = true
        scroll.prefViewportHeight = 400.0

        val closeButton = Button("X")
        closeButton.setOnAction {
            stage.close()
        }

        val top = HBox(closeButton)
        top.alignment = Pos.TOP_RIGHT
        top.padding = Insets(10.0)

        val root = VBox(
            top,
            scroll
        )

        root.style =
            "-fx-background-color:white;" +
            "-fx-padding:10;"

        stage.scene =
            Scene(root, 450.0, 500.0)

        stage.showAndWait()
    }

    private fun showPriceDialog(
        index: Int,
        priceLabel: Label
    ) {
        val stage = Stage()
        stage.title = "Set Price"
        stage.initOwner(slotGrid.scene.window)
        stage.initModality(Modality.APPLICATION_MODAL)

        val textField = TextField()
        textField.promptText = "Enter price"
        textField.text =
            if (machine.slots[index].item != null)
                machine.slots[index].price.toString()
            else
                ""

        val setButton = Button("Set")
        val cancelButton = Button("Cancel")

        val buttons = HBox(10.0)
        buttons.alignment = Pos.CENTER
        buttons.children.addAll(
            setButton,
            cancelButton
        )

        val root = VBox(15.0)
        root.alignment = Pos.CENTER
        root.padding = Insets(20.0)
        root.style = "-fx-background-color:white;"

        root.children.addAll(
            Label("Enter Item Price"),
            textField,
            buttons
        )

        setButton.setOnAction {
            if (machine.slots[index].item == null) {
                showWarning(
                    "No Item",
                    "Please assign an item before setting its price."
                )
                return@setOnAction
            }

            val value =
                textField.text.toFloatOrNull()

            if (value == null || value < 0f) {
                showWarning(
                    "Invalid Price",
                    "Please enter a valid price."
                )
                return@setOnAction
            }

            machine.changePrice(index, value)

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
        index: Int,
        qtyLabel: Label
    ) {
        if (machine.slots[index].item == null) {
            showWarning(
                "No Item",
                "Please assign an item before restocking."
            )
            return
        }

        val current =
            machine.slots[index].quantity

        val stage = Stage()

        stage.title = "Restock"
        stage.initOwner(slotGrid.scene.window)
        stage.initModality(Modality.APPLICATION_MODAL)

        val spinner = Spinner<Int>()

        spinner.valueFactory =
            SpinnerValueFactory.IntegerSpinnerValueFactory(
                0,
                machine.itemLimit,
                current
            )

        spinner.isEditable = true
        spinner.prefWidth = 120.0

        val setButton = Button("Set")
        val cancelButton = Button("Cancel")

        val buttons = HBox(10.0)
        buttons.alignment = Pos.CENTER
        buttons.children.addAll(
            setButton,
            cancelButton
        )

        val root = VBox(15.0)
        root.alignment = Pos.CENTER
        root.padding = Insets(20.0)
        root.style = "-fx-background-color:white;"

        root.children.addAll(
            Label(
                "Select Quantity (Max ${machine.itemLimit})"
            ),
            spinner,
            buttons
        )

        setButton.setOnAction {
            val quantity = spinner.value

            if (quantity < 0 ||
                quantity > machine.itemLimit
            ) {
                showWarning(
                    "Invalid Quantity",
                    "Quantity must be between 0 and ${machine.itemLimit}."
                )
                return@setOnAction
            }

            machine.restockSlot(
                index,
                quantity
            )

            qtyLabel.text =
                "Quantity: $quantity"

            stage.close()
        }

        cancelButton.setOnAction {
            stage.close()
        }

        stage.scene =
            Scene(root, 330.0, 190.0)

        stage.showAndWait()
    }

    private fun showSimpleQuantityDialog(
        qtyLabel: Label
    ) {
        val stage = Stage()

        stage.title = "Restock"
        stage.initOwner(slotGrid.scene.window)
        stage.initModality(Modality.APPLICATION_MODAL)

        val spinner = Spinner<Int>()

        spinner.valueFactory =
            SpinnerValueFactory.IntegerSpinnerValueFactory(
                0,
                Int.MAX_VALUE,
                0
            )

        val setButton = Button("Set")
        val cancelButton = Button("Cancel")

        val buttons = HBox(10.0)
        buttons.alignment = Pos.CENTER
        buttons.children.addAll(
            setButton,
            cancelButton
        )

        val root = VBox(15.0)
        root.alignment = Pos.CENTER
        root.padding = Insets(20.0)

        root.children.addAll(
            Label("Select Quantity"),
            spinner,
            buttons
        )

        setButton.setOnAction {
            qtyLabel.text =
                "Quantity: ${spinner.value}"
            stage.close()
        }

        cancelButton.setOnAction {
            stage.close()
        }

        stage.scene =
            Scene(root, 300.0, 180.0)

        stage.showAndWait()
    }

    private fun showSimplePriceDialog(
        priceLabel: Label
    ) {
        val stage = Stage()

        stage.title = "Set Price"
        stage.initOwner(slotGrid.scene.window)
        stage.initModality(Modality.APPLICATION_MODAL)

        val textField = TextField()
        textField.promptText = "Enter price"

        val setButton = Button("Set")
        val cancelButton = Button("Cancel")

        val buttons = HBox(10.0)
        buttons.alignment = Pos.CENTER
        buttons.children.addAll(
            setButton,
            cancelButton
        )

        val root = VBox(15.0)
        root.alignment = Pos.CENTER
        root.padding = Insets(20.0)

        root.children.addAll(
            Label("Enter Price"),
            textField,
            buttons
        )

        setButton.setOnAction {
            val value =
                textField.text.toFloatOrNull()

            if (value == null || value < 0f) {
                showWarning(
                    "Invalid Price",
                    "Please enter a valid price."
                )
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
            Scene(root, 300.0, 200.0)

        stage.showAndWait()
    }

    @FXML
    fun replenishCash() {
        showReplenishCashDialog()
    }

    private fun showReplenishCashDialog() {
        val stage = Stage()

        stage.title = "Replenish Cash"
        stage.initOwner(slotGrid.scene.window)
        stage.initModality(Modality.APPLICATION_MODAL)

        val denominations =
            listOf(
                1000f,
                500f,
                200f,
                100f,
                50f,
                20f,
                10f,
                5f,
                1f
            )

        val grid = GridPane()
        grid.hgap = 10.0
        grid.vgap = 10.0

        val spinners =
            mutableMapOf<Float, Spinner<Int>>()

        denominations.forEachIndexed { row, value ->
            val label =
                Label("₱${value.toInt()}")

            val spinner = Spinner<Int>()

            spinner.valueFactory =
                SpinnerValueFactory.IntegerSpinnerValueFactory(
                    0,
                    Int.MAX_VALUE,
                    machine.register.getQuantity(value)
                )

            spinner.isEditable = true
            spinner.prefWidth = 120.0

            spinners[value] = spinner

            grid.add(label, 0, row)
            grid.add(spinner, 1, row)
        }

        val save = Button("Save")
        val cancel = Button("Cancel")

        val buttons = HBox(10.0)
        buttons.alignment = Pos.CENTER
        buttons.children.addAll(
            save,
            cancel
        )

        val root = VBox(15.0)
        root.alignment = Pos.CENTER
        root.padding = Insets(20.0)
        root.style = "-fx-background-color:white;"

        root.children.addAll(
            grid,
            buttons
        )

        save.setOnAction {
            machine.register.clear()

            spinners.forEach { (denomination, spinner) ->
                if (spinner.value > 0) {
                    machine.register.addCash(
                        denomination,
                        spinner.value
                    )
                }
            }

            updateCashLabel()
            stage.close()
        }

        cancel.setOnAction {
            stage.close()
        }

        stage.scene =
            Scene(root, 300.0, 420.0)

        stage.showAndWait()
    }

    @FXML
    fun collectBalance() {
        val amount =
            machine.register.getTotalCash()

        val alert =
            Alert(Alert.AlertType.CONFIRMATION)

        alert.title = "Collect Balance"
        alert.headerText =
            "Collect ₱%.2f?".format(amount)
        alert.contentText =
            "This will empty the machine's cash register."

        val result = alert.showAndWait()

        if (
            result.isPresent &&
            result.get() == ButtonType.OK
        ) {
            machine.register.clear()
            updateCashLabel()

            popup(
                "Balance Collected",
                "Collected ₱%.2f".format(amount)
            )
        }
    }

    @FXML
    fun saveInventory() {
        MachineManager.saveMachine(
            machineFolder,
            machine
        )

        popup(
            "Saved",
            "Machine inventory and register saved successfully."
        )
    }

    @FXML
    fun displaySummary() {
        popup(
            "Summary",
            "Summary page coming soon."
        )
    }

    @FXML
    fun removeMachine(event: ActionEvent) {
        val alert =
            Alert(Alert.AlertType.CONFIRMATION)

        alert.title = "Remove Machine"
        alert.headerText = "Remove Machine?"
        alert.contentText =
            "This action cannot be undone."

        val result = alert.showAndWait()

        if (
            result.isPresent &&
            result.get() == ButtonType.OK
        ) {
            deleteFolder(machineFolder)
            MachineManager.machines.removeIf {
                it.folder == machineFolder
            }
            backToMainPage(event, false)
        }
    }

    private fun deleteFolder(folder: File) {
        folder.listFiles()?.forEach {
            if (it.isDirectory) {
                deleteFolder(it)
            } else {
                it.delete()
            }
        }

        folder.delete()
    }

    @FXML
    fun backToMainPage(event: ActionEvent) {
        MachineManager.saveMachine(
            machineFolder,
            machine
        )

        backToMainPage(event, false)
    }

    private fun backToMainPage(
        event: ActionEvent,
        save: Boolean
    ) {
        if (save) {
            MachineManager.saveMachine(
                machineFolder,
                machine
            )
        }

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

    private fun updateCashLabel() {
        cashLabel.text =
            "Cash Register: ₱%.2f".format(
                machine.register.getTotalCash()
            )
    }

    private fun popup(
        title: String,
        message: String
    ) {
        val alert =
            Alert(Alert.AlertType.INFORMATION)

        alert.title = title
        alert.headerText = null
        alert.contentText = message
        alert.showAndWait()
    }

    private fun showWarning(
        title: String,
        message: String
    ) {
        Alert(Alert.AlertType.WARNING).apply {
            this.title = title
            headerText = null
            contentText = message
        }.showAndWait()
    }
}
