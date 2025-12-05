package com.diamond.appcliente.dto.reserva;

public class ReservaResponse {
    private String adicionales;
    private String barberoNombre;
    private Integer estRecompensa;
    private String estado;
    private String fechaCreacion;
    private String fechaReserva;
    private String horarioRango;
    private String motivoDescripcion;
    private Long precioServicio;
    private Long reservaId;
    private String servicioNombre;
    private String urlPago;
    private String usuarioNombre;

    public Long getReservaId() {
        return this.reservaId;
    }

    public void setReservaId(Long reservaId2) {
        this.reservaId = reservaId2;
    }

    public String getBarberoNombre() {
        return this.barberoNombre;
    }

    public void setBarberoNombre(String barberoNombre2) {
        this.barberoNombre = barberoNombre2;
    }

    public String getUsuarioNombre() {
        return this.usuarioNombre;
    }

    public void setUsuarioNombre(String usuarioNombre2) {
        this.usuarioNombre = usuarioNombre2;
    }

    public String getHorarioRango() {
        return this.horarioRango;
    }

    public void setHorarioRango(String horarioRango2) {
        this.horarioRango = horarioRango2;
    }

    public String getEstado() {
        return this.estado;
    }

    public void setEstado(String estado2) {
        this.estado = estado2;
    }

    public String getFechaReserva() {
        return this.fechaReserva;
    }

    public void setFechaReserva(String fechaReserva2) {
        this.fechaReserva = fechaReserva2;
    }

    public String getMotivoDescripcion() {
        return this.motivoDescripcion;
    }

    public void setMotivoDescripcion(String motivoDescripcion2) {
        this.motivoDescripcion = motivoDescripcion2;
    }

    public String getAdicionales() {
        return this.adicionales;
    }

    public void setAdicionales(String adicionales2) {
        this.adicionales = adicionales2;
    }

    public String getFechaCreacion() {
        return this.fechaCreacion;
    }

    public void setFechaCreacion(String fechaCreacion2) {
        this.fechaCreacion = fechaCreacion2;
    }

    public String getServicioNombre() {
        return this.servicioNombre;
    }

    public void setServicioNombre(String servicioNombre2) {
        this.servicioNombre = servicioNombre2;
    }

    public Long getPrecioServicio() {
        return this.precioServicio;
    }

    public Integer getEstRecompensa() {
        return this.estRecompensa;
    }

    public String getUrlPago() {
        return this.urlPago;
    }

    public void setUrlPago(String urlPago2) {
        this.urlPago = urlPago2;
    }

    public void setEstRecompensa(Integer estRecompensa2) {
        this.estRecompensa = estRecompensa2;
    }

    public void setPrecioServicio(Long precioServicio2) {
        this.precioServicio = precioServicio2;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ReservaResponse that = (ReservaResponse) obj;
        if (this.reservaId == null || !this.reservaId.equals(that.reservaId)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        if (this.reservaId != null) {
            return this.reservaId.hashCode();
        }
        return 0;
    }
}