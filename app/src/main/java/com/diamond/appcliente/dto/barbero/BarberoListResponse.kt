package com.diamond.appcliente.dto.barbero

data class BarberoListResponse(
    val status: String? = null,
    val message: String? = null,
    val data: List<DtoBarberoDisponible>? = null
)
