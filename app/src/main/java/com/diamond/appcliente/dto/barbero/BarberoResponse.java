// BarberoResponse.java
package com.diamond.appcliente.dto.barbero;

import java.util.List;


//UTILIZADO PARA CREAR Y ELIMINAR
public class BarberoResponse {
    private int status;
    private String message;
    private List<BarberoDto> data;

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<BarberoDto> getData() {
        return data;
    }
}
