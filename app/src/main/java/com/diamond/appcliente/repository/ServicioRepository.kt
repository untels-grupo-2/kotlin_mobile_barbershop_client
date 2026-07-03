package com.diamond.appcliente.repository

import com.diamond.appcliente.util.CacheAwareData
import com.diamond.barbershop.shared.dto.servicio.ServicioDto
import com.diamond.barbershop.shared.dto.servicio.ServicioRequest
import kotlinx.coroutines.flow.Flow

interface ServicioRepository {
    fun observarServicios(): Flow<CacheAwareData<List<ServicioDto>>>
    suspend fun crearServicio(request: ServicioRequest): String
    suspend fun actualizarServicio(id: Int, request: ServicioRequest): String
    suspend fun eliminarServicio(id: Int): String
}
