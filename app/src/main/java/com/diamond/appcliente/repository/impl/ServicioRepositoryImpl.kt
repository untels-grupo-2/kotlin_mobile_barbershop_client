package com.diamond.appcliente.repository.impl

import com.diamond.appcliente.api.AuthApiService
import com.diamond.appcliente.di.AuthenticatedApi
import com.diamond.barbershop.shared.dto.servicio.ServicioDto
import com.diamond.barbershop.shared.dto.servicio.ServicioRequest
import com.diamond.appcliente.repository.ServicioRepository
import javax.inject.Inject

class ServicioRepositoryImpl @Inject constructor(
    @AuthenticatedApi private val apiService: AuthApiService
) : ServicioRepository {

    override suspend fun listarServicios(): List<ServicioDto> {
        val response = apiService.listarServicios()
        if (response.isSuccessful && response.body() != null) return response.body()!!.data ?: emptyList()
        throw Exception("Error al obtener servicios")
    }

    override suspend fun crearServicio(request: ServicioRequest): String {
        val response = apiService.crearServicio(request)
        if (response.isSuccessful && response.body() != null) return response.body()!!.message ?: "Servicio creado"
        throw Exception("Error al crear servicio")
    }

    override suspend fun actualizarServicio(id: Int, request: ServicioRequest): String {
        val response = apiService.actualizarServicio(id, request)
        if (response.isSuccessful && response.body() != null) return response.body()!!.message ?: "Servicio actualizado"
        throw Exception("Error al actualizar servicio")
    }

    override suspend fun eliminarServicio(id: Int): String {
        val response = apiService.eliminarServicio(id)
        if (response.isSuccessful && response.body() != null) return response.body()!!.message ?: "Servicio eliminado"
        throw Exception("Error al eliminar servicio")
    }
}
