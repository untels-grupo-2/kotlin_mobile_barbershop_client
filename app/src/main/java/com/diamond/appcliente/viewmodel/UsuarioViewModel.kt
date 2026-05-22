package com.diamond.appcliente.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diamond.appcliente.api.AuthApiService
import com.diamond.appcliente.di.AuthenticatedApi
import com.diamond.appcliente.dto.usuario.UsuarioDto
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class UsuarioViewModel @Inject constructor(
    @AuthenticatedApi private val authApiService: AuthApiService,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _usuario = MutableStateFlow<UsuarioDto?>(null)
    val usuario: StateFlow<UsuarioDto?> = _usuario.asStateFlow()

    private val _actualizarEvento = MutableSharedFlow<String>()
    val actualizarEvento: SharedFlow<String> = _actualizarEvento.asSharedFlow()

    private val _error = MutableSharedFlow<String>()
    val error: SharedFlow<String> = _error.asSharedFlow()

    fun obtenerMiUsuario() {
        viewModelScope.launch {
            try {
                val response = authApiService.obtenerMiUsuario()
                if (response.isSuccessful && response.body() != null) {
                    _usuario.value = response.body()!!.data
                } else {
                    _error.emit("Error al obtener usuario")
                }
            } catch (e: Exception) {
                _error.emit(e.message ?: "Error desconocido")
            }
        }
    }

    fun actualizarMiPerfil(dtoUsuario: UsuarioDto, imagenUri: Uri?) {
        viewModelScope.launch {
            if (dtoUsuario.nombre.isNullOrEmpty()) { _error.emit("El campo nombre no puede estar vacío"); return@launch }
            if (dtoUsuario.apellido.isNullOrEmpty()) { _error.emit("El campo apellido no puede estar vacío"); return@launch }
            if (dtoUsuario.celular.isNullOrEmpty()) { _error.emit("El campo celular no puede estar vacío"); return@launch }

            val requestBody = RequestBody.create(MediaType.parse("application/json"), Gson().toJson(dtoUsuario))
            Log.d("UsuarioViewModel", "Datos del usuario enviados: ${Gson().toJson(dtoUsuario)}")

            val imagenPart: MultipartBody.Part? = imagenUri?.let { uri ->
                try {
                    val inputStream = context.contentResolver.openInputStream(uri)!!
                    val file = File(context.cacheDir, "user_image.jpg")
                    FileOutputStream(file).use { out -> inputStream.copyTo(out) }
                    inputStream.close()
                    Log.d("UsuarioViewModel", "Imagen seleccionada: $uri")
                    MultipartBody.Part.createFormData("imagen", file.name, RequestBody.create(MediaType.parse("image/*"), file))
                } catch (e: Exception) {
                    Log.e("UsuarioViewModel", "Error al manejar la imagen", e)
                    null
                }
            }

            try {
                val response = authApiService.actualizarMiPerfil(requestBody, imagenPart!!)
                if (response.isSuccessful) {
                    Log.d("UsuarioViewModel", "Actualización exitosa: ${response.body()}")
                    _actualizarEvento.emit("Usuario actualizado exitosamente")
                    obtenerMiUsuario()
                } else {
                    Log.e("UsuarioViewModel", "Error ${response.code()}: ${response.message()}")
                    try { Log.e("UsuarioViewModel", "Error body: ${response.errorBody()?.string()}") } catch (e: Exception) { }
                    _error.emit("Error al actualizar usuario")
                }
            } catch (e: Exception) {
                Log.e("UsuarioViewModel", "Error al actualizar usuario", e)
                _error.emit(e.message ?: "Error desconocido")
            }
        }
    }
}
