package com.diamond.appcliente.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diamond.appcliente.dto.reserva.ReservaResponse
import com.diamond.appcliente.repository.ReservaRepository
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

    private val _reservas = MutableStateFlow<List<ReservaResponse>?>(null)
    val reservas: StateFlow<List<ReservaResponse>?> = _reservas.asStateFlow()

    private val _comprobanteEvento = MutableSharedFlow<String>()
    val comprobanteEvento: SharedFlow<String> = _comprobanteEvento.asSharedFlow()

    private val _error = MutableSharedFlow<String>()
    val error: SharedFlow<String> = _error.asSharedFlow()

    fun cargarReservas() {
        viewModelScope.launch {
            _reservas.value = null
            try {
                _reservas.value = reservaRepository.listarMisReservas()
            } catch (e: Exception) {
                Log.e("ListarReservaViewModel", "Error al cargar reservas", e)
                _reservas.value = emptyList()
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
