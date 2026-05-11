package ui  // Must match fx:controller in FXML

import javafx.fxml.FXML              // Marks fields/methods for FXML injection
import javafx.scene.control.ListView
import javafx.scene.control.Alert
import model.VendingMachine         // Your backend class

class MainController {

    // Stores actual machine objects (references in heap) //so this is our actual thing already?
    private val machines = mutableListOf<VendingMachine>()

    @FXML
    private lateinit var machineList: ListView<String>
    // lateinit = initialized later by FXMLLoader (not constructor)
    // ListView<String> internally holds ObservableList<String>

    @FXML
    fun handleCreate() {
        // Called when "Create Machine" button is clicked

        val machine = VendingMachine(8, 10)
        // Allocates a new VendingMachine object in memory

        machines.add(machine)
        // Stores reference in MutableList

        refreshList()
        // Updates UI list to reflect new data
    }

    @FXML
    fun handleOpen() {
        // Called when "Open Machine" button is clicked

        val index = machineList.selectionModel.selectedIndex
        // selectionModel tracks user selection
        // returns -1 if nothing selected

        if (index < 0) {
            showError("Select a machine first.")
            return
        }

        println("Opening machine #$index")
        // Placeholder, later you switch scene or open new window
    }

    private fun refreshList() {
        // Syncs backend list with UI list

        machineList.items.clear()
        // items is ObservableList<String>
        // clearing triggers UI update automatically

        machines.forEachIndexed { i, _ ->
            machineList.items.add("Machine #${i + 1}")
            // Adds display string (not the object itself)
        }
    }

    private fun showError(message: String) {
        val alert = Alert(Alert.AlertType.ERROR)
        // Creates modal dialog window

        alert.contentText = message
        // Sets message text

        alert.showAndWait()
        // Displays dialog and blocks interaction until closed
    }
}