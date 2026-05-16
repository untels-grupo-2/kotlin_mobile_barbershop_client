package com.diamond.appcliente.dto

data class ReservasRequest(
    val hora: String,
    val barbero: String,
    val servicio: String,
    val costo: String
)
