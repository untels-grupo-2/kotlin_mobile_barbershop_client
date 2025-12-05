package com.diamond.appcliente.dto.barbero;

import java.time.LocalDate;

public class DtoHorarioBarberoInstanciaResponse {
    private LocalDate fecha;        // La fecha en la que el barbero está disponible
    private String dia;             // El día de la semana (Lunes, Martes, etc.)
    private String tipoHorario;     // El tipo de horario (Ej. Mañana, Tarde)
    private String barbero;         // Nombre del barbero
    private String urlBarbero;      // URL de la imagen del barbero

    // Constructor sin Lombok (con todos los parámetros)
    public DtoHorarioBarberoInstanciaResponse(LocalDate fecha, String dia, String tipoHorario, String barbero, String urlBarbero) {
        this.fecha = fecha;
        this.dia = dia;
        this.tipoHorario = tipoHorario;
        this.barbero = barbero;
        this.urlBarbero = urlBarbero;
    }

    // Getters y setters manuales

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
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

    // Método toString() para imprimir la instancia si es necesario
    @Override
    public String toString() {
        return "DtoHorarioBarberoInstanciaResponse{" +
                "fecha=" + fecha +
                ", dia='" + dia + '\'' +
                ", tipoHorario='" + tipoHorario + '\'' +
                ", barbero='" + barbero + '\'' +
                ", urlBarbero='" + urlBarbero + '\'' +
                '}';
    }

    // Método equals() y hashCode() si es necesario comparar objetos
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DtoHorarioBarberoInstanciaResponse that = (DtoHorarioBarberoInstanciaResponse) o;

        if (!fecha.equals(that.fecha)) return false;
        if (!dia.equals(that.dia)) return false;
        if (!tipoHorario.equals(that.tipoHorario)) return false;
        if (!barbero.equals(that.barbero)) return false;
        return urlBarbero.equals(that.urlBarbero);
    }

    @Override
    public int hashCode() {
        int result = fecha.hashCode();
        result = 31 * result + dia.hashCode();
        result = 31 * result + tipoHorario.hashCode();
        result = 31 * result + barbero.hashCode();
        result = 31 * result + urlBarbero.hashCode();
        return result;
    }
}
