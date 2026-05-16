package com.diamond.appcliente.dto.barbero

import java.time.LocalDate

data class DtoHorarioBarberoInstanciaResponse(
    var fecha: LocalDate? = null,
    var dia: String? = null,
    var tipoHorario: String? = null,
    var barbero: String? = null,
    var urlBarbero: String? = null
)
