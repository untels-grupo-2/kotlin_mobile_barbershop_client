package com.diamond.appcliente.api;

import com.diamond.appcliente.dto.barbero.BarberoListResponse;
import com.diamond.appcliente.dto.barbero.BarberoRequest;
import com.diamond.appcliente.dto.barbero.BarberoResponse;
import com.diamond.appcliente.dto.barbero.BarberoSimpleResponse;
import com.diamond.appcliente.dto.common.ApiResponse;
import com.diamond.appcliente.dto.horariorango.HorarioRangoResponse;
import com.diamond.appcliente.dto.login.LoginRequest;
import com.diamond.appcliente.dto.login.LoginResponse;
import com.diamond.appcliente.dto.recuperacion.RecuperacionRequest;
import com.diamond.appcliente.dto.recuperacion.RecuperacionResponse;
import com.diamond.appcliente.dto.refresh.RefreshRequest;
import com.diamond.appcliente.dto.reserva.DtoReserva;
import com.diamond.appcliente.dto.reserva.ReservaListResponse;
import com.diamond.appcliente.dto.reserva.ReservaResponse;
import com.diamond.appcliente.dto.servicio.ServicioRequest;
import com.diamond.appcliente.dto.servicio.ServicioResponse;
import com.diamond.appcliente.dto.servicio.ServicioSimpleResponse;
import com.diamond.appcliente.dto.usuario.UsuarioResponse;
import com.diamond.appcliente.dto.valoracion.ValoracionRequest;
import com.diamond.appcliente.dto.valoracion.ValoracionResponse;  // Importa la clase de respuesta de Valoración

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AuthApiService {
    @POST("api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("api/auth/register")
    Call<Void> register(@Body LoginRequest registerRequest);

    @POST("api/servicio/crear")
    Call<Void> crear(@Body ServicioRequest serviciosRequest);

    @POST("api/auth/refreshToken")
    Call<LoginResponse> refresh(@Body RefreshRequest refreshRequest);

    @POST("emailPassword/sendEmail")
    Call<RecuperacionResponse> recuperarContraseña(@Body RecuperacionRequest recuperacionRequest);

    @GET("api/barbero/listar")
    Call<BarberoResponse> listarBarberos();

    @POST("api/barbero/crear")
    Call<BarberoResponse> crearBarbero(@Body BarberoRequest request);

    @DELETE("api/barbero/eliminar/{id}")
    Call<BarberoResponse> eliminarBarbero(@Path("id") int id);

    @PUT("api/barbero/actualizar/{id}")
    Call<BarberoSimpleResponse> actualizarBarbero(@Path("id") int id, @Body BarberoRequest barberoRequest);

    @GET("api/servicio/listar")
    Call<ServicioResponse> listarServicios();

    @POST("api/servicio/crear")
    Call<ServicioResponse> crearServicio(@Body ServicioRequest request);

    @DELETE("api/servicio/eliminar/{id}")
    Call<ServicioResponse> eliminarServicio(@Path("id") int id);

    @PUT("api/servicio/actualizar/{id}")
    Call<ServicioSimpleResponse> actualizarServicio(@Path("id") int id, @Body ServicioRequest request);

    // Agregado para crear una valoración
    @POST("api/valoracion/crear")
    Call<ValoracionResponse> crearValoracion(@Body ValoracionRequest valoracionRequest);// Asegúrate de que el backend espere un ValoracionRequest

    @GET("api/rango/listar")
    Call<HorarioRangoResponse> obtenerHorariosRangos(@Query("tipoHorarioId") int tipoHorarioId);

    @GET("api/usuario/listarme")
    Call<UsuarioResponse> obtenerMiUsuario();


    @GET("api/reserva/barberos-disponibles")
    Call<BarberoListResponse> obtenerBarberosDisponibles(
            @Header("Authorization") String token, // Pasamos el token como encabezado
            @Query("fecha") String fecha,
            @Query("tipoHorarioId") Long tipoHorarioId,
            @Query("horarioRangoId") Long horarioRangoId
    );

    @POST("api/reserva/crear")
    Call<ReservaResponse> crearReserva(@Body DtoReserva dtoReserva);

    @GET("api/reserva/mis-reservas")
    Call<ReservaListResponse> listarMisReservas();

    @POST("api/reserva/crearReservaRecompensa")
    Call<ReservaResponse> crearReservaRecompensa(@Body DtoReserva dtoReserva); // Usamos ReservaResponse

    @PUT("api/usuario/actualizar-mi-perfil")
    @Multipart
    Call<ApiResponse<Object>> actualizarMiPerfil(@Part("dtoUsuario") RequestBody requestBody, @Part MultipartBody.Part part);

    @POST("api/reserva/subir-comprobante/{reservaId}")
    @Multipart
    Call<ApiResponse<Object>> subirComprobante(@Path("reservaId") Long l, @Part MultipartBody.Part part);







}
