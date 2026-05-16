package com.diamond.appcliente.api

import android.content.Context
import com.diamond.appcliente.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    fun getRetrofit(context: Context, withAuth: Boolean): Retrofit {
        val clientBuilder = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)

        if (withAuth) {
            clientBuilder.addInterceptor(AuthInterceptor(context))
        }

        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(clientBuilder.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
