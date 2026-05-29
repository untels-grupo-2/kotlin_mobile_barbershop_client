package com.diamond.appcliente.repository.impl

import android.util.Log
import com.diamond.appcliente.api.AuthApiService
import com.diamond.appcliente.di.AuthenticatedApi
import com.diamond.appcliente.dto.reserva.DtoReserva
import com.diamond.appcliente.dto.reserva.ReservaResponse
import com.diamond.appcliente.repository.ReservaRepository
import okhttp3.MultipartBody
import javax.inject.Inject

class ReservaRepositoryImpl @Inject constructor(
    @AuthenticatedApi private val apiService: AuthApiService
) : ReservaRepository {

    override suspend fun crearReserva(request: DtoReserva): String {
        val response = apiService.crearReserva(request)
        if (response.isSuccessful) return "Reserva creada con éxito"
        val errorMsg = try { response.errorBody()?.string() ?: response.message() } catch (e: Exception) { "Error desconocido" }
        throw Exception(errorMsg)
    }

    override suspend fun crearReservaRecompensa(request: DtoReserva): String {
        val response = apiService.crearReservaRecompensa(request)
        if (response.isSuccessful) return "Reserva con recompensa creada con éxito"
        val errorMsg = try { response.errorBody()?.string() ?: response.message() } catch (e: Exception) { "Error desconocido" }
        throw Exception(errorMsg)
    }

    override suspend fun listarMisReservas(): List<ReservaResponse> {
        Log.d("ReservaRepository", "Cargando reservas...")
        val response = apiService.listarMisReservas()
        Log.d("ReservaRepository", "Respuesta: ${response.code()}")
        if (response.isSuccessful && response.body() != null) return response.body()!!.data ?: emptyList()
        Log.d("ReservaRepository", "Sin reservas o error en respuesta")
        return emptyList()
    }

    override suspend fun subirComprobante(reservaId: Long, imagenPart: MultipartBody.Part): String {
        val response = apiService.subirComprobante(reservaId, imagenPart)
        if (response.isSuccessful) return "Comprobante subido correctamente."
        throw Exception("Error al subir el comprobante.")
    }
}
