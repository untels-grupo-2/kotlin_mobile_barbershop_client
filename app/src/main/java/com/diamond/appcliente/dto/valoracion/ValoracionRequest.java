package com.diamond.appcliente.dto.valoracion;

public class ValoracionRequest {

    private Integer valoracion;  // Valor de la valoración (1-5)
    private Boolean util;        // Si fue útil: 'true' o 'false'
    private String mensaje;      // Comentario adicional

    // Constructor para inicializar los valores
    public ValoracionRequest(Integer valoracion, Boolean util, String mensaje) {
        this.valoracion = valoracion;
        this.util = util;
        this.mensaje = mensaje;
    }

    // Getters y Setters
    public Integer getValoracion() {
        return valoracion;
    }

    public void setValoracion(Integer valoracion) {
        this.valoracion = valoracion;
    }

    public Boolean getUtil() {
        return util;
    }

    public void setUtil(Boolean util) {
        this.util = util;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
