package com.diamond.appcliente.di

import android.content.Context
import com.diamond.appcliente.BuildConfig
import com.diamond.appcliente.api.AuthApiService
import com.diamond.appcliente.api.AuthInterceptor
import com.diamond.appcliente.api.TokenManager
import com.diamond.appcliente.util.PreferenciasHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun providePreferenciasHelper(@ApplicationContext context: Context): PreferenciasHelper =
        PreferenciasHelper(context)

    @Provides
    @Singleton
    @UnauthenticatedApi
    fun provideUnauthOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

    @Provides
    @Singleton
    @UnauthenticatedApi
    fun provideUnauthRetrofit(@UnauthenticatedApi client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    @UnauthenticatedApi
    fun provideUnauthApiService(@UnauthenticatedApi retrofit: Retrofit): AuthApiService =
        retrofit.create(AuthApiService::class.java)

    @Provides
    @Singleton
    fun provideTokenManager(
        preferenciasHelper: PreferenciasHelper,
        @UnauthenticatedApi authApiService: AuthApiService
    ): TokenManager = TokenManager(preferenciasHelper, authApiService)

    @Provides
    @Singleton
    fun provideAuthInterceptor(
        preferenciasHelper: PreferenciasHelper,
        tokenManager: TokenManager
    ): AuthInterceptor = AuthInterceptor(preferenciasHelper, tokenManager)

    @Provides
    @Singleton
    @AuthenticatedApi
    fun provideAuthOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(authInterceptor)
            .build()

    @Provides
    @Singleton
    @AuthenticatedApi
    fun provideAuthRetrofit(@AuthenticatedApi client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    @AuthenticatedApi
    fun provideAuthApiService(@AuthenticatedApi retrofit: Retrofit): AuthApiService =
        retrofit.create(AuthApiService::class.java)
}
