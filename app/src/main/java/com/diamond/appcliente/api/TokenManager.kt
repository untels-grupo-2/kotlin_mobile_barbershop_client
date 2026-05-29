package com.diamond.appcliente.api

import com.diamond.appcliente.di.UnauthenticatedApi
import com.diamond.appcliente.dto.refresh.RefreshRequest
import com.diamond.appcliente.util.PreferenciasHelper
import javax.inject.Inject

class TokenManager @Inject constructor(
    private val prefs: PreferenciasHelper,
    @UnauthenticatedApi private val authApi: AuthApiService
) {

    @Synchronized
    fun refreshToken(expiredToken: String): String? {
        // Si otro hilo ya refrescó el token mientras esperábamos el lock, lo devolvemos directamente
        val currentToken = prefs.obtenerToken()
        if (currentToken != null && currentToken != expiredToken) {
            return currentToken
        }

        val refreshToken = prefs.obtenerRefreshToken() ?: run {
            prefs.limpiarPreferencias()
            return null
        }

        return try {
            val response = authApi.refresh(RefreshRequest(refreshToken)).execute()
            if (response.isSuccessful) {
                val body = response.body()?.data ?: return null
                val newToken = body.token ?: return null
                val newRefresh = body.refreshToken ?: return null
                prefs.guardarToken(newToken)
                prefs.guardarRefreshToken(newRefresh)
                newToken
            } else {
                // Refresh token caducado — limpiar sesión
                prefs.limpiarPreferencias()
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}
