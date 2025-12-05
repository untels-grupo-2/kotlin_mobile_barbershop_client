package com.diamond.appcliente.dto.servicio;

public class ServicioRequest {
    private String nombre;
    private double precio;
    private String descripcion;
    private int tipoServicio_id;

    public ServicioRequest(String nombre, double precio, String descripcion, int tipoServicio_id) {
        this.nombre = nombre;
        this.precio = precio;
        this.descripcion = descripcion;
        this.tipoServicio_id = tipoServicio_id;
    }

    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public int getTipoServicio_id() { return tipoServicio_id; }
    public void setTipoServicio_id(int tipoServicio_id) { this.tipoServicio_id = tipoServicio_id; }
}
