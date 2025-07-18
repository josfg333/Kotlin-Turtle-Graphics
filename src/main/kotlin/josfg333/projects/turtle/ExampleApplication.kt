package josfg333.projects.turtle

import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.input.KeyCode
import javafx.scene.input.MouseButton
import javafx.scene.paint.Color
import javafx.stage.Stage

import kotlin.math.*

class ExampleApplication : Application() {
    override fun start(stage: Stage) {
        val screen = TurtleScreen(1000.0, 768.0)
        screen.mainCanvas.graphicsContext2D.fillRect(-screen.width/2, -screen.height/2, screen.width, screen.height)
        val scene = Scene(screen)

        val t = Turtle(screen)

        // Will be run on window startup and on 'R' keypress
        fun run() {
            t.speed = 2.0
            t.penColor = Color.WHITESMOKE
            t.fillColor = Color.GREEN
            t.up()
            t.moveTo(-300.0, 200.0*cos(radians(30.0)))
            t.down()
        }


        // Last recorded mouse point
        var lastPoint = Vec2(0.0, 0.0)

        // Precompute Koch fractal
        var initialHue = 0.0
        var seed = "f--f--f"
        val fReplace = "f+f--f-f--f--ff"
        val seedFCount = seed.count({c->c=='f'}).toDouble()
        val fReplaceFCount = fReplace.count({c->c=='f'}).toDouble()
        val iterations = 4
        for (i in 0..<iterations) {
            // seed = seed.replace("f", "f+f--f+f")
            //seed = seed.replace("f", "f+f--f-f--f--ff")
            seed = seed.replace("f", fReplace)
        }
        val sideLength = 600.0
        val numLines = seedFCount*(fReplaceFCount.pow(iterations))
        val dist = sideLength*3.0.pow(-iterations)
        fun koch() {
            t.speed = 0.0
            t.up()
            val pos = t.pos
            t.heading(0.0)
            t.forward(sideLength/3)
            t.right(60.0)
            t.forward(sideLength/3)
            t.beginFill()
            t.moveTo(pos)
            t.heading = 0.0
            t.down()
            t.size = 1.0
            t.hide()

            val fillCentres = arrayOf<Vec2>(
                t.pos,
                t.pos + Vec2(x=sideLength),
                t.pos + Vec2(sideLength/2, -sideLength*cos(radians(30.0)))
            )

            var i = 0L
            for (c in seed) {
                when (c) {
                    'f' -> {
                        val hue = (360.0*(initialHue + i.toDouble()/numLines))%360.0
                        t.penColor = Color.hsb(hue, 1.0, 1.0)
                        t.fillColor = Color.hsb(hue, 1.0, 1.0, 0.01)
                        val temp = t.pos
                        t.up()
                        for (fillCentre in fillCentres) {
                            t.beginFill()
                            t.forward(dist)
                            t.moveTo(fillCentre)
                            t.moveTo(temp)
                        }
                        t.endFill()
                        t.down()
                        t.forward(dist)
                        i++
                    }
                    '+' -> t.left(60.0)
                    '-' -> t.right(60.0)
                }
            }

            initialHue -= 0.008
            t.speed = 2.0
            t.size = 1.0
        }

        scene.setOnKeyPressed {keyEvent ->
            when (keyEvent.code) {
                KeyCode.UP -> t.forward(5.0)
                KeyCode.DOWN -> t.back(5.0)
                KeyCode.LEFT -> t.left(3.0)
                KeyCode.RIGHT -> t.right(3.0)
                KeyCode.R -> run()
                KeyCode.F -> koch()
                else -> null
            }
        }



        // Click to move turtle without drawing, Shift-click to move turtle while drawing
        screen.setOnMouseClicked { mouseEvent ->
            when (mouseEvent.button) {
                MouseButton.PRIMARY -> {
                    val x = mouseEvent.x - screen.width / 2
                    val y = -mouseEvent.y + screen.height / 2
                    val isDown = t.isDown
                    if (mouseEvent.isShiftDown) t.down()
                    else t.up()
                    t.moveTo(x, y)
                    t.isDown = isDown
                }
                MouseButton.MIDDLE -> {
                    t.isFilling = !t.isFilling
                }
                else -> null
            }
        }

        // Turtle follows mouse when Control is held
        screen.setOnMouseMoved  { mouseEvent ->
            val x = mouseEvent.x - screen.width/2
            val y = -mouseEvent.y + screen.height/2
            lastPoint = Vec2(x, y)
            if (mouseEvent.isControlDown) {
                val isDown = t.isDown
                t.up()
                t.moveTo(x, y)
                t.isDown = isDown
            }
        }

        // Drag to draw
        screen.setOnMouseDragged { mouseEvent ->
            val x = mouseEvent.x - screen.width/2
            val y = -mouseEvent.y + screen.height/2
            val targetPoint = Vec2(x, y)
            val isDown = t.isDown

            if (mouseEvent.isShiftDown) t.down()
            else t.up()
            t.moveTo(lastPoint.x, lastPoint.y)
            t.down()
            t.moveTo(x, y)

            if (targetPoint != lastPoint) {
                val movement = targetPoint - lastPoint
                var heading = (degrees(atan2(movement.y, movement.x)) + 360.0) % 360.0
                if (heading - t.heading > 180.0) heading -= 360.0
                else if (heading - t.heading < -180.0) heading += 360.0
                t.heading = heading
            }

            lastPoint = targetPoint
            t.isDown = isDown
        }

        // Scroll to change turtle size
        screen.setOnScroll { scrollEvent ->
            t.size += if(scrollEvent.deltaY > 0.0) 0.1 else if (scrollEvent.deltaY < 0.0) -0.1 else 0.0
            println(scrollEvent)
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