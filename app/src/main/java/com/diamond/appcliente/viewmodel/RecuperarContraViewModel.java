package com.diamond.appcliente.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.diamond.appcliente.api.ApiClient;
import com.diamond.appcliente.api.AuthApiService;
import com.diamond.appcliente.dto.recuperacion.RecuperacionRequest;
import com.diamond.appcliente.dto.recuperacion.RecuperacionResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecuperarContraViewModel extends AndroidViewModel {

    private final AuthApiService authApiService;
    public final MutableLiveData<String> resultado = new MutableLiveData<>();
    public final MutableLiveData<String> error = new MutableLiveData<>();

    public RecuperarContraViewModel(@NonNull Application application) {
        super(application);
        authApiService = ApiClient.getRetrofit(application, false).create(AuthApiService.class);
    }

    public void recuperar(String usuario, String correo) {
        RecuperacionRequest request = new RecuperacionRequest(usuario, correo);
        Call<RecuperacionResponse> call = authApiService.recuperarContraseña(request);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<RecuperacionResponse> call, Response<RecuperacionResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    RecuperacionResponse body = response.body();
                    if (body.getStatus() == 200) {
                        resultado.postValue(body.getMessage());
                    } else {
                        error.postValue(body.getMessage());
                    }
                } else {
                    String rawError = "";
                    try {
                        rawError = response.errorBody() != null ? response.errorBody().string() : "sin cuerpo";
                    } catch (Exception e) {
                        rawError = "error al leer errorBody";
                    }

                    error.postValue("Error HTTP " + response.code() + ": " + rawError);
                }
            }


            @Override
            public void onFailure(Call<RecuperacionResponse> call, Throwable t) {
                error.postValue("Error de conexión: " + t.getMessage());
            }
        });
    }
}

