package com.diamond.appcliente.repository.impl

import android.util.Log
import com.diamond.appcliente.api.AuthApiService
import com.diamond.appcliente.di.AuthenticatedApi
import com.diamond.appcliente.dto.usuario.UsuarioDto
import com.diamond.appcliente.repository.UsuarioRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class UsuarioRepositoryImpl @Inject constructor(
    @AuthenticatedApi private val apiService: AuthApiService
) : UsuarioRepository {

    override suspend fun obtenerMiUsuario(): UsuarioDto? {
        val response = apiService.obtenerMiUsuario()
        if (response.isSuccessful && response.body() != null) return response.body()!!.data
        throw Exception("Error al obtener usuario")
    }

    override suspend fun actualizarMiPerfil(requestBody: RequestBody, imagenPart: MultipartBody.Part): String {
        Log.d("UsuarioRepository", "Actualizando perfil...")
        val response = apiService.actualizarMiPerfil(requestBody, imagenPart)
        if (response.isSuccessful) {
            Log.d("UsuarioRepository", "Actualización exitosa")
            return "Usuario actualizado exitosamente"
        }
        Log.e("UsuarioRepository", "Error ${response.code()}: ${response.message()}")
        try { Log.e("UsuarioRepository", "Error body: ${response.errorBody()?.string()}") } catch (e: Exception) { }
        throw Exception("Error al actualizar usuario")
    }
}
