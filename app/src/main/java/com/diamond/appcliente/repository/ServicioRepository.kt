package com.diamond.appcliente.repository

import com.diamond.barbershop.shared.dto.servicio.ServicioDto
import com.diamond.barbershop.shared.dto.servicio.ServicioRequest

interface ServicioRepository {
    suspend fun listarServicios(): List<ServicioDto>
    suspend fun crearServicio(request: ServicioRequest): String
    suspend fun actualizarServicio(id: Int, request: ServicioRequest): String
    suspend fun eliminarServicio(id: Int): String
}
