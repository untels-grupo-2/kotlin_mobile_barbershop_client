package com.diamond.appcliente.repository

import com.diamond.appcliente.dto.servicio.ServicioDto
import com.diamond.appcliente.dto.servicio.ServicioRequest

interface ServicioRepository {
    suspend fun listarServicios(): List<ServicioDto>
    suspend fun crearServicio(request: ServicioRequest): String
    suspend fun actualizarServicio(id: Int, request: ServicioRequest): String
    suspend fun eliminarServicio(id: Int): String
}
