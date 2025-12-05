package com.diamond.appcliente.dto.horario;

// GenericResponse.java
public class GenericResponse {
    private int status;
    private String message;
    private Object data; // o puedes usar un tipo específico

    public int getStatus() { return status; }
    public String getMessage() { return message; }
    public Object getData() { return data; }

    public void setStatus(int status) { this.status = status; }
    public void setMessage(String message) { this.message = message; }
    public void setData(Object data) { this.data = data; }
}

