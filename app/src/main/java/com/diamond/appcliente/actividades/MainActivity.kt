package com.diamond.appcliente.actividades

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.diamond.appcliente.R
import com.diamond.appcliente.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var campoUsuario: EditText
    private lateinit var campoContraseña: EditText
    private lateinit var btnIngresarApp: Button
    private lateinit var btnOlvideContrasena: Button
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        campoUsuario = findViewById(R.id.campoUsuario)
        campoContraseña = findViewById(R.id.campoContraseña)
        btnIngresarApp = findViewById(R.id.btnIngresarApp)
        btnOlvideContrasena = findViewById(R.id.btnOlvideContrasena)

        val shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)

        btnIngresarApp.setOnClickListener {
            val usuario = campoUsuario.text.toString()
            val contraseña = campoContraseña.text.toString()

            if (usuario == "abc" && contraseña == "123") {
                val intent = Intent(this, ClienteHomeActivity::class.java)
                intent.putExtra("nombre", "Arian")
                intent.putExtra("apellido", "Prueba")
                intent.putExtra("urlUsuario", "url_de_imagen")
                startActivity(intent)
                finish()
            } else if (usuario.isEmpty() || contraseña.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                if (usuario.isEmpty()) campoUsuario.startAnimation(shakeAnimation)
                if (contraseña.isEmpty()) campoContraseña.startAnimation(shakeAnimation)
            } else {
                mainViewModel.login(usuario, contraseña)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val decorView = window.decorView
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }

        btnOlvideContrasena.setOnClickListener {
            startActivity(Intent(this, RecuperarContraActivity::class.java))
        }

        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        findViewById<View>(R.id.diamondLogo).startAnimation(fadeIn)
        findViewById<View>(R.id.layoutUsuario).startAnimation(fadeIn)
        findViewById<View>(R.id.layoutContraseña).startAnimation(fadeIn)
        btnIngresarApp.startAnimation(fadeIn)
        btnOlvideContrasena.startAnimation(fadeIn)

        mainViewModel.loginStatus.observe(this) { status ->
            when (status) {
                "SUCCESS" -> {
                    val intent = Intent(this, ClienteHomeActivity::class.java)
                    intent.putExtra("nombre", mainViewModel.nombre.value)
                    intent.putExtra("apellido", mainViewModel.apellido.value)
                    intent.putExtra("urlUsuario", mainViewModel.url_usuario.value)
                    startActivity(intent)
                    finish()
                }
                "NO_ADMIN" -> Toast.makeText(this, "Rol no autorizado", Toast.LENGTH_SHORT).show()
                "INVALID" -> Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                else -> Toast.makeText(this, "Error: $status", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
