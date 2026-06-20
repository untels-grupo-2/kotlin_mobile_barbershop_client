package com.diamond.appcliente.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diamond.appcliente.dto.horariorango.HorarioRangoDto
import com.diamond.appcliente.repository.HorarioRangoRepository
import com.diamond.appcliente.ui.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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
                _horarios.value = UiState.Success(horarioRangoRepository.obtenerHorariosRangos(tipoHorarioId))
            } catch (e: Exception) {
                _horarios.value = UiState.Error(e.message ?: "Error desconocido")
            }
        }
    }
}
