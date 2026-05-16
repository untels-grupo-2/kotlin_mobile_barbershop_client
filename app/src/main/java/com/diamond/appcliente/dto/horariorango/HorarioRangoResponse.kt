package com.diamond.appcliente.dto.horariorango

data class HorarioRangoResponse(
    var status: Int = 0,
    var message: String? = null,
    var data: List<HorarioRangoDto>? = null
)
