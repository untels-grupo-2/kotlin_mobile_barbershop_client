package com.diamond.appcliente.dto.servicio;

public class ServicioDto {
    private int servicio_id;
    private String nombre;
    private double precio;
    private String descripcion;
    private String nombre_tipoServicio;
    private int tipoServicio_id;
    private String urlServicio;

    // Getters y Setters
    public int getServicio_id() {
        return servicio_id;
    }

    public void setServicio_id(int servicio_id) {
        this.servicio_id = servicio_id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getNombre_tipoServicio() {
        return nombre_tipoServicio;
    }

    public void setNombre_tipoServicio(String nombre_tipoServicio) {
        this.nombre_tipoServicio = nombre_tipoServicio;
    }

    public int getTipoServicio_id() {
        return tipoServicio_id;
    }

    public void setTipoServicio_id(int tipoServicio_id) {
        this.tipoServicio_id = tipoServicio_id;
    }

    public String getUrlServicio() {
        return urlServicio;
    }

    public void setUrlServicio(String urlServicio) {
        this.urlServicio = urlServicio;
    }
}
