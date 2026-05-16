package com.diamond.appcliente.dto.reserva

data class ReservaRequest(
    val barberoId: Long,
    val horarioRangoId: Long,
    val fechaReserva: String,
    val servicioId: Long,
    val adicionales: String? = null
)
