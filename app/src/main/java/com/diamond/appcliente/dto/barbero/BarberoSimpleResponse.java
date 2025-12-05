package com.diamond.appcliente.dto.barbero;

//USADO PARA EL ACTUALIZAR BARBERRO
// BarberoSimpleResponse.java
public class BarberoSimpleResponse {
    private int status;
    private String message;
    private BarberoDto data;

    public int getStatus() { return status; }
    public String getMessage() { return message; }
    public BarberoDto getData() { return data; }
}
