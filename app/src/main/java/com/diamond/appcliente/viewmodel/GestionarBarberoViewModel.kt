package com.diamond.appcliente.viewmodel

import androidx.lifecycle.ViewModel
import com.diamond.appcliente.api.AuthApiService
import com.diamond.appcliente.di.AuthenticatedApi
import com.diamond.appcliente.dto.barbero.BarberoDto
import com.diamond.appcliente.dto.barbero.BarberoRequest
import com.diamond.appcliente.dto.barbero.BarberoResponse
import com.diamond.appcliente.dto.barbero.BarberoSimpleResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class GestionarBarberoViewModel @Inject constructor(
    @AuthenticatedApi private val authApiService: AuthApiService
) : ViewModel() {

    interface BarberoCallback {
        fun onSuccess(barberos: List<BarberoDto>?)
        fun onError(mensaje: String?)
    }

    interface BarberoOperacionCallback {
        fun onSuccess(mensaje: String?)
        fun onError(mensaje: String?)
    }

    interface ActualizarCallback {
        fun onSuccess(mensaje: String?)
        fun onError(mensaje: String?)
    }

    fun obtenerBarberos(callback: BarberoCallback) {
        authApiService.listarBarberos().enqueue(object : Callback<BarberoResponse> {
            override fun onResponse(call: Call<BarberoResponse>, response: Response<BarberoResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    callback.onSuccess(response.body()!!.data)
                } else {
                    callback.onError("Error al obtener barberos")
                }
            }
            override fun onFailure(call: Call<BarberoResponse>, t: Throwable) {
                callback.onError(t.message)
            }
        })
    }

    fun crearBarbero(nombre: String, callback: BarberoOperacionCallback) {
        authApiService.crearBarbero(BarberoRequest(nombre)).enqueue(object : Callback<BarberoResponse> {
            override fun onResponse(call: Call<BarberoResponse>, response: Response<BarberoResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    callback.onSuccess(response.body()!!.message)
                } else {
                    callback.onError("Error al crear barbero")
                }
            }
            override fun onFailure(call: Call<BarberoResponse>, t: Throwable) {
                callback.onError(t.message)
            }
        })
    }

    fun actualizarBarbero(id: Int, nuevoNombre: String, callback: ActualizarCallback) {
        authApiService.actualizarBarbero(id, BarberoRequest(nuevoNombre)).enqueue(object : Callback<BarberoSimpleResponse> {
            override fun onResponse(call: Call<BarberoSimpleResponse>, response: Response<BarberoSimpleResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    callback.onSuccess(response.body()!!.message)
                } else {
                    callback.onError("Error al actualizar barbero")
                }
            }
            override fun onFailure(call: Call<BarberoSimpleResponse>, t: Throwable) {
                callback.onError(t.message)
            }
        })
    }

    fun eliminarBarbero(id: Int, callback: BarberoOperacionCallback) {
        authApiService.eliminarBarbero(id).enqueue(object : Callback<BarberoResponse> {
            override fun onResponse(call: Call<BarberoResponse>, response: Response<BarberoResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    callback.onSuccess(response.body()!!.message)
                } else {
                    callback.onError("Error al eliminar barbero")
                }
            }
            override fun onFailure(call: Call<BarberoResponse>, t: Throwable) {
                callback.onError(t.message)
            }
        })
    }
}
