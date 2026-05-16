package com.diamond.appcliente.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.auth0.android.jwt.JWT
import com.diamond.appcliente.api.AuthApiService
import com.diamond.appcliente.di.UnauthenticatedApi
import com.diamond.appcliente.dto.login.LoginRequest
import com.diamond.appcliente.dto.login.LoginResponse
import com.diamond.appcliente.util.PreferenciasHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @UnauthenticatedApi private val authApiService: AuthApiService,
    private val preferenciasHelper: PreferenciasHelper
) : ViewModel() {

    val apellido = MutableLiveData<String>()
    val loginStatus = MutableLiveData<String>()
    val nombre = MutableLiveData<String>()
    val url_usuario = MutableLiveData<String>()
    val usuario_id = MutableLiveData<String>()

    fun login(usuario: String, password: String) {
        authApiService.login(LoginRequest(usuario, password)).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    val token = body.data?.token ?: return
                    val refreshToken = body.data?.refreshToken ?: return
                    val jwt = JWT(token)

                    if (jwt.getClaim("rol").asString() != "USER") {
                        loginStatus.postValue("NO_USER")
                        return
                    }

                    preferenciasHelper.guardarToken(token)
                    preferenciasHelper.guardarRefreshToken(refreshToken)

                    nombre.postValue(jwt.getClaim("nombre").asString())
                    apellido.postValue(jwt.getClaim("apellido").asString())
                    url_usuario.postValue(jwt.getClaim("urlUsuario").asString())
                    loginStatus.postValue("SUCCESS")
                } else if (response.code() == 401) {
                    loginStatus.postValue("INVALID")
                } else {
                    loginStatus.postValue("ERROR_${response.code()}")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                loginStatus.postValue("FAILURE: ${t.message}")
            }
        })
    }
}
