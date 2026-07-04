package ui

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.TextField
import javafx.scene.control.Spinner
import javafx.scene.control.SpinnerValueFactory
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

        val stage =
            nameField.scene.window as Stage

        val file: File? =
            chooser.showOpenDialog(stage)

        if (file != null) {

            imagePathField.text = file.absolutePath

        }
    }

    @FXML
    fun submitItem(event: ActionEvent) {

        // Saving will be added later.

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

//     @FXML
// fun initialize() {

//     calorieSpinner.valueFactory =
//         SpinnerValueFactory.IntegerSpinnerValueFactory(
//             0,
//             99999,
//             0
//         )

//     calorieSpinner.isEditable = true

//     calorieSpinner.editor.textFormatter = TextFormatter<String> { change ->

//         if (change.controlNewText.matches(Regex("\\d*"))) {
//             change
//         } else {
//             null
//         }

//     }
// }

}