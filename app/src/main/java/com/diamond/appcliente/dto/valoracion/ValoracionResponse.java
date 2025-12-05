package com.diamond.appcliente.dto.valoracion;

public class ValoracionResponse {

    private Long valoracion_id;
    private Integer valoracion;
    private Boolean util;
    private String mensaje;
    private String usuario_nombre; // Añadido para el getter que falta

    // Getters y Setters
    public Long getValoracion_id() {
        return valoracion_id;
    }

    public void setValoracion_id(Long valoracion_id) {
        this.valoracion_id = valoracion_id;
    }

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

    public String getUsuario_nombre() { // Método getter para 'usuario_nombre'
        return usuario_nombre;
    }

    public void setUsuario_nombre(String usuario_nombre) { // Método setter para 'usuario_nombre'
        this.usuario_nombre = usuario_nombre;
    }
}

