package com.diamond.appcliente.api

import android.content.Context
import com.diamond.appcliente.dto.refresh.RefreshRequest
import com.diamond.appcliente.util.PreferenciasHelper

class TokenManager(context: Context) {

    private val prefs = PreferenciasHelper(context)
    private val authApi = ApiClient.getRetrofit(context, false).create(AuthApiService::class.java)

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
