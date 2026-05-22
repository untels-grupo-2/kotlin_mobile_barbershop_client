package com.diamond.appcliente.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diamond.appcliente.api.AuthApiService
import com.diamond.appcliente.di.AuthenticatedApi
import com.diamond.appcliente.dto.barbero.DtoBarberoDisponible
import com.diamond.appcliente.util.PreferenciasHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HorarioBarberoInstanciaViewModel @Inject constructor(
    @AuthenticatedApi private val apiService: AuthApiService,
    private val preferenciasHelper: PreferenciasHelper
) : ViewModel() {

    private val _barberosDisponibles = MutableStateFlow<List<DtoBarberoDisponible>>(emptyList())
    val barberosDisponibles: StateFlow<List<DtoBarberoDisponible>> = _barberosDisponibles.asStateFlow()

    fun obtenerBarberosDisponibles(fecha: String, tipoHorarioId: Long, horarioRangoId: Long) {
        val token = preferenciasHelper.obtenerToken()
        if (token.isNullOrEmpty()) {
            Log.e("HorarioBarberoInstanciaViewModel", "Token no encontrado o es inválido")
            return
        }

        Log.d("HorarioBarberoInstanciaViewModel", "Fecha: $fecha | TipoHorarioId: $tipoHorarioId | HorarioRangoId: $horarioRangoId")

        viewModelScope.launch {
            try {
                val response = apiService.obtenerBarberosDisponibles("Bearer $token", fecha, tipoHorarioId, horarioRangoId)
                if (response.isSuccessful && response.body() != null) {
                    Log.d("HorarioBarberoInstanciaViewModel", "Código: ${response.code()} | Cuerpo: ${response.body()}")
                    _barberosDisponibles.value = response.body()!!.data ?: emptyList()
                } else {
                    Log.e("HorarioBarberoInstanciaViewModel", "Error ${response.code()}: ${response.message()}")
                    try { Log.e("HorarioBarberoInstanciaViewModel", "Error Body: ${response.errorBody()?.string()}") } catch (e: Exception) { }
                    _barberosDisponibles.value = emptyList()
                }
            } catch (e: Exception) {
                Log.e("HorarioBarberoInstanciaViewModel", "Error de conexión: ${e.message}")
                _barberosDisponibles.value = emptyList()
            }
        }
    }
}
