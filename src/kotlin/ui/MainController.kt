package ui

import javafx.fxml.FXML
import javafx.scene.control.ListView
import model.VendingMachine

class MainController {

    private val machines = mutableListOf<VendingMachine>()

    @FXML
    private lateinit var machineList: ListView<String>

    @FXML
    fun handleCreate() {
        val machine = VendingMachine(8, 10)
        machines.add(machine)
        refreshList()
    }

    @FXML
    fun handleTest() {
        val index = machineList.selectionModel.selectedIndex
        if (index >= 0) {
            println("Testing machine #$index")
            // later: open new window
        }
    }

    private fun refreshList() {
        machineList.items.clear()
        machines.forEachIndexed { i, _ ->
            machineList.items.add("Machine #$i")
        }
    }
}