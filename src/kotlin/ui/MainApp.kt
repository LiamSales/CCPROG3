package // used for classpath and FXML controller resolution

import javafx.application.Application  // Base class that launches JavaFX runtime
import javafx.fxml.FXMLLoader         // Loads and parses FXML files
import javafx.scene.Parent            // Base class for all UI nodes that can have children
import javafx.scene.Scene             // Container for the UI tree
import javafx.stage.Stage             // Represents the application window

class MainApp : Application() {  // Must extend Application to start JavaFX

    override fun start(stage: Stage) {  
        // This method is called automatically by JavaFX after launch

        val root: Parent = FXMLLoader.load(
            javaClass.getResource("/fxml/main.fxml")
            // Loads the FXML file from resources
            // Leading "/" means absolute path inside resources folder
        )

        val scene = Scene(root, 400.0, 300.0)
        // Scene wraps the root node
        // 400x300 are initial window dimensions (Double values)

        stage.scene = scene  
        // Attaches the scene (UI tree) to the window

        stage.title = "Vending Machines"  
        // Sets window title (stored as a String property)

        stage.show()  
        // Makes the window visible and starts rendering
    }
}

fun main() {
    Application.launch(MainApp::class.java)
    // Starts JavaFX runtime
    // Creates UI thread
    // Calls start() internally
} //what about the backend?

/*
main()
Application.launch()
JavaFX runtime starts
new MainApp()
start(Stage)
FXMLLoader parses XML
Scene graph created
Stage shows window
*/