package com.diamond.appcliente.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diamond.appcliente.dto.reserva.ReservaResponse
import com.diamond.appcliente.repository.ReservaRepository
import com.diamond.appcliente.ui.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class ListarReservaViewModel @Inject constructor(
    private val reservaRepository: ReservaRepository
) : ViewModel() {

    private val _reservas = MutableStateFlow<UiState<List<ReservaResponse>>>(UiState.Idle)
    val reservas: StateFlow<UiState<List<ReservaResponse>>> = _reservas.asStateFlow()

    private val _comprobanteEvento = MutableSharedFlow<String>()
    val comprobanteEvento: SharedFlow<String> = _comprobanteEvento.asSharedFlow()

    private val _error = MutableSharedFlow<String>()
    val error: SharedFlow<String> = _error.asSharedFlow()

    fun cargarReservas() {
        viewModelScope.launch {
            _reservas.value = UiState.Loading
            try {
                _reservas.value = UiState.Success(reservaRepository.listarMisReservas())
            } catch (e: Exception) {
                _reservas.value = UiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun subirComprobante(reservaId: Long, imagenPart: MultipartBody.Part) {
        viewModelScope.launch {
            try {
                _comprobanteEvento.emit(reservaRepository.subirComprobante(reservaId, imagenPart))
            } catch (e: Exception) {
                _error.emit(e.message ?: "Error desconocido")
            }
        }
    }
}
