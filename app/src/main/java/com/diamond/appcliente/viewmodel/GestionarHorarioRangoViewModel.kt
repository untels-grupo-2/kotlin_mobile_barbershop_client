package com.diamond.appcliente.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.diamond.appcliente.api.AuthApiService
import com.diamond.appcliente.di.AuthenticatedApi
import com.diamond.appcliente.dto.horariorango.HorarioRangoDto
import com.diamond.appcliente.dto.horariorango.HorarioRangoResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class GestionarHorarioRangoViewModel @Inject constructor(
    @AuthenticatedApi private val authApiService: AuthApiService
) : ViewModel() {

    interface HorarioRangoCallback {
        fun onSuccess(horarioRangos: List<HorarioRangoDto>?)
        fun onError(mensaje: String?)
    }

    fun obtenerHorariosRangos(tipoHorarioId: Int, callback: HorarioRangoCallback) {
        authApiService.obtenerHorariosRangos(tipoHorarioId).enqueue(object : Callback<HorarioRangoResponse> {
            override fun onResponse(call: Call<HorarioRangoResponse>, response: Response<HorarioRangoResponse>) {
                Log.d("API_Response", "Estado de la respuesta: ${response.code()}")
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    Log.d("API_Response", "Datos recibidos: $body")
                    if (body.status == 200 && body.data != null) {
                        callback.onSuccess(body.data)
                    } else {
                        Log.e("API_Error", "Error en los datos de la respuesta: ${body.message}")
                        callback.onError("Error en los datos de la respuesta")
                    }
                } else {
                    Log.e("API_Error", "Error al obtener los horarios: ${response.message()}")
                    callback.onError("Error al obtener los horarios")
                }
            }
            override fun onFailure(call: Call<HorarioRangoResponse>, t: Throwable) {
                Log.e("API_Failure", "Fallo en la conexión: ${t.message}")
                callback.onError(t.message)
            }
        })
    }
}
