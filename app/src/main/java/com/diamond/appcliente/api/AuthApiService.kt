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
import retrofit2.http.*

interface AuthApiService {

    @POST("api/auth/login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @POST("api/auth/register")
    fun register(@Body registerRequest: LoginRequest): Call<Void>

    @POST("api/auth/refreshToken")
    fun refresh(@Body refreshRequest: RefreshRequest): Call<LoginResponse>

    @POST("emailPassword/sendEmail")
    fun recuperarContraseña(@Body recuperacionRequest: RecuperacionRequest): Call<RecuperacionResponse>

    @GET("api/barbero/listar")
    fun listarBarberos(): Call<BarberoResponse>

    @POST("api/barbero/crear")
    fun crearBarbero(@Body request: BarberoRequest): Call<BarberoResponse>

    @DELETE("api/barbero/eliminar/{id}")
    fun eliminarBarbero(@Path("id") id: Int): Call<BarberoResponse>

    @PUT("api/barbero/actualizar/{id}")
    fun actualizarBarbero(@Path("id") id: Int, @Body barberoRequest: BarberoRequest): Call<BarberoSimpleResponse>

    @GET("api/servicio/listar")
    fun listarServicios(): Call<ServicioResponse>

    @POST("api/servicio/crear")
    fun crearServicio(@Body request: ServicioRequest): Call<ServicioResponse>

    @DELETE("api/servicio/eliminar/{id}")
    fun eliminarServicio(@Path("id") id: Int): Call<ServicioResponse>

    @PUT("api/servicio/actualizar/{id}")
    fun actualizarServicio(@Path("id") id: Int, @Body request: ServicioRequest): Call<ServicioSimpleResponse>

    @POST("api/valoracion/crear")
    fun crearValoracion(@Body valoracionRequest: ValoracionRequest): Call<ValoracionResponse>

    @GET("api/rango/listar")
    fun obtenerHorariosRangos(@Query("tipoHorarioId") tipoHorarioId: Int): Call<HorarioRangoResponse>

    @GET("api/usuario/listarme")
    fun obtenerMiUsuario(): Call<UsuarioResponse>

    @GET("api/reserva/barberos-disponibles")
    fun obtenerBarberosDisponibles(
        @Header("Authorization") token: String,
        @Query("fecha") fecha: String,
        @Query("tipoHorarioId") tipoHorarioId: Long,
        @Query("horarioRangoId") horarioRangoId: Long
    ): Call<BarberoListResponse>

    @POST("api/reserva/crear")
    fun crearReserva(@Body dtoReserva: DtoReserva): Call<ReservaResponse>

    @GET("api/reserva/mis-reservas")
    fun listarMisReservas(): Call<ReservaListResponse>

    @POST("api/reserva/crearReservaRecompensa")
    fun crearReservaRecompensa(@Body dtoReserva: DtoReserva): Call<ReservaResponse>

    @Multipart
    @PUT("api/usuario/actualizar-mi-perfil")
    fun actualizarMiPerfil(
        @Part("dtoUsuario") requestBody: RequestBody,
        @Part part: MultipartBody.Part
    ): Call<ApiResponse<Any>>

    @Multipart
    @POST("api/reserva/subir-comprobante/{reservaId}")
    fun subirComprobante(
        @Path("reservaId") reservaId: Long,
        @Part part: MultipartBody.Part
    ): Call<ApiResponse<Any>>
}
