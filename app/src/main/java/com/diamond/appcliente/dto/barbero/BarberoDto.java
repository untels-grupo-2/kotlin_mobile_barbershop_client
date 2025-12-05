package com.diamond.appcliente.dto.barbero;

public class BarberoDto {
    private int barbero_id;
    private String nombre;
    private int estado;
    private String urlBarbero;  // Campo para la URL de la imagen del barbero

    // Getters y Setters
    public int getBarbero_id() {
        return barbero_id;
    }

    public void setBarbero_id(int barbero_id) {
        this.barbero_id = barbero_id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public String getUrlBarbero() {
        return urlBarbero;
    }

    public void setUrlBarbero(String urlBarbero) {
        this.urlBarbero = urlBarbero;
    }
}
