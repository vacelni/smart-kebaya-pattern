package com.smartkebaya.pattern.viewmodel

import androidx.lifecycle.ViewModel
import com.smartkebaya.pattern.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class KebayaUiState(
    val rawInputs: Map<MeasurementField, String> = MeasurementField.entries.associateWith { "" },
    val inputs: MeasurementInputs = MeasurementInputs(),
    val unit: UnitSystem = UnitSystem.CM,
    val isDarkTheme: Boolean = false,
    val patternSet: KebayaPatternSet? = null,
    val errorMessage: String? = null,
    val isPrinting: Boolean = false
)

class KebayaViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(KebayaUiState())
    val uiState: StateFlow<KebayaUiState> = _uiState.asStateFlow()

    fun onInputChange(field: MeasurementField, raw: String) {
        val sanitized = raw.filter { it.isDigit() || it == '.' || it == '-' }
        _uiState.update { state -> state.copy(rawInputs = state.rawInputs + (field to sanitized)) }
    }

    fun toggleUnit() {
        _uiState.update { it.copy(unit = if (it.unit == UnitSystem.CM) UnitSystem.INCH else UnitSystem.CM) }
    }

    fun toggleTheme() {
        _uiState.update { it.copy(isDarkTheme = !it.isDarkTheme) }
    }

    fun generatePattern() {
        val state = _uiState.value
        val toCm: (Double) -> Double = { if (state.unit == UnitSystem.INCH) it * 2.54 else it }

        fun v(field: MeasurementField) = toCm(state.rawInputs[field]?.toDoubleOrNull() ?: 0.0)

        val inputs = MeasurementInputs(
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
            lingkarPergelangan = v(MeasurementField.LINGKAR_PERGELANGAN)
        )

        val hasAnyValue = MeasurementField.entries.any { (state.rawInputs[it]?.toDoubleOrNull() ?: 0.0) > 0.0 }

        if (!hasAnyValue) {
            _uiState.update { it.copy(errorMessage = "Isi minimal satu ukuran badan sebelum generate pola.") }
            return
        }

        _uiState.update {
            it.copy(inputs = inputs, patternSet = KebayaCalculator.calculateAll(inputs), errorMessage = null)
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
