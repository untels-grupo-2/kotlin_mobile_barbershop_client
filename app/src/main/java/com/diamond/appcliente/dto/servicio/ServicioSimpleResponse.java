package com.diamond.appcliente.dto.servicio;

//USADO PARA EL ACTUALIZAR BARBERRO
// BarberoSimpleResponse.java
public class ServicioSimpleResponse {
    private int status;
    private String message;
    private ServicioDto data;

    public int getStatus() { return status; }
    public String getMessage() { return message; }
    public ServicioDto getData() { return data; }
}