package josfg333.projects.turtle

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.layout.Pane
import javafx.stage.Stage

class HelloApplication : Application() {
    override fun start(stage: Stage) {
        val root = Pane()
        val canvas = TurtleCanvas(1000.0, 700.0)
        root.children.add(canvas)
        val scene = Scene(root, canvas.width, canvas.height)

        val t = Turtle(canvas)
        t.size = 2.0
        t.forward(100.0)
        t.left(90.0)
        t.back(100.0)
        t.posX = 0.0
        t.posY = 0.0
        t.up()
        t.moveTo(-100.0, 100.0)
        t.down()
        t.forward(100.0)

        for (i in 1..100) {
            t.right(61.5)
            t.forward(i.toDouble())
        }


        stage.title = "Turtle Window"
        stage.scene = scene
        stage.show()
    }
}

fun main() {
    Application.launch(HelloApplication::class.java)
}