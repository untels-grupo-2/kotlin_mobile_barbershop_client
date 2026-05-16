package com.diamond.appcliente.dto.usuario

import com.google.gson.annotations.SerializedName

data class UsuarioDto(
    @SerializedName("apellido") var apellido: String? = null,
    @SerializedName("celular") var celular: String? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("nombre") var nombre: String? = null,
    @SerializedName("urlUsuario") var urlUsuario: String? = null,
    @SerializedName("username") var username: String? = null,
    @SerializedName("usuario_id") var usuario_id: Long? = null
)
