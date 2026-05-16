package com.diamond.appcliente.viewmodel

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.diamond.appcliente.api.ApiClient
import com.diamond.appcliente.api.AuthApiService
import com.diamond.appcliente.dto.reserva.DtoReserva
import com.diamond.appcliente.dto.reserva.ReservaResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GestionarReservaViewModel {

    interface ReservaCallback {
        fun onSuccess(message: String)
        fun onError(message: String)
    }

    fun enviarReserva(context: Context, request: DtoReserva, callback: ReservaCallback) {
        val api = ApiClient.getRetrofit(context, true).create(AuthApiService::class.java)
        api.crearReserva(request).enqueue(object : Callback<ReservaResponse> {
            override fun onResponse(call: Call<ReservaResponse>, response: Response<ReservaResponse>) {
                if (response.isSuccessful) {
                    callback.onSuccess("Reserva creada con éxito")
                } else {
                    val errorMessage = try {
                        response.errorBody()?.string() ?: response.message()
                    } catch (e: Exception) {
                        "Error al obtener los detalles del error"
                    }
                    Toast.makeText(context, "Error al crear la reserva: $errorMessage", Toast.LENGTH_LONG).show()
                    callback.onError("Error al crear la reserva: $errorMessage")
                }
            }

            override fun onFailure(call: Call<ReservaResponse>, t: Throwable) {
                val errorMessage = "Fallo en la conexión: ${t.message}"
                Log.e("ReservaError", errorMessage, t)
                callback.onError(errorMessage)
            }
        })
    }

    fun crearReservaRecompensa(context: Context, request: DtoReserva, callback: ReservaCallback) {
        val api = ApiClient.getRetrofit(context, true).create(AuthApiService::class.java)
        api.crearReservaRecompensa(request).enqueue(object : Callback<ReservaResponse> {
            override fun onResponse(call: Call<ReservaResponse>, response: Response<ReservaResponse>) {
                if (response.isSuccessful) {
                    callback.onSuccess("Reserva con recompensa creada con éxito")
                } else {
                    val errorMessage = try {
                        response.errorBody()?.string() ?: response.message()
                    } catch (e: Exception) {
                        "Error al obtener los detalles del error"
                    }
                    Toast.makeText(context, "Error al crear la reserva: $errorMessage", Toast.LENGTH_LONG).show()
                    callback.onError("Error al crear la reserva: $errorMessage")
                }
            }

            override fun onFailure(call: Call<ReservaResponse>, t: Throwable) {
                val errorMessage = "Fallo en la conexión: ${t.message}"
                Log.e("ReservaError", errorMessage, t)
                callback.onError(errorMessage)
            }
        })
    }
}
