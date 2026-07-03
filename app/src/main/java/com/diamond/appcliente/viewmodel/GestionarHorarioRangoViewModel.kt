package com.diamond.appcliente.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diamond.appcliente.dto.horariorango.HorarioRangoDto
import com.diamond.appcliente.repository.HorarioRangoRepository
import com.shared.models.ui.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import javax.inject.Inject

@HiltViewModel
class GestionarHorarioRangoViewModel @Inject constructor(
    private val horarioRangoRepository: HorarioRangoRepository
) : ViewModel() {

    private val _horarios = MutableStateFlow<UiState<List<HorarioRangoDto>>>(UiState.Idle)
    val horarios: StateFlow<UiState<List<HorarioRangoDto>>> = _horarios.asStateFlow()

    fun cargarHorarios(tipoHorarioId: Int) {
        viewModelScope.launch {
            _horarios.value = UiState.Loading
            try {
                val raw = horarioRangoRepository.obtenerHorariosRangos(tipoHorarioId)
                _horarios.value = UiState.Success(agruparYOrdenar(raw))
            } catch (e: Exception) {
                _horarios.value = UiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun obtenerTipoHorario(rango: String): String {
        val parts = rango.split(" ")
        var horaInt = parts[0].toIntOrNull() ?: return "MAÑANA"
        val periodo = if (parts.size > 1) parts[1].uppercase() else "AM"
        if (periodo == "PM" && horaInt != 12) horaInt += 12
        else if (periodo == "AM" && horaInt == 12) horaInt = 0
        return when {
            horaInt in 6..12 -> "MAÑANA"
            horaInt in 13..17 -> "TARDE"
            else -> "NOCHE"
        }
    }

    private fun agruparYOrdenar(horarios: List<HorarioRangoDto>): List<HorarioRangoDto> {
        val fmt = SimpleDateFormat("h a", java.util.Locale.US)

        fun parseTime(rango: String?): Long {
            val hora = rango?.split(" - ")?.get(0)?.trim() ?: return Long.MAX_VALUE
            return try {
                val fixed = if (hora == "12 AM") "12 PM" else hora
                fmt.parse(fixed)?.time ?: Long.MAX_VALUE
            } catch (e: Exception) { Long.MAX_VALUE }
        }

        val manana = horarios.filter { it.tipoHorario == "MAÑANA" }.sortedBy { parseTime(it.rango) }
        val tarde  = horarios.filter { it.tipoHorario == "TARDE"  }.sortedBy { parseTime(it.rango) }
        val noche  = horarios.filter { it.tipoHorario == "NOCHE"  }.sortedBy { parseTime(it.rango) }

        val result = mutableListOf<HorarioRangoDto>()
        result.add(HorarioRangoDto(rango = "🌅 Turno mañana", tipoHorario = "Encabezado"))
        result.addAll(manana)
        result.add(HorarioRangoDto(rango = "☀️ Turno tarde", tipoHorario = "Encabezado"))
        result.addAll(tarde)
        result.add(HorarioRangoDto(rango = "🌙 Turno noche", tipoHorario = "Encabezado"))
        result.addAll(noche)
        return result
    }
}
