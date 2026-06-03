package ui

import javafx.fxml.FXML
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.control.TextInputDialog
import model.Item
import model.saveItem
import javafx.stage.Stage

class CreateItemController {

    @FXML
    private lateinit var previewImage: ImageView

    @FXML
    private lateinit var nameField: TextField

    @FXML
    private lateinit var calorieField: TextField


    /*
        Stores selected image file path.
    */
    private var selectedImagePath: String? = null


    /*
        Opens native OS file picker.
    */
    @FXML
    fun uploadImage() {

        val dialog = TextInputDialog()
        dialog.title = "Image URL"
        dialog.headerText = "Enter image URL"
        dialog.contentText = "URL:"

        val result = dialog.showAndWait()
        result.ifPresent { url ->
            try {
                val image = Image(url, true)
                previewImage.image = image
                selectedImagePath = url
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }


    @FXML
    fun saveItem() {

        val name = nameField.text

        val calories =
            calorieField.text.toIntOrNull() ?: 0


        val item = Item(
            name = name,
            calories = calories,
            iconPath = selectedImagePath
        )


        // persist item to CSV
        saveItem(item)

        // close modal window
        val stage = nameField.scene.window as Stage
        stage.close()
    }
}