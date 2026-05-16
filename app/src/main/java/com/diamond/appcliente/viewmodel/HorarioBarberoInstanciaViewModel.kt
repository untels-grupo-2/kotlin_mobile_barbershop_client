package com.diamond.appcliente.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.diamond.appcliente.api.ApiClient
import com.diamond.appcliente.api.AuthApiService
import com.diamond.appcliente.dto.barbero.BarberoListResponse
import com.diamond.appcliente.dto.barbero.DtoBarberoDisponible
import com.diamond.appcliente.util.PreferenciasHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HorarioBarberoInstanciaViewModel(application: Application) : AndroidViewModel(application) {

    private val barberosDisponibles = MutableLiveData<List<DtoBarberoDisponible>?>()
    private val apiService = ApiClient.getRetrofit(application, true).create(AuthApiService::class.java)

    fun getBarberos(): LiveData<List<DtoBarberoDisponible>?> = barberosDisponibles

    fun obtenerBarberosDisponibles(fecha: String, tipoHorarioId: Long, horarioRangoId: Long) {
        val token = PreferenciasHelper(getApplication()).obtenerToken()
        if (token.isNullOrEmpty()) {
            Log.e("HorarioBarberoInstanciaViewModel", "Token no encontrado o es inválido")
            return
        }

        Log.d("HorarioBarberoInstanciaViewModel", "Fecha: $fecha | TipoHorarioId: $tipoHorarioId | HorarioRangoId: $horarioRangoId")

        apiService.obtenerBarberosDisponibles("Bearer $token", fecha, tipoHorarioId, horarioRangoId)
            .enqueue(object : Callback<BarberoListResponse> {
                override fun onResponse(call: Call<BarberoListResponse>, response: Response<BarberoListResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        Log.d("HorarioBarberoInstanciaViewModel", "Código: ${response.code()} | Cuerpo: ${response.body()}")
                        barberosDisponibles.value = response.body()!!.data
                    } else {
                        Log.e("HorarioBarberoInstanciaViewModel", "Error ${response.code()}: ${response.message()}")
                        try {
                            Log.e("HorarioBarberoInstanciaViewModel", "Error Body: ${response.errorBody()?.string()}")
                        } catch (e: Exception) {
                            Log.e("HorarioBarberoInstanciaViewModel", "Error al leer el cuerpo de error: ${e.message}")
                        }
                    }
                }

                override fun onFailure(call: Call<BarberoListResponse>, t: Throwable) {
                    Log.e("HorarioBarberoInstanciaViewModel", "Error de conexión: ${t.message}")
                    barberosDisponibles.value = null
                }
            })
    }
}
