package com.diamond.appcliente.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diamond.appcliente.api.AuthApiService
import com.diamond.appcliente.di.AuthenticatedApi
import com.diamond.appcliente.dto.reserva.ReservaResponse
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
    @AuthenticatedApi private val apiService: AuthApiService
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
            Log.d("ListarReservaViewModel", "Cargando reservas...")
            try {
                val response = apiService.listarMisReservas()
                Log.d("ListarReservaViewModel", "Respuesta: ${response.code()}")
                if (response.isSuccessful && response.body() != null) {
                    _reservas.value = response.body()!!.data ?: emptyList()
                } else {
                    Log.d("ListarReservaViewModel", "Sin reservas o error en respuesta")
                    _reservas.value = emptyList()
                }
            } catch (e: Exception) {
                Log.e("ListarReservaViewModel", "Error en la llamada a la API: ", e)
                _reservas.value = emptyList()
            }
        }
    }

    fun subirComprobante(reservaId: Long, imagenPart: MultipartBody.Part) {
        viewModelScope.launch {
            try {
                val response = apiService.subirComprobante(reservaId, imagenPart)
                if (response.isSuccessful) {
                    _comprobanteEvento.emit("Comprobante subido correctamente.")
                } else {
                    _error.emit("Error al subir el comprobante.")
                }
            } catch (e: Exception) {
                _error.emit(e.message ?: "Error desconocido")
            }
        }
    }
}
