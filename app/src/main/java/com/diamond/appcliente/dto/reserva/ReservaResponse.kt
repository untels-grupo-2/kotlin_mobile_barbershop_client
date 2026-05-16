package com.diamond.appcliente.dto.reserva

data class ReservaResponse(
    var adicionales: String? = null,
    var barberoNombre: String? = null,
    var estRecompensa: Int? = null,
    var estado: String? = null,
    var fechaCreacion: String? = null,
    var fechaReserva: String? = null,
    var horarioRango: String? = null,
    var motivoDescripcion: String? = null,
    var precioServicio: Long? = null,
    var reservaId: Long? = null,
    var servicioNombre: String? = null,
    var urlPago: String? = null,
    var usuarioNombre: String? = null
)
