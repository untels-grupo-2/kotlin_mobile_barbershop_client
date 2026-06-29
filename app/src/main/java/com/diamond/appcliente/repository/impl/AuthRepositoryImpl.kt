package com.diamond.appcliente.repository.impl

import com.auth0.android.jwt.JWT
import com.diamond.appcliente.api.AuthApiService
import com.diamond.appcliente.di.UnauthenticatedApi
import com.diamond.barbershop.shared.dto.login.LoginRequest
import com.diamond.barbershop.shared.dto.recuperacion.RecuperacionRequest
import com.diamond.appcliente.repository.AuthRepository
import com.diamond.appcliente.repository.UserSession
import com.diamond.barbershop.shared.util.PreferenciasHelper
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    @UnauthenticatedApi private val apiService: AuthApiService,
    private val preferenciasHelper: PreferenciasHelper
) : AuthRepository {

    override suspend fun login(usuario: String, password: String): UserSession {
        val response = apiService.login(LoginRequest(usuario, password))
        if (!response.isSuccessful || response.body() == null) {
            if (response.code() == 401) throw Exception("INVALID")
            throw Exception("ERROR_${response.code()}")
        }
        val body = response.body()!!
        val token = body.data?.token ?: throw Exception("Token nulo")
        val refreshToken = body.data?.refreshToken ?: throw Exception("Refresh token nulo")
        val jwt = JWT(token)

        if (jwt.getClaim("rol").asString() != "USER") throw Exception("NO_USER")

        preferenciasHelper.guardarToken(token)
        preferenciasHelper.guardarRefreshToken(refreshToken)

        return UserSession(
            nombre = jwt.getClaim("nombre").asString() ?: "",
            apellido = jwt.getClaim("apellido").asString() ?: "",
            urlUsuario = jwt.getClaim("urlUsuario").asString() ?: ""
        )
    }

    override suspend fun recuperarContraseña(usuario: String, correo: String): String {
        val response = apiService.recuperarContraseña(RecuperacionRequest(usuario, correo))
        if (response.isSuccessful && response.body() != null) {
            val body = response.body()!!
            if (body.status == 200) return body.message ?: "Correo enviado"
            throw Exception(body.message ?: "Error al recuperar contraseña")
        }
        val rawError = try { response.errorBody()?.string() ?: "sin cuerpo" } catch (e: Exception) { "error al leer errorBody" }
        throw Exception("Error HTTP ${response.code()}: $rawError")
    }
}
