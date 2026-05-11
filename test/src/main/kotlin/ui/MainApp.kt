package ui

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage

class MainApp : Application() {

    override fun start(stage: Stage) {

        val root = FXMLLoader.load<javafx.scene.Parent>(
            javaClass.getResource("/fxml/main.fxml")
        )

        stage.scene = Scene(root, 400.0, 300.0)

        stage.title = "JavaFX Test"

        stage.show()
    }
}

fun main() {
    Application.launch(MainApp::class.java)
}