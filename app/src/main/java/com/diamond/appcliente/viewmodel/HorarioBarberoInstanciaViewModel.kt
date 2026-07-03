package com.diamond.appcliente.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diamond.appcliente.dto.barbero.DtoBarberoDisponible
import com.diamond.appcliente.repository.BarberoRepository
import com.shared.models.ui.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HorarioBarberoInstanciaViewModel @Inject constructor(
    private val barberoRepository: BarberoRepository
) : ViewModel() {

    private val _barberosDisponibles = MutableStateFlow<UiState<List<DtoBarberoDisponible>>>(UiState.Idle)
    val barberosDisponibles: StateFlow<UiState<List<DtoBarberoDisponible>>> = _barberosDisponibles.asStateFlow()

    fun tipoHorarioToId(tipoHorario: String?): Long = when (tipoHorario) {
        "MAÑANA" -> 1L
        "TARDE"  -> 2L
        "NOCHE"  -> 3L
        else     -> -1L
    }

    fun cargarBarberosDisponibles(fecha: String?, tipoHorario: String?, horarioRangoId: Long) {
        val tipoHorarioId = tipoHorarioToId(tipoHorario)
        if (fecha == null || tipoHorarioId == -1L || horarioRangoId == -1L) {
            Log.e("HorarioBarberoInstanciaViewModel", "Datos inválidos: fecha=$fecha tipoHorarioId=$tipoHorarioId horarioRangoId=$horarioRangoId")
            _barberosDisponibles.value = UiState.Error("Datos de reserva inválidos")
            return
        }
        obtenerBarberosDisponibles(fecha, tipoHorarioId, horarioRangoId)
    }

    fun obtenerBarberosDisponibles(fecha: String, tipoHorarioId: Long, horarioRangoId: Long) {
        viewModelScope.launch {
            _barberosDisponibles.value = UiState.Loading
            try {
                _barberosDisponibles.value = UiState.Success(
                    barberoRepository.obtenerBarberosDisponibles(fecha, tipoHorarioId, horarioRangoId)
                )
            } catch (e: Exception) {
                _barberosDisponibles.value = UiState.Error(e.message ?: "Error desconocido")
            }
        }
    }
}
