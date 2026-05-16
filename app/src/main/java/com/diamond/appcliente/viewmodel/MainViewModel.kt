package com.diamond.appcliente.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.auth0.android.jwt.JWT
import com.diamond.appcliente.api.ApiClient
import com.diamond.appcliente.api.AuthApiService
import com.diamond.appcliente.dto.login.LoginRequest
import com.diamond.appcliente.dto.login.LoginResponse
import com.diamond.appcliente.util.PreferenciasHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val apellido = MutableLiveData<String>()
    val loginStatus = MutableLiveData<String>()
    val nombre = MutableLiveData<String>()
    val url_usuario = MutableLiveData<String>()
    val usuario_id = MutableLiveData<String>()

    private val authApiService: AuthApiService =
        ApiClient.getRetrofit(application, false).create(AuthApiService::class.java)

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

                    PreferenciasHelper(getApplication()).apply {
                        guardarToken(token)
                        guardarRefreshToken(refreshToken)
                    }

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
