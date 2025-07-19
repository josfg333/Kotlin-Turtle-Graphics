package josfg333.projects.turtle

import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.input.KeyCode
import javafx.scene.paint.Color
import javafx.stage.Stage

import kotlin.math.*

class ExampleApplication : Application() {
    override fun start(stage: Stage) {
        val screen = TurtleScreen(1000.0, 768.0)
        //screen.mainCanvas.graphicsContext2D.fillRect(-screen.width/2, -screen.height/2, screen.width, screen.height)

        val t = Turtle(screen)

        fun reset() {
            t.speed = 0.0
            t.up()
            t.moveTo(0.0, 0.0)
            t.heading = 0.0
            t.size = 1.0
            t.penColor = Color.BLACK
            t.fillColor = Color.GRAY
            t.show()
            t.down()
            t.speed = 1.0
        }

        val exampleProductions = mapOf(
            Pair('F', "F+f--F+F"),
            Pair('f', "fff")
            )

        fun apply(string: String, productions: Map<Char, String>): String {
            // TODO: return a string obtained by applying the replacement productions to the string
            return ""
        }

        fun turtleInterpret(string: String) {
            // TODO: make the turtle interpret (run) the string.
        }

        // Will be run on window startup and on 'R' keypress
        fun run() {
            // TODO: Put code here

        }


        val scene = Scene(screen)

        scene.setOnKeyPressed {keyEvent ->
            when (keyEvent.code) {
                KeyCode.R -> run()
                KeyCode.C -> {t.screen.clear(); reset()}
                else -> null
            }
        }

        stage.title = "Turtle Window"
        stage.scene = scene
        stage.onShown = EventHandler {event -> run()}
        stage.show()
    }

}

fun main() {
    Application.launch(ExampleApplication::class.java)
}