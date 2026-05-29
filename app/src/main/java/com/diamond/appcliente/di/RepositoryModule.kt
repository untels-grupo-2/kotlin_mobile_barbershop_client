package com.diamond.appcliente.di

import com.diamond.appcliente.repository.AuthRepository
import com.diamond.appcliente.repository.BarberoRepository
import com.diamond.appcliente.repository.HorarioRangoRepository
import com.diamond.appcliente.repository.ReservaRepository
import com.diamond.appcliente.repository.ServicioRepository
import com.diamond.appcliente.repository.UsuarioRepository
import com.diamond.appcliente.repository.ValoracionRepository
import com.diamond.appcliente.repository.impl.AuthRepositoryImpl
import com.diamond.appcliente.repository.impl.BarberoRepositoryImpl
import com.diamond.appcliente.repository.impl.HorarioRangoRepositoryImpl
import com.diamond.appcliente.repository.impl.ReservaRepositoryImpl
import com.diamond.appcliente.repository.impl.ServicioRepositoryImpl
import com.diamond.appcliente.repository.impl.UsuarioRepositoryImpl
import com.diamond.appcliente.repository.impl.ValoracionRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds @Singleton
    abstract fun bindBarberoRepository(impl: BarberoRepositoryImpl): BarberoRepository

    @Binds @Singleton
    abstract fun bindServicioRepository(impl: ServicioRepositoryImpl): ServicioRepository

    @Binds @Singleton
    abstract fun bindReservaRepository(impl: ReservaRepositoryImpl): ReservaRepository

    @Binds @Singleton
    abstract fun bindUsuarioRepository(impl: UsuarioRepositoryImpl): UsuarioRepository

    @Binds @Singleton
    abstract fun bindValoracionRepository(impl: ValoracionRepositoryImpl): ValoracionRepository

    @Binds @Singleton
    abstract fun bindHorarioRangoRepository(impl: HorarioRangoRepositoryImpl): HorarioRangoRepository
}
