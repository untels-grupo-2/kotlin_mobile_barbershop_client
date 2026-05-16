package com.diamond.appcliente.dto.servicio

data class ServicioResponse(
    var status: Int = 0,
    var message: String? = null,
    var data: List<ServicioDto>? = null
)
