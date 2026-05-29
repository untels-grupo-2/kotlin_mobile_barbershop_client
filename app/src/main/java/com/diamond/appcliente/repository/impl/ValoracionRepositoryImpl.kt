package com.diamond.appcliente.repository.impl

import com.diamond.appcliente.api.AuthApiService
import com.diamond.appcliente.di.AuthenticatedApi
import com.diamond.appcliente.dto.valoracion.ValoracionRequest
import com.diamond.appcliente.repository.ValoracionRepository
import javax.inject.Inject

class ValoracionRepositoryImpl @Inject constructor(
    @AuthenticatedApi private val apiService: AuthApiService
) : ValoracionRepository {

    override suspend fun crearValoracion(request: ValoracionRequest): String {
        val response = apiService.crearValoracion(request)
        if (response.isSuccessful) return "Valoración enviada con éxito"
        throw Exception("Error al enviar la valoración")
    }
}
