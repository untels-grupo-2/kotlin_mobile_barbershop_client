package com.diamond.appcliente.util;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenciasHelper {

    private static final String PREFS_NAME = "MisPreferencias";
    private static final String TOKEN_KEY = "token";
    private static final String REFRESH = "refreshToken";

    private final SharedPreferences preferences;

    public PreferenciasHelper(Context context) {
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void guardarToken(String token) {
        preferences.edit().putString(TOKEN_KEY, token).apply();
    }

    public void guardarRefreshToken(String refreshToken) {
        preferences.edit().putString(REFRESH, refreshToken).apply();
    }

    public String obtenerRefreshToken() {
        return preferences.getString(REFRESH, null);
    }

    public String obtenerToken() {
        return preferences.getString(TOKEN_KEY, null);
    }

    public void limpiarPreferencias() {
        preferences.edit().clear().apply();
    }


}

