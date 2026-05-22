package com.diamond.appcliente.api

import com.diamond.appcliente.dto.barbero.BarberoListResponse
import com.diamond.appcliente.dto.barbero.BarberoRequest
import com.diamond.appcliente.dto.barbero.BarberoResponse
import com.diamond.appcliente.dto.barbero.BarberoSimpleResponse
import com.diamond.appcliente.dto.common.ApiResponse
import com.diamond.appcliente.dto.horariorango.HorarioRangoResponse
import com.diamond.appcliente.dto.login.LoginRequest
import com.diamond.appcliente.dto.login.LoginResponse
import com.diamond.appcliente.dto.recuperacion.RecuperacionRequest
import com.diamond.appcliente.dto.recuperacion.RecuperacionResponse
import com.diamond.appcliente.dto.refresh.RefreshRequest
import com.diamond.appcliente.dto.reserva.DtoReserva
import com.diamond.appcliente.dto.reserva.ReservaListResponse
import com.diamond.appcliente.dto.reserva.ReservaResponse
import com.diamond.appcliente.dto.servicio.ServicioRequest
import com.diamond.appcliente.dto.servicio.ServicioResponse
import com.diamond.appcliente.dto.servicio.ServicioSimpleResponse
import com.diamond.appcliente.dto.usuario.UsuarioResponse
import com.diamond.appcliente.dto.valoracion.ValoracionRequest
import com.diamond.appcliente.dto.valoracion.ValoracionResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface AuthApiService {

    @POST("api/auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @POST("api/auth/register")
    suspend fun register(@Body registerRequest: LoginRequest): Response<Void>

    // Keep as Call<T> — used synchronously in TokenManager inside OkHttp interceptor
    @POST("api/auth/refreshToken")
    fun refresh(@Body refreshRequest: RefreshRequest): Call<LoginResponse>

    @POST("emailPassword/sendEmail")
    suspend fun recuperarContraseña(@Body recuperacionRequest: RecuperacionRequest): Response<RecuperacionResponse>

    @GET("api/barbero/listar")
    suspend fun listarBarberos(): Response<BarberoResponse>

    @POST("api/barbero/crear")
    suspend fun crearBarbero(@Body request: BarberoRequest): Response<BarberoResponse>

    @DELETE("api/barbero/eliminar/{id}")
    suspend fun eliminarBarbero(@Path("id") id: Int): Response<BarberoResponse>

    @PUT("api/barbero/actualizar/{id}")
    suspend fun actualizarBarbero(@Path("id") id: Int, @Body barberoRequest: BarberoRequest): Response<BarberoSimpleResponse>

    @GET("api/servicio/listar")
    suspend fun listarServicios(): Response<ServicioResponse>

    @POST("api/servicio/crear")
    suspend fun crearServicio(@Body request: ServicioRequest): Response<ServicioResponse>

    @DELETE("api/servicio/eliminar/{id}")
    suspend fun eliminarServicio(@Path("id") id: Int): Response<ServicioResponse>

    @PUT("api/servicio/actualizar/{id}")
    suspend fun actualizarServicio(@Path("id") id: Int, @Body request: ServicioRequest): Response<ServicioSimpleResponse>

    @POST("api/valoracion/crear")
    suspend fun crearValoracion(@Body valoracionRequest: ValoracionRequest): Response<ValoracionResponse>

    @GET("api/rango/listar")
    suspend fun obtenerHorariosRangos(@Query("tipoHorarioId") tipoHorarioId: Int): Response<HorarioRangoResponse>

    @GET("api/usuario/listarme")
    suspend fun obtenerMiUsuario(): Response<UsuarioResponse>

    @GET("api/reserva/barberos-disponibles")
    suspend fun obtenerBarberosDisponibles(
        @Header("Authorization") token: String,
        @Query("fecha") fecha: String,
        @Query("tipoHorarioId") tipoHorarioId: Long,
        @Query("horarioRangoId") horarioRangoId: Long
    ): Response<BarberoListResponse>

    @POST("api/reserva/crear")
    suspend fun crearReserva(@Body dtoReserva: DtoReserva): Response<ReservaResponse>

    @GET("api/reserva/mis-reservas")
    suspend fun listarMisReservas(): Response<ReservaListResponse>

    @POST("api/reserva/crearReservaRecompensa")
    suspend fun crearReservaRecompensa(@Body dtoReserva: DtoReserva): Response<ReservaResponse>

    @Multipart
    @PUT("api/usuario/actualizar-mi-perfil")
    suspend fun actualizarMiPerfil(
        @Part("dtoUsuario") requestBody: RequestBody,
        @Part part: MultipartBody.Part
    ): Response<ApiResponse<Any>>

    @Multipart
    @POST("api/reserva/subir-comprobante/{reservaId}")
    suspend fun subirComprobante(
        @Path("reservaId") reservaId: Long,
        @Part part: MultipartBody.Part
    ): Response<ApiResponse<Any>>
}
