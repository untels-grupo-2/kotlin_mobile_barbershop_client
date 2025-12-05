package com.diamond.appcliente.dto.barbero;

public class BarberoRequest {
    private String nombre;

    public BarberoRequest(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }
}
