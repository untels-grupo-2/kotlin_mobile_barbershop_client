package com.diamond.appcliente.dto.horario

data class HorarioInstanciaResponse(
    var fecha: String? = null,
    var dia: String? = null,
    var tipoHorario: String? = null,
    var barbero: String? = null,
    var urlBarbero: String? = null
)
