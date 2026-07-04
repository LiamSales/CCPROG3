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

        val name = nameField.text.trim()

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

        // Saving will be added later.

        openMainPage(event)
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