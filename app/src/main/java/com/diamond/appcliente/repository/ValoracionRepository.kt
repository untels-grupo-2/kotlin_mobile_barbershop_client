package com.diamond.appcliente.repository

import com.diamond.appcliente.dto.valoracion.ValoracionRequest

interface ValoracionRepository {
    suspend fun crearValoracion(request: ValoracionRequest): String
}
