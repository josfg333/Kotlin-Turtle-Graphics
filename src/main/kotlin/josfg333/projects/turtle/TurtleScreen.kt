package josfg333.projects.turtle

import javafx.animation.AnimationTimer
import javafx.scene.canvas.Canvas
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.shape.StrokeLineCap
import javafx.scene.transform.Affine

import java.util.LinkedList
import java.util.Queue

import kotlin.math.*
import kotlin.time.*
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds


const val TURTLE_SPEED_SCALE = 1000.0
const val TURTLE_ROTATION_SPEED_SCALE = 1.0
const val TURTLE_SHAPE_SCALE = 6.0
const val TURTLE_SHAPE_LINE_SCALE = 0.2

const val DEFAULT_INSTRUCTION_LIMIT = 2048

typealias TurtleDatum = Triple<Turtle, TurtleState, Queue<TurtleInstruction>>
val TurtleDatum.turtle get() = this.first
val TurtleDatum.state get() = this.second
val TurtleDatum.queue get() = this.third

class TurtleScreen(width: Double=500.0, height: Double=500.0): StackPane() {
    public val mainCanvas = Canvas(width, height)
    private val turtlesCanvas = Canvas(width, height)

    public val instructionLimit = DEFAULT_INSTRUCTION_LIMIT

    private var lastFrame = 0L
    private val timer = object: AnimationTimer() {
        override fun handle(t: Long) {
            if (lastFrame == 0L) {
                // First frame, skip
                lastFrame = t
                return
            }
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

    public fun clear() {
        this.mainCanvas.graphicsContext2D.clearRect(this.width/2, this.height/2, this.width, this.height)
    }


    private val turtleData: MutableList<TurtleDatum> = mutableListOf()

    public val turtles: Collection<Turtle>
        get() = turtleData.map {datum -> datum.turtle}

    public fun addTurtle(turtle: Turtle, initialState: TurtleState): Queue<TurtleInstruction> {


        val instructionQueue = LinkedList<TurtleInstruction>()
        turtleData.add(TurtleDatum(turtle, initialState, instructionQueue))

        return instructionQueue
    }

    private fun draw(dt: Duration) {
        turtlesCanvas.graphicsContext2D.clearRect(0.0, 0.0, width, height)

        val gc = mainCanvas.graphicsContext2D
        for ((turtle, state, instructions) in turtleData) {
            var instructionQuota = instructionLimit
            var timeQuota = dt

            while (instructions.isNotEmpty() && instructionQuota-- > 0 && timeQuota > 0.seconds) {
                val instruction = instructions.peek()
                val timeQuotaSeconds = timeQuota.toDouble(DurationUnit.SECONDS)
                when (instruction.type) {
                    TurtleInstructionType.MOVE_TO -> {
                        val speed = state.speed * TURTLE_SPEED_SCALE
                        val currentPos = state.pos
                        val targetPos = Vec2(instruction.d0, instruction.d1)
                        val movement = targetPos - currentPos
                        val targetDistance = movement.length()
                        val maxDistance = timeQuotaSeconds*speed

                        val endPos =
                            if (targetDistance <= maxDistance) targetPos
                            else currentPos + movement*(maxDistance/targetDistance)

                        timeQuota -= (targetDistance/speed).seconds


                        if (state.isDown && state.size > 0.0) {
                            gc.save()

                            gc.lineWidth = state.size
                            gc.stroke = state.penColor

                            gc.beginPath()
                            gc.moveTo(state.pos.x, state.pos.y)
                            gc.lineTo(endPos.x, endPos.y)
                            gc.stroke()

                            gc.restore()
                        }
                        state.pos = endPos

                        if (endPos != targetPos) continue // Prevents removing the instruction

                        if (state.isFilling) state.fillPoints.add(endPos)
                    }
                    TurtleInstructionType.ROTATE_TO -> {
                        val speed = state.speed*TURTLE_SPEED_SCALE*TURTLE_ROTATION_SPEED_SCALE
                        val target = instruction.d0
                        val movement = target - state.heading
                        val targetDistance = abs(movement)
                        val maxDistance = timeQuotaSeconds*speed

                        val end =
                            if (targetDistance <= maxDistance) target
                            else state.heading + movement*(maxDistance/targetDistance)

                        timeQuota -= (targetDistance/speed).seconds

                        state.heading = end

                        if (end != target) continue // Prevents removing the instruction
                        else state.heading = ((end % 360) + 360) % 360 // Canonicalises the heading
                    }
                    TurtleInstructionType.IS_DOWN -> state.isDown = instruction.flag
                    TurtleInstructionType.IS_VISIBLE -> state.isVisible = instruction.flag
                    TurtleInstructionType.SIZE ->  state.size = instruction.d0
                    TurtleInstructionType.PEN_COLOR -> state.penColor = instruction.color
                    TurtleInstructionType.FILL_COLOR ->  state.fillColor = instruction.color
                    TurtleInstructionType.SPEED ->  state.speed = instruction.d0
                    TurtleInstructionType.BEGIN_FILL -> {
                        state.isFilling = true
                        state.fillPoints.add(state.pos)
                    }
                    TurtleInstructionType.END_FILL -> {
                        gc.save()
                        gc.beginPath()
                        for (point in state.fillPoints) {
                            gc.lineTo(point.x, point.y)
                        }
                        gc.fill = state.fillColor
                        gc.fill()
//                        val xs = state.fillPoints.map {point -> point.x}
//                        val ys = state.fillPoints.map {point -> point.y}
//                        gc.fillPolygon(xs.toDoubleArray(), ys.toDoubleArray(), state.fillPoints.size)
                        gc.restore()

                        state.fillPoints.clear()
                        state.isFilling = false
                    }
                }
                instructions.remove()
            }

            drawTurtleState(state)
        }
    }

    private fun drawTurtleState(state: TurtleState) {
        val shape = TurtleShape.ARROW
        val scale = sqrt(state.size) * TURTLE_SHAPE_SCALE

        val gc = turtlesCanvas.graphicsContext2D

        if (state.isVisible) {
            gc.save()

            val rad = radians(state.heading)
            gc.transform = Affine(
                scale * cos(rad), scale * -sin(rad), width/2 + state.pos.x,
                -scale * sin(rad), -scale * cos(rad), height/2 - state.pos.y
            )

            gc.beginPath()
            for (point in shape.points) {
                gc.lineTo(point.x, point.y)
            }
            gc.closePath()

            gc.stroke = Color.WHITESMOKE.darker()
            gc.fill = state.penColor
            gc.lineWidth = TURTLE_SHAPE_LINE_SCALE
            gc.fill()
            gc.stroke()

            gc.restore()
        }

    }

}