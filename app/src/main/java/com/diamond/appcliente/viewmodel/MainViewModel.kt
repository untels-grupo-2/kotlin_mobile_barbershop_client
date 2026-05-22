package com.diamond.appcliente.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.auth0.android.jwt.JWT
import com.diamond.appcliente.api.AuthApiService
import com.diamond.appcliente.di.UnauthenticatedApi
import com.diamond.appcliente.dto.login.LoginRequest
import com.diamond.appcliente.util.PreferenciasHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @UnauthenticatedApi private val authApiService: AuthApiService,
    private val preferenciasHelper: PreferenciasHelper
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
            Log.d("MainViewModel", "Iniciando login para: $usuario | URL: ${authApiService}")
            try {
                val response = authApiService.login(LoginRequest(usuario, password))
                Log.d("MainViewModel", "Respuesta recibida: HTTP ${response.code()}")
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    val token = body.data?.token ?: run { _loginResult.value = LoginResult.Error("Token nulo"); return@launch }
                    val refreshToken = body.data?.refreshToken ?: run { _loginResult.value = LoginResult.Error("Refresh token nulo"); return@launch }
                    val jwt = JWT(token)

                    if (jwt.getClaim("rol").asString() != "USER") {
                        _loginResult.value = LoginResult.Error("NO_USER")
                        return@launch
                    }

                    preferenciasHelper.guardarToken(token)
                    preferenciasHelper.guardarRefreshToken(refreshToken)
                    _loginResult.value = LoginResult.Success(
                        jwt.getClaim("nombre").asString() ?: "",
                        jwt.getClaim("apellido").asString() ?: "",
                        jwt.getClaim("urlUsuario").asString() ?: ""
                    )
                } else if (response.code() == 401) {
                    _loginResult.value = LoginResult.Error("INVALID")
                } else {
                    _loginResult.value = LoginResult.Error("ERROR_${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error de conexión: ${e.javaClass.simpleName} - ${e.message}")
                _loginResult.value = LoginResult.Error("FAILURE: ${e.message}")
            }
        }
    }
}
