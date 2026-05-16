package com.diamond.appcliente.dto.usuario

data class UsuarioResponse(
    var status: Int = 0,
    var message: String? = null,
    var data: UsuarioDto? = null
)
