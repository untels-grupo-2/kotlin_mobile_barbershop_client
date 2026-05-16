package com.diamond.appcliente.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.diamond.appcliente.api.AuthApiService
import com.diamond.appcliente.di.AuthenticatedApi
import com.diamond.appcliente.dto.common.ApiResponse
import com.diamond.appcliente.dto.reserva.ReservaListResponse
import com.diamond.appcliente.dto.reserva.ReservaResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class ListarReservaViewModel @Inject constructor(
    @AuthenticatedApi private val apiService: AuthApiService
) : ViewModel() {

    interface ActualizarCallback {
        fun onSuccess(str: String)
        fun onError(str: String?)
    }

    val reservasLiveData = MutableLiveData<List<ReservaResponse>?>()

    fun getReservas(): LiveData<List<ReservaResponse>?> {
        loadReservations()
        return reservasLiveData
    }

    private fun loadReservations() {
        Log.d("ListarReservaViewModel", "Haciendo llamada a la API para obtener reservas...")
        apiService.listarMisReservas().enqueue(object : Callback<ReservaListResponse> {
            override fun onResponse(call: Call<ReservaListResponse>, response: Response<ReservaListResponse>) {
                Log.d("ListarReservaViewModel", "Respuesta de la API: ${response.code()}")
                if (response.isSuccessful && response.body() != null) {
                    reservasLiveData.value = response.body()!!.data
                    Log.d("ListarReservaViewModel", "Reservas recibidas: ${response.body()!!.data}")
                } else {
                    Log.d("ListarReservaViewModel", "No se encontraron reservas o error en la respuesta.")
                    reservasLiveData.value = null
                }
            }
            override fun onFailure(call: Call<ReservaListResponse>, t: Throwable) {
                Log.e("ListarReservaViewModel", "Error en la llamada a la API: ", t)
                reservasLiveData.value = null
            }
        })
    }

    fun subirComprobante(reservaId: Long, imagenPart: MultipartBody.Part, callback: ActualizarCallback) {
        apiService.subirComprobante(reservaId, imagenPart).enqueue(object : Callback<ApiResponse<Any>> {
            override fun onResponse(call: Call<ApiResponse<Any>>, response: Response<ApiResponse<Any>>) {
                if (response.isSuccessful) {
                    callback.onSuccess("Comprobante subido correctamente.")
                } else {
                    callback.onError("Error al subir el comprobante.")
                }
            }
            override fun onFailure(call: Call<ApiResponse<Any>>, t: Throwable) {
                callback.onError(t.message)
            }
        })
    }
}
