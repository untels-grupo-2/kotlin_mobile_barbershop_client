package com.diamond.appcliente.api

import com.diamond.appcliente.dto.barbero.BarberoListResponse
import com.diamond.barbershop.shared.dto.barbero.BarberoRequest
import com.diamond.barbershop.shared.dto.barbero.BarberoResponse
import com.diamond.barbershop.shared.dto.barbero.BarberoSimpleResponse
import com.diamond.appcliente.dto.common.ApiResponse
import com.diamond.appcliente.dto.horariorango.HorarioRangoResponse
import com.diamond.barbershop.shared.dto.login.LoginRequest
import com.diamond.appcliente.dto.login.LoginResponse
import com.diamond.barbershop.shared.dto.recuperacion.RecuperacionRequest
import com.diamond.barbershop.shared.dto.recuperacion.RecuperacionResponse
import com.diamond.barbershop.shared.dto.refresh.RefreshRequest
import com.diamond.appcliente.dto.reserva.DtoReserva
import com.diamond.appcliente.dto.reserva.ReservaListResponse
import com.diamond.appcliente.dto.reserva.ReservaResponse
import com.diamond.barbershop.shared.dto.servicio.ServicioRequest
import com.diamond.barbershop.shared.dto.servicio.ServicioResponse
import com.diamond.barbershop.shared.dto.servicio.ServicioSimpleResponse
import com.diamond.appcliente.dto.usuario.UsuarioResponse
import com.diamond.appcliente.dto.valoracion.ValoracionRequest
import com.diamond.appcliente.dto.valoracion.ValoracionResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface AuthApiService {

    @POST("api/autenticacion/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @POST("api/autenticacion/registro/cliente")
    suspend fun register(@Body registerRequest: LoginRequest): Response<Void>

    // Keep as Call<T> — used synchronously in TokenManager inside OkHttp interceptor
    @POST("api/autenticacion/refresh-token")
    fun refresh(@Body refreshRequest: RefreshRequest): Call<LoginResponse>

    @POST("api/email/password")
    suspend fun recuperarContraseña(@Body recuperacionRequest: RecuperacionRequest): Response<RecuperacionResponse>

    @GET("api/barberos")
    suspend fun listarBarberos(): Response<BarberoResponse>

    @POST("api/barberos")
    suspend fun crearBarbero(@Body request: BarberoRequest): Response<BarberoResponse>

    @DELETE("api/barberos/{id}")
    suspend fun eliminarBarbero(@Path("id") id: Int): Response<BarberoResponse>

    @PUT("api/barberos/{id}")
    suspend fun actualizarBarbero(@Path("id") id: Int, @Body barberoRequest: BarberoRequest): Response<BarberoSimpleResponse>

    @GET("api/servicios")
    suspend fun listarServicios(): Response<ServicioResponse>

    @POST("api/servicios")
    suspend fun crearServicio(@Body request: ServicioRequest): Response<ServicioResponse>

    @DELETE("api/servicios/{id}")
    suspend fun eliminarServicio(@Path("id") id: Int): Response<ServicioResponse>

    @PUT("api/servicios/{id}")
    suspend fun actualizarServicio(@Path("id") id: Int, @Body request: ServicioRequest): Response<ServicioSimpleResponse>

    @POST("api/valoraciones")
    suspend fun crearValoracion(@Body valoracionRequest: ValoracionRequest): Response<ValoracionResponse>

    @GET("api/rangos-horario")
    suspend fun obtenerHorariosRangos(): Response<HorarioRangoResponse>

    @GET("api/usuarios/me")
    suspend fun obtenerMiUsuario(): Response<UsuarioResponse>

    @GET("api/reservas/barberos-disponibles")
    suspend fun obtenerBarberosDisponibles(
        @Query("fecha") fecha: String,
        @Query("tipoHorarioId") tipoHorarioId: Long,
        @Query("horarioRangoId") horarioRangoId: Long
    ): Response<BarberoListResponse>

    @POST("api/reservas")
    suspend fun crearReserva(@Body dtoReserva: DtoReserva): Response<ReservaResponse>

    @GET("api/reservas/mis-reservas")
    suspend fun listarMisReservas(): Response<ReservaListResponse>

    @POST("api/reservas/recompensa")
    suspend fun crearReservaRecompensa(@Body dtoReserva: DtoReserva): Response<ReservaResponse>

    @Multipart
    @PUT("api/usuarios/me")
    suspend fun actualizarMiPerfil(
        @Part("dtoUsuario") requestBody: RequestBody,
        @Part part: MultipartBody.Part
    ): Response<ApiResponse<Any>>

    @Multipart
    @POST("api/reservas/{reservaId}/comprobante")
    suspend fun subirComprobante(
        @Path("reservaId") reservaId: Long,
        @Part part: MultipartBody.Part
    ): Response<ApiResponse<Any>>
}
