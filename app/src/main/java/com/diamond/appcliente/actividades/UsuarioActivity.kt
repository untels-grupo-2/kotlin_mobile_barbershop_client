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
import androidx.appcompat.app.AppCompatActivity
import com.auth0.android.jwt.JWT
import com.bumptech.glide.Glide
import com.diamond.appcliente.R
import com.diamond.appcliente.dto.usuario.UsuarioDto
import com.diamond.appcliente.util.PreferenciasHelper
import com.diamond.appcliente.viewmodel.UsuarioViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class UsuarioActivity : AppCompatActivity() {

    private lateinit var btnLogoutUser: Button
    private lateinit var imageUsuario: ImageView
    private lateinit var textCelular: TextView
    private lateinit var textEmail: TextView
    private lateinit var textNombre: TextView
    private lateinit var viewModel: UsuarioViewModel
    private var imagenSeleccionadaUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usuario)

        imageUsuario = findViewById(R.id.imageUsuario)
        textNombre = findViewById(R.id.textNombre)
        textEmail = findViewById(R.id.textEmail)
        textCelular = findViewById(R.id.textCelular)
        btnLogoutUser = findViewById(R.id.btnLogoutUser)
        viewModel = UsuarioViewModel()

        val token = PreferenciasHelper(application).obtenerToken()
        if (token != null) {
            Log.d("UsuarioActivity", "Token recibido: $token")
            JWT(token)
            viewModel.obtenerMiUsuario(this, object : UsuarioViewModel.UsuarioCallback {
                override fun onSuccess(usuario: UsuarioDto?) {
                    usuario ?: return
                    textNombre.text = "${usuario.nombre} ${usuario.apellido}"
                    textEmail.text = usuario.email
                    textCelular.text = usuario.celular
                    Glide.with(this@UsuarioActivity).load(usuario.urlUsuario)
                        .placeholder(R.drawable.perfil_default).into(imageUsuario)
                }
                override fun onError(mensaje: String?) {
                    Toast.makeText(this@UsuarioActivity, "Error al cargar usuario: $mensaje", Toast.LENGTH_SHORT).show()
                }
            })
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
    }

    private fun mostrarPopupActualizar() {
        val popupView = layoutInflater.inflate(R.layout.popup_actualizar_usuario, null)
        val popupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)

        val editTextNombre = popupView.findViewById<EditText>(R.id.etNombreUsuario)
        val editTextApellido = popupView.findViewById<EditText>(R.id.etApellidoUsuario)
        val editTextEmail = popupView.findViewById<EditText>(R.id.etEmailUsuario)
        val editTextCelular = popupView.findViewById<EditText>(R.id.etCelularUsuario)

        if (PreferenciasHelper(application).obtenerToken() != null) {
            viewModel.obtenerMiUsuario(this, object : UsuarioViewModel.UsuarioCallback {
                override fun onSuccess(usuario: UsuarioDto?) {
                    usuario ?: return
                    editTextNombre.setText(usuario.nombre)
                    editTextApellido.setText(usuario.apellido)
                    editTextEmail.setText(usuario.email)
                    editTextCelular.setText(usuario.celular)
                }
                override fun onError(mensaje: String?) {
                    Toast.makeText(this@UsuarioActivity, "Error al cargar usuario: $mensaje", Toast.LENGTH_SHORT).show()
                }
            })
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
                if (imagenSeleccionadaUri != null) {
                    actualizarUsuarioConImagen(dtoUsuario, imagenSeleccionadaUri!!)
                } else {
                    actualizarUsuario(dtoUsuario)
                }
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

    private fun actualizarUsuario(dtoUsuario: UsuarioDto) {
        if (PreferenciasHelper(application).obtenerToken() == null) return
        viewModel.actualizarMiPerfil(this, dtoUsuario, null, object : UsuarioViewModel.ActualizarCallback {
            override fun onSuccess(str: String) {
                Toast.makeText(this@UsuarioActivity, str, Toast.LENGTH_SHORT).show()
                cargarUsuario()
            }
            override fun onError(str: String?) {
                Toast.makeText(this@UsuarioActivity, str, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun actualizarUsuarioConImagen(dtoUsuario: UsuarioDto, imagenUri: Uri) {
        if (PreferenciasHelper(application).obtenerToken() == null) return
        viewModel.actualizarMiPerfil(this, dtoUsuario, imagenUri, object : UsuarioViewModel.ActualizarCallback {
            override fun onSuccess(str: String) {
                Toast.makeText(this@UsuarioActivity, str, Toast.LENGTH_SHORT).show()
                cargarUsuario()
            }
            override fun onError(str: String?) {
                Toast.makeText(this@UsuarioActivity, str, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun cerrarSesion() {
        PreferenciasHelper(application).limpiarPreferencias()
        Toast.makeText(this, "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }

    private fun cargarUsuario() {
        if (PreferenciasHelper(application).obtenerToken() == null) return
        viewModel.obtenerMiUsuario(this, object : UsuarioViewModel.UsuarioCallback {
            override fun onSuccess(usuario: UsuarioDto?) {
                usuario ?: return
                textNombre.text = "${usuario.nombre} ${usuario.apellido}"
                textEmail.text = usuario.email
                textCelular.text = usuario.celular
                Glide.with(this@UsuarioActivity).load(usuario.urlUsuario).into(imageUsuario)
            }
            override fun onError(mensaje: String?) {
                Toast.makeText(this@UsuarioActivity, "Error al cargar usuario: $mensaje", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
