package com.diamond.appcliente.dto.usuario;

import com.google.gson.annotations.SerializedName;

public class UsuarioDto {
    @SerializedName("apellido")
    private String apellido;
    @SerializedName("celular")
    private String celular;
    @SerializedName("email")
    private String email;
    @SerializedName("nombre")
    private String nombre;
    @SerializedName("urlUsuario")
    private String urlUsuario;
    @SerializedName("username")
    private String username;
    @SerializedName("usuario_id")
    private Long usuario_id;

    public Long getUsuario_id() {
        return this.usuario_id;
    }

    public void setUsuario_id(Long usuario_id2) {
        this.usuario_id = usuario_id2;
    }

    public String getNombre() {
        return this.nombre;
    }

    public void setNombre(String nombre2) {
        this.nombre = nombre2;
    }

    public String getApellido() {
        return this.apellido;
    }

    public void setApellido(String apellido2) {
        this.apellido = apellido2;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email2) {
        this.email = email2;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username2) {
        this.username = username2;
    }

    public String getCelular() {
        return this.celular;
    }

    public void setCelular(String celular2) {
        this.celular = celular2;
    }

    public String getUrlUsuario() {
        return this.urlUsuario;
    }

    public void setUrlUsuario(String urlUsuario2) {
        this.urlUsuario = urlUsuario2;
    }

    public String toString() {
        return "UsuarioDto{usuario_id=" + this.usuario_id + ", nombre='" + this.nombre + '\'' + ", apellido='" + this.apellido + '\'' + ", email='" + this.email + '\'' + ", celular='" + this.celular + '\'' + ", urlUsuario='" + this.urlUsuario + '\'' + '}';
    }
}