package com.diamond.appcliente.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.diamond.appcliente.api.AuthApiService
import com.diamond.appcliente.di.AuthenticatedApi
import com.diamond.appcliente.dto.common.ApiResponse
import com.diamond.appcliente.dto.usuario.UsuarioDto
import com.diamond.appcliente.dto.usuario.UsuarioResponse
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class UsuarioViewModel @Inject constructor(
    @AuthenticatedApi private val authApiService: AuthApiService,
    @ApplicationContext private val context: Context
) : ViewModel() {

    interface ActualizarCallback {
        fun onSuccess(str: String)
        fun onError(str: String?)
    }

    interface UsuarioCallback {
        fun onSuccess(usuarioDto: UsuarioDto?)
        fun onError(str: String?)
    }

    fun obtenerMiUsuario(callback: UsuarioCallback) {
        authApiService.obtenerMiUsuario().enqueue(object : Callback<UsuarioResponse> {
            override fun onResponse(call: Call<UsuarioResponse>, response: Response<UsuarioResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    callback.onSuccess(response.body()!!.data)
                } else {
                    callback.onError("Error al obtener usuario")
                }
            }
            override fun onFailure(call: Call<UsuarioResponse>, t: Throwable) {
                callback.onError(t.message)
            }
        })
    }

    fun actualizarMiPerfil(dtoUsuario: UsuarioDto, imagenUri: Uri?, callback: ActualizarCallback) {
        if (dtoUsuario.nombre.isNullOrEmpty()) { callback.onError("El campo nombre no puede estar vacío"); return }
        if (dtoUsuario.apellido.isNullOrEmpty()) { callback.onError("El campo apellido no puede estar vacío"); return }
        if (dtoUsuario.celular.isNullOrEmpty()) { callback.onError("El campo celular no puede estar vacío"); return }

        val requestBody = RequestBody.create(MediaType.parse("application/json"), Gson().toJson(dtoUsuario))
        Log.d("UsuarioActivity", "Datos del usuario enviados: ${Gson().toJson(dtoUsuario)}")

        val imagenPart: MultipartBody.Part? = imagenUri?.let { uri ->
            try {
                val inputStream = context.contentResolver.openInputStream(uri)!!
                val file = File(context.cacheDir, "user_image.jpg")
                FileOutputStream(file).use { out -> inputStream.copyTo(out) }
                inputStream.close()
                Log.d("UsuarioActivity", "Imagen seleccionada: $uri")
                MultipartBody.Part.createFormData("imagen", file.name, RequestBody.create(MediaType.parse("image/*"), file))
            } catch (e: Exception) {
                Log.e("UsuarioActivity", "Error al manejar la imagen", e)
                null
            }
        }

        authApiService.actualizarMiPerfil(requestBody, imagenPart!!).enqueue(object : Callback<ApiResponse<Any>> {
            override fun onResponse(call: Call<ApiResponse<Any>>, response: Response<ApiResponse<Any>>) {
                if (response.isSuccessful) {
                    Log.d("UsuarioActivity", "Respuesta exitosa: ${response.body()}")
                    callback.onSuccess("Usuario actualizado exitosamente")
                } else {
                    Log.e("UsuarioActivity", "Error ${response.code()}: ${response.message()}")
                    try { Log.e("UsuarioActivity", "Error body: ${response.errorBody()?.string()}") } catch (e: Exception) { }
                    callback.onError("Error al actualizar usuario")
                }
            }
            override fun onFailure(call: Call<ApiResponse<Any>>, t: Throwable) {
                Log.e("UsuarioActivity", "Error al actualizar usuario", t)
                callback.onError(t.message)
            }
        })
    }
}
