package com.smartkebaya.pattern.model

import kotlin.math.ceil
import kotlin.math.max

object MeasurementValidator {

    /** Plausible adult measurements in cm. Wide on purpose: they catch typos, not unusual bodies. */
    val rangesCm: Map<MeasurementField, ClosedFloatingPointRange<Double>> = mapOf(
        MeasurementField.LINGKAR_BADAN to 60.0..170.0,
        MeasurementField.LINGKAR_LEHER to 25.0..60.0,
        MeasurementField.LEBAR_BAHU to 8.0..22.0,
        MeasurementField.PANJANG_DADA to 25.0..55.0,
        MeasurementField.LEBAR_DADA to 24.0..55.0,
        MeasurementField.PANJANG_PUNGGUNG to 28.0..55.0,
        MeasurementField.LEBAR_PUNGGUNG to 24.0..55.0,
        MeasurementField.PANJANG_SISI to 10.0..30.0,
        MeasurementField.LINGKAR_PINGGANG to 45.0..150.0,
        MeasurementField.LINGKAR_PINGGUL to 65.0..180.0,
        MeasurementField.TINGGI_PINGGUL to 10.0..30.0,
        MeasurementField.PANJANG_KEBAYA to 45.0..140.0,
        MeasurementField.PANJANG_LENGAN to 10.0..80.0,
        MeasurementField.LINGKAR_KERUNG_LENGAN to 30.0..65.0,
        MeasurementField.LINGKAR_PERGELANGAN to 12.0..40.0,
    )

    /** A standard Indonesian size M, for trying the app without a tape measure. */
    val exampleCm: Map<MeasurementField, Double> = mapOf(
        MeasurementField.LINGKAR_BADAN to 88.0,
        MeasurementField.LINGKAR_LEHER to 36.0,
        MeasurementField.LEBAR_BAHU to 12.0,
        MeasurementField.PANJANG_DADA to 33.0,
        MeasurementField.LEBAR_DADA to 32.0,
        MeasurementField.PANJANG_PUNGGUNG to 37.0,
        MeasurementField.LEBAR_PUNGGUNG to 34.0,
        MeasurementField.PANJANG_SISI to 17.0,
        MeasurementField.LINGKAR_PINGGANG to 68.0,
        MeasurementField.LINGKAR_PINGGUL to 94.0,
        MeasurementField.TINGGI_PINGGUL to 18.0,
        MeasurementField.PANJANG_KEBAYA to 65.0,
        MeasurementField.PANJANG_LENGAN to 56.0,
        MeasurementField.LINGKAR_KERUNG_LENGAN to 42.0,
        MeasurementField.LINGKAR_PERGELANGAN to 18.0,
    )

    /**
     * Checks every measurement (already in cm) and the relations the draft depends on.
     * Returns one message per field that needs fixing; an empty map means the pattern can be drafted.
     */
    fun validate(valuesCm: Map<MeasurementField, Double?>, unit: UnitSystem): Map<MeasurementField, String> {
        val errors = mutableMapOf<MeasurementField, String>()
        val factor = if (unit == UnitSystem.INCH) 1 / 2.54 else 1.0
        val unitLabel = if (unit == UnitSystem.INCH) "in" else "cm"
        fun show(cm: Double) = ((cm * factor) * 10).toInt() / 10.0

        for ((field, range) in rangesCm) {
            val value = valuesCm[field]
            when {
                value == null -> errors[field] = "Wajib diisi."
                value !in range -> errors[field] =
                    "Biasanya antara ${show(range.start)} dan ${show(range.endInclusive)} $unitLabel. Periksa lagi."
            }
        }
        if (errors.isNotEmpty()) return errors

        fun v(field: MeasurementField) = valuesCm.getValue(field)!!

        // The armpit (side length up from the waist) must sit below the neck point.
        if (v(MeasurementField.PANJANG_SISI) >= v(MeasurementField.PANJANG_DADA)) {
            errors[MeasurementField.PANJANG_SISI] = "Harus lebih pendek dari panjang dada."
        }
        // The kebaya has to reach past the hip line on the front and the back.
        val shoulderToWaist = v(MeasurementField.LINGKAR_LEHER) / 6 + 2 + v(MeasurementField.PANJANG_DADA)
        val minLength = shoulderToWaist + max(v(MeasurementField.TINGGI_PINGGUL), 20.0) + 2
        if (v(MeasurementField.PANJANG_KEBAYA) < minLength) {
            errors[MeasurementField.PANJANG_KEBAYA] = "Minimal ${show(ceil(minLength))} $unitLabel agar melewati pinggul."
        }
        return errors
    }

    fun toInputs(valuesCm: Map<MeasurementField, Double?>): MeasurementInputs {
        fun v(field: MeasurementField) = valuesCm[field] ?: 0.0
        return MeasurementInputs(
            lingkarBadan = v(MeasurementField.LINGKAR_BADAN),
            lingkarLeher = v(MeasurementField.LINGKAR_LEHER),
            lebarBahu = v(MeasurementField.LEBAR_BAHU),
            panjangDada = v(MeasurementField.PANJANG_DADA),
            lebarDada = v(MeasurementField.LEBAR_DADA),
            panjangPunggung = v(MeasurementField.PANJANG_PUNGGUNG),
            lebarPunggung = v(MeasurementField.LEBAR_PUNGGUNG),
            panjangSisi = v(MeasurementField.PANJANG_SISI),
            lingkarPinggang = v(MeasurementField.LINGKAR_PINGGANG),
            lingkarPinggul = v(MeasurementField.LINGKAR_PINGGUL),
            tinggiPinggul = v(MeasurementField.TINGGI_PINGGUL),
            panjangKebaya = v(MeasurementField.PANJANG_KEBAYA),
            panjangLengan = v(MeasurementField.PANJANG_LENGAN),
            lingkarKerungLengan = v(MeasurementField.LINGKAR_KERUNG_LENGAN),
            lingkarPergelangan = v(MeasurementField.LINGKAR_PERGELANGAN),
        )
    }
}
