package com.diamond.appcliente.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diamond.appcliente.dto.barbero.DtoBarberoDisponible
import com.diamond.appcliente.repository.BarberoRepository
import com.diamond.appcliente.ui.state.UiState
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
