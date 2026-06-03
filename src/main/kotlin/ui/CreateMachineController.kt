package ui

import javafx.fxml.FXML
import javafx.scene.control.TextField
import javafx.stage.Stage

class CreateMachineController {

    @FXML
    private lateinit var nameField: TextField

    @FXML
    private lateinit var locationField: TextField

    @FXML
    private fun saveMachine() {
        val name = nameField.text ?: ""
        val location = locationField.text ?: ""

        // TODO: persist the new machine in your backend / model
        println("Saving machine: name=$name, location=$location")

        // close the window
        val stage = nameField.scene.window as Stage
        stage.close()
    }
}
