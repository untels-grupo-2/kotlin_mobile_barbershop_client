package com.diamond.appcliente.dto.barbero

data class DtoBarberoDisponible(
    var barberoId: Long? = null,
    var nombre: String? = null,
    var urlBarbero: String? = null,
    var disponible: Boolean = false
)
