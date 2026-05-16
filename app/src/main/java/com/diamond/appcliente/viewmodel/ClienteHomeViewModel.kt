package com.diamond.appcliente.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.diamond.appcliente.util.PreferenciasHelper

class ClienteHomeViewModel(application: Application) : AndroidViewModel(application) {

    val nombreCompleto = MutableLiveData<String>()
    val imagenUrlCliente = MutableLiveData<String>()

    private val preferenciasHelper = PreferenciasHelper(application)

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
