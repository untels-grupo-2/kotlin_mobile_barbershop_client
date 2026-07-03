package com.diamond.appcliente.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diamond.appcliente.dto.reserva.ReservaResponse
import com.diamond.appcliente.repository.ReservaRepository
import com.shared.models.ui.state.UiState
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
import javax.inject.Inject

@HiltViewModel
class ListarReservaViewModel @Inject constructor(
    private val reservaRepository: ReservaRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _reservas = MutableStateFlow<UiState<List<ReservaResponse>>>(UiState.Idle)
    val reservas: StateFlow<UiState<List<ReservaResponse>>> = _reservas.asStateFlow()

    private val _recompensaCount = MutableStateFlow(0)
    val recompensaCount: StateFlow<Int> = _recompensaCount.asStateFlow()

    private val _comprobanteEvento = MutableSharedFlow<String>()
    val comprobanteEvento: SharedFlow<String> = _comprobanteEvento.asSharedFlow()

    private val _error = MutableSharedFlow<String>()
    val error: SharedFlow<String> = _error.asSharedFlow()

    fun cargarReservas() {
        viewModelScope.launch {
            _reservas.value = UiState.Loading
            try {
                val lista = reservaRepository.listarMisReservas()
                _recompensaCount.value = lista.count { it.estRecompensa == 0 }
                _reservas.value = UiState.Success(lista)
            } catch (e: Exception) {
                _reservas.value = UiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun subirComprobante(reservaId: Long, uri: Uri) {
        viewModelScope.launch {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)!!
                val file = File(context.cacheDir, "comprobante.jpg")
                file.outputStream().use { inputStream.copyTo(it) }
                inputStream.close()
                val imagenPart = MultipartBody.Part.createFormData(
                    "imagen", file.name,
                    RequestBody.create(MediaType.parse("image/*"), file)
                )
                _comprobanteEvento.emit(reservaRepository.subirComprobante(reservaId, imagenPart))
            } catch (e: Exception) {
                _error.emit(e.message ?: "Error desconocido")
            }
        }
    }
}
