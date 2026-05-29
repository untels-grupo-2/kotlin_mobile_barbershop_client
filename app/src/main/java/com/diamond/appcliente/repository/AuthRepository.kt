package com.diamond.appcliente.repository

data class UserSession(
    val nombre: String,
    val apellido: String,
    val urlUsuario: String
)

interface AuthRepository {
    suspend fun login(usuario: String, password: String): UserSession
    suspend fun recuperarContraseña(usuario: String, correo: String): String
}
