package com.smartkebaya.pattern.model

enum class UnitSystem { CM, INCH }

enum class MeasurementField(val label: String, val abbr: String, val guide: String) {
    LINGKAR_BADAN("Lingkar Badan", "LB", "Ukur sekeliling badan yang terbesar melalui puncak dada, diambil angka pertemuan pita ukur."),
    LINGKAR_LEHER("Lingkar Leher", "LL", "Ukur sekeliling batas leher, dengan meletakkan jari telunjuk pada lekuk leher."),
    LEBAR_BAHU("Lebar Bahu", "LBA", "Ukur dari titik bahu tertinggi sampai titik bahu terendah."),
    PANJANG_DADA("Panjang Dada", "PD", "Ukur dari lekuk leher ke bawah sampai batas pinggang."),
    LEBAR_DADA("Lebar Dada", "LD", "Ukur dari batas ketiak kiri sampai ketiak kanan, sekitar 5 cm di bawah lekuk leher."),
    PANJANG_PUNGGUNG("Panjang Punggung", "PP", "Ukur dari tulang leher yang menonjol di belakang ke bawah sampai batas pinggang."),
    LEBAR_PUNGGUNG("Lebar Punggung", "LP", "Ukur dari batas ketiak kiri belakang sampai ketiak kanan belakang."),
    PANJANG_SISI("Panjang Sisi", "PS", "Ukur dari batas ketiak ke bawah sampai batas pinggang."),
    LINGKAR_PINGGANG("Lingkar Pinggang", "LPG", "Ukur sekeliling pinggang yang terkecil."),
    LINGKAR_PINGGUL("Lingkar Pinggul", "LPL", "Ukur sekeliling pinggul yang terbesar."),
    TINGGI_PINGGUL("Tinggi Pinggul", "TPL", "Ukur dari batas pinggang sampai batas pinggul yang terbesar di samping."),
    PANJANG_KEBAYA("Panjang Kebaya", "PK", "Ukur dari bahu tertinggi ke bawah sampai panjang yang diinginkan."),
    PANJANG_LENGAN("Panjang Lengan", "PL", "Ukur dari titik bahu terendah sampai panjang lengan yang diinginkan."),
    LINGKAR_KERUNG_LENGAN("Lingkar Kerung Lengan", "LKL", "Ukur sekeliling lubang lengan, dari titik bahu melalui ketiak kembali ke titik bahu."),
    LINGKAR_PERGELANGAN("Lingkar Pergelangan", "LPR", "Ukur sekeliling pergelangan tangan sesuai dengan ukuran yang diinginkan.")
}

data class MeasurementInputs(
    val lingkarBadan: Double = 0.0,
    val lingkarLeher: Double = 0.0,
    val lebarBahu: Double = 0.0,
    val panjangDada: Double = 0.0,
    val lebarDada: Double = 0.0,
    val panjangPunggung: Double = 0.0,
    val lebarPunggung: Double = 0.0,
    val panjangSisi: Double = 0.0,
    val lingkarPinggang: Double = 0.0,
    val lingkarPinggul: Double = 0.0,
    val tinggiPinggul: Double = 0.0,
    val panjangKebaya: Double = 0.0,
    val panjangLengan: Double = 0.0,
    val lingkarKerungLengan: Double = 0.0,
    val lingkarPergelangan: Double = 0.0
)

data class PatternPoint(val id: String, val label: String, val xCm: Float, val yCm: Float)

sealed class PatternSegment {
    data class Line(val from: String, val to: String) : PatternSegment()
    data class Curve(val from: String, val to: String, val bulge: Float = 0.25f) : PatternSegment()
}

data class PatternDrawing(
    val title: String,
    val points: List<PatternPoint>,
    val segments: List<PatternSegment>,
    val allValues: LinkedHashMap<String, Double>
)

data class KebayaPatternSet(
    val front: PatternDrawing,
    val back: PatternDrawing,
    val sleeve: PatternDrawing,
    val kutubaru: PatternDrawing
)
