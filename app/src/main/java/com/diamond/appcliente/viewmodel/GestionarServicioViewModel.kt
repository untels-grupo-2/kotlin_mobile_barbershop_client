package com.diamond.appcliente.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.diamond.appcliente.api.AuthApiService
import com.diamond.appcliente.di.AuthenticatedApi
import com.diamond.appcliente.dto.servicio.ServicioDto
import com.diamond.appcliente.dto.servicio.ServicioRequest
import com.diamond.appcliente.dto.servicio.ServicioResponse
import com.diamond.appcliente.dto.servicio.ServicioSimpleResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class GestionarServicioViewModel @Inject constructor(
    @AuthenticatedApi private val authApiService: AuthApiService
) : ViewModel() {

    interface ServicioCallback {
        fun onSuccess(servicios: List<ServicioDto>?)
        fun onError(mensaje: String?)
    }

    interface ServicioOperacionCallback {
        fun onSuccess(mensaje: String?)
        fun onError(mensaje: String?)
    }

    interface ActualizarCallback {
        fun onSuccess(mensaje: String?)
        fun onError(mensaje: String?)
    }

    private val listaServiciosLiveData = MutableLiveData<List<ServicioDto>>()
    private val servicioOperacionStatus = MutableLiveData<String>()

    fun getListaServicios(): LiveData<List<ServicioDto>> = listaServiciosLiveData
    fun getServicioOperacionStatus(): LiveData<String> = servicioOperacionStatus

    fun obtenerServicios(callback: ServicioCallback) {
        authApiService.listarServicios().enqueue(object : Callback<ServicioResponse> {
            override fun onResponse(call: Call<ServicioResponse>, response: Response<ServicioResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    listaServiciosLiveData.value = response.body()!!.data
                    callback.onSuccess(response.body()!!.data)
                } else {
                    servicioOperacionStatus.value = "Error al obtener servicios"
                    callback.onError("Error al obtener servicios")
                }
            }
            override fun onFailure(call: Call<ServicioResponse>, t: Throwable) {
                servicioOperacionStatus.value = "Error: ${t.message}"
                callback.onError(t.message)
            }
        })
    }

    fun crearServicio(request: ServicioRequest, callback: ServicioOperacionCallback) {
        authApiService.crearServicio(request).enqueue(object : Callback<ServicioResponse> {
            override fun onResponse(call: Call<ServicioResponse>, response: Response<ServicioResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    servicioOperacionStatus.value = "Servicio creado con éxito"
                    obtenerServicios(object : ServicioCallback {
                        override fun onSuccess(servicios: List<ServicioDto>?) { listaServiciosLiveData.value = servicios }
                        override fun onError(mensaje: String?) { servicioOperacionStatus.value = mensaje }
                    })
                    callback.onSuccess(response.body()!!.message)
                } else {
                    servicioOperacionStatus.value = "Error al crear servicio"
                    callback.onError("Error al crear servicio")
                }
            }
            override fun onFailure(call: Call<ServicioResponse>, t: Throwable) {
                servicioOperacionStatus.value = "Error: ${t.message}"
                callback.onError(t.message)
            }
        })
    }

    fun actualizarServicio(id: Int, request: ServicioRequest, callback: ActualizarCallback) {
        authApiService.actualizarServicio(id, request).enqueue(object : Callback<ServicioSimpleResponse> {
            override fun onResponse(call: Call<ServicioSimpleResponse>, response: Response<ServicioSimpleResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    servicioOperacionStatus.value = "Servicio actualizado con éxito"
                    obtenerServicios(object : ServicioCallback {
                        override fun onSuccess(servicios: List<ServicioDto>?) { listaServiciosLiveData.value = servicios }
                        override fun onError(mensaje: String?) { servicioOperacionStatus.value = mensaje }
                    })
                    callback.onSuccess(response.body()!!.message)
                } else {
                    servicioOperacionStatus.value = "Error al actualizar servicio"
                    callback.onError("Error al actualizar servicio")
                }
            }
            override fun onFailure(call: Call<ServicioSimpleResponse>, t: Throwable) {
                servicioOperacionStatus.value = "Error: ${t.message}"
                callback.onError(t.message)
            }
        })
    }

    fun eliminarServicio(id: Int, callback: ServicioOperacionCallback) {
        authApiService.eliminarServicio(id).enqueue(object : Callback<ServicioResponse> {
            override fun onResponse(call: Call<ServicioResponse>, response: Response<ServicioResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    servicioOperacionStatus.value = "Servicio eliminado con éxito"
                    obtenerServicios(object : ServicioCallback {
                        override fun onSuccess(servicios: List<ServicioDto>?) { listaServiciosLiveData.value = servicios }
                        override fun onError(mensaje: String?) { servicioOperacionStatus.value = mensaje }
                    })
                    callback.onSuccess(response.body()!!.message)
                } else {
                    servicioOperacionStatus.value = "Error al eliminar servicio"
                    callback.onError("Error al eliminar servicio")
                }
            }
            override fun onFailure(call: Call<ServicioResponse>, t: Throwable) {
                servicioOperacionStatus.value = "Error: ${t.message}"
                callback.onError(t.message)
            }
        })
    }
}
