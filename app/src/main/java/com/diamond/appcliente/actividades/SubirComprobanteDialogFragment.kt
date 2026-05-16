package com.diamond.appcliente.actividades

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.DialogFragment
import com.diamond.appcliente.R
import com.diamond.appcliente.viewmodel.ListarReservaViewModel
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.IOException

@AndroidEntryPoint
class SubirComprobanteDialogFragment : DialogFragment() {

    private var imagenSeleccionadaUri: Uri? = null
    private val listarReservaViewModel: ListarReservaViewModel by activityViewModels()
    private var reservaId: Long = -1L

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        reservaId = requireArguments().getLong("reservaId")

        val dialog = super.onCreateDialog(savedInstanceState)
        val view = requireActivity().layoutInflater.inflate(R.layout.activity_subir_comprobante, null)

        view.findViewById<Button>(R.id.btnSeleccionarImagen).setOnClickListener { seleccionarImagen() }

        view.findViewById<Button>(R.id.btnSubirComprobante).setOnClickListener {
            if (imagenSeleccionadaUri != null) {
                subirComprobante(reservaId, imagenSeleccionadaUri!!)
            } else {
                Toast.makeText(context, "Por favor, seleccione una imagen", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.setContentView(view)
        return dialog
    }

    private fun seleccionarImagen() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == requireActivity().RESULT_OK && requestCode == PICK_IMAGE_REQUEST && data != null) {
            imagenSeleccionadaUri = data.data
        }
    }

    private fun subirComprobante(reservaId: Long, imagenUri: Uri) {
        try {
            val inputStream = requireActivity().contentResolver.openInputStream(imagenUri)!!
            val file = File(requireActivity().cacheDir, "user_image.jpg")
            file.outputStream().use { inputStream.copyTo(it) }
            inputStream.close()

            Log.d("SubirComprobante", "Archivo creado en: ${file.absolutePath}")
            Log.d("SubirComprobante", "Tamaño del archivo: ${file.length()}")

            val imagenPart = MultipartBody.Part.createFormData(
                "imagen", file.name,
                RequestBody.create(MediaType.parse("image/*"), file)
            )

            listarReservaViewModel.subirComprobante(reservaId, imagenPart, object : ListarReservaViewModel.ActualizarCallback {
                override fun onSuccess(str: String) {
                    Toast.makeText(activity, str, Toast.LENGTH_SHORT).show()
                    dismiss()
                }
                override fun onError(str: String?) {
                    Toast.makeText(activity, str, Toast.LENGTH_SHORT).show()
                }
            })
        } catch (e: IOException) {
            Toast.makeText(activity, "Error al manejar la imagen", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }
}
