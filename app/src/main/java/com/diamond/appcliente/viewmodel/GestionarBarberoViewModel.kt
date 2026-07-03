package com.diamond.appcliente.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diamond.barbershop.shared.dto.barbero.BarberoDto
import com.diamond.appcliente.repository.BarberoRepository
import com.shared.models.ui.state.UiState
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

    private val _barberos = MutableStateFlow<UiState<List<BarberoDto>>>(UiState.Idle)
    val barberos: StateFlow<UiState<List<BarberoDto>>> = _barberos.asStateFlow()

    private val _mensaje = MutableSharedFlow<String>()
    val mensaje: SharedFlow<String> = _mensaje.asSharedFlow()

    private val _crudError = MutableSharedFlow<String>()
    val crudError: SharedFlow<String> = _crudError.asSharedFlow()

    fun cargarBarberos() {
        viewModelScope.launch {
            _barberos.value = UiState.Loading
            try {
                _barberos.value = UiState.Success(barberoRepository.listarBarberos())
            } catch (e: Exception) {
                _barberos.value = UiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun crearBarbero(nombre: String) {
        viewModelScope.launch {
            try {
                _mensaje.emit(barberoRepository.crearBarbero(nombre))
                cargarBarberos()
            } catch (e: Exception) {
                _crudError.emit(e.message ?: "Error desconocido")
            }
        }
    }

    fun actualizarBarbero(id: Int, nuevoNombre: String) {
        viewModelScope.launch {
            try {
                _mensaje.emit(barberoRepository.actualizarBarbero(id, nuevoNombre))
                cargarBarberos()
            } catch (e: Exception) {
                _crudError.emit(e.message ?: "Error desconocido")
            }
        }
    }

    fun eliminarBarbero(id: Int) {
        viewModelScope.launch {
            try {
                _mensaje.emit(barberoRepository.eliminarBarbero(id))
                cargarBarberos()
            } catch (e: Exception) {
                _crudError.emit(e.message ?: "Error desconocido")
            }
        }
    }

    fun tipoHorarioToValor(tipoHorario: String?): Int = when (tipoHorario) {
        "MAÑANA" -> 1; "TARDE" -> 2; "NOCHE" -> 3; else -> -1
    }

    fun turnoReservaToValor(turno: String?): Int = when (turno) {
        "9 AM - 10 AM"  -> 1;  "10 AM - 11 AM" -> 2;  "11 AM - 12 PM" -> 3
        "12 PM - 1 PM"  -> 4;  "1 PM - 2 PM"   -> 5;  "2 PM - 3 PM"   -> 6
        "3 PM - 4 PM"   -> 7;  "4 PM - 5 PM"   -> 8;  "5 PM - 6 PM"   -> 9
        "6 PM - 7 PM"   -> 10; "7 PM - 8 PM"   -> 11; "8 PM - 9 PM"   -> 12
        "9 PM - 10 PM"  -> 13; else -> -1
    }
}
