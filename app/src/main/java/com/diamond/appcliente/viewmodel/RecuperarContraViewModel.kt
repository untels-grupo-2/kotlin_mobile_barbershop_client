package com.diamond.appcliente.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diamond.appcliente.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecuperarContraViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _resultado = MutableSharedFlow<String>()
    val resultado: SharedFlow<String> = _resultado.asSharedFlow()

    private val _error = MutableSharedFlow<String>()
    val error: SharedFlow<String> = _error.asSharedFlow()

    fun recuperar(usuario: String, correo: String) {
        viewModelScope.launch {
            try {
                _resultado.emit(authRepository.recuperarContraseña(usuario, correo))
            } catch (e: Exception) {
                _error.emit(e.message ?: "Error de conexión")
            }
        }
    }
}
