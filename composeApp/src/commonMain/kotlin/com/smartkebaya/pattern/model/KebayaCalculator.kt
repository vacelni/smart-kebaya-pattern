package com.smartkebaya.pattern.model

import com.smartkebaya.pattern.model.PatternGeometry.along
import com.smartkebaya.pattern.model.PatternGeometry.atHeight
import kotlin.math.sqrt

/**
 * Drafts the four kebaya pieces from body measurements (all in cm).
 *
 * Coordinates: x is the distance from the centre line (centre front, centre back or the sleeve's
 * centre), y runs downward from the shoulder line. Letters follow the reference drafts (Gambar B for
 * the front, C for the back), so a tailor can check every point against the module.
 *
 * Where the module's numbers and its own drawing disagreed, the drawing was followed:
 *  - front: D-F = D-E − E-F (F, the armpit line, lies above the waist E);
 *  - front: the length N is measured from the shoulder line, as the measuring guide says;
 *  - front: the dart apex S sits above the dart's centre;
 *  - back: the hem sits as far below the waist as the front's, so the side seams sew together;
 *  - kutubaru: the measurements printed on its drawing.
 */
object KebayaCalculator {

    private fun p(id: String, x: Double, y: Double) = PatternPoint(id, x, y)

    fun calculateFront(m: MeasurementInputs): PatternDrawing {
        val ab = m.lingkarLeher / 6 + 0.5
        val ad = m.lingkarLeher / 6 + 2
        val ci = 3.5
        val ef = m.panjangSisi
        val df = m.panjangDada - ef
        val el = m.lingkarPinggang / 4 + 1 + 3 // +3 is the dart's intake
        val fj = m.lingkarBadan / 4 + 1
        val mp = m.lingkarPinggul / 4 + 1
        val nq = 4.0
        val er = 13.0
        val et = 9.0 - 1.5
        val tu = 3.0
        val tv = 12.0

        val a = p("A", 0.0, 0.0)
        val b = p("B", ab, 0.0)
        val c = p("C", ab + m.lebarBahu, 0.0)
        val i = p("I", c.x, ci)
        val d = p("D", 0.0, ad)
        val e = p("E", 0.0, ad + m.panjangDada)
        val f = p("F", 0.0, e.y - ef)
        val g = p("G", 0.0, d.y + df / 2)
        val h = p("H", m.lebarDada / 2, g.y)
        val j = p("J", fj, f.y)
        val l = p("L", el, e.y)
        val mm = p("M", 0.0, e.y + m.tinggiPinggul)
        val pp = p("P", mp, mm.y)
        val n = p("N", 0.0, m.panjangKebaya)
        val o = p("O", mp, n.y)
        val q = p("Q", 0.0, n.y + nq)
        val dartCentre = et + tu / 2
        val t = p("T", et, e.y)
        val u = p("U", et + tu, e.y)
        val s = p("S", dartCentre, e.y - er)
        val r = p("R", 0.0, s.y)
        val v = p("V", dartCentre, e.y + tv)
        // The neckline bows toward the corner of its box; the armhole hugs the corner at the armpit.
        val neck = p("neck", ab * 0.6, ad * 0.6)
        val armpit = p("armpit", h.x + (j.x - h.x) * 0.3, f.y - (f.y - g.y) * 0.25)

        val values = linkedMapOf(
            "A-B" to ab, "A-D" to ad, "B-C" to m.lebarBahu, "C-I" to ci,
            "D-E" to m.panjangDada, "E-F" to ef, "D-F" to df, "D-G" to df / 2, "G-H" to h.x,
            "F-J" to fj, "E-L" to el, "E-M" to m.tinggiPinggul, "M-P" to mp,
            "E-N" to n.y - e.y, "N-O" to mp, "N-Q" to nq,
            "E-R" to er, "R-S" to dartCentre, "E-T" to et, "T-U" to tu, "T-V" to tv
        )
        return PatternDrawing(
            "Pola Depan",
            listOf(a, b, c, i, d, e, f, g, h, j, l, mm, pp, n, o, q, t, u, s, r, v, neck, armpit),
            listOf(
                PatternPath(listOf("A", "C"), Stroke.CONSTRUCTION),
                PatternPath(listOf("G", "H"), Stroke.CONSTRUCTION),
                PatternPath(listOf("F", "J"), Stroke.CONSTRUCTION),
                PatternPath(listOf("E", "L"), Stroke.CONSTRUCTION),
                PatternPath(listOf("M", "P"), Stroke.CONSTRUCTION),
                PatternPath(listOf("N", "O"), Stroke.CONSTRUCTION),
                PatternPath(listOf("R", "S"), Stroke.CONSTRUCTION),
                PatternPath(listOf("B", "neck", "D"), Stroke.OUTLINE, smooth = true),
                PatternPath(listOf("D", "Q", "O", "P", "L", "J"), Stroke.OUTLINE),
                PatternPath(listOf("J", "armpit", "H", "I"), Stroke.OUTLINE, smooth = true),
                PatternPath(listOf("I", "B"), Stroke.OUTLINE),
                PatternPath(listOf("S", "U", "V", "T", "S"), Stroke.DART),
            ),
            values
        )
    }

