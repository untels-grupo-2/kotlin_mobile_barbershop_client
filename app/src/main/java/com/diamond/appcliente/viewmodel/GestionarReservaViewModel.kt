package com.diamond.appcliente.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diamond.appcliente.dto.reserva.DtoReserva
import com.diamond.appcliente.repository.ReservaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GestionarReservaViewModel @Inject constructor(
    private val reservaRepository: ReservaRepository
) : ViewModel() {

    sealed class ReservaUiEvent {
        data class Exito(val message: String) : ReservaUiEvent()
        data class Error(val message: String) : ReservaUiEvent()
    }

    private val _evento = MutableSharedFlow<ReservaUiEvent>()
    val evento: SharedFlow<ReservaUiEvent> = _evento.asSharedFlow()

    fun enviarReserva(request: DtoReserva) {
        viewModelScope.launch {
            try {
                _evento.emit(ReservaUiEvent.Exito(reservaRepository.crearReserva(request)))
            } catch (e: Exception) {
                Log.e("GestionarReservaViewModel", "Error al crear reserva", e)
                _evento.emit(ReservaUiEvent.Error("Error al crear la reserva: ${e.message}"))
            }
        }
    }

    fun crearReservaRecompensa(request: DtoReserva) {
        viewModelScope.launch {
            try {
                _evento.emit(ReservaUiEvent.Exito(reservaRepository.crearReservaRecompensa(request)))
            } catch (e: Exception) {
                Log.e("GestionarReservaViewModel", "Error al crear reserva recompensa", e)
                _evento.emit(ReservaUiEvent.Error("Error al crear la reserva: ${e.message}"))
            }
        }
    }
}
