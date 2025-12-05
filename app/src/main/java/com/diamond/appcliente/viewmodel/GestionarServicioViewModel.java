package com.diamond.appcliente.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.diamond.appcliente.api.ApiClient;
import com.diamond.appcliente.api.AuthApiService;
import com.diamond.appcliente.dto.servicio.ServicioDto;
import com.diamond.appcliente.dto.servicio.ServicioRequest;
import com.diamond.appcliente.dto.servicio.ServicioResponse;
import com.diamond.appcliente.dto.servicio.ServicioSimpleResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GestionarServicioViewModel extends ViewModel {

    // LiveData para observar los cambios en la lista de servicios
    private final MutableLiveData<List<ServicioDto>> listaServiciosLiveData = new MutableLiveData<>();

    // LiveData para observar el estado de las operaciones de servicio
    private final MutableLiveData<String> servicioOperacionStatus = new MutableLiveData<>();

    public LiveData<List<ServicioDto>> getListaServicios() {
        return listaServiciosLiveData;
    }

    public LiveData<String> getServicioOperacionStatus() {
        return servicioOperacionStatus;
    }

    // Obtener los servicios desde el backend con el callback
    public void obtenerServicios(Context context, ServicioCallback callback) {
        AuthApiService api = ApiClient.getRetrofit(context, true).create(AuthApiService.class);
        Call<ServicioResponse> call = api.listarServicios();

        call.enqueue(new Callback<ServicioResponse>() {
            @Override
            public void onResponse(Call<ServicioResponse> call, Response<ServicioResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaServiciosLiveData.setValue(response.body().getData());
                    callback.onSuccess(response.body().getData());  // Se pasa la lista de servicios al callback
                } else {
                    servicioOperacionStatus.setValue("Error al obtener servicios");
                    callback.onError("Error al obtener servicios");
                }
            }

            @Override
            public void onFailure(Call<ServicioResponse> call, Throwable t) {
                servicioOperacionStatus.setValue("Error: " + t.getMessage());
                callback.onError(t.getMessage());
            }
        });
    }

    // Crear un nuevo servicio
    public void crearServicio(Context context, ServicioRequest request, ServicioOperacionCallback callback) {
        AuthApiService api = ApiClient.getRetrofit(context, true).create(AuthApiService.class);

        api.crearServicio(request).enqueue(new Callback<ServicioResponse>() {
            @Override
            public void onResponse(Call<ServicioResponse> call, Response<ServicioResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    servicioOperacionStatus.setValue("Servicio creado con éxito");
                    obtenerServicios(context, new ServicioCallback() {
                        @Override
                        public void onSuccess(List<ServicioDto> servicios) {
                            listaServiciosLiveData.setValue(servicios);
                        }

                        @Override
                        public void onError(String mensaje) {
                            servicioOperacionStatus.setValue(mensaje);
                        }
                    });  // Refrescar la lista después de crear el servicio
                    callback.onSuccess(response.body().getMessage());
                } else {
                    servicioOperacionStatus.setValue("Error al crear servicio");
                    callback.onError("Error al crear servicio");
                }
            }

            @Override
            public void onFailure(Call<ServicioResponse> call, Throwable t) {
                servicioOperacionStatus.setValue("Error: " + t.getMessage());
                callback.onError(t.getMessage());
            }
        });
    }

    // Actualizar un servicio existente
    public void actualizarServicio(Context context, int id, ServicioRequest request, ActualizarCallback callback) {
        AuthApiService api = ApiClient.getRetrofit(context, true).create(AuthApiService.class);
        api.actualizarServicio(id, request).enqueue(new Callback<ServicioSimpleResponse>() {
            @Override
            public void onResponse(Call<ServicioSimpleResponse> call, Response<ServicioSimpleResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    servicioOperacionStatus.setValue("Servicio actualizado con éxito");
                    obtenerServicios(context, new ServicioCallback() {
                        @Override
                        public void onSuccess(List<ServicioDto> servicios) {
                            listaServiciosLiveData.setValue(servicios);
                        }

                        @Override
                        public void onError(String mensaje) {
                            servicioOperacionStatus.setValue(mensaje);
                        }
                    });  // Refrescar la lista después de actualizar el servicio
                    callback.onSuccess(response.body().getMessage());
                } else {
                    servicioOperacionStatus.setValue("Error al actualizar servicio");
                    callback.onError("Error al actualizar servicio");
                }
            }

            @Override
            public void onFailure(Call<ServicioSimpleResponse> call, Throwable t) {
                servicioOperacionStatus.setValue("Error: " + t.getMessage());
                callback.onError(t.getMessage());
            }
        });
    }

    // Eliminar un servicio
    public void eliminarServicio(Context context, int id, ServicioOperacionCallback callback) {
        AuthApiService api = ApiClient.getRetrofit(context, true).create(AuthApiService.class);

        api.eliminarServicio(id).enqueue(new Callback<ServicioResponse>() {
            @Override
            public void onResponse(Call<ServicioResponse> call, Response<ServicioResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    servicioOperacionStatus.setValue("Servicio eliminado con éxito");
                    obtenerServicios(context, new ServicioCallback() {
                        @Override
                        public void onSuccess(List<ServicioDto> servicios) {
                            listaServiciosLiveData.setValue(servicios);
                        }

                        @Override
                        public void onError(String mensaje) {
                            servicioOperacionStatus.setValue(mensaje);
                        }
                    });  // Refrescar la lista después de eliminar el servicio
                    callback.onSuccess(response.body().getMessage());
                } else {
                    servicioOperacionStatus.setValue("Error al eliminar servicio");
                    callback.onError("Error al eliminar servicio");
                }
            }

            @Override
            public void onFailure(Call<ServicioResponse> call, Throwable t) {
                servicioOperacionStatus.setValue("Error: " + t.getMessage());
                callback.onError(t.getMessage());
            }
        });
    }

    // Interfaces de los callbacks
    public interface ServicioCallback {
        void onSuccess(List<ServicioDto> servicios); // Retorna la lista de servicios
        void onError(String mensaje); // Retorna un mensaje de error
    }

    public interface ServicioOperacionCallback {
        void onSuccess(String mensaje);
        void onError(String mensaje);
    }

    public interface ActualizarCallback {
        void onSuccess(String mensaje);
        void onError(String mensaje);
    }

}
