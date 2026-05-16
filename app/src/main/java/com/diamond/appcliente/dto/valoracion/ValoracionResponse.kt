package com.diamond.appcliente.dto.valoracion

data class ValoracionResponse(
    var valoracion_id: Long? = null,
    var valoracion: Int? = null,
    var util: Boolean? = null,
    var mensaje: String? = null,
    var usuario_nombre: String? = null
)
