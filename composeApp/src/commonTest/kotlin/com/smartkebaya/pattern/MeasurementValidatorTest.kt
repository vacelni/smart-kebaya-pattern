package com.smartkebaya.pattern

import com.smartkebaya.pattern.model.MeasurementField
import com.smartkebaya.pattern.model.MeasurementValidator
import com.smartkebaya.pattern.model.UnitSystem
import com.smartkebaya.pattern.viewmodel.parseMeasurement
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MeasurementValidatorTest {

    private val valid: Map<MeasurementField, Double?> = MeasurementValidator.exampleCm

    @Test
    fun exampleSizeIsValid() {
        assertEquals(emptyMap(), MeasurementValidator.validate(valid, UnitSystem.CM))
    }

    @Test
    fun everyEmptyFieldIsReported() {
        val errors = MeasurementValidator.validate(valid + (MeasurementField.LINGKAR_LEHER to null), UnitSystem.CM)
        assertEquals(mapOf(MeasurementField.LINGKAR_LEHER to "Wajib diisi."), errors)
    }

    @Test
    fun typoOutsideThePlausibleRangeIsCaught() {
        // 880 instead of 88.0
        val errors = MeasurementValidator.validate(valid + (MeasurementField.LINGKAR_BADAN to 880.0), UnitSystem.CM)
        assertTrue(errors.getValue(MeasurementField.LINGKAR_BADAN).startsWith("Biasanya antara 60.0 dan 170.0 cm"))
    }

    @Test
    fun rangeMessageUsesTheChosenUnit() {
        val errors = MeasurementValidator.validate(valid + (MeasurementField.LINGKAR_BADAN to 880.0), UnitSystem.INCH)
        assertTrue(errors.getValue(MeasurementField.LINGKAR_BADAN).contains(" in."))
    }

    @Test
    fun sideLengthMustBeShorterThanChestLength() {
        val errors = MeasurementValidator.validate(valid + (MeasurementField.PANJANG_SISI to 28.0) + (MeasurementField.PANJANG_DADA to 27.0), UnitSystem.CM)
        assertEquals(setOf(MeasurementField.PANJANG_SISI), errors.keys)
    }

    @Test
    fun kebayaMustReachPastTheHips() {
        val errors = MeasurementValidator.validate(valid + (MeasurementField.PANJANG_KEBAYA to 50.0), UnitSystem.CM)
        // shoulder to waist 36/6 + 2 + 33 = 41, plus max(18, 20) + 2 = 63
        assertEquals("Minimal 63.0 cm agar melewati pinggul.", errors[MeasurementField.PANJANG_KEBAYA])
    }

    @Test
    fun decimalCommaIsAccepted() {
        assertEquals(33.5, parseMeasurement("33,5"))
        assertEquals(33.5, parseMeasurement(" 33.5 "))
        assertEquals(null, parseMeasurement(""))
    }
}
