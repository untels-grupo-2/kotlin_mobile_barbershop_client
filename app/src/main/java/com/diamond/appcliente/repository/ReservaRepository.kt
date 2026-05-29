package com.diamond.appcliente.repository

import com.diamond.appcliente.dto.reserva.DtoReserva
import com.diamond.appcliente.dto.reserva.ReservaResponse
import okhttp3.MultipartBody

interface ReservaRepository {
    suspend fun crearReserva(request: DtoReserva): String
    suspend fun crearReservaRecompensa(request: DtoReserva): String
    suspend fun listarMisReservas(): List<ReservaResponse>
    suspend fun subirComprobante(reservaId: Long, imagenPart: MultipartBody.Part): String
}
