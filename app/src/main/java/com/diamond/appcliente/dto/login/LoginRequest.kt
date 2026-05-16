package com.diamond.appcliente.dto.login

data class LoginRequest(
    val username: String,
    val password: String,
    val nombre: String? = null,
    val apellido: String? = null,
    val email: String? = null,
    val celular: String? = null
)
