package com.diamond.appcliente.dto

import com.diamond.appcliente.dto.horariorango.HorarioRangoDto

data class HorarioItem(
    val encabezado: String,
    val horarios: List<HorarioRangoDto>
)
