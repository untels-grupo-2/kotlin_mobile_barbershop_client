package com.diamond.appcliente.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diamond.appcliente.dto.horariorango.HorarioRangoDto
import com.diamond.appcliente.repository.HorarioRangoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GestionarHorarioRangoViewModel @Inject constructor(
    private val horarioRangoRepository: HorarioRangoRepository
) : ViewModel() {

    private val _horarios = MutableStateFlow<List<HorarioRangoDto>>(emptyList())
    val horarios: StateFlow<List<HorarioRangoDto>> = _horarios.asStateFlow()

    private val _error = MutableSharedFlow<String>()
    val error: SharedFlow<String> = _error.asSharedFlow()

    fun cargarHorarios(tipoHorarioId: Int) {
        viewModelScope.launch {
            try {
                _horarios.value = horarioRangoRepository.obtenerHorariosRangos(tipoHorarioId)
            } catch (e: Exception) {
                _error.emit(e.message ?: "Error desconocido")
            }
        }
    }
}
