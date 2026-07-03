package com.diamond.appcliente.repository.impl

import android.util.Log
import com.diamond.appcliente.api.AuthApiService
import com.diamond.appcliente.di.AuthenticatedApi
import com.diamond.barbershop.shared.dto.barbero.BarberoDto
import com.diamond.barbershop.shared.dto.barbero.BarberoRequest
import com.diamond.appcliente.dto.barbero.DtoBarberoDisponible
import com.diamond.appcliente.repository.BarberoRepository
import javax.inject.Inject

class BarberoRepositoryImpl @Inject constructor(
    @AuthenticatedApi private val apiService: AuthApiService
) : BarberoRepository {

    override suspend fun listarBarberos(): List<BarberoDto> {
        val response = apiService.listarBarberos()
        if (response.isSuccessful && response.body() != null) return response.body()!!.data ?: emptyList()
        throw Exception("Error al obtener barberos")
    }

    override suspend fun crearBarbero(nombre: String): String {
        val response = apiService.crearBarbero(BarberoRequest(nombre))
        if (response.isSuccessful && response.body() != null) return response.body()!!.message ?: "Barbero creado"
        throw Exception("Error al crear barbero")
    }

    override suspend fun actualizarBarbero(id: Int, nombre: String): String {
        val response = apiService.actualizarBarbero(id, BarberoRequest(nombre))
        if (response.isSuccessful && response.body() != null) return response.body()!!.message ?: "Barbero actualizado"
        throw Exception("Error al actualizar barbero")
    }

    override suspend fun eliminarBarbero(id: Int): String {
        val response = apiService.eliminarBarbero(id)
        if (response.isSuccessful && response.body() != null) return response.body()!!.message ?: "Barbero eliminado"
        throw Exception("Error al eliminar barbero")
    }

    override suspend fun obtenerBarberosDisponibles(fecha: String, tipoHorarioId: Long, horarioRangoId: Long): List<DtoBarberoDisponible> {
        Log.d("BarberoRepository", "Fecha: $fecha | TipoHorarioId: $tipoHorarioId | HorarioRangoId: $horarioRangoId")
        val response = apiService.obtenerBarberosDisponibles(fecha, tipoHorarioId, horarioRangoId)
        if (response.isSuccessful && response.body() != null) {
            Log.d("BarberoRepository", "Código: ${response.code()} | Cuerpo: ${response.body()}")
            return response.body()!!.data ?: emptyList()
        }
        Log.e("BarberoRepository", "Error ${response.code()}: ${response.message()}")
        throw Exception("Error ${response.code()}: ${response.message()}")
    }
}
