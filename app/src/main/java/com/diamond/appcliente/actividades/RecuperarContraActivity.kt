package com.diamond.appcliente.actividades

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.diamond.appcliente.R
import com.diamond.appcliente.viewmodel.RecuperarContraViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecuperarContraActivity : AppCompatActivity() {

    private lateinit var campoUsuario: EditText
    private lateinit var campoCorreo: EditText
    private lateinit var btnRecuperarContra: Button
    private lateinit var btnVolverLogin: Button
    private val viewModel: RecuperarContraViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recuperar_contra)

        campoUsuario = findViewById(R.id.campoUsuario)
        campoCorreo = findViewById(R.id.campoEmail)
        btnRecuperarContra = findViewById(R.id.btnRecuperarContra)
        btnVolverLogin = findViewById(R.id.btnVolverLogin)

        btnRecuperarContra.setOnClickListener {
            val usuario = campoUsuario.text.toString().trim()
            val correo = campoCorreo.text.toString().trim()
            if (usuario.isEmpty() || correo.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.recuperar(usuario, correo)
        }

        btnVolverLogin.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        viewModel.resultado.observe(this) { mensaje ->
            Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
            finish()
        }

        viewModel.error.observe(this) { mensaje ->
            Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = Color.BLACK
        }
    }
}
