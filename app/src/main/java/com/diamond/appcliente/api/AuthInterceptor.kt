package com.diamond.appcliente.api

import com.diamond.appcliente.util.PreferenciasHelper
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val preferenciasHelper: PreferenciasHelper,
    private val tokenManager: TokenManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = preferenciasHelper.obtenerToken()
        val originalRequest = chain.request()

        val requestWithToken = if (token != null) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }

        val response = chain.proceed(requestWithToken)

        if (response.code == 401) {
            response.close()
            val newToken = tokenManager.refreshToken()
            if (newToken != null) {
                val newRequest = originalRequest.newBuilder()
                    .header("Authorization", "Bearer $newToken")
                    .build()
                return chain.proceed(newRequest)
            }
        }

        return response
    }
}
