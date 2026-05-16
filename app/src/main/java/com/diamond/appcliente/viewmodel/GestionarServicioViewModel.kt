package com.diamond.appcliente.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diamond.appcliente.api.AuthApiService
import com.diamond.appcliente.di.AuthenticatedApi
import com.diamond.appcliente.dto.servicio.ServicioDto
import com.diamond.appcliente.dto.servicio.ServicioRequest
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
    @AuthenticatedApi private val authApiService: AuthApiService
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
                val response = authApiService.listarServicios()
                if (response.isSuccessful && response.body() != null) {
                    _servicios.value = response.body()!!.data ?: emptyList()
                } else {
                    _error.emit("Error al obtener servicios")
                }
            } catch (e: Exception) {
                _error.emit(e.message ?: "Error desconocido")
            }
        }
    }

    fun crearServicio(request: ServicioRequest) {
        viewModelScope.launch {
            try {
                val response = authApiService.crearServicio(request)
                if (response.isSuccessful && response.body() != null) {
                    _mensaje.emit(response.body()!!.message ?: "Servicio creado")
                    cargarServicios()
                } else {
                    _error.emit("Error al crear servicio")
                }
            } catch (e: Exception) {
                _error.emit(e.message ?: "Error desconocido")
            }
        }
    }

    fun actualizarServicio(id: Int, request: ServicioRequest) {
        viewModelScope.launch {
            try {
                val response = authApiService.actualizarServicio(id, request)
                if (response.isSuccessful && response.body() != null) {
                    _mensaje.emit(response.body()!!.message ?: "Servicio actualizado")
                    cargarServicios()
                } else {
                    _error.emit("Error al actualizar servicio")
                }
            } catch (e: Exception) {
                _error.emit(e.message ?: "Error desconocido")
            }
        }
    }

    fun eliminarServicio(id: Int) {
        viewModelScope.launch {
            try {
                val response = authApiService.eliminarServicio(id)
                if (response.isSuccessful && response.body() != null) {
                    _mensaje.emit(response.body()!!.message ?: "Servicio eliminado")
                    cargarServicios()
                } else {
                    _error.emit("Error al eliminar servicio")
                }
            } catch (e: Exception) {
                _error.emit(e.message ?: "Error desconocido")
            }
        }
    }
}
