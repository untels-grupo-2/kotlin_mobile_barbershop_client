package com.diamond.appcliente.viewmodel;

import android.content.Context;
import android.util.Log;

import com.diamond.appcliente.api.ApiClient;
import com.diamond.appcliente.api.AuthApiService;
import com.diamond.appcliente.dto.horariorango.HorarioRangoDto;
import com.diamond.appcliente.dto.horariorango.HorarioRangoResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GestionarHorarioRangoViewModel {

    public interface HorarioRangoCallback {
        void onSuccess(List<HorarioRangoDto> horarioRangos);
        void onError(String mensaje);
    }

    public void obtenerHorariosRangos(Context context, int tipoHorarioId, HorarioRangoCallback callback) {
        AuthApiService api = ApiClient.getRetrofit(context, true).create(AuthApiService.class);
        Call<HorarioRangoResponse> call = api.obtenerHorariosRangos(tipoHorarioId); // Asegúrate de que este endpoint sea correcto

        call.enqueue(new Callback<HorarioRangoResponse>() {
            @Override
            public void onResponse(Call<HorarioRangoResponse> call, Response<HorarioRangoResponse> response) {
                // Log para ver el estado de la respuesta de la API
                Log.d("API_Response", "Estado de la respuesta: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    HorarioRangoResponse horarioRangoResponse = response.body();
                    Log.d("API_Response", "Datos recibidos: " + horarioRangoResponse.toString());  // Log para ver los datos recibidos
                    if (horarioRangoResponse.getStatus() == 200 && horarioRangoResponse.getData() != null) {
                        callback.onSuccess(horarioRangoResponse.getData());
                    } else {
                        Log.e("API_Error", "Error en los datos de la respuesta: " + horarioRangoResponse.getMessage());
                        callback.onError("Error en los datos de la respuesta");
                    }
                } else {
                    Log.e("API_Error", "Error al obtener los horarios: " + response.message());
                    callback.onError("Error al obtener los horarios");
                }
            }

            @Override
            public void onFailure(Call<HorarioRangoResponse> call, Throwable t) {
                // Log para ver el error de conexión o fallo
                Log.e("API_Failure", "Fallo en la conexión: " + t.getMessage());
                callback.onError(t.getMessage());
            }
        });
    }


}
