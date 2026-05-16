package com.diamond.appcliente.dto.servicio

data class ServicioDto(
    var servicio_id: Int = 0,
    var nombre: String? = null,
    var precio: Double = 0.0,
    var descripcion: String? = null,
    var nombre_tipoServicio: String? = null,
    var tipoServicio_id: Int = 0,
    var urlServicio: String? = null
)
