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
    fun refreshToken(): String? {
        val currentRefresh = prefs.obtenerRefreshToken() ?: return null

        val response = authApi.refresh(RefreshRequest(currentRefresh)).execute()

        if (response.isSuccessful) {
            val body = response.body()?.data ?: return null
            val newToken = body.token ?: return null
            val newRefresh = body.refreshToken ?: return null

            prefs.guardarToken(newToken)
            prefs.guardarRefreshToken(newRefresh)

            return newToken
        }

        return null
    }
}
