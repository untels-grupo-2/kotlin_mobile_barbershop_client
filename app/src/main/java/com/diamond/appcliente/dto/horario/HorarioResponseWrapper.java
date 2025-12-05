package com.diamond.appcliente.dto.horario;

import java.util.List;
import java.util.Map;

public class HorarioResponseWrapper {
    private int status;
    private String message;
    private Map<String, List<HorarioInstanciaResponse>> data;

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, List<HorarioInstanciaResponse>> getData() {
        return data;
    }
}
