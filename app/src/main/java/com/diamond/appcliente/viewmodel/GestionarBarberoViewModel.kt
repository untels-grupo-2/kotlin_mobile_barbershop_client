package com.diamond.appcliente.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diamond.appcliente.api.AuthApiService
import com.diamond.appcliente.di.AuthenticatedApi
import com.diamond.appcliente.dto.barbero.BarberoDto
import com.diamond.appcliente.dto.barbero.BarberoRequest
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
class GestionarBarberoViewModel @Inject constructor(
    @AuthenticatedApi private val authApiService: AuthApiService
) : ViewModel() {

    private val _barberos = MutableStateFlow<List<BarberoDto>>(emptyList())
    val barberos: StateFlow<List<BarberoDto>> = _barberos.asStateFlow()

    private val _mensaje = MutableSharedFlow<String>()
    val mensaje: SharedFlow<String> = _mensaje.asSharedFlow()

    private val _error = MutableSharedFlow<String>()
    val error: SharedFlow<String> = _error.asSharedFlow()

    fun cargarBarberos() {
        viewModelScope.launch {
            try {
                val response = authApiService.listarBarberos()
                if (response.isSuccessful && response.body() != null) {
                    _barberos.value = response.body()!!.data ?: emptyList()
                } else {
                    _error.emit("Error al obtener barberos")
                }
            } catch (e: Exception) {
                _error.emit(e.message ?: "Error desconocido")
            }
        }
    }

    fun crearBarbero(nombre: String) {
        viewModelScope.launch {
            try {
                val response = authApiService.crearBarbero(BarberoRequest(nombre))
                if (response.isSuccessful && response.body() != null) {
                    _mensaje.emit(response.body()!!.message ?: "Barbero creado")
                    cargarBarberos()
                } else {
                    _error.emit("Error al crear barbero")
                }
            } catch (e: Exception) {
                _error.emit(e.message ?: "Error desconocido")
            }
        }
    }

    fun actualizarBarbero(id: Int, nuevoNombre: String) {
        viewModelScope.launch {
            try {
                val response = authApiService.actualizarBarbero(id, BarberoRequest(nuevoNombre))
                if (response.isSuccessful && response.body() != null) {
                    _mensaje.emit(response.body()!!.message ?: "Barbero actualizado")
                    cargarBarberos()
                } else {
                    _error.emit("Error al actualizar barbero")
                }
            } catch (e: Exception) {
                _error.emit(e.message ?: "Error desconocido")
            }
        }
    }

    fun eliminarBarbero(id: Int) {
        viewModelScope.launch {
            try {
                val response = authApiService.eliminarBarbero(id)
                if (response.isSuccessful && response.body() != null) {
                    _mensaje.emit(response.body()!!.message ?: "Barbero eliminado")
                    cargarBarberos()
                } else {
                    _error.emit("Error al eliminar barbero")
                }
            } catch (e: Exception) {
                _error.emit(e.message ?: "Error desconocido")
            }
        }
    }
}
