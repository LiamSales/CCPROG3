package ui

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.stage.Stage

class MaintenanceController {

    @FXML
    fun backToMainPage(event: ActionEvent) {

        openPage("/fxml/main.fxml", event)

    }

    @FXML
    fun removeMachine(event: ActionEvent) {

        val confirmation = Alert(Alert.AlertType.CONFIRMATION)

        confirmation.title = "Remove Machine"
        confirmation.headerText = "Remove this machine?"
        confirmation.contentText =
            "This action cannot be undone."

        val result = confirmation.showAndWait()

        if (result.isPresent && result.get() == ButtonType.OK) {

            // Remove machine later.

            openPage("/fxml/main.fxml", event)

        }
    }

    @FXML
    fun collectBalance() {

        val alert = Alert(Alert.AlertType.INFORMATION)

        alert.title = "Balance Collected"
        alert.headerText = null
        alert.contentText = "Balance was collected."

        alert.showAndWait()
    }

    @FXML
    fun openRestockPage(event: ActionEvent) {

        // Restock functionality later.

        openPage("/fxml/restock.fxml", event)

    }

    /*
        Helper function used to switch pages.
     */
    private fun openPage(path: String, event: ActionEvent) {

        val root: Parent =
            FXMLLoader.load(
                javaClass.getResource(path)
            )

        val stage =
            (event.source as javafx.scene.Node)
                .scene
                .window as Stage

        stage.scene = Scene(root)
    }
}