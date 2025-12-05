package com.diamond.appcliente.dto;

import com.diamond.appcliente.dto.horariorango.HorarioRangoDto;

import java.util.List;

public class HorarioItem {
    private String encabezado;
    private List<HorarioRangoDto> horarios;

    public HorarioItem(String encabezado, List<HorarioRangoDto> horarios) {
        this.encabezado = encabezado;
        this.horarios = horarios;
    }

    public String getEncabezado() {
        return encabezado;
    }

    public List<HorarioRangoDto> getHorarios() {
        return horarios;
    }
}
