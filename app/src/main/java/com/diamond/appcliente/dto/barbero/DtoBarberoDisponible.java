package com.diamond.appcliente.dto.barbero;

public class DtoBarberoDisponible {

    private Long barberoId;       // ID del barbero
    private String nombre;        // Nombre del barbero
    private String urlBarbero;    // URL de la imagen del barbero
    private boolean disponible;   // Disponibilidad del barbero (true = disponible, false = no disponible)

    // Constructor
    public DtoBarberoDisponible(Long barberoId, String nombre, String urlBarbero, boolean disponible) {
        this.barberoId = barberoId;
        this.nombre = nombre;
        this.urlBarbero = urlBarbero;
        this.disponible = disponible;
    }

    // Getters y setters
    public Long getBarberoId() {
        return barberoId;
    }

    public void setBarberoId(Long barberoId) {
        this.barberoId = barberoId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUrlBarbero() {
        return urlBarbero;
    }

    public void setUrlBarbero(String urlBarbero) {
        this.urlBarbero = urlBarbero;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }
}
