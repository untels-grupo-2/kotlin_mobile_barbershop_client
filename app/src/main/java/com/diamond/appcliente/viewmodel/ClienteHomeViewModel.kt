package com.diamond.appcliente.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.diamond.appcliente.util.PreferenciasHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ClienteHomeViewModel @Inject constructor(
    private val preferenciasHelper: PreferenciasHelper
) : ViewModel() {

    val nombreCompleto = MutableLiveData<String>()
    val imagenUrlCliente = MutableLiveData<String>()

    fun setNombreYApellido(nombre: String, apellido: String) {
        nombreCompleto.value = "$nombre $apellido"
    }

    fun setImagenUrlCliente(imagenUrl: String) {
        imagenUrlCliente.value = imagenUrl
    }

    fun getNombreCliente(): String = nombreCompleto.value ?: "Desconocido"

    fun getApellidoCliente(): String = nombreCompleto.value?.split(" ")?.getOrNull(1) ?: "Desconocido"

    fun getImagenUrlCliente(): String = imagenUrlCliente.value ?: "default"

    fun cerrarSesion() = preferenciasHelper.limpiarPreferencias()
}
