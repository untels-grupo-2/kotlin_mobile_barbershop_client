package com.diamond.appcliente.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diamond.appcliente.util.CacheAwareData
import com.diamond.barbershop.shared.dto.servicio.ServicioDto
import com.diamond.barbershop.shared.dto.servicio.ServicioRequest
import com.diamond.appcliente.repository.ServicioRepository
import com.shared.models.ui.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
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

    private val _servicios = MutableStateFlow<UiState<List<ServicioDto>>>(UiState.Idle)
    val servicios: StateFlow<UiState<List<ServicioDto>>> = _servicios.asStateFlow()

    private val _mensaje = MutableSharedFlow<String>()
    val mensaje: SharedFlow<String> = _mensaje.asSharedFlow()

    private val _crudError = MutableSharedFlow<String>()
    val crudError: SharedFlow<String> = _crudError.asSharedFlow()

    private var loadJob: Job? = null

    fun cargarServicios() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            if (_servicios.value !is UiState.Success) {
                _servicios.value = UiState.Loading
            }
            try {
                servicioRepository.observarServicios().collect { result ->
                    _servicios.value = when (result) {
                        is CacheAwareData.Stale -> UiState.Success(result.data, isStale = true)
                        is CacheAwareData.Fresh -> UiState.Success(result.data, isStale = false)
                    }
                }
            } catch (e: Exception) {
                if (_servicios.value !is UiState.Success) {
                    _servicios.value = UiState.Error(e.message ?: "Error desconocido")
                }
            }
        }
    }

    fun crearServicio(request: ServicioRequest) {
        viewModelScope.launch {
            try {
                _mensaje.emit(servicioRepository.crearServicio(request))
                cargarServicios()
            } catch (e: Exception) {
                _crudError.emit(e.message ?: "Error desconocido")
            }
        }
    }

    fun actualizarServicio(id: Int, request: ServicioRequest) {
        viewModelScope.launch {
            try {
                _mensaje.emit(servicioRepository.actualizarServicio(id, request))
                cargarServicios()
            } catch (e: Exception) {
                _crudError.emit(e.message ?: "Error desconocido")
            }
        }
    }

    fun eliminarServicio(id: Int) {
        viewModelScope.launch {
            try {
                _mensaje.emit(servicioRepository.eliminarServicio(id))
                cargarServicios()
            } catch (e: Exception) {
                _crudError.emit(e.message ?: "Error desconocido")
            }
        }
    }
}
