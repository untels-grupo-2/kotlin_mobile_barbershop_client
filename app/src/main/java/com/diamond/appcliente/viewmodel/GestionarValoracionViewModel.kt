package com.diamond.appcliente.viewmodel

import android.content.Context
import android.widget.Toast
import com.diamond.appcliente.api.ApiClient
import com.diamond.appcliente.api.AuthApiService
import com.diamond.appcliente.dto.valoracion.ValoracionRequest
import com.diamond.appcliente.dto.valoracion.ValoracionResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GestionarValoracionViewModel {

    interface ValoracionCallback {
        fun onSuccess(message: String)
        fun onError(message: String?)
    }

    fun enviarValoracion(context: Context, request: ValoracionRequest, callback: ValoracionCallback) {
        val api = ApiClient.getRetrofit(context, true).create(AuthApiService::class.java)
        api.crearValoracion(request).enqueue(object : Callback<ValoracionResponse> {
            override fun onResponse(call: Call<ValoracionResponse>, response: Response<ValoracionResponse>) {
                if (response.isSuccessful) {
                    callback.onSuccess("Valoración enviada con éxito")
                } else {
                    Toast.makeText(context, "Error al enviar la valoración: ${response.message()}", Toast.LENGTH_LONG).show()
                    callback.onError("Error al enviar la valoración")
                }
            }

            override fun onFailure(call: Call<ValoracionResponse>, t: Throwable) {
                callback.onError(t.message)
            }
        })
    }
}
