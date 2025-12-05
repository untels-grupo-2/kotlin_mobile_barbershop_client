package com.diamond.appcliente.viewmodel;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.diamond.appcliente.api.ApiClient;
import com.diamond.appcliente.api.AuthApiService;
import com.diamond.appcliente.dto.reserva.DtoReserva;  // Usar DtoReserva
import com.diamond.appcliente.dto.reserva.ReservaResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GestionarReservaViewModel {

    public interface ReservaCallback {
        void onSuccess(String message);
        void onError(String message);
    }

    public void enviarReserva(Context context, DtoReserva request, ReservaCallback callback) { // Cambiar a DtoReserva
        AuthApiService api = ApiClient.getRetrofit(context, true).create(AuthApiService.class);

        // Llamar la API para crear la reserva
        Call<ReservaResponse> call = api.crearReserva(request);

        call.enqueue(new Callback<ReservaResponse>() {
            @Override
            public void onResponse(Call<ReservaResponse> call, Response<ReservaResponse> response) {
                if (response.isSuccessful()) {
                    // Si la respuesta es exitosa, llamar el callback de éxito
                    callback.onSuccess("Reserva creada con éxito");
                } else {
                    // Si la respuesta no es exitosa, capturar más detalles sobre el error
                    String errorMessage = "Error desconocido";
                    try {
                        // Si el cuerpo de la respuesta tiene un mensaje específico, obtenerlo
                        errorMessage = response.errorBody() != null ? response.errorBody().string() : response.message();
                    } catch (Exception e) {
                        // En caso de un error al intentar obtener el errorBody
                        errorMessage = "Error al obtener los detalles del error";
                    }
                    // Mostrar el mensaje de error y pasar al callback
                    Toast.makeText(context, "Error al crear la reserva: " + errorMessage, Toast.LENGTH_LONG).show();
                    callback.onError("Error al crear la reserva: " + errorMessage);
                }
            }

            @Override
            public void onFailure(Call<ReservaResponse> call, Throwable t) {
                // Capturar los errores de red o de conexión
                String errorMessage = "Fallo en la conexión: " + t.getMessage();
                Log.e("ReservaError", errorMessage, t);  // Log detallado del error
                callback.onError(errorMessage);
            }
        });
    }

    public void crearReservaRecompensa(Context context, DtoReserva request, ReservaCallback callback) {
        AuthApiService api = ApiClient.getRetrofit(context, true).create(AuthApiService.class);

        // Llamar a la API para crear la reserva con recompensa
        Call<ReservaResponse> call = api.crearReservaRecompensa(request); // Cambiar a ReservaResponse

        call.enqueue(new Callback<ReservaResponse>() {
            @Override
            public void onResponse(Call<ReservaResponse> call, Response<ReservaResponse> response) {
                if (response.isSuccessful()) {
                    // Si la respuesta es exitosa, llamar el callback de éxito
                    callback.onSuccess("Reserva con recompensa creada con éxito");
                } else {
                    // Si la respuesta no es exitosa, capturar más detalles sobre el error
                    String errorMessage = "Error desconocido";
                    try {
                        errorMessage = response.errorBody() != null ? response.errorBody().string() : response.message();
                    } catch (Exception e) {
                        errorMessage = "Error al obtener los detalles del error";
                    }
                    Toast.makeText(context, "Error al crear la reserva: " + errorMessage, Toast.LENGTH_LONG).show();
                    callback.onError("Error al crear la reserva: " + errorMessage);
                }
            }

            @Override
            public void onFailure(Call<ReservaResponse> call, Throwable t) {
                String errorMessage = "Fallo en la conexión: " + t.getMessage();
                Log.e("ReservaError", errorMessage, t);
                callback.onError(errorMessage);
            }
        });
    }


}
