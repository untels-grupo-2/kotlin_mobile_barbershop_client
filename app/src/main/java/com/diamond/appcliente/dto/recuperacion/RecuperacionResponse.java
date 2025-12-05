package com.diamond.appcliente.dto.recuperacion;

public class RecuperacionResponse {
    private int status;  // <--- CORREGIDO
    private String message;

    public RecuperacionResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