    fun calculateBack(m: MeasurementInputs, hemBelowWaist: Double): PatternDrawing {
        val xn = m.lingkarLeher / 6 + 0.5
        val xk = 2.0
        val dq = 3.5
        val mb = 18.0
        val bt = m.lingkarBadan / 4 - 1
        val my = m.lingkarPinggang / 4 - 1 + 3 // +3 is the dart's intake
        val mmPrime = m.lingkarPinggang / 10
        val dartWidth = 3.0
        val bPrimeO = 4.0
        val mPrimeOPrime = 12.0
        val mc = 2.0
        val ch = 18.0
        val hi = m.lingkarPinggul / 4 - 1
        val ku = 0.5

        val x = p("X", 0.0, 0.0)
        val n = p("N", xn, 0.0)
        val k = p("K", 0.0, xk)
        val d = p("D", xn + m.lebarBahu, 0.0)
        val q = p("Q", d.x, dq)
        val mm = p("M", 0.0, xk + m.panjangPunggung)
        val b = p("B", 0.0, mm.y - mb)
        val j = p("J", 0.0, (k.y + b.y) / 2)
        val pp = p("P", m.lebarPunggung / 2, j.y)
        val t = p("T", bt, b.y)
        val y = p("Y", my, mm.y)
        val dartCentre = mmPrime + dartWidth / 2
        val mPrime = p("M'", mmPrime, mm.y)
        val yPrime = p("Y'", mmPrime + dartWidth, mm.y)
        val bPrime = p("B'", dartCentre, b.y)
        val o = p("O", dartCentre, b.y + bPrimeO)
        val oPrime = p("O'", dartCentre, mm.y + mPrimeOPrime)
        val c = p("C", 0.0, mm.y + mc)
        val h = p("H", 0.0, c.y + ch)
        val i = p("I", hi, h.y)
        val hemY = mm.y + hemBelowWaist
        val rr = p("R", hi, hemY)
        val s = p("S", 0.0, hemY + ku)
        val neck = p("neck", xn * 0.55, xk * 0.85)
        val armpit = p("armpit", pp.x + (t.x - pp.x) * 0.35, b.y - (b.y - j.y) * 0.3)

        val values = linkedMapOf(
            "X-N" to xn, "X-K" to xk, "N-D" to m.lebarBahu, "D-Q" to dq,
            "K-M" to m.panjangPunggung, "M-B" to mb, "J-P" to pp.x, "B-T" to bt,
            "M-Y" to my, "M-M'" to mmPrime, "M'-Y'" to dartWidth, "B'-O" to bPrimeO, "M'-O'" to mPrimeOPrime,
            "M-C" to mc, "C-H" to ch, "H-I" to hi, "C-S" to s.y - c.y, "R-S" to ku
        )
        return PatternDrawing(
            "Pola Belakang",
            listOf(x, n, k, d, q, mm, b, j, pp, t, y, mPrime, yPrime, bPrime, o, oPrime, c, h, i, rr, s, neck, armpit),
            listOf(
                PatternPath(listOf("X", "D"), Stroke.CONSTRUCTION),
                PatternPath(listOf("X", "K"), Stroke.CONSTRUCTION),
                PatternPath(listOf("J", "P"), Stroke.CONSTRUCTION),
                PatternPath(listOf("B", "T"), Stroke.CONSTRUCTION),
                PatternPath(listOf("M", "Y"), Stroke.CONSTRUCTION),
                PatternPath(listOf("H", "I"), Stroke.CONSTRUCTION),
                PatternPath(listOf("B'", "O"), Stroke.CONSTRUCTION),
                PatternPath(listOf("N", "neck", "K"), Stroke.OUTLINE, smooth = true),
                PatternPath(listOf("K", "S", "R", "I", "Y", "T"), Stroke.OUTLINE),
                PatternPath(listOf("T", "armpit", "P", "Q"), Stroke.OUTLINE, smooth = true),
                PatternPath(listOf("Q", "N"), Stroke.OUTLINE),
                PatternPath(listOf("O", "Y'", "O'", "M'", "O"), Stroke.DART),
            ),
            values
        )
    }

