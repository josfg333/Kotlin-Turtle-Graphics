package josfg333.projects.turtle

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.input.KeyCode
import javafx.scene.paint.Color
import javafx.stage.Stage
import java.lang.Math.random
import kotlin.math.pow

class HelloApplication : Application() {
    override fun start(stage: Stage) {
        val screen = TurtleScreen(1000.0, 700.0)
        val scene = Scene(screen)

        val t = Turtle(screen)

        fun run() {
            t.speed = 0.0

            for (i in 1..2000) {
                t.size = 5.0*i/2000
                t.right(61.5)
                t.forward(i.toDouble())
            }
            t.size = 1.0
        }


        fun koch() {
            t.heading = 0.0
            t.speed = 0.0
            t.size = 0.5
            t.hide()
            val initialHue = random()
            var seed = "f--f--f"
            val iterations = 7
            for (i in 0..<iterations) {
                seed = seed.replace("f", "f+f--f--f++f++f-f")
            }

            val numLines = 3.0*(7.0.pow(iterations))
            val dist = 500*3.0.pow(-iterations)
            var i = 0L
            for (c in seed) {
                when (c) {
                    'f' -> {
                        t.penColor = Color.hsb((360.0*(initialHue + i.toDouble()/numLines))%360.0, 1.0, 1.0)
                        t.forward(dist)
                        i++
                    }
                    '+' -> t.left(60.0)
                    '-' -> t.right(60.0)
                }
            }
            t.show()

        }

        scene.setOnKeyPressed {keyEvent ->
            when (keyEvent.code) {
                KeyCode.W -> t.forward(5.0)
                KeyCode.S -> t.back(5.0)
                KeyCode.A -> t.left(3.0)
                KeyCode.D -> t.right(3.0)
                KeyCode.R -> run()
                KeyCode.F -> koch()
                else -> null
            }
        }

        screen.setOnMouseClicked { mouseEvent ->
            val x = mouseEvent.x - screen.width/2
            val y = -mouseEvent.y + screen.height/2
            val isDown = t.isDown
            if (mouseEvent.isShiftDown) {
                t.up()
            } else {
                t.down()
            }
            t.moveTo(x, y)
            t.isDown = isDown
        }

        screen.setOnMouseDragged { mouseEvent ->
            val x = mouseEvent.x - screen.width/2
            val y = -mouseEvent.y + screen.height/2
            val isDown = t.isDown
            if (mouseEvent.isShiftDown) {
                t.up()
            } else {
                t.down()
            }
            t.moveTo(x, y)
            t.isDown = isDown
        }

        screen.setOnMouseMoved  { mouseEvent ->
            val x = mouseEvent.x - screen.width/2
            val y = -mouseEvent.y + screen.height/2
            val isDown = t.isDown
            if (!mouseEvent.isControlDown) {
                t.up()
            } else {
                t.down()
            }
            t.moveTo(x, y)
            t.isDown = isDown
        }



        stage.title = "Turtle Window"
        stage.scene = scene
        stage.show()
    }
}

fun main() {
    Application.launch(HelloApplication::class.java)
}