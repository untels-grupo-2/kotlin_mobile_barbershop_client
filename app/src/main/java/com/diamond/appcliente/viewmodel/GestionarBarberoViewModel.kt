package com.diamond.appcliente.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diamond.appcliente.dto.barbero.BarberoDto
import com.diamond.appcliente.repository.BarberoRepository
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
    private val barberoRepository: BarberoRepository
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
                _barberos.value = barberoRepository.listarBarberos()
            } catch (e: Exception) {
                _error.emit(e.message ?: "Error desconocido")
            }
        }
    }

    fun crearBarbero(nombre: String) {
        viewModelScope.launch {
            try {
                _mensaje.emit(barberoRepository.crearBarbero(nombre))
                cargarBarberos()
            } catch (e: Exception) {
                _error.emit(e.message ?: "Error desconocido")
            }
        }
    }

    fun actualizarBarbero(id: Int, nuevoNombre: String) {
        viewModelScope.launch {
            try {
                _mensaje.emit(barberoRepository.actualizarBarbero(id, nuevoNombre))
                cargarBarberos()
            } catch (e: Exception) {
                _error.emit(e.message ?: "Error desconocido")
            }
        }
    }

    fun eliminarBarbero(id: Int) {
        viewModelScope.launch {
            try {
                _mensaje.emit(barberoRepository.eliminarBarbero(id))
                cargarBarberos()
            } catch (e: Exception) {
                _error.emit(e.message ?: "Error desconocido")
            }
        }
    }
}
