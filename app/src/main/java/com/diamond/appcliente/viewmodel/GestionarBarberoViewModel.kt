package com.diamond.appcliente.viewmodel

import android.content.Context
import com.diamond.appcliente.api.ApiClient
import com.diamond.appcliente.api.AuthApiService
import com.diamond.appcliente.dto.barbero.BarberoDto
import com.diamond.appcliente.dto.barbero.BarberoRequest
import com.diamond.appcliente.dto.barbero.BarberoResponse
import com.diamond.appcliente.dto.barbero.BarberoSimpleResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GestionarBarberoViewModel {

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

    fun obtenerBarberos(context: Context, callback: BarberoCallback) {
        val api = ApiClient.getRetrofit(context, true).create(AuthApiService::class.java)
        api.listarBarberos().enqueue(object : Callback<BarberoResponse> {
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

    fun crearBarbero(context: Context, nombre: String, callback: BarberoOperacionCallback) {
        val api = ApiClient.getRetrofit(context, true).create(AuthApiService::class.java)
        api.crearBarbero(BarberoRequest(nombre)).enqueue(object : Callback<BarberoResponse> {
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

    fun actualizarBarbero(context: Context, id: Int, nuevoNombre: String, callback: ActualizarCallback) {
        val api = ApiClient.getRetrofit(context, true).create(AuthApiService::class.java)
        api.actualizarBarbero(id, BarberoRequest(nuevoNombre)).enqueue(object : Callback<BarberoSimpleResponse> {
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

    fun eliminarBarbero(context: Context, id: Int, callback: BarberoOperacionCallback) {
        val api = ApiClient.getRetrofit(context, true).create(AuthApiService::class.java)
        api.eliminarBarbero(id).enqueue(object : Callback<BarberoResponse> {
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
