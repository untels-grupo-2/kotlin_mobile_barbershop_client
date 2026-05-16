package com.diamond.appcliente.dto.horario

data class GenericResponse(
    var status: Int = 0,
    var message: String? = null,
    var data: Any? = null
)
