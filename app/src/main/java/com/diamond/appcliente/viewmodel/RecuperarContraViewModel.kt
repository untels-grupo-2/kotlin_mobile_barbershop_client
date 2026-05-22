package com.diamond.appcliente.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diamond.appcliente.api.AuthApiService
import com.diamond.appcliente.di.UnauthenticatedApi
import com.diamond.appcliente.dto.recuperacion.RecuperacionRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecuperarContraViewModel @Inject constructor(
    @UnauthenticatedApi private val authApiService: AuthApiService
) : ViewModel() {

    private val _resultado = MutableSharedFlow<String>()
    val resultado: SharedFlow<String> = _resultado.asSharedFlow()

    private val _error = MutableSharedFlow<String>()
    val error: SharedFlow<String> = _error.asSharedFlow()

    fun recuperar(usuario: String, correo: String) {
        viewModelScope.launch {
            try {
                val response = authApiService.recuperarContraseña(RecuperacionRequest(usuario, correo))
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    if (body.status == 200) {
                        _resultado.emit(body.message ?: "Correo enviado")
                    } else {
                        _error.emit(body.message ?: "Error al recuperar contraseña")
                    }
                } else {
                    val rawError = try { response.errorBody()?.string() ?: "sin cuerpo" } catch (e: Exception) { "error al leer errorBody" }
                    _error.emit("Error HTTP ${response.code()}: $rawError")
                }
            } catch (e: Exception) {
                _error.emit("Error de conexión: ${e.message}")
            }
        }
    }
}
