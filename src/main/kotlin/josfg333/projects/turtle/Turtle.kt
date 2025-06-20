package josfg333.projects.turtle

import javafx.scene.paint.Color
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

fun radians(degrees: Double) = PI * degrees / 180.0

class Turtle(val canvas: TurtleCanvas) {
    init {
        canvas.addTurtle(this)
    }
    private val gc = canvas.graphicsContext2D

    public var isDown = true
    public var penColor: Color = Color.BLACK
//    public var fillColor = Color.BLACK
    public var size = 1.0
        set(value)  {field = max(0.0, value)}

    public var heading = 0.0 // Heading in degrees, 0=East, 90 = North
        set(value) {field=((value%360)+360)%360}
    public var pos = Pos()
        set(value) {
            if (isDown) {
                gc.save()
                gc.beginPath()
                gc.moveTo(pos.x, pos.y)
                gc.lineTo(value.x, value.y)
                gc.stroke = penColor
                gc.lineWidth = size
                gc.stroke()
                gc.restore()
            }
            field = value
        }
    public var posX: Double = pos.x
        set(value) {moveTo(value, pos.y); field=value}
        get() = pos.x

    public var posY: Double = pos.y
        set(value) {moveTo(pos.x, value); field=value}
        get() = pos.y

    public fun down(){isDown = true}
    public fun up(){isDown = false}

    public fun moveTo(pos: Pos){this.pos = pos}
    public fun moveTo(x: Double, y:Double) = moveTo(Pos(x,y))

    public fun forward(pixels: Double) {
        val rad = radians(heading)
        moveTo(pos.x + pixels*cos(rad), pos.y + pixels*sin(rad))
    }
    public fun back(pixels: Double) = forward(-pixels)

//    public fun setHeading(degrees: Double) {heading=degrees}
    public fun left(degrees: Double) {heading+=degrees}
    public fun right(degrees: Double) {heading-=degrees}
}

data class Pos(val x: Double=0.0, val y: Double=0.0)