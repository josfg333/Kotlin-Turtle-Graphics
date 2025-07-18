package josfg333.projects.turtle

import javafx.scene.paint.Color
import java.util.Queue
import kotlin.math.*


fun radians(degrees: Double) = PI * degrees / 180.0
fun degrees(radians: Double) = 180.0 * radians / PI

enum class TurtleShape(val points: Iterable<Vec2>) {
    ARROW(listOf(
        Vec2(-0.5, 0.0),
        Vec2(-1.0, 1.0),
        Vec2(1.0, 0.0),
        Vec2(-1.0, -1.0))
    ),

}

enum class TurtleInstructionType {
    MOVE_TO,
    ROTATE_TO,
    SIZE,
    IS_VISIBLE,
    IS_DOWN,
    PEN_COLOR,
    FILL_COLOR,
    SPEED,
    BEGIN_FILL,
    END_FILL
}

class TurtleInstruction(
    val type: TurtleInstructionType,
    val flag: Boolean = true,
    val d0: Double = 0.0,
    val d1: Double = 0.0,
    val color: Color = Color.BLACK
)

data class TurtleState(
    var pos: Vec2 = Vec2(0.0, 0.0),
    var heading: Double = 0.0,
    var size: Double = 1.0,
    var penColor: Color = Color.BLACK,
    var fillColor: Color = Color.WHITE,
    var isDown:Boolean = true,
    var isVisible:Boolean = true,
    var speed:Double = TURTLE_SPEED_SCALE,
    var isFilling: Boolean = false,
    val fillPoints: MutableList<Vec2> = mutableListOf()
)


class Turtle(public val screen: TurtleScreen) {
    constructor() : this(TurtleScreen())

    private val state = TurtleState()

    private val instructionQueue: Queue<TurtleInstruction>
    init {
        val instructionQueue = screen.addTurtle(this, state.copy())
        this.instructionQueue = instructionQueue
    }





    public var x get() = state.pos.x
        set(value) = moveTo(value, y)
    public var y get() = state.pos.y
        set(value) = moveTo(x, value)
    public var pos get() = state.pos
        set(value) = moveTo(value)

    public var heading
        get() = state.heading
        set(value) {
            instructionQueue.add(TurtleInstruction(TurtleInstructionType.ROTATE_TO, d0 = value))
            state.heading = ((value%360) + 360) % 360
        }

    public var size
        get() = state.size
        set(value) {
            val value = max(0.0, value)
            instructionQueue.add(TurtleInstruction(TurtleInstructionType.SIZE, d0 = value))
            state.size = value
        }
    public var penColor
        get() = state.penColor
        set(value) {
            instructionQueue.add(TurtleInstruction(TurtleInstructionType.PEN_COLOR, color = value))
            state.penColor = value
        }
    public var fillColor
        get() = state.fillColor
        set(value) {
            instructionQueue.add(TurtleInstruction(TurtleInstructionType.FILL_COLOR, color = value))
            state.fillColor = value
        }
    public var isDown
        get() = state.isDown
        set(value) {
            instructionQueue.add(TurtleInstruction(TurtleInstructionType.IS_DOWN, flag = value))
            state.isDown = value
        }
    public var isVisible
        get() = state.isVisible
        set(value) {
            instructionQueue.add(TurtleInstruction(TurtleInstructionType.IS_VISIBLE, flag = value))
            state.isVisible = value
        }
    public var speed
        get() = state.speed
        set(value) {
            val value = if (value<=0.0) Double.POSITIVE_INFINITY else  value
            instructionQueue.add(TurtleInstruction(type=TurtleInstructionType.SPEED, d0=value))
            state.speed = value
        }
    public var isFilling
        get() = state.isFilling
        set(value) {
            if ((!state.isFilling) && value) {
                instructionQueue.add(TurtleInstruction(type=TurtleInstructionType.BEGIN_FILL))
                state.isFilling = true
            } else if (state.isFilling && (!value)) {
                instructionQueue.add(TurtleInstruction(type=TurtleInstructionType.END_FILL))
                state.isFilling = false
            }

        }

    public fun heading(degrees: Double){heading=degrees}
    public fun size(value: Double){size=value}
    public fun penColor(value: Color){penColor=value}
    public fun fillColor(value: Color){fillColor=value}
    public fun speed(value: Double){speed=value}

    public fun down(){isDown = true}
    public fun up(){isDown = false}

    public fun show(){isVisible = true}
    public fun hide(){isVisible = false}

    public fun beginFill(){isFilling = true}
    public fun endFill(){isFilling = false}

    public fun moveTo(x: Double, y:Double) {
        instructionQueue.add(TurtleInstruction(TurtleInstructionType.MOVE_TO, d0=x, d1=y))
        state.pos = Vec2(x, y)
    }
    public fun moveTo(pos: Vec2) = moveTo(pos.x, pos.y)

    public fun forward(distance: Double) {
        val rad = radians(heading)
        moveTo(x + distance*cos(rad), y + distance*sin(rad))
    }
    public fun back(distance: Double) = forward(-distance)

    public fun left(degrees: Double) {heading+=degrees}
    public fun right(degrees: Double) {heading-=degrees}
}
