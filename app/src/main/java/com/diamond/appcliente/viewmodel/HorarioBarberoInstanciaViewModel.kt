package com.diamond.appcliente.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.diamond.appcliente.api.AuthApiService
import com.diamond.appcliente.di.AuthenticatedApi
import com.diamond.appcliente.dto.barbero.BarberoListResponse
import com.diamond.appcliente.dto.barbero.DtoBarberoDisponible
import com.diamond.appcliente.util.PreferenciasHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class HorarioBarberoInstanciaViewModel @Inject constructor(
    @AuthenticatedApi private val apiService: AuthApiService,
    private val preferenciasHelper: PreferenciasHelper
) : ViewModel() {

    private val barberosDisponibles = MutableLiveData<List<DtoBarberoDisponible>?>()

    fun getBarberos(): LiveData<List<DtoBarberoDisponible>?> = barberosDisponibles

    fun obtenerBarberosDisponibles(fecha: String, tipoHorarioId: Long, horarioRangoId: Long) {
        val token = preferenciasHelper.obtenerToken()
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
