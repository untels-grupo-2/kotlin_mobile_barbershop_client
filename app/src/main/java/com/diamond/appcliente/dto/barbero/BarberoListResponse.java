package com.diamond.appcliente.dto.barbero;

import java.util.List;

public class BarberoListResponse {
    private String status;
    private String message;
    private List<DtoBarberoDisponible> data; // Aquí va el array de barberos

    // Getters y setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<DtoBarberoDisponible> getData() {
        return data;
    }

    public void setData(List<DtoBarberoDisponible> data) {
        this.data = data;
    }
}
