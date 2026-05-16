package com.diamond.appcliente.dto.barbero

data class BarberoResponse(
    val status: Int = 0,
    val message: String? = null,
    val data: List<BarberoDto>? = null
)
