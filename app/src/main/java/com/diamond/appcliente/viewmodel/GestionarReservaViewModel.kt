package com.diamond.appcliente.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.diamond.appcliente.api.AuthApiService
import com.diamond.appcliente.di.AuthenticatedApi
import com.diamond.appcliente.dto.reserva.DtoReserva
import com.diamond.appcliente.dto.reserva.ReservaResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class GestionarReservaViewModel @Inject constructor(
    @AuthenticatedApi private val authApiService: AuthApiService
) : ViewModel() {

    interface ReservaCallback {
        fun onSuccess(message: String)
        fun onError(message: String)
    }

    fun enviarReserva(request: DtoReserva, callback: ReservaCallback) {
        authApiService.crearReserva(request).enqueue(object : Callback<ReservaResponse> {
            override fun onResponse(call: Call<ReservaResponse>, response: Response<ReservaResponse>) {
                if (response.isSuccessful) {
                    callback.onSuccess("Reserva creada con éxito")
                } else {
                    val errorMessage = try {
                        response.errorBody()?.string() ?: response.message()
                    } catch (e: Exception) {
                        "Error al obtener los detalles del error"
                    }
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

    fun crearReservaRecompensa(request: DtoReserva, callback: ReservaCallback) {
        authApiService.crearReservaRecompensa(request).enqueue(object : Callback<ReservaResponse> {
            override fun onResponse(call: Call<ReservaResponse>, response: Response<ReservaResponse>) {
                if (response.isSuccessful) {
                    callback.onSuccess("Reserva con recompensa creada con éxito")
                } else {
                    val errorMessage = try {
                        response.errorBody()?.string() ?: response.message()
                    } catch (e: Exception) {
                        "Error al obtener los detalles del error"
                    }
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
