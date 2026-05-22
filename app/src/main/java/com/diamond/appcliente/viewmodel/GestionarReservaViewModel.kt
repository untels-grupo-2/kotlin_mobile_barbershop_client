package com.diamond.appcliente.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diamond.appcliente.api.AuthApiService
import com.diamond.appcliente.di.AuthenticatedApi
import com.diamond.appcliente.dto.reserva.DtoReserva
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GestionarReservaViewModel @Inject constructor(
    @AuthenticatedApi private val authApiService: AuthApiService
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
                val response = authApiService.crearReserva(request)
                if (response.isSuccessful) {
                    _evento.emit(ReservaUiEvent.Exito("Reserva creada con éxito"))
                } else {
                    val errorMsg = try { response.errorBody()?.string() ?: response.message() } catch (e: Exception) { "Error desconocido" }
                    _evento.emit(ReservaUiEvent.Error("Error al crear la reserva: $errorMsg"))
                }
            } catch (e: Exception) {
                Log.e("GestionarReservaViewModel", "Error de conexión", e)
                _evento.emit(ReservaUiEvent.Error("Fallo en la conexión: ${e.message}"))
            }
        }
    }

    fun crearReservaRecompensa(request: DtoReserva) {
        viewModelScope.launch {
            try {
                val response = authApiService.crearReservaRecompensa(request)
                if (response.isSuccessful) {
                    _evento.emit(ReservaUiEvent.Exito("Reserva con recompensa creada con éxito"))
                } else {
                    val errorMsg = try { response.errorBody()?.string() ?: response.message() } catch (e: Exception) { "Error desconocido" }
                    _evento.emit(ReservaUiEvent.Error("Error al crear la reserva: $errorMsg"))
                }
            } catch (e: Exception) {
                Log.e("GestionarReservaViewModel", "Error de conexión", e)
                _evento.emit(ReservaUiEvent.Error("Fallo en la conexión: ${e.message}"))
            }
        }
    }
}
