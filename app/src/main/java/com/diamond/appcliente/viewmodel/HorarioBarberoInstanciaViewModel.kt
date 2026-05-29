package com.diamond.appcliente.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diamond.appcliente.dto.barbero.DtoBarberoDisponible
import com.diamond.appcliente.repository.BarberoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HorarioBarberoInstanciaViewModel @Inject constructor(
    private val barberoRepository: BarberoRepository
) : ViewModel() {

    private val _barberosDisponibles = MutableStateFlow<List<DtoBarberoDisponible>>(emptyList())
    val barberosDisponibles: StateFlow<List<DtoBarberoDisponible>> = _barberosDisponibles.asStateFlow()

    fun obtenerBarberosDisponibles(fecha: String, tipoHorarioId: Long, horarioRangoId: Long) {
        viewModelScope.launch {
            try {
                _barberosDisponibles.value = barberoRepository.obtenerBarberosDisponibles(fecha, tipoHorarioId, horarioRangoId)
            } catch (e: Exception) {
                Log.e("HorarioBarberoInstanciaViewModel", "Error: ${e.message}")
                _barberosDisponibles.value = emptyList()
            }
        }
    }
}
