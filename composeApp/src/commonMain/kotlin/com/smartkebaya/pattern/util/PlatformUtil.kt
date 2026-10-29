package com.smartkebaya.pattern.util

import com.smartkebaya.pattern.model.KebayaPatternSet
import com.smartkebaya.pattern.model.MeasurementInputs
import com.smartkebaya.pattern.model.UnitSystem

expect object PlatformUtil {
    val isWeb: Boolean
    fun printPattern(patternSet: KebayaPatternSet, inputs: MeasurementInputs, unit: UnitSystem)
    suspend fun downloadGuide()
}
