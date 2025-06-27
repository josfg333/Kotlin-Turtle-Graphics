package josfg333.projects.turtle

import javafx.animation.AnimationTimer
import javafx.scene.canvas.Canvas
import javafx.scene.layout.Pane
import javafx.scene.shape.StrokeLineCap
import javafx.scene.transform.Affine
import java.util.LinkedList
import java.util.Queue
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit


const val SPEED_SCALE = 1000.0
const val ROTATION_SPEED_SCALE = 1.0
const val TURTLE_SHAPE_SCALE = 6.0
const val TURTLE_SHAPE_LINE_SCALE = 0.2

data class Vec2(val x: Double = 0.0, val y: Double = 0.0) {
    fun length() = sqrt(x*x+y*y)

    operator fun plus(other: Vec2) = Vec2(x+other.x, y+other.y)
    operator fun minus(other: Vec2) = Vec2(x-other.x, y-other.y)

    operator fun times(scalar: Double) = Vec2(x*scalar, y*scalar)
}
operator fun Double.times(vec: Vec2) = vec*this

class TurtleScreen(width: Double=500.0, height: Double=500.0): Pane() {
    private val mainCanvas = Canvas(width, height)
    private val turtlesCanvas = Canvas(width, height)

    private var lastFrame = 0L
    private val timer = object: AnimationTimer() {
        override fun handle(t: Long) {
            val dt = t - lastFrame
            lastFrame = t

            draw(dt.nanoseconds)
        }
    }

    init{
        this.width = width
        this.height = height

        mainCanvas.graphicsContext2D.transform = Affine(
            1.0, 0.0, width/2,
            0.0, -1.0, height/2
        )
        mainCanvas.graphicsContext2D.lineCap = StrokeLineCap.ROUND
        this.children.add(mainCanvas)
        turtlesCanvas.graphicsContext2D.lineCap = StrokeLineCap.ROUND
        this.children.add(turtlesCanvas)

        timer.start()
    }

    private val turtlesById: MutableMap<Int, Turtle> = mutableMapOf()
    private val idsByTurtle: MutableMap<Turtle, Int> = mutableMapOf()

    private val turtleCanvases: MutableMap<Turtle, Canvas> = mutableMapOf()
    private val turtleStates: MutableMap<Turtle, TurtleState> = mutableMapOf()
    private val turtleInstructionQueues: MutableMap<Turtle, Queue<TurtleInstruction>> = mutableMapOf()

    private val turtles: Collection<Turtle>
        get() = turtlesById.values

    private var nextTurtleId = 0
    public fun addTurtle(turtle: Turtle, initialState: TurtleState): Pair<Int, Queue<TurtleInstruction>> {
        turtlesById[nextTurtleId] = turtle
        idsByTurtle[turtle] = nextTurtleId

        turtleStates[turtle] = initialState
        val instructionQueue = LinkedList<TurtleInstruction>()
        turtleInstructionQueues[turtle] = instructionQueue

//        val canvas = Canvas(width, height)
//        turtleCanvases[turtle] = canvas
//        this.children.add(canvas)

        return Pair(nextTurtleId++, instructionQueue)
    }

    private fun draw(dt: Duration) {
        turtlesCanvas.graphicsContext2D.clearRect(0.0, 0.0, width, height)

        val gc = mainCanvas.graphicsContext2D
        for ((turtle, instructions) in turtleInstructionQueues.entries) {
            val state = turtleStates[turtle]!!
            var instructionQuota = 10000
            var timeQuota = dt

            while (instructions.isNotEmpty() && instructionQuota-- > 0 && timeQuota > 0.seconds) {
                val instruction = instructions.peek()
                val timeQuotaSeconds = timeQuota.toDouble(DurationUnit.SECONDS)
                when (instruction.type) {
                    TurtleInstructionType.MOVE_TO -> {
                        val currentPos = Vec2(state.x, state.y)
                        val targetPos = Vec2(instruction.d0, instruction.d1)
                        val movement = targetPos - currentPos
                        val targetDistance = movement.length()
                        val maxDistance = timeQuotaSeconds*state.speed

                        val endPos =
                            if (targetDistance <= maxDistance) targetPos
                            else currentPos + movement*(maxDistance/targetDistance)

                        timeQuota -= (targetDistance/state.speed).seconds

                        if (state.isDown) {
                            gc.save()

                            gc.lineWidth = state.size
                            gc.stroke = state.penColor

                            gc.beginPath()
                            gc.moveTo(state.x, state.y)
                            gc.lineTo(endPos.x, endPos.y)
                            gc.stroke()

                            gc.restore()
                        }
                        state.x = endPos.x
                        state.y = endPos.y

                        if (endPos != targetPos) continue

                    }
                    TurtleInstructionType.ROTATE_TO -> {
                        val target = instruction.d0
                        val movement = target - state.heading
                        val targetDistance = abs(movement)
                        val maxDistance = timeQuotaSeconds*state.speed*ROTATION_SPEED_SCALE

                        val end =
                            if (targetDistance <= maxDistance) target
                            else state.heading + movement*(maxDistance/targetDistance)

                        timeQuota -= (targetDistance/(state.speed*ROTATION_SPEED_SCALE)).seconds

                        state.heading = end

                        if (end != target) continue
                        else state.heading = ((end % 360) + 360) % 360
                    }
                    TurtleInstructionType.IS_DOWN -> state.isDown = instruction.flag
                    TurtleInstructionType.IS_VISIBLE -> state.isVisible = instruction.flag
                    TurtleInstructionType.SIZE ->  state.size = instruction.d0
                    TurtleInstructionType.PEN_COLOR -> state.penColor = instruction.color
                    TurtleInstructionType.FILL_COLOR ->  state.fillColor = instruction.color
                    TurtleInstructionType.SPEED ->  state.speed = instruction.d0
                }
                instructions.remove()
            }

            drawTurtle(turtle)
        }
    }

    private fun drawTurtle(turtle: Turtle) {
        val shape = TurtleShape.ARROW
        val state = turtleStates[turtle]!!
        val scale = sqrt(state.size) * TURTLE_SHAPE_SCALE

        val gc = turtlesCanvas.graphicsContext2D

        if (state.isVisible) {
            gc.save()

            val rad = radians(state.heading)
            gc.transform = Affine(
                scale * cos(rad), scale * -sin(rad), width/2 + state.x,
                -scale * sin(rad), -scale * cos(rad), height/2 - state.y
            )

            gc.beginPath()
            for (point in shape.points) {
                gc.lineTo(point.x, point.y)
            }
            gc.closePath()

            gc.stroke = state.penColor
            gc.fill = state.fillColor
            gc.lineWidth = TURTLE_SHAPE_LINE_SCALE
            gc.fill()
            gc.stroke()

            gc.restore()
        }

    }



//    public fun removeTurtle(id: Int) {
//        val turtle = turtlesById[id]
//        if (turtle == null) return
//        turtlesById.remove(id)
//        idsByTurtle.remove(turtle)
//    }


//    private val turtleKeyListeners: MutableMap<Turtle, (KeyEvent) -> Unit> = mutableMapOf()
//    private val turtleMouseListeners: MutableMap<Turtle, (MouseEvent) -> Unit> = mutableMapOf()
//

//
//    public fun setKeyListener(turtle: Turtle, eventFunc: (KeyEvent) -> Unit) {
//        turtleKeyListeners[turtle] = eventFunc
//    }
//    public fun setMouseListener(tur)


}