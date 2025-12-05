package com.diamond.appcliente.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.auth0.android.jwt.JWT;
import com.diamond.appcliente.api.ApiClient;
import com.diamond.appcliente.api.AuthApiService;
import com.diamond.appcliente.dto.login.LoginRequest;
import com.diamond.appcliente.dto.login.LoginResponse;
import com.diamond.appcliente.util.PreferenciasHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainViewModel extends AndroidViewModel {
    public final MutableLiveData<String> apellido = new MutableLiveData<>();
    private final AuthApiService authApiService;
    public final MutableLiveData<String> loginStatus = new MutableLiveData<>();
    public final MutableLiveData<String> nombre = new MutableLiveData<>();
    public final MutableLiveData<String> url_usuario = new MutableLiveData<>();
    public final MutableLiveData<String> usuario_id = new MutableLiveData<>();

    public MainViewModel(Application application) {
        super(application);
        this.authApiService = (AuthApiService) ApiClient.getRetrofit(application, false).create(AuthApiService.class);
    }

    public void login(String usuario, String str) {
        this.authApiService.login(new LoginRequest(usuario, str)).enqueue(new Callback<LoginResponse>() {
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = ((LoginResponse) response.body()).getData().getToken();
                    String refreshToken = ((LoginResponse) response.body()).getData().getRefreshToken();
                    JWT jwt = new JWT(token);
                    String asString = jwt.getClaim("nombre").asString();
                    String asString2 = jwt.getClaim("apellido").asString();
                    String urlUsuario = jwt.getClaim("urlUsuario").asString();
                    String asString3 = jwt.getClaim("usuario_id").asString();
                    if (!"USER".equals(jwt.getClaim("rol").asString())) {
                        MainViewModel.this.loginStatus.postValue("NO_USER");
                        return;
                    }
                    PreferenciasHelper prefs = new PreferenciasHelper(MainViewModel.this.getApplication());
                    prefs.guardarToken(token);
                    prefs.guardarRefreshToken(refreshToken);
                    MainViewModel.this.nombre.postValue(jwt.getClaim("nombre").asString());
                    MainViewModel.this.apellido.postValue(jwt.getClaim("apellido").asString());
                    MainViewModel.this.url_usuario.postValue(urlUsuario);
                    MainViewModel.this.loginStatus.postValue("SUCCESS");
                } else if (response.code() == 401) {
                    MainViewModel.this.loginStatus.postValue("INVALID");
                } else {
                    MainViewModel.this.loginStatus.postValue("ERROR_" + response.code());
                }
            }

            public void onFailure(Call<LoginResponse> call, Throwable t) {
                MainViewModel.this.loginStatus.postValue("FAILURE: " + t.getMessage());
            }
        });
    }
}