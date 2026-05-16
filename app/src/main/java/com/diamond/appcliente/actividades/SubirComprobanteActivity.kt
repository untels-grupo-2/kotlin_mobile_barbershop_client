package com.diamond.appcliente.actividades

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.diamond.appcliente.R
import com.diamond.appcliente.viewmodel.ListarReservaViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.IOException

@AndroidEntryPoint
class SubirComprobanteActivity : AppCompatActivity() {

    private lateinit var imagePreview: ImageView
    private var imagenSeleccionadaUri: Uri? = null
    private val listarReservaViewModel: ListarReservaViewModel by viewModels()
    private var reservaId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subir_comprobante)

        reservaId = intent.getLongExtra("reservaId", -1L)
        imagePreview = findViewById(R.id.imagePreview)

        if (ContextCompat.checkSelfPermission(this, "android.permission.READ_EXTERNAL_STORAGE") != 0) {
            ActivityCompat.requestPermissions(this, arrayOf("android.permission.READ_EXTERNAL_STORAGE"), REQUEST_CODE_PERMISSIONS)
        }

        window.statusBarColor = -16777216

        findViewById<Button>(R.id.btnSeleccionarImagen).setOnClickListener { seleccionarImagen() }

        findViewById<Button>(R.id.btnSubirComprobante).setOnClickListener {
            if (imagenSeleccionadaUri != null) {
                subirComprobante(reservaId, imagenSeleccionadaUri!!)
            } else {
                Toast.makeText(this, "Por favor, seleccione una imagen", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.btnCancelar).setOnClickListener {
            startActivity(Intent(this, ListarReservaActivity::class.java))
            finish()
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.historial
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> { startActivity(Intent(this, ClienteHomeActivity::class.java)); overridePendingTransition(0, 0); true }
                R.id.nav_servicios -> { startActivity(Intent(this, SeccionServiciosActivity::class.java)); overridePendingTransition(0, 0); true }
                R.id.nav_perfil -> { startActivity(Intent(this, UsuarioActivity::class.java)); overridePendingTransition(0, 0); true }
                else -> false
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    listarReservaViewModel.comprobanteEvento.collect { msg ->
                        Log.d("SubirComprobanteActivity", "Comprobante subido exitosamente.")
                        Toast.makeText(this@SubirComprobanteActivity, msg, Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
                launch {
                    listarReservaViewModel.error.collect { msg ->
                        Log.e("SubirComprobanteActivity", "Error al subir el comprobante: $msg")
                        Toast.makeText(this@SubirComprobanteActivity, msg, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS && grantResults.isNotEmpty() && grantResults[0] == 0) {
            Toast.makeText(this, "Permiso concedido", Toast.LENGTH_SHORT).show()
        }
    }

    private fun seleccionarImagen() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE_REQUEST && data != null) {
            imagenSeleccionadaUri = data.data
            mostrarVistaPrevia(imagenSeleccionadaUri!!)
        }
    }

    private fun mostrarVistaPrevia(imagenUri: Uri) {
        try {
            imagePreview.setImageBitmap(BitmapFactory.decodeStream(contentResolver.openInputStream(imagenUri)))
            imagePreview.visibility = View.VISIBLE
        } catch (e: IOException) {
            Log.e("SubirComprobanteActivity", "Error al mostrar la vista previa de la imagen", e)
        }
    }

    private fun subirComprobante(reservaId: Long, imagenUri: Uri) {
        try {
            val inputStream = contentResolver.openInputStream(imagenUri)!!
            val file = File(cacheDir, "user_image.jpg")
            file.outputStream().use { inputStream.copyTo(it) }
            inputStream.close()

            val imagenPart = MultipartBody.Part.createFormData(
                "imagen", file.name,
                RequestBody.create(MediaType.parse("image/*"), file)
            )
            listarReservaViewModel.subirComprobante(reservaId, imagenPart)
        } catch (e: IOException) {
            Log.e("SubirComprobanteActivity", "Error al manejar la imagen", e)
            Toast.makeText(this, "Error al manejar la imagen", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
        private const val REQUEST_CODE_PERMISSIONS = 1001
    }
}
