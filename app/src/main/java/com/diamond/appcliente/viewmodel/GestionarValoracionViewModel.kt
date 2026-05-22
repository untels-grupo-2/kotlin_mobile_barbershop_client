package com.diamond.appcliente.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diamond.appcliente.api.AuthApiService
import com.diamond.appcliente.di.AuthenticatedApi
import com.diamond.appcliente.dto.valoracion.ValoracionRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GestionarValoracionViewModel @Inject constructor(
    @AuthenticatedApi private val authApiService: AuthApiService
) : ViewModel() {

    sealed class ValoracionUiEvent {
        data class Exito(val message: String) : ValoracionUiEvent()
        data class Error(val message: String) : ValoracionUiEvent()
    }

    private val _evento = MutableSharedFlow<ValoracionUiEvent>()
    val evento: SharedFlow<ValoracionUiEvent> = _evento.asSharedFlow()

    fun enviarValoracion(request: ValoracionRequest) {
        viewModelScope.launch {
            try {
                val response = authApiService.crearValoracion(request)
                if (response.isSuccessful) {
                    _evento.emit(ValoracionUiEvent.Exito("Valoración enviada con éxito"))
                } else {
                    _evento.emit(ValoracionUiEvent.Error("Error al enviar la valoración"))
                }
            } catch (e: Exception) {
                _evento.emit(ValoracionUiEvent.Error(e.message ?: "Error desconocido"))
            }
        }
    }
}
