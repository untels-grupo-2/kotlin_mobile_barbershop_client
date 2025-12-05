package com.diamond.appcliente.dto.usuario;

public class UsuarioResponse {
    private UsuarioDto data;
    private String message;
    private int status;

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status2) {
        this.status = status2;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message2) {
        this.message = message2;
    }

    public UsuarioDto getData() {
        return this.data;
    }

    public void setData(UsuarioDto data2) {
        this.data = data2;
    }
}