package com.diamond.appcliente.repository.impl

import com.diamond.appcliente.api.AuthApiService
import com.diamond.appcliente.di.AuthenticatedApi
import com.diamond.appcliente.repository.ServicioRepository
import com.diamond.appcliente.util.CacheAwareData
import com.diamond.appcliente.util.ServicioLocalCache
import com.diamond.barbershop.shared.dto.servicio.ServicioDto
import com.diamond.barbershop.shared.dto.servicio.ServicioRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ServicioRepositoryImpl @Inject constructor(
    @AuthenticatedApi private val apiService: AuthApiService,
    private val cache: ServicioLocalCache
) : ServicioRepository {

    override fun observarServicios(): Flow<CacheAwareData<List<ServicioDto>>> = flow {
        val cached = cache.load()
        if (cached != null) emit(CacheAwareData.Stale(cached))

        try {
            val response = apiService.listarServicios()
            if (response.isSuccessful && response.body() != null) {
                val fresh = response.body()!!.data ?: emptyList()
                cache.save(fresh)
                emit(CacheAwareData.Fresh(fresh))
            } else if (cached == null) {
                throw Exception("Error al obtener servicios")
            }
        } catch (e: Exception) {
            if (cached == null) throw e
        }
    }

    override suspend fun crearServicio(request: ServicioRequest): String {
        val response = apiService.crearServicio(request)
        if (response.isSuccessful && response.body() != null) {
            cache.invalidate()
            return response.body()!!.message ?: "Servicio creado"
        }
        throw Exception("Error al crear servicio")
    }

    override suspend fun actualizarServicio(id: Int, request: ServicioRequest): String {
        val response = apiService.actualizarServicio(id, request)
        if (response.isSuccessful && response.body() != null) {
            cache.invalidate()
            return response.body()!!.message ?: "Servicio actualizado"
        }
        throw Exception("Error al actualizar servicio")
    }

    override suspend fun eliminarServicio(id: Int): String {
        val response = apiService.eliminarServicio(id)
        if (response.isSuccessful && response.body() != null) {
            cache.invalidate()
            return response.body()!!.message ?: "Servicio eliminado"
        }
        throw Exception("Error al eliminar servicio")
    }
}
