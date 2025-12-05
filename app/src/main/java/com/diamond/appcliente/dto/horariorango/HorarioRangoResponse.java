package com.diamond.appcliente.dto.horariorango;

import java.util.List;

public class HorarioRangoResponse {
    private int status;
    private String message;
    private List<HorarioRangoDto> data;

    // Getters y Setters
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<HorarioRangoDto> getData() {
        return data;
    }

    public void setData(List<HorarioRangoDto> data) {
        this.data = data;
    }
}
