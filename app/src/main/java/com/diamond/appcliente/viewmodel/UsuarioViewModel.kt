package com.diamond.appcliente.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diamond.appcliente.dto.usuario.UsuarioDto
import com.diamond.appcliente.repository.UsuarioRepository
import com.diamond.barbershop.shared.util.PreferenciasHelper
import com.shared.models.ui.state.UiState
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
    private val usuarioRepository: UsuarioRepository,
    private val preferenciasHelper: PreferenciasHelper,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _usuario = MutableStateFlow<UiState<UsuarioDto>>(UiState.Idle)
    val usuario: StateFlow<UiState<UsuarioDto>> = _usuario.asStateFlow()

    private val _actualizarEvento = MutableSharedFlow<String>()
    val actualizarEvento: SharedFlow<String> = _actualizarEvento.asSharedFlow()

    private val _error = MutableSharedFlow<String>()
    val error: SharedFlow<String> = _error.asSharedFlow()

    fun cerrarSesion() {
        context.getSharedPreferences("diamond_prefs", Context.MODE_PRIVATE)
            .edit().putBoolean("welcome_shown", false).apply()
        preferenciasHelper.limpiarPreferencias()
    }

    fun obtenerMiUsuario() {
        viewModelScope.launch {
            _usuario.value = UiState.Loading
            try {
                val result = usuarioRepository.obtenerMiUsuario()
                _usuario.value = if (result != null) UiState.Success(result) else UiState.Empty
            } catch (e: Exception) {
                _usuario.value = UiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun actualizarMiPerfil(nombre: String, apellido: String, email: String, celular: String, imagenUri: Uri?) {
        if (celular.length != 9) {
            viewModelScope.launch { _error.emit("El número de celular debe tener 9 dígitos.") }
            return
        }
        val dto = UsuarioDto().apply {
            this.nombre = nombre
            this.apellido = apellido
            this.email = email
            this.celular = celular
        }
        actualizarMiPerfil(dto, imagenUri)
    }

    private fun actualizarMiPerfil(dtoUsuario: UsuarioDto, imagenUri: Uri?) {
        viewModelScope.launch {
            if (dtoUsuario.nombre.isNullOrEmpty()) { _error.emit("El campo nombre no puede estar vacío"); return@launch }
            if (dtoUsuario.apellido.isNullOrEmpty()) { _error.emit("El campo apellido no puede estar vacío"); return@launch }
            if (dtoUsuario.celular.isNullOrEmpty()) { _error.emit("El campo celular no puede estar vacío"); return@launch }

            val requestBody = RequestBody.create(MediaType.parse("application/json"), Gson().toJson(dtoUsuario))
            Log.d("UsuarioViewModel", "Datos enviados: ${Gson().toJson(dtoUsuario)}")

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
                _actualizarEvento.emit(usuarioRepository.actualizarMiPerfil(requestBody, imagenPart!!))
                obtenerMiUsuario()
            } catch (e: Exception) {
                Log.e("UsuarioViewModel", "Error al actualizar usuario", e)
                _error.emit(e.message ?: "Error desconocido")
            }
        }
    }
}
