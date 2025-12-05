package com.diamond.appcliente.viewmodel;

import android.content.Context;
import android.widget.Toast;

import com.diamond.appcliente.api.ApiClient;
import com.diamond.appcliente.api.AuthApiService;
import com.diamond.appcliente.dto.valoracion.ValoracionRequest;
import com.diamond.appcliente.dto.valoracion.ValoracionResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GestionarValoracionViewModel {

    public interface ValoracionCallback {
        void onSuccess(String message);
        void onError(String message);
    }

    public void enviarValoracion(Context context, ValoracionRequest request, ValoracionCallback callback) {
        AuthApiService api = ApiClient.getRetrofit(context, true).create(AuthApiService.class);

        Call<ValoracionResponse> call = api.crearValoracion(request);

        call.enqueue(new Callback<ValoracionResponse>() {
            @Override
            public void onResponse(Call<ValoracionResponse> call, Response<ValoracionResponse> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess("Valoración enviada con éxito");
                } else {
                    // Mostrar el mensaje de error
                    String errorMessage = response.message();
                    Toast.makeText(context, "Error al enviar la valoración: " + errorMessage, Toast.LENGTH_LONG).show();
                    callback.onError("Error al enviar la valoración");
                }
            }

            @Override
            public void onFailure(Call<ValoracionResponse> call, Throwable t) {
                // Mostrar el error en caso de fallo de la conexión
                callback.onError(t.getMessage());
            }
        });
    }


}
