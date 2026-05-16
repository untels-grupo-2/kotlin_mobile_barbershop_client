package com.diamond.appcliente.dto.horario

data class HorarioResponseWrapper(
    val status: Int = 0,
    val message: String? = null,
    val data: Map<String, List<HorarioInstanciaResponse>>? = null
)
