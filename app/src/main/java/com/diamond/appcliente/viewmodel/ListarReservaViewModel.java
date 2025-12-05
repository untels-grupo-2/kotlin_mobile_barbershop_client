package com.diamond.appcliente.viewmodel;

import android.app.Application;
import android.util.Log;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.diamond.appcliente.api.ApiClient;
import com.diamond.appcliente.api.AuthApiService;
import com.diamond.appcliente.dto.common.ApiResponse;
import com.diamond.appcliente.dto.reserva.ReservaListResponse;
import com.diamond.appcliente.dto.reserva.ReservaResponse;
import java.util.List;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListarReservaViewModel extends AndroidViewModel {
    private AuthApiService apiService;
    /* access modifiers changed from: private */
    public MutableLiveData<List<ReservaResponse>> reservasLiveData = new MutableLiveData<>();

    public interface ActualizarCallback {
        void onError(String str);

        void onSuccess(String str);
    }

    public ListarReservaViewModel(Application application) {
        super(application);
        this.apiService = (AuthApiService) ApiClient.getRetrofit(application.getApplicationContext(), true).create(AuthApiService.class);
    }

    public LiveData<List<ReservaResponse>> getReservas() {
        loadReservations();
        return this.reservasLiveData;
    }

    private void loadReservations() {
        Log.d("ListarReservaViewModel", "Haciendo llamada a la API para obtener reservas...");
        this.apiService.listarMisReservas().enqueue(new Callback<ReservaListResponse>() {
            public void onResponse(Call<ReservaListResponse> call, Response<ReservaListResponse> response) {
                Log.d("ListarReservaViewModel", "Respuesta de la API: " + response.code());
                if (!response.isSuccessful() || response.body() == null) {
                    Log.d("ListarReservaViewModel", "No se encontraron reservas o error en la respuesta.");
                    ListarReservaViewModel.this.reservasLiveData.setValue((List<ReservaResponse>) null);
                    return;
                }
                ListarReservaViewModel.this.reservasLiveData.setValue(((ReservaListResponse) response.body()).getData());
                Log.d("ListarReservaViewModel", "Reservas recibidas: " + ((ReservaListResponse) response.body()).getData().toString());
            }

            public void onFailure(Call<ReservaListResponse> call, Throwable t) {
                Log.e("ListarReservaViewModel", "Error en la llamada a la API: ", t);
                ListarReservaViewModel.this.reservasLiveData.setValue((List<ReservaResponse>) null);
            }
        });
    }

    public void subirComprobante(Long reservaId, MultipartBody.Part imagenPart, final ActualizarCallback callback) {
        this.apiService.subirComprobante(reservaId, imagenPart).enqueue(new Callback<ApiResponse<Object>>() {
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess("Comprobante subido correctamente.");
                } else {
                    callback.onError("Error al subir el comprobante.");
                }
            }

            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
}