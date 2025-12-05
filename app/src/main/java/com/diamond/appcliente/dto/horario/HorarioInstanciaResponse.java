package com.diamond.appcliente.dto.horario;

public class HorarioInstanciaResponse {
    private String fecha;
    private String dia;
    private String tipoHorario;
    private String barbero;
    private String urlBarbero;

    // Getters y setters
    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getDia() {
        return dia;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    public String getTipoHorario() {
        return tipoHorario;
    }

    public void setTipoHorario(String tipoHorario) {
        this.tipoHorario = tipoHorario;
    }

    public String getBarbero() {
        return barbero;
    }

    public void setBarbero(String barbero) {
        this.barbero = barbero;
    }

    public String getUrlBarbero() {
        return urlBarbero;
    }

    public void setUrlBarbero(String urlBarbero) {
        this.urlBarbero = urlBarbero;
    }
}


