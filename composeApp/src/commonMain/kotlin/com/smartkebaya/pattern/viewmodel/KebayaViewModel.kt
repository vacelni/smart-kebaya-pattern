package com.smartkebaya.pattern.viewmodel

import androidx.lifecycle.ViewModel
import com.smartkebaya.pattern.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.round

data class KebayaUiState(
    val rawInputs: Map<MeasurementField, String> = MeasurementField.entries.associateWith { "" },
    val fieldErrors: Map<MeasurementField, String> = emptyMap(),
    val inputs: MeasurementInputs = MeasurementInputs(),
    val unit: UnitSystem = UnitSystem.CM,
    val isDarkTheme: Boolean = false,
    val patternSet: KebayaPatternSet? = null,
    val errorMessage: String? = null,
    val isPrinting: Boolean = false
)

/** Parses a typed measurement; accepts the Indonesian decimal comma ("33,5"). */
internal fun parseMeasurement(raw: String?): Double? = raw?.trim()?.replace(',', '.')?.toDoubleOrNull()

internal fun formatMeasurement(value: Double): String {
    val rounded = round(value * 10) / 10
    return if (rounded % 1.0 == 0.0) rounded.toInt().toString() else rounded.toString()
}

class KebayaViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(KebayaUiState())
    val uiState: StateFlow<KebayaUiState> = _uiState.asStateFlow()

    fun onInputChange(field: MeasurementField, raw: String) {
        val sanitized = raw.filter { it.isDigit() || it == '.' || it == ',' }.take(6)
        _uiState.update { state ->
            state.copy(rawInputs = state.rawInputs + (field to sanitized), fieldErrors = state.fieldErrors - field)
        }
    }

    /** Switches units and converts what was already typed, so the numbers keep their meaning. */
    fun toggleUnit() {
        _uiState.update { state ->
            val next = if (state.unit == UnitSystem.CM) UnitSystem.INCH else UnitSystem.CM
            val factor = if (next == UnitSystem.INCH) 1 / 2.54 else 2.54
            val converted = state.rawInputs.mapValues { (_, raw) -> parseMeasurement(raw)?.let { formatMeasurement(it * factor) } ?: raw }
            state.copy(unit = next, rawInputs = converted, fieldErrors = emptyMap())
        }
    }

    fun toggleTheme() {
        _uiState.update { it.copy(isDarkTheme = !it.isDarkTheme) }
    }

    /** Fills a standard size M so the app can be tried without measuring anyone. */
    fun fillExample() {
        _uiState.update { state ->
            val factor = if (state.unit == UnitSystem.INCH) 1 / 2.54 else 1.0
            state.copy(
                rawInputs = MeasurementValidator.exampleCm.mapValues { (_, cm) -> formatMeasurement(cm * factor) },
                fieldErrors = emptyMap()
            )
        }
    }

    fun generatePattern() {
        val state = _uiState.value
        val toCm = if (state.unit == UnitSystem.INCH) 2.54 else 1.0
        val valuesCm = MeasurementField.entries.associateWith { field -> parseMeasurement(state.rawInputs[field])?.times(toCm) }

        val errors = MeasurementValidator.validate(valuesCm, state.unit)
        if (errors.isNotEmpty()) {
            _uiState.update {
                it.copy(
                    fieldErrors = errors,
                    patternSet = null,
                    errorMessage = "Periksa ${errors.size} ukuran yang ditandai sebelum membuat pola."
                )
            }
            return
        }

        val inputs = MeasurementValidator.toInputs(valuesCm)
        _uiState.update {
            it.copy(inputs = inputs, patternSet = KebayaCalculator.calculateAll(inputs), fieldErrors = emptyMap(), errorMessage = null)
        }
    }

    fun reset() {
        _uiState.update { KebayaUiState(unit = it.unit, isDarkTheme = it.isDarkTheme) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun setPrinting(printing: Boolean) {
        _uiState.update { it.copy(isPrinting = printing) }
    }
}
