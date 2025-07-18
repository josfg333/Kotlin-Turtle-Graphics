package josfg333.projects.turtle

import kotlin.math.sqrt

data class Vec2(val x: Double = 0.0, val y: Double = 0.0) {
    fun length() = sqrt(x*x+y*y)

    operator fun plus(other: Vec2) = Vec2(x+other.x, y+other.y)
    operator fun minus(other: Vec2) = Vec2(x-other.x, y-other.y)

    operator fun times(scalar: Double) = Vec2(x*scalar, y*scalar)
}
operator fun Double.times(vec: Vec2) = vec*this