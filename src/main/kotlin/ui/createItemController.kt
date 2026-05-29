package ui

import javafx.fxml.FXML
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.stage.FileChooser
import java.io.File
import model.Item

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

        val chooser = FileChooser()

        chooser.title = "Select Item Image"

        chooser.extensionFilters.add(
            FileChooser.ExtensionFilter(
                "Images",
                "*.png",
                "*.jpg",
                "*.jpeg"
            )
        )


        /*
            Opens operating system dialog.
        */
        val file: File? =
            chooser.showOpenDialog(null)


        if (file != null) {

            selectedImagePath = file.absolutePath


            /*
                JavaFX Image object.

                Internally:
                decoded image data
                uploaded for rendering.
            */
            val image =
                Image(file.toURI().toString())

            previewImage.image = image
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


        println(item)
    }
}