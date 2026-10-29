package com.smartkebaya.pattern.model

object KebayaCalculator {

    fun calculateFront(m: MeasurementInputs): PatternDrawing {
        val ll = m.lingkarLeher
        val lba = m.lebarBahu
        val pd = m.panjangDada
        val ps = m.panjangSisi
        val lpg = m.lingkarPinggang
        val lb = m.lingkarBadan
        val ld = m.lebarDada
        val tpl = m.tinggiPinggul
        val pk = m.panjangKebaya
        val lpl = m.lingkarPinggul

        // Rumus Gambar B
        val ab = (ll / 6.0) + 0.5
        val ad = (ll / 6.0) + 2.0
        val bc = lba
        val ci = 3.5
        val de = pd
        val ef = ps
        val el = (lpg / 4.0) + 1.0 + 3.0
        val fj = (lb / 4.0) + 1.0
        val df = de + ef
        val dg = 0.5 * df
        val gh = ld / 2.0
        val em = tpl
        val en = pk
        val mp = (lpl / 4.0) + 1.0
        val no = mp
        val nq = 4.0
        val er = 13.0
        val rs = 18.0
        val et = 9.0 - 1.5
        val tu = 3.0
        val tv = 12.0

        val values = linkedMapOf(
            "A-B" to ab, "A-D" to ad, "B-C" to bc, "C-I" to ci,
            "D-E" to de, "E-F" to ef, "E-L" to el, "F-J" to fj,
            "D-G" to dg, "G-H" to gh, "E-M" to em, "E-N" to en,
            "M-P" to mp, "N-O" to no, "N-Q" to nq, "E-R" to er,
            "R-S" to rs, "E-T" to et, "T-U" to tu, "T-V" to tv
        )

        return PatternDrawing("Pola Depan", emptyList(), emptyList(), values)
    }

    fun calculateBack(m: MeasurementInputs): PatternDrawing {
        val ll = m.lingkarLeher
        val lba = m.lebarBahu
        val pp = m.panjangPunggung
        val lp = m.lebarPunggung
        val lb = m.lingkarBadan
        val lpg = m.lingkarPinggang
        val lpl = m.lingkarPinggul

        // Rumus Gambar C
        val xn = (ll / 6.0) + 0.5
        val xk = 2.0
        val nd = lba
        val dq = 3.5
        val km = pp
        val mb = 18.0
        val jp = lp / 2.0
        val bt = (lb / 4.0) - 1.0
        val my = (lpg / 4.0) - 1.0 + 3.0
        val mmPrime = lpg / 10.0
        val bPrimeO = 4.0
        val mPrimeOPrime = 12.0
        val mPrimeYPrime = 3.0
        val mc = 2.0
        val ch = 18.0
        val hi = (lpl / 4.0) - 1.0
        val cn = 22.0
        val ku = 0.5

        val values = linkedMapOf(
            "X-N" to xn, "X-K" to xk, "N-D" to nd, "D-Q" to dq,
            "K-M" to km, "M-B" to mb, "J-P" to jp, "B-T" to bt,
            "M-Y" to my, "M-M'" to mmPrime, "B'-O" to bPrimeO,
            "M'-O'" to mPrimeOPrime, "M'-Y'" to mPrimeYPrime,
            "M-C" to mc, "C-H" to ch, "H-I" to hi, "C-N" to cn, "K-U" to ku
        )

        return PatternDrawing("Pola Belakang", emptyList(), emptyList(), values)
    }

    fun calculateSleeve(m: MeasurementInputs): PatternDrawing {
        val pl = m.panjangLengan
        val lkl = m.lingkarKerungLengan
        val lpt = m.lingkarPergelangan

        val af = pl
        val ab = (lkl / 4.0) + 1.5
        val ad = lkl / 2.0
        val ac = lkl / 2.0
        
        // C-K-L-A
        val ck = ac / 3.0
        val kl = ac / 3.0
        val la = ac / 3.0
        
        val r = 0.5
        val mLabel = 2.0
        
        // A-N-O-P-D
        val an = ad / 4.0
        val nLabel = 1.5
        val pLabel = 1.5
        
        val ae = 30.0
        val iej = 30.0
        val fg = lpt / 2.0
        val fh = lpt / 2.0

        val values = linkedMapOf(
            "A-F" to af, "A-B" to ab, "A-D" to ad, "A-C" to ac,
            "M" to mLabel, "N" to nLabel, "P" to pLabel, "R" to r,
            "A-E" to ae, "I-E-J" to iej, "F-G" to fg, "F-H" to fh,
            "G-H" to lpt
        )

        return PatternDrawing("Pola Lengan", emptyList(), emptyList(), values)
    }

    fun calculateKutubaru(m: MeasurementInputs): PatternDrawing {
        val values = linkedMapOf(
            "X-V" to 4.0, "W-V'" to 2.0, "V-V'" to 2.0,
            "X-O" to 25.0, "X-I" to 30.0, "I-U" to 2.0
        )
        return PatternDrawing("Pola Kutubaru", emptyList(), emptyList(), values)
    }

    fun calculateAll(m: MeasurementInputs): KebayaPatternSet = KebayaPatternSet(
        front = calculateFront(m),
        back = calculateBack(m),
        sleeve = calculateSleeve(m),
        kutubaru = calculateKutubaru(m)
    )
}