    fun calculateSleeve(m: MeasurementInputs): PatternDrawing {
        val ab = m.lingkarKerungLengan / 4 + 1.5 // sleeve cap height
        val ac = m.lingkarKerungLengan / 2 // each half of the cap seam, measured on the slant
        val half = sqrt(ac * ac - ab * ab)
        val elbow = 30.0

        val a = p("A", 0.0, 0.0)
        val b = p("B", 0.0, ab)
        val c = p("C", -half, ab)
        val d = p("D", half, ab)
        val f = p("F", 0.0, m.panjangLengan)
        val g = p("G", -m.lingkarPergelangan / 2, f.y)
        val h = p("H", m.lingkarPergelangan / 2, f.y)

        // Offsets are perpendicular to the guide line: positive toward the sleeve's centre B, negative away.
        // Back half of the cap (C side): hollow 0.5 near the underarm, raised 2 near the top.
        val k = along("K", c, a, 1.0 / 3)
        val r = along("R", c, a, 1.0 / 6, offset = 0.5)
        val mm = along("M", c, a, 2.0 / 3, offset = -2.0)
        // Front half (D side): raised 1.5 near the top, hollow 1.5 near the underarm.
        val nn = along("N", a, d, 1.0 / 4, offset = -1.5)
        val o = along("O", a, d, 1.0 / 2)
        val pp = along("P", a, d, 3.0 / 4, offset = 1.5)

        val points = mutableListOf(a, b, c, d, f, g, h, k, r, mm, nn, o, pp)
        val paths = mutableListOf(
            PatternPath(listOf("C", "D"), Stroke.CONSTRUCTION),
            PatternPath(listOf("A", "F"), Stroke.CONSTRUCTION),
            PatternPath(listOf("C", "A", "D"), Stroke.CONSTRUCTION),
            PatternPath(listOf("C", "R", "K", "M", "A"), Stroke.OUTLINE, smooth = true),
            PatternPath(listOf("A", "N", "O", "P", "D"), Stroke.OUTLINE, smooth = true),
            PatternPath(listOf("D", "H", "G", "C"), Stroke.OUTLINE),
        )
        val values = linkedMapOf("A-B" to ab, "A-C" to ac, "A-D" to ac, "C-D" to 2 * half, "A-F" to m.panjangLengan)
        // Long sleeves get an elbow line.
        if (m.panjangLengan > elbow + 4) {
            points += listOf(p("E", 0.0, elbow), atHeight("I", c, g, elbow), atHeight("J", d, h, elbow))
            paths += PatternPath(listOf("I", "J"), Stroke.CONSTRUCTION)
            values["A-E"] = elbow
        }
        values += linkedMapOf("F-G" to -g.x, "F-H" to h.x, "M" to 2.0, "N" to 1.5, "P" to 1.5, "R" to 0.5)
        return PatternDrawing("Pola Lengan", points, paths, values)
    }

    /** The front opening's facing. Fixed size: it does not depend on the body measurements. */
    fun calculateKutubaru(): PatternDrawing {
        val xw = 7.0
        val wv = 2.0
        val vv = 2.0
        val xo = 25.0
        val xi = 40.0
        val iu = 6.0
        val points = listOf(
            p("X", 0.0, 0.0), p("W", -xw, 0.0), p("W2", xw, 0.0),
            p("V'", -xw, wv), p("V'2", xw, wv), p("V", -(xw - vv), wv), p("V2", xw - vv, wv),
            p("O", 0.0, xo), p("I", 0.0, xi), p("U", -iu, xi), p("U2", iu, xi),
        )
        return PatternDrawing(
            "Pola Kutubaru",
            points,
            listOf(
                PatternPath(listOf("V'", "V'2"), Stroke.CONSTRUCTION),
                PatternPath(listOf("X", "I"), Stroke.CONSTRUCTION),
                PatternPath(listOf("W", "W2", "U2", "U", "W"), Stroke.OUTLINE),
                PatternPath(listOf("V", "O", "V2"), Stroke.DART),
            ),
            linkedMapOf("X-W" to xw, "W-V'" to wv, "V-V'" to vv, "X-O" to xo, "X-I" to xi, "I-U" to iu)
        )
    }

    fun calculateAll(m: MeasurementInputs): KebayaPatternSet {
        val front = calculateFront(m)
        val hemBelowWaist = front.point("N").y - front.point("E").y
        return KebayaPatternSet(front, calculateBack(m, hemBelowWaist), calculateSleeve(m), calculateKutubaru())
    }
}
