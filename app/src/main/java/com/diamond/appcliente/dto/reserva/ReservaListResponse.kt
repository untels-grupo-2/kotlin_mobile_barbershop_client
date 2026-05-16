package com.diamond.appcliente.dto.reserva

data class ReservaListResponse(
    var status: String? = null,
    var message: String? = null,
    var data: List<ReservaResponse>? = null
)
