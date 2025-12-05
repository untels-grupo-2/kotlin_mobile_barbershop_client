package com.diamond.appcliente.dto;

public class ReservasRequest {
    private final String hora;
    private final String barbero;
    private final String servicio;
    private final String costo;

    public ReservasRequest(String hora, String barbero, String servicio, String costo) {
        this.hora = hora;
        this.barbero = barbero;
        this.servicio = servicio;
        this.costo = costo;
    }

    public String getHora() {
        return hora;
    }

    public String getBarbero() {
        return barbero;
    }

    public String getServicio() {
        return servicio;
    }

    public String getCosto() {
        return costo;
    }
}
