package com.diamond.appcliente.repository

import com.diamond.appcliente.dto.horariorango.HorarioRangoDto

interface HorarioRangoRepository {
    suspend fun obtenerHorariosRangos(tipoHorarioId: Int): List<HorarioRangoDto>
}
