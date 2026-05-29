package com.diamond.appcliente.repository

import com.diamond.appcliente.dto.usuario.UsuarioDto
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface UsuarioRepository {
    suspend fun obtenerMiUsuario(): UsuarioDto?
    suspend fun actualizarMiPerfil(requestBody: RequestBody, imagenPart: MultipartBody.Part): String
}
