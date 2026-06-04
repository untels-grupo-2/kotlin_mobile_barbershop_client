package com.diamond.appcliente.viewmodel

import androidx.lifecycle.ViewModel
import com.diamond.appcliente.util.PreferenciasHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ClienteHomeViewModel @Inject constructor(
    private val preferenciasHelper: PreferenciasHelper
) : ViewModel() {

    private val _nombreCompleto = MutableStateFlow("")
    val nombreCompleto: StateFlow<String> = _nombreCompleto.asStateFlow()

    private val _imagenUrlCliente = MutableStateFlow<String?>(null)
    val imagenUrlCliente: StateFlow<String?> = _imagenUrlCliente.asStateFlow()

    fun setNombreYApellido(nombre: String, apellido: String) {
        _nombreCompleto.value = "$nombre $apellido"
    }

    fun setImagenUrlCliente(imagenUrl: String) {
        _imagenUrlCliente.value = imagenUrl
    }

    fun getNombreCliente(): String = _nombreCompleto.value.ifEmpty { "Desconocido" }

    fun getApellidoCliente(): String = _nombreCompleto.value.split(" ").getOrNull(1) ?: "Desconocido"

    fun getImagenUrlCliente(): String = _imagenUrlCliente.value ?: "default"

    fun cerrarSesion() = preferenciasHelper.limpiarPreferencias()
}
