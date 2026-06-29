package com.diamond.appcliente.repository

import com.diamond.barbershop.shared.dto.barbero.BarberoDto
import com.diamond.appcliente.dto.barbero.DtoBarberoDisponible

interface BarberoRepository {
    suspend fun listarBarberos(): List<BarberoDto>
    suspend fun crearBarbero(nombre: String): String
    suspend fun actualizarBarbero(id: Int, nombre: String): String
    suspend fun eliminarBarbero(id: Int): String
    suspend fun obtenerBarberosDisponibles(fecha: String, tipoHorarioId: Long, horarioRangoId: Long): List<DtoBarberoDisponible>
}
