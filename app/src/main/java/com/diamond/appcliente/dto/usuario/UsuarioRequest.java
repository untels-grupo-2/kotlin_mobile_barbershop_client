package com.diamond.appcliente.dto.usuario;

public class UsuarioRequest {
    private Long usuario_id;

    public UsuarioRequest(Long usuario_id2) {
        this.usuario_id = usuario_id2;
    }

    public Long getUsuario_id() {
        return this.usuario_id;
    }

    public void setUsuario_id(Long usuario_id2) {
        this.usuario_id = usuario_id2;
    }
}