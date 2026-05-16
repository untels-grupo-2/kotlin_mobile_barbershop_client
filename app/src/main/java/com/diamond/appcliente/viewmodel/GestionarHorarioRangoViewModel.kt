package com.diamond.appcliente.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diamond.appcliente.api.AuthApiService
import com.diamond.appcliente.di.AuthenticatedApi
import com.diamond.appcliente.dto.horariorango.HorarioRangoDto
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
class GestionarHorarioRangoViewModel @Inject constructor(
    @AuthenticatedApi private val authApiService: AuthApiService
) : ViewModel() {

    private val _horarios = MutableStateFlow<List<HorarioRangoDto>>(emptyList())
    val horarios: StateFlow<List<HorarioRangoDto>> = _horarios.asStateFlow()

    private val _error = MutableSharedFlow<String>()
    val error: SharedFlow<String> = _error.asSharedFlow()

    fun cargarHorarios(tipoHorarioId: Int) {
        viewModelScope.launch {
            try {
                Log.d("GestionarHorarioRangoViewModel", "Cargando horarios tipoHorarioId=$tipoHorarioId")
                val response = authApiService.obtenerHorariosRangos(tipoHorarioId)
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    if (body.status == 200 && body.data != null) {
                        _horarios.value = body.data
                    } else {
                        Log.e("GestionarHorarioRangoViewModel", "Error en datos: ${body.message}")
                        _error.emit("Error en los datos de la respuesta")
                    }
                } else {
                    Log.e("GestionarHorarioRangoViewModel", "Error HTTP: ${response.code()}")
                    _error.emit("Error al obtener los horarios")
                }
            } catch (e: Exception) {
                Log.e("GestionarHorarioRangoViewModel", "Fallo de conexión: ${e.message}")
                _error.emit(e.message ?: "Error desconocido")
            }
        }
    }
}
