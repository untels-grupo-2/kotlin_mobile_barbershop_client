package com.diamond.appcliente.dto.barbero

data class BarberoSimpleResponse(
    val status: Int = 0,
    val message: String? = null,
    val data: BarberoDto? = null
)
