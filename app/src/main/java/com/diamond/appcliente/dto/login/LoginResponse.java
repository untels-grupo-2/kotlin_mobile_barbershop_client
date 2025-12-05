package com.diamond.appcliente.dto.login;

public class LoginResponse {
    private String status;
    private String message;
    private LoginData data;  // Esta es la nueva clase que contendrá el token y el rol

    public static class LoginData {
        private String token;
        private String refreshToken;

        public String getRefreshToken() { return refreshToken; }

        public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }

    public LoginData getData() {
        return data;
    }

    public void setData(LoginData data) {
        this.data = data;
    }
}
