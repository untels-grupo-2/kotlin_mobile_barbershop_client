package com.diamond.appcliente.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.diamond.barbershop.shared.util.PreferenciasHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ClienteHomeViewModel @Inject constructor(
    private val preferenciasHelper: PreferenciasHelper,
    @ApplicationContext private val context: Context
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

    /** Returns true the first time called per session; marks the welcome as shown. */
    fun consumirBienvenidaSiPendiente(): Boolean {
        val prefs = context.getSharedPreferences("diamond_prefs", Context.MODE_PRIVATE)
        if (!prefs.getBoolean("welcome_shown", false)) {
            prefs.edit().putBoolean("welcome_shown", true).apply()
            return true
        }
        return false
    }
}
