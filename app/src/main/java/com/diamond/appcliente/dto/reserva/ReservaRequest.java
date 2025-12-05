package com.diamond.appcliente.dto.reserva;

public class ReservaRequest {

    private Long barberoId;
    private Long horarioRangoId;
    private String fechaReserva;
    private Long servicioId;
    private String adicionales; // Comentarios adicionales del usuario

    // Constructor
    public ReservaRequest(Long barberoId, Long horarioRangoId, String fechaReserva, Long servicioId, String adicionales) {
        this.barberoId = barberoId;
        this.horarioRangoId = horarioRangoId;
        this.fechaReserva = fechaReserva;
        this.servicioId = servicioId;
        this.adicionales = adicionales;
    }

    // Getters y Setters
    public Long getBarberoId() {
        return barberoId;
    }

    public void setBarberoId(Long barberoId) {
        this.barberoId = barberoId;
    }

    public Long getHorarioRangoId() {
        return horarioRangoId;
    }

    public void setHorarioRangoId(Long horarioRangoId) {
        this.horarioRangoId = horarioRangoId;
    }

    public String getFechaReserva() {
        return fechaReserva;
    }

    public void setFechaReserva(String fechaReserva) {
        this.fechaReserva = fechaReserva;
    }

    public Long getServicioId() {
        return servicioId;
    }

    public void setServicioId(Long servicioId) {
        this.servicioId = servicioId;
    }

    public String getAdicionales() {
        return adicionales;
    }

    public void setAdicionales(String adicionales) {
        this.adicionales = adicionales;
    }
}
