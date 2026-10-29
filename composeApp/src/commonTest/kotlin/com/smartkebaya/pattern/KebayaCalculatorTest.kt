package com.smartkebaya.pattern

import com.smartkebaya.pattern.model.KebayaCalculator
import com.smartkebaya.pattern.model.MeasurementValidator
import com.smartkebaya.pattern.model.PatternGeometry
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class KebayaCalculatorTest {

    private val sizeM = MeasurementValidator.toInputs(MeasurementValidator.exampleCm)
    private val set = KebayaCalculator.calculateAll(sizeM)

    private fun assertClose(expected: Double, actual: Double, message: String) =
        assertTrue(abs(expected - actual) < 1e-6, "$message: expected $expected, got $actual")

    @Test
    fun frontFollowsTheDraftingRules() {
        val front = set.front
        assertClose(36.0 / 6 + 0.5, front.point("B").x, "A-B neck width")
        assertClose(68.0 / 4 + 4, front.point("L").x, "E-L waist with dart")
        assertClose(33.0 - 17.0, front.point("E").y - front.point("D").y - 17.0, "D-F = D-E - E-F")
        assertTrue(front.point("F").y < front.point("E").y, "the armpit line lies above the waist")
        assertClose(65.0, front.point("N").y, "kebaya length measured from the shoulder line")
        assertClose(front.point("U").x - 1.5, front.point("S").x, "dart apex above the dart's centre")
    }

    @Test
    fun backHemMatchesTheFrontSoTheSideSeamsSewTogether() {
        val frontBelowWaist = set.front.point("N").y - set.front.point("E").y
        val backBelowWaist = set.back.point("R").y - set.back.point("M").y
        assertClose(frontBelowWaist, backBelowWaist, "hem distance below the waist")
    }

    @Test
    fun sleeveCapSeamHasTheArmholeLength() {
        val sleeve = set.sleeve
        val half = 42.0 / 2
        assertClose(half, PatternGeometry.distance(sleeve.point("A"), sleeve.point("C")), "A-C")
        assertClose(half, PatternGeometry.distance(sleeve.point("A"), sleeve.point("D")), "A-D")
        assertTrue(sleeve.points.any { it.id == "E" }, "a 56 cm sleeve gets an elbow line")
    }

    @Test
    fun capCurveRisesAboveTheGuideNearTheTopAndDipsNearTheUnderarm() {
        val sleeve = set.sleeve
        // M is raised away from the sleeve's centre, R hollowed toward it.
        assertTrue(sleeve.point("M").y < sleeve.point("K").y, "M sits higher than the guide line")
        assertTrue(sleeve.point("R").x > sleeve.point("C").x, "R moves toward the centre")
    }

    @Test
    fun everyPathResolvesAndEveryPointIsFinite() {
        set.all.forEach { drawing ->
            drawing.polylines().forEach { (path, line) ->
                assertTrue(line.size >= 2, "${drawing.title} ${path.pointIds}")
                line.forEach { (x, y) -> assertTrue(x.isFinite() && y.isFinite(), "${drawing.title} has a non-finite point") }
            }
        }
    }

    @Test
    fun smoothCurvesPassThroughTheirEndPoints() {
        val neck = set.front.polylines().first { it.first.pointIds == listOf("B", "neck", "D") }.second
        assertEquals(set.front.point("B").let { it.x to it.y }, neck.first())
        val last = neck.last()
        assertClose(set.front.point("D").x, last.first, "neckline ends on D (x)")
        assertClose(set.front.point("D").y, last.second, "neckline ends on D (y)")
    }

    @Test
    fun patternGrowsWithTheBody() {
        val bigger = KebayaCalculator.calculateAll(sizeM.copy(lingkarBadan = 100.0, lingkarPinggul = 110.0))
        assertTrue(bigger.front.point("J").x > set.front.point("J").x)
        assertTrue(bigger.back.point("R").x > set.back.point("R").x)
    }
}
