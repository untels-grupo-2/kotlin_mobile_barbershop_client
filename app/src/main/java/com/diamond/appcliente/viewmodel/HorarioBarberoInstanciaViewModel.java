package com.diamond.appcliente.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.diamond.appcliente.api.AuthApiService;
import com.diamond.appcliente.api.ApiClient;
import com.diamond.appcliente.dto.barbero.DtoBarberoDisponible;
import com.diamond.appcliente.dto.barbero.BarberoListResponse;
import com.diamond.appcliente.util.PreferenciasHelper;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HorarioBarberoInstanciaViewModel extends AndroidViewModel {

    private MutableLiveData<List<DtoBarberoDisponible>> barberosDisponibles;
    private AuthApiService apiService;

    public HorarioBarberoInstanciaViewModel(Application application) {
        super(application);
        apiService = ApiClient.getRetrofit(application, true).create(AuthApiService.class);
        barberosDisponibles = new MutableLiveData<>();
    }

    public LiveData<List<DtoBarberoDisponible>> getBarberos() {
        return barberosDisponibles;
    }

    public void obtenerBarberosDisponibles(String fecha, Long tipoHorarioId, Long horarioRangoId) {
        // Recuperar el token almacenado
        PreferenciasHelper prefs = new PreferenciasHelper(getApplication());
        String token = prefs.obtenerToken();  // Asegúrate de que obtienes el token almacenado.

        if (token == null || token.isEmpty()) {
            Log.e("HorarioBarberoInstanciaViewModel", "Token no encontrado o es inválido");
            return;
        }

        Log.d("HorarioBarberoInstanciaViewModel", "Fecha: " + fecha);
        Log.d("HorarioBarberoInstanciaViewModel", "TipoHorarioId: " + tipoHorarioId);
        Log.d("HorarioBarberoInstanciaViewModel", "HorarioRangoId: " + horarioRangoId);

        // Llamada a la API con el token en el encabezado
        Call<BarberoListResponse> call = apiService.obtenerBarberosDisponibles("Bearer " + token, fecha, tipoHorarioId, horarioRangoId);
        call.enqueue(new Callback<BarberoListResponse>() {
            @Override
            public void onResponse(Call<BarberoListResponse> call, Response<BarberoListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Logs para revisar la respuesta exitosa
                    Log.d("HorarioBarberoInstanciaViewModel", "Código de respuesta: " + response.code());
                    Log.d("HorarioBarberoInstanciaViewModel", "Mensaje: " + response.message());
                    Log.d("HorarioBarberoInstanciaViewModel", "Cuerpo de la respuesta: " + response.body());

                    // Verificamos que la respuesta esté bien estructurada
                    if (response.body().getData() != null) {
                        // Establecer los datos en el LiveData
                        barberosDisponibles.setValue(response.body().getData());
                    } else {
                        Log.e("HorarioBarberoInstanciaViewModel", "Respuesta vacía o sin datos en 'data'");
                        barberosDisponibles.setValue(null);
                    }
                } else {
                    Log.e("HorarioBarberoInstanciaViewModel", "Error en la respuesta: " + response.code());
                    Log.e("HorarioBarberoInstanciaViewModel", "Mensaje: " + response.message());
                    if (response.errorBody() != null) {
                        try {
                            // Ver el contenido del error si hay
                            Log.e("HorarioBarberoInstanciaViewModel", "Error Body: " + response.errorBody().string());
                        } catch (IOException e) {
                            Log.e("HorarioBarberoInstanciaViewModel", "Error al leer el cuerpo de error: " + e.getMessage());
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<BarberoListResponse> call, Throwable t) {
                Log.e("HorarioBarberoInstanciaViewModel", "Error de conexión: " + t.getMessage());
                barberosDisponibles.setValue(null);  // No hay datos disponibles en caso de fallo
            }
        });
    }
}
