package com.diamond.appcliente.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthenticatedApi

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class UnauthenticatedApi
