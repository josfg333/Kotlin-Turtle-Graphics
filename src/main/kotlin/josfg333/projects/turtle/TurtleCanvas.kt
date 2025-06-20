package josfg333.projects.turtle

import javafx.scene.canvas.Canvas
import javafx.scene.transform.Affine

class TurtleCanvas(width: Double=500.0, height: Double=500.0): Canvas(width, height) {
    init{
        this.graphicsContext2D.transform = Affine(
            1.0, 0.0, width/2,
            0.0, -1.0, height/2
        )
    }

    private val turtlesById: MutableMap<Int, Turtle> = mutableMapOf()
    private val turtles: Collection<Turtle>
        get() = turtlesById.values

    private var nextTurtleId = 0

    public fun addTurtle(turtle: Turtle): Int {
        turtlesById[nextTurtleId] = turtle
        return nextTurtleId++
    }

    public fun removeTurtle(id: Int) {
        val turtle = turtlesById[id]
        if (turtle == null) return
        turtlesById.remove(id)
    }


//    private val turtleKeyListeners: MutableMap<Turtle, (KeyEvent) -> Unit> = mutableMapOf()
//    private val turtleMouseListeners: MutableMap<Turtle, (MouseEvent) -> Unit> = mutableMapOf()
//

//
//    public fun setKeyListener(turtle: Turtle, eventFunc: (KeyEvent) -> Unit) {
//        turtleKeyListeners[turtle] = eventFunc
//    }
//    public fun setMouseListener(tur)


}