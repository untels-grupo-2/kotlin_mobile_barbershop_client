package com.diamond.appcliente.actividades

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
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
import com.diamond.appcliente.util.PreferenciasHelper
import com.diamond.appcliente.viewmodel.UsuarioViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class UsuarioActivity : AppCompatActivity() {

    private lateinit var btnLogoutUser: Button
    private lateinit var imageUsuario: ImageView
    private lateinit var textCelular: TextView
    private lateinit var textEmail: TextView
    private lateinit var textNombre: TextView
    private val viewModel: UsuarioViewModel by viewModels()
    private var imagenSeleccionadaUri: Uri? = null

    @Inject lateinit var preferenciasHelper: PreferenciasHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usuario)

        imageUsuario = findViewById(R.id.imageUsuario)
        textNombre = findViewById(R.id.textNombre)
        textEmail = findViewById(R.id.textEmail)
        textCelular = findViewById(R.id.textCelular)
        btnLogoutUser = findViewById(R.id.btnLogoutUser)

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
                    viewModel.usuario.collect { usuario ->
                        usuario ?: return@collect
                        textNombre.text = "${usuario.nombre} ${usuario.apellido}"
                        textEmail.text = usuario.email
                        textCelular.text = usuario.celular
                        Glide.with(this@UsuarioActivity).load(usuario.urlUsuario)
                            .placeholder(R.drawable.perfil_default).into(imageUsuario)
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
            }
        }
    }

    private fun mostrarPopupActualizar() {
        val popupView = layoutInflater.inflate(R.layout.popup_actualizar_usuario, null)
        val popupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)

        val editTextNombre = popupView.findViewById<EditText>(R.id.etNombreUsuario)
        val editTextApellido = popupView.findViewById<EditText>(R.id.etApellidoUsuario)
        val editTextEmail = popupView.findViewById<EditText>(R.id.etEmailUsuario)
        val editTextCelular = popupView.findViewById<EditText>(R.id.etCelularUsuario)

        if (preferenciasHelper.obtenerToken() != null) {
            val u = viewModel.usuario.value
            if (u != null) {
                editTextNombre.setText(u.nombre)
                editTextApellido.setText(u.apellido)
                editTextEmail.setText(u.email)
                editTextCelular.setText(u.celular)
            }
        }

        popupView.findViewById<Button>(R.id.btnSeleccionarImagenUsuario).setOnClickListener { seleccionarImagen() }

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

        popupWindow.showAtLocation(findViewById(R.id.activity_usuario), Gravity.CENTER, 0, 0)
    }

    private fun seleccionarImagen() {
        startActivityForResult(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), 1)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == 1) {
            imagenSeleccionadaUri = data?.data
        }
    }

    private fun cerrarSesion() {
        preferenciasHelper.limpiarPreferencias()
        Toast.makeText(this, "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }
}
