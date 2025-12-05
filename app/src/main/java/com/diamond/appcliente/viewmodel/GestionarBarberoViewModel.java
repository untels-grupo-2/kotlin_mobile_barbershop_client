package com.diamond.appcliente.viewmodel;

import android.content.Context;

import com.diamond.appcliente.api.ApiClient;
import com.diamond.appcliente.api.AuthApiService;
import com.diamond.appcliente.dto.barbero.BarberoDto;
import com.diamond.appcliente.dto.barbero.BarberoRequest;
import com.diamond.appcliente.dto.barbero.BarberoResponse;
import com.diamond.appcliente.dto.barbero.BarberoSimpleResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GestionarBarberoViewModel {

    public interface BarberoCallback {
        void onSuccess(List<BarberoDto> barberos);
        void onError(String mensaje);
    }

    public interface BarberoOperacionCallback {
        void onSuccess(String mensaje);
        void onError(String mensaje);
    }

    public interface ActualizarCallback {
        void onSuccess(String mensaje);
        void onError(String mensaje);
    }

    public void obtenerBarberos(Context context, BarberoCallback callback) {
        AuthApiService api = ApiClient.getRetrofit(context, true).create(AuthApiService.class);
        Call<BarberoResponse> call = api.listarBarberos();

        call.enqueue(new Callback<BarberoResponse>() {
            @Override
            public void onResponse(Call<BarberoResponse> call, Response<BarberoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getData());
                } else {
                    callback.onError("Error al obtener barberos");
                }
            }

            @Override
            public void onFailure(Call<BarberoResponse> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void crearBarbero(Context context, String nombre, BarberoOperacionCallback callback) {
        AuthApiService api = ApiClient.getRetrofit(context, true).create(AuthApiService.class);
        BarberoRequest nuevo = new BarberoRequest(nombre);

        api.crearBarbero(nuevo).enqueue(new Callback<BarberoResponse>() {
            @Override
            public void onResponse(Call<BarberoResponse> call, Response<BarberoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getMessage());
                } else {
                    callback.onError("Error al crear barbero");
                }
            }

            @Override
            public void onFailure(Call<BarberoResponse> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void actualizarBarbero(Context context, int id, String nuevoNombre, ActualizarCallback callback) {
        AuthApiService api = ApiClient.getRetrofit(context, true).create(AuthApiService.class);
        BarberoRequest request = new BarberoRequest(nuevoNombre);

        api.actualizarBarbero(id, request).enqueue(new Callback<BarberoSimpleResponse>() {
            @Override
            public void onResponse(Call<BarberoSimpleResponse> call, Response<BarberoSimpleResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getMessage());
                } else {
                    callback.onError("Error al actualizar barbero");
                }
            }

            @Override
            public void onFailure(Call<BarberoSimpleResponse> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });

    }

    public void eliminarBarbero(Context context, int id, BarberoOperacionCallback callback) {
        AuthApiService api = ApiClient.getRetrofit(context, true).create(AuthApiService.class);

        api.eliminarBarbero(id).enqueue(new Callback<BarberoResponse>() {
            @Override
            public void onResponse(Call<BarberoResponse> call, Response<BarberoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getMessage());
                } else {
                    callback.onError("Error al eliminar barbero");
                }
            }

            @Override
            public void onFailure(Call<BarberoResponse> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
}
