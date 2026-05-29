package com.diamond.appcliente.repository.impl

import android.util.Log
import com.diamond.appcliente.api.AuthApiService
import com.diamond.appcliente.di.AuthenticatedApi
import com.diamond.appcliente.dto.horariorango.HorarioRangoDto
import com.diamond.appcliente.repository.HorarioRangoRepository
import javax.inject.Inject

class HorarioRangoRepositoryImpl @Inject constructor(
    @AuthenticatedApi private val apiService: AuthApiService
) : HorarioRangoRepository {

    override suspend fun obtenerHorariosRangos(tipoHorarioId: Int): List<HorarioRangoDto> {
        Log.d("HorarioRangoRepository", "Cargando horarios tipoHorarioId=$tipoHorarioId")
        val response = apiService.obtenerHorariosRangos(tipoHorarioId)
        if (response.isSuccessful && response.body() != null) {
            val body = response.body()!!
            if (body.status == 200 && body.data != null) return body.data
            Log.e("HorarioRangoRepository", "Error en datos: ${body.message}")
            throw Exception("Error en los datos de la respuesta")
        }
        Log.e("HorarioRangoRepository", "Error HTTP: ${response.code()}")
        throw Exception("Error al obtener los horarios")
    }
}
