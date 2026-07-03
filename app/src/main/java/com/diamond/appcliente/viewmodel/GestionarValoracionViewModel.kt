package com.diamond.appcliente.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diamond.appcliente.dto.valoracion.ValoracionRequest
import com.diamond.appcliente.repository.ValoracionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GestionarValoracionViewModel @Inject constructor(
    private val valoracionRepository: ValoracionRepository
) : ViewModel() {

    sealed class ValoracionUiEvent {
        data class Exito(val message: String) : ValoracionUiEvent()
        data class Error(val message: String) : ValoracionUiEvent()
    }

    private val _evento = MutableSharedFlow<ValoracionUiEvent>()
    val evento: SharedFlow<ValoracionUiEvent> = _evento.asSharedFlow()

    fun submitValoracion(valoracion: Int, utilidad: Boolean?, comentario: String) {
        viewModelScope.launch {
            if (valoracion == -1 || utilidad == null || comentario.isEmpty()) {
                _evento.emit(ValoracionUiEvent.Error("Por favor, complete todos los campos"))
                return@launch
            }
            try {
                _evento.emit(ValoracionUiEvent.Exito(valoracionRepository.crearValoracion(ValoracionRequest(valoracion, utilidad, comentario))))
            } catch (e: Exception) {
                _evento.emit(ValoracionUiEvent.Error(e.message ?: "Error desconocido"))
            }
        }
    }
}
