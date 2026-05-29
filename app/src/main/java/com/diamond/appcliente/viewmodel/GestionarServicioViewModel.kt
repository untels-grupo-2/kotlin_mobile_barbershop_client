package com.diamond.appcliente.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diamond.appcliente.dto.servicio.ServicioDto
import com.diamond.appcliente.dto.servicio.ServicioRequest
import com.diamond.appcliente.repository.ServicioRepository
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
class GestionarServicioViewModel @Inject constructor(
    private val servicioRepository: ServicioRepository
) : ViewModel() {

    private val _servicios = MutableStateFlow<List<ServicioDto>>(emptyList())
    val servicios: StateFlow<List<ServicioDto>> = _servicios.asStateFlow()

    private val _mensaje = MutableSharedFlow<String>()
    val mensaje: SharedFlow<String> = _mensaje.asSharedFlow()

    private val _error = MutableSharedFlow<String>()
    val error: SharedFlow<String> = _error.asSharedFlow()

    fun cargarServicios() {
        viewModelScope.launch {
            try {
                _servicios.value = servicioRepository.listarServicios()
            } catch (e: Exception) {
                _error.emit(e.message ?: "Error desconocido")
            }
        }
    }

    fun crearServicio(request: ServicioRequest) {
        viewModelScope.launch {
            try {
                _mensaje.emit(servicioRepository.crearServicio(request))
                cargarServicios()
            } catch (e: Exception) {
                _error.emit(e.message ?: "Error desconocido")
            }
        }
    }

    fun actualizarServicio(id: Int, request: ServicioRequest) {
        viewModelScope.launch {
            try {
                _mensaje.emit(servicioRepository.actualizarServicio(id, request))
                cargarServicios()
            } catch (e: Exception) {
                _error.emit(e.message ?: "Error desconocido")
            }
        }
    }

    fun eliminarServicio(id: Int) {
        viewModelScope.launch {
            try {
                _mensaje.emit(servicioRepository.eliminarServicio(id))
                cargarServicios()
            } catch (e: Exception) {
                _error.emit(e.message ?: "Error desconocido")
            }
        }
    }
}
