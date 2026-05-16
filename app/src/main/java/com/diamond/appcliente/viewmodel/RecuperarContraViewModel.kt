package com.diamond.appcliente.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.diamond.appcliente.api.AuthApiService
import com.diamond.appcliente.di.UnauthenticatedApi
import com.diamond.appcliente.dto.recuperacion.RecuperacionRequest
import com.diamond.appcliente.dto.recuperacion.RecuperacionResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class RecuperarContraViewModel @Inject constructor(
    @UnauthenticatedApi private val authApiService: AuthApiService
) : ViewModel() {

    val resultado = MutableLiveData<String>()
    val error = MutableLiveData<String>()

    fun recuperar(usuario: String, correo: String) {
        authApiService.recuperarContraseña(RecuperacionRequest(usuario, correo))
            .enqueue(object : Callback<RecuperacionResponse> {
                override fun onResponse(call: Call<RecuperacionResponse>, response: Response<RecuperacionResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        val body = response.body()!!
                        if (body.status == 200) {
                            resultado.postValue(body.message)
                        } else {
                            error.postValue(body.message)
                        }
                    } else {
                        val rawError = try {
                            response.errorBody()?.string() ?: "sin cuerpo"
                        } catch (e: Exception) {
                            "error al leer errorBody"
                        }
                        error.postValue("Error HTTP ${response.code()}: $rawError")
                    }
                }
                override fun onFailure(call: Call<RecuperacionResponse>, t: Throwable) {
                    error.postValue("Error de conexión: ${t.message}")
                }
            })
    }
}
