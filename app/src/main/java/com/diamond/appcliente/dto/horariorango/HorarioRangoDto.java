package com.diamond.appcliente.dto.horariorango;

public class HorarioRangoDto {
    private int horarioRango_id; // Ya existe
    private String rango;
    private String tipoHorario;

    // Constructor
    public HorarioRangoDto(String rango, String tipoHorario) {
        this.rango = rango;
        this.tipoHorario = tipoHorario;
    }

    // Getters y Setters
    public String getRango() {
        return rango;
    }

    public void setRango(String rango) {
        this.rango = rango;
    }

    public String getTipoHorario() {
        return tipoHorario;
    }

    public void setTipoHorario(String tipoHorario) {
        this.tipoHorario = tipoHorario;
    }

    public int getHorarioRango_id() {
        return horarioRango_id;
    }

    public void setHorarioRango_id(int horarioRango_id) {
        this.horarioRango_id = horarioRango_id;
    }
}



