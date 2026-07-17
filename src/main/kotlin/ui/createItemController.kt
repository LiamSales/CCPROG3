package ui

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.scene.control.Spinner
import javafx.scene.control.TextField
import javafx.stage.FileChooser
import javafx.stage.Stage
import java.io.File

class CreateItemController {

    @FXML
    private lateinit var nameField: TextField

    @FXML
    private lateinit var calorieSpinner: Spinner<Int>

    @FXML
    private lateinit var imagePathField: TextField

    @FXML
    fun browseImage() {

        val chooser = FileChooser()

        chooser.title = "Choose Item Image"

        chooser.extensionFilters.add(
            FileChooser.ExtensionFilter(
                "Images",
                "*.png",
                "*.jpg",
                "*.jpeg"
            )
        )

        val stage = nameField.scene.window as Stage

        val file: File? = chooser.showOpenDialog(stage)

        if (file != null) {
            imagePathField.text = file.absolutePath
        }
    }

    @FXML
    fun back(event: ActionEvent) {

        openMainPage(event)

    }

    @FXML
    fun submitItem(event: ActionEvent) {

        val name =
            nameField.text.trim()

        val calories =
            calorieSpinner.editor.text.toIntOrNull()

        val imagePath =
            imagePathField.text.trim()

        if (name.isBlank()) {

            showError("Please enter an item name.")
            return

        }

        if (calories == null || calories <= 0) {

            showError("Calories must be a whole number greater than zero.")
            return

        }

        if (imagePath.isBlank()) {

            showError("Please choose an image.")
            return

        }

        val csv =
            File("data/items.csv")

        if (csv.exists()) {

            val duplicate =
                csv.readLines().any { line ->

                    if (line.isBlank())
                        false
                    else
                        line.split(",")[0]
                            .trim()
                            .equals(name, ignoreCase = true)

                }

            if (duplicate) {

                showError("An item with that name already exists.")
                return

            }
        }

        val source =
            File(imagePath)

        if (!source.exists()) {

            showError("The selected image does not exist.")
            return

        }

        val imageFolder =
            File("data/images")

        imageFolder.mkdirs()

        val destination =
            File(
                imageFolder,
                source.name
            )

        source.copyTo(
            destination,
            overwrite = true
        )

        saveItem(
            name,
            calories,
            "data/images/${source.name}"
        )

        println("Item saved:")
        println("$name, $calories, data/images/${source.name}")

        openMainPage(event)
    }

    private fun saveItem(

        name: String,
        calories: Int,
        imagePath: String

    ) {

        val dataFolder =
            File("data")

        dataFolder.mkdirs()

        val file =
            File(dataFolder, "items.csv")

        file.appendText(

            "$name," +
            "$calories," +
            "$imagePath\n"

        )
    }

    private fun openMainPage(event: ActionEvent) {

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

    private fun showError(message: String) {

        Alert(Alert.AlertType.ERROR).apply {

            title = "Invalid Input"
            headerText = null
            contentText = message

        }.showAndWait()
    }
}