package com.diamond.appcliente.viewmodel;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import com.diamond.appcliente.api.ApiClient;
import com.diamond.appcliente.api.AuthApiService;
import com.diamond.appcliente.dto.common.ApiResponse;
import com.diamond.appcliente.dto.usuario.UsuarioDto;
import com.diamond.appcliente.dto.usuario.UsuarioResponse;
import com.google.gson.Gson;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsuarioViewModel {

    public interface ActualizarCallback {
        void onError(String str);

        void onSuccess(String str);
    }

    public interface UsuarioCallback {
        void onError(String str);

        void onSuccess(UsuarioDto usuarioDto);
    }

    public void obtenerMiUsuario(Context context, final UsuarioCallback callback) {
        ((AuthApiService) ApiClient.getRetrofit(context, true).create(AuthApiService.class)).obtenerMiUsuario().enqueue(new Callback<UsuarioResponse>() {
            public void onResponse(Call<UsuarioResponse> call, Response<UsuarioResponse> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    callback.onError("Error al obtener usuario");
                } else {
                    callback.onSuccess(((UsuarioResponse) response.body()).getData());
                }
            }

            public void onFailure(Call<UsuarioResponse> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void actualizarMiPerfil(Context context, UsuarioDto dtoUsuario, Uri imagenUri, ActualizarCallback callback) {
        UsuarioDto usuarioDto = dtoUsuario;
        Uri uri = imagenUri;
        final ActualizarCallback actualizarCallback = callback;
        Context context2 = context;
        AuthApiService api = (AuthApiService) ApiClient.getRetrofit(context2, true).create(AuthApiService.class);
        if (usuarioDto.getNombre() != null) {
            if (!usuarioDto.getNombre().isEmpty()) {
                if (usuarioDto.getApellido() != null) {
                    if (!usuarioDto.getApellido().isEmpty()) {
                        if (usuarioDto.getCelular() != null) {
                            if (!usuarioDto.getCelular().isEmpty()) {
                                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(usuarioDto));
                                Log.d("UsuarioActivity", "Datos del usuario enviados: " + new Gson().toJson(usuarioDto));
                                MultipartBody.Part imagenPart = null;
                                if (uri != null) {
                                    try {
                                        InputStream inputStream = context2.getContentResolver().openInputStream(uri);
                                        File file = new File(context2.getCacheDir(), "user_image.jpg");
                                        OutputStream outputStream = new FileOutputStream(file);
                                        byte[] buffer = new byte[1024];
                                        while (true) {
                                            int read = inputStream.read(buffer);
                                            int length = read;
                                            if (read == -1) {
                                                break;
                                            }
                                            outputStream.write(buffer, 0, length);
                                        }
                                        inputStream.close();
                                        outputStream.close();
                                        imagenPart = MultipartBody.Part.createFormData("imagen", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
                                        Log.d("UsuarioActivity", "Imagen seleccionada: " + uri.toString());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        Log.e("UsuarioActivity", "Error al manejar la imagen", e);
                                    }
                                }
                                api.actualizarMiPerfil(requestBody, imagenPart).enqueue(new Callback<ApiResponse<Object>>() {
                                    public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                                        if (response.isSuccessful()) {
                                            Log.d("UsuarioActivity", "Respuesta exitosa: " + ((ApiResponse) response.body()).toString());
                                            actualizarCallback.onSuccess("Usuario actualizado exitosamente");
                                            return;
                                        }
                                        Log.e("UsuarioActivity", "Error en la respuesta: " + response.code() + " - " + response.message());
                                        try {
                                            Log.e("UsuarioActivity", "Cuerpo de la respuesta de error: " + response.errorBody().string());
                                        } catch (IOException e) {
                                            Log.e("UsuarioActivity", "Error al leer el cuerpo de la respuesta", e);
                                        }
                                        actualizarCallback.onError("Error al actualizar usuario");
                                    }

                                    public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                                        Log.e("UsuarioActivity", "Error al actualizar usuario", t);
                                        actualizarCallback.onError(t.getMessage());
                                    }
                                });
                                return;
                            }
                        }
                        actualizarCallback.onError("El campo celular no puede estar vacío");
                        return;
                    }
                }
                actualizarCallback.onError("El campo apellido no puede estar vacío");
                return;
            }
        }
        actualizarCallback.onError("El campo nombre no puede estar vacío");
    }
}