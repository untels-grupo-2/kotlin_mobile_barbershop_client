// com.example.ta_avance.api.AuthInterceptor.java
package com.diamond.appcliente.api;

import android.content.Context;

import com.diamond.appcliente.util.PreferenciasHelper;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private final PreferenciasHelper preferenciasHelper;
    private final Context context;

    public AuthInterceptor(Context context) {
        this.context = context;
        this.preferenciasHelper = new PreferenciasHelper(context);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        String token = preferenciasHelper.obtenerToken();
        Request originalRequest = chain.request();

        Request requestWithToken = originalRequest;
        if (token != null) {
            requestWithToken = originalRequest.newBuilder()
                    .header("Authorization", "Bearer " + token)
                    .build();
        }

        Response response = chain.proceed(requestWithToken);

        if (response.code() == 401) {
            response.close(); // Cerramos respuesta anterior

            // Intentar renovar el token
            TokenManager tokenManager = new TokenManager(context);
            String newToken = tokenManager.refreshToken();

            if (newToken != null) {
                // Reintentar la petición con el nuevo token
                Request newRequest = originalRequest.newBuilder()
                        .header("Authorization", "Bearer " + newToken)
                        .build();

                return chain.proceed(newRequest);
            }
        }

        return response;
    }
}
