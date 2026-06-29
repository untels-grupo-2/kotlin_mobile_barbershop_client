package com.diamond.appcliente.actividades

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.auth0.android.jwt.JWT
import com.bumptech.glide.Glide
import com.diamond.appcliente.R
import com.diamond.appcliente.dto.usuario.UsuarioDto
import com.diamond.appcliente.ui.state.UiState
import com.diamond.barbershop.shared.util.PreferenciasHelper
import com.diamond.appcliente.viewmodel.ListarReservaViewModel
import com.diamond.appcliente.viewmodel.UsuarioViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.progressindicator.LinearProgressIndicator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class UsuarioActivity : AuthActivity() {

    private lateinit var btnLogoutUser: Button
    private lateinit var imageUsuario: ImageView
    private lateinit var textCelular: TextView
    private lateinit var textEmail: TextView
    private lateinit var textNombre: TextView
    private lateinit var textProgresoReservas: TextView
    private lateinit var progressRecompensa: LinearProgressIndicator
    private lateinit var textRecompensaDisponible: TextView
    private val viewModel: UsuarioViewModel by viewModels()
    private val listarReservaViewModel: ListarReservaViewModel by viewModels()
    private var imagenSeleccionadaUri: Uri? = null
    private var tvImagenSeleccionadaRef: TextView? = null
    private var layoutImagenSeleccionadaRef: android.view.View? = null
    private var btnSeleccionarImagenRef: android.widget.Button? = null

    @Inject lateinit var preferenciasHelper: PreferenciasHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usuario)

        imageUsuario = findViewById(R.id.imageUsuario)
        textNombre = findViewById(R.id.textNombre)
        textEmail = findViewById(R.id.textEmail)
        textCelular = findViewById(R.id.textCelular)
        btnLogoutUser = findViewById(R.id.btnLogoutUser)
        textProgresoReservas = findViewById(R.id.textProgresoReservas)
        progressRecompensa = findViewById(R.id.progressRecompensa)
        textRecompensaDisponible = findViewById(R.id.textRecompensaDisponible)

        val token = preferenciasHelper.obtenerToken()
        if (token != null) {
            Log.d("UsuarioActivity", "Token recibido: $token")
            JWT(token)
            viewModel.obtenerMiUsuario()
        } else {
            Log.d("UsuarioActivity", "Token no encontrado")
        }

        window.statusBarColor = -16777216

        findViewById<Button>(R.id.buttonActualizarUsuario).setOnClickListener { mostrarPopupActualizar() }
        btnLogoutUser.setOnClickListener { cerrarSesion() }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.nav_perfil
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> { startActivity(Intent(this, ClienteHomeActivity::class.java)); overridePendingTransition(0, 0); true }
                R.id.nav_servicios -> { startActivity(Intent(this, SeccionServiciosActivity::class.java)); overridePendingTransition(0, 0); true }
                R.id.historial -> { startActivity(Intent(this, ListarReservaActivity::class.java)); overridePendingTransition(0, 0); true }
                R.id.nav_perfil -> { startActivity(Intent(this, UsuarioActivity::class.java)); overridePendingTransition(0, 0); true }
                else -> false
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.usuario.collect { state ->
                        when (state) {
                            is UiState.Success -> {
                                val usuario = state.data
                                textNombre.text = "${usuario.nombre} ${usuario.apellido}"
                                textEmail.text = usuario.email
                                textCelular.text = usuario.celular
                                Glide.with(this@UsuarioActivity).load(usuario.urlUsuario)
                                    .placeholder(R.drawable.perfil_default).into(imageUsuario)
                            }
                            is UiState.Error -> Toast.makeText(this@UsuarioActivity, state.message, Toast.LENGTH_SHORT).show()
                            else -> {}
                        }
                    }
                }
                launch {
                    viewModel.actualizarEvento.collect { msg ->
                        Toast.makeText(this@UsuarioActivity, msg, Toast.LENGTH_SHORT).show()
                    }
                }
                launch {
                    viewModel.error.collect { msg ->
                        Toast.makeText(this@UsuarioActivity, msg, Toast.LENGTH_SHORT).show()
                    }
                }
                launch {
                    listarReservaViewModel.reservas.collect { state ->
                        if (state is UiState.Success) {
                            actualizarProgresoRecompensa(state.data.count { it.estRecompensa == 0 })
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        listarReservaViewModel.cargarReservas()
    }

    private fun actualizarProgresoRecompensa(reservasAcumuladas: Int) {
        val umbral = 7
        val count = reservasAcumuladas.coerceAtMost(umbral)
        val porcentaje = (count * 100) / umbral
        textProgresoReservas.text = "$count de $umbral reservas para tu próxima reserva gratuita"
        progressRecompensa.progress = porcentaje
        textRecompensaDisponible.visibility = if (reservasAcumuladas >= umbral) View.VISIBLE else View.GONE
    }

    private fun mostrarPopupActualizar() {
        val popupView = layoutInflater.inflate(R.layout.popup_actualizar_usuario, null)
        val popupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)

        val editTextNombre = popupView.findViewById<EditText>(R.id.etNombreUsuario)
        val editTextApellido = popupView.findViewById<EditText>(R.id.etApellidoUsuario)
        val editTextEmail = popupView.findViewById<EditText>(R.id.etEmailUsuario)
        val editTextCelular = popupView.findViewById<EditText>(R.id.etCelularUsuario)

        if (preferenciasHelper.obtenerToken() != null) {
            val u = (viewModel.usuario.value as? UiState.Success)?.data
            if (u != null) {
                editTextNombre.setText(u.nombre)
                editTextApellido.setText(u.apellido)
                editTextEmail.setText(u.email)
                editTextCelular.setText(u.celular)
            }
        }

        val btnSeleccionarImagen = popupView.findViewById<Button>(R.id.btnSeleccionarImagenUsuario)
        btnSeleccionarImagenRef = btnSeleccionarImagen
        tvImagenSeleccionadaRef = popupView.findViewById(R.id.tvImagenSeleccionada)
        layoutImagenSeleccionadaRef = popupView.findViewById(R.id.layoutImagenSeleccionada)
        btnSeleccionarImagen.setOnClickListener { seleccionarImagen() }

        popupView.findViewById<Button>(R.id.btnActualizarUsuario).setOnClickListener {
            val nombre = editTextNombre.text.toString()
            val apellido = editTextApellido.text.toString()
            val email = editTextEmail.text.toString()
            val celular = editTextCelular.text.toString()

            if (celular.length == 9) {
                val dtoUsuario = UsuarioDto().apply {
                    this.nombre = nombre
                    this.apellido = apellido
                    this.email = email
                    this.celular = celular
                }
                viewModel.actualizarMiPerfil(dtoUsuario, imagenSeleccionadaUri)
                popupWindow.dismiss()
            } else {
                Toast.makeText(this, "El número de celular debe tener 9 dígitos.", Toast.LENGTH_SHORT).show()
            }
        }

        popupView.findViewById<Button>(R.id.btnCancelarActualizarUsuario).setOnClickListener {
            popupWindow.dismiss()
            startActivity(Intent(this, UsuarioActivity::class.java))
        }

        popupWindow.setOnDismissListener {
            tvImagenSeleccionadaRef = null
            layoutImagenSeleccionadaRef = null
            btnSeleccionarImagenRef = null
        }
        popupWindow.showAtLocation(findViewById(R.id.activity_usuario), Gravity.CENTER, 0, 0)
    }

    private fun seleccionarImagen() {
        startActivityForResult(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), 1)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == 1) {
            val uri = data?.data
            imagenSeleccionadaUri = uri
            if (uri != null) {
                val fileName = uri.lastPathSegment?.substringAfterLast("/") ?: uri.toString().substringAfterLast("/")
                tvImagenSeleccionadaRef?.text = fileName
                layoutImagenSeleccionadaRef?.visibility = android.view.View.VISIBLE
                btnSeleccionarImagenRef?.text = "CAMBIAR IMAGEN"
            }
        }
    }

    private fun cerrarSesion() {
        getSharedPreferences("diamond_prefs", MODE_PRIVATE).edit().putBoolean("welcome_shown", false).apply()
        preferenciasHelper.limpiarPreferencias()
        Toast.makeText(this, "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }
}
