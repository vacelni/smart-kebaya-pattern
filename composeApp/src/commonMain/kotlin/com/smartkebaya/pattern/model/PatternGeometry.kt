package com.smartkebaya.pattern.model

import kotlin.math.hypot

object PatternGeometry {

    /**
     * Samples a uniform Catmull-Rom spline through every point, so a curve such as
     * an armhole passes exactly through the drafting points instead of only near them.
     */
    fun catmullRom(points: List<Pair<Double, Double>>, samplesPerSpan: Int = 16): List<Pair<Double, Double>> {
        if (points.size < 3) return points
        val padded = listOf(reflect(points[1], points[0])) + points + listOf(reflect(points[points.size - 2], points.last()))
        val out = mutableListOf(points.first())
        for (i in 1 until padded.size - 2) {
            val (p0, p1, p2, p3) = listOf(padded[i - 1], padded[i], padded[i + 1], padded[i + 2])
            for (s in 1..samplesPerSpan) {
                val t = s.toDouble() / samplesPerSpan
                val t2 = t * t
                val t3 = t2 * t
                fun axis(a: Double, b: Double, c: Double, d: Double) =
                    0.5 * (2 * b + (-a + c) * t + (2 * a - 5 * b + 4 * c - d) * t2 + (-a + 3 * b - 3 * c + d) * t3)
                out += axis(p0.first, p1.first, p2.first, p3.first) to axis(p0.second, p1.second, p2.second, p3.second)
            }
        }
        return out
    }

    /** The point mirrored through [pivot], used as a phantom end point so the curve starts on its end. */
    private fun reflect(p: Pair<Double, Double>, pivot: Pair<Double, Double>) =
        (2 * pivot.first - p.first) to (2 * pivot.second - p.second)

    fun distance(a: PatternPoint, b: PatternPoint) = hypot(a.x - b.x, a.y - b.y)

    /** The point [t] of the way from [a] to [b], moved [offset] cm along the left-hand normal of a→b. */
    fun along(id: String, a: PatternPoint, b: PatternPoint, t: Double, offset: Double = 0.0): PatternPoint {
        val dx = b.x - a.x
        val dy = b.y - a.y
        val length = hypot(dx, dy)
        val nx = -dy / length
        val ny = dx / length
        return PatternPoint(id, a.x + dx * t + nx * offset, a.y + dy * t + ny * offset)
    }

    /** The point on segment a→b at height [y]. */
    fun atHeight(id: String, a: PatternPoint, b: PatternPoint, y: Double): PatternPoint {
        val t = (y - a.y) / (b.y - a.y)
        return PatternPoint(id, a.x + (b.x - a.x) * t, y)
    }
}
