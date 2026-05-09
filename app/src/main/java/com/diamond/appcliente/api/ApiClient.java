package com.diamond.appcliente.api;

import android.content.Context;

import com.diamond.appcliente.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    public static Retrofit getRetrofit(Context context, boolean withAuth) {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)  // Espera para conectarse al servidor
                .readTimeout(30, TimeUnit.SECONDS)     // Espera para leer respuesta
                .writeTimeout(30, TimeUnit.SECONDS); // Espera para enviar datos

        if (withAuth) {
            clientBuilder.addInterceptor(new AuthInterceptor(context));
        }

        return new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .client(clientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}