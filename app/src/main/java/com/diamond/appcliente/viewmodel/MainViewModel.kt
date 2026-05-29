package com.diamond.appcliente.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diamond.appcliente.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    sealed class LoginResult {
        object Idle : LoginResult()
        object Loading : LoginResult()
        data class Success(val nombre: String, val apellido: String, val urlUsuario: String) : LoginResult()
        data class Error(val message: String) : LoginResult()
    }

    private val _loginResult = MutableStateFlow<LoginResult>(LoginResult.Idle)
    val loginResult: StateFlow<LoginResult> = _loginResult.asStateFlow()

    fun login(usuario: String, password: String) {
        viewModelScope.launch {
            _loginResult.value = LoginResult.Loading
            Log.d("MainViewModel", "Iniciando login para: $usuario")
            try {
                val session = authRepository.login(usuario, password)
                Log.d("MainViewModel", "Login exitoso: ${session.nombre}")
                _loginResult.value = LoginResult.Success(session.nombre, session.apellido, session.urlUsuario)
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error: ${e.javaClass.simpleName} - ${e.message}")
                when (e.message) {
                    "INVALID" -> _loginResult.value = LoginResult.Error("INVALID")
                    "NO_USER" -> _loginResult.value = LoginResult.Error("NO_USER")
                    else -> _loginResult.value = LoginResult.Error("FAILURE: ${e.message}")
                }
            }
        }
    }
}
