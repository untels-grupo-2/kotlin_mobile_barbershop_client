package com.diamond.appcliente.viewmodel

import androidx.lifecycle.ViewModel
import com.diamond.appcliente.api.AuthApiService
import com.diamond.appcliente.di.AuthenticatedApi
import com.diamond.appcliente.dto.valoracion.ValoracionRequest
import com.diamond.appcliente.dto.valoracion.ValoracionResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class GestionarValoracionViewModel @Inject constructor(
    @AuthenticatedApi private val authApiService: AuthApiService
) : ViewModel() {

    interface ValoracionCallback {
        fun onSuccess(message: String)
        fun onError(message: String?)
    }

    fun enviarValoracion(request: ValoracionRequest, callback: ValoracionCallback) {
        authApiService.crearValoracion(request).enqueue(object : Callback<ValoracionResponse> {
            override fun onResponse(call: Call<ValoracionResponse>, response: Response<ValoracionResponse>) {
                if (response.isSuccessful) {
                    callback.onSuccess("Valoración enviada con éxito")
                } else {
                    callback.onError("Error al enviar la valoración")
                }
            }
            override fun onFailure(call: Call<ValoracionResponse>, t: Throwable) {
                callback.onError(t.message)
            }
        })
    }
}
