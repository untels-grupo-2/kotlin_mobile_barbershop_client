package com.diamond.appcliente.actividades

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.diamond.appcliente.R
import com.diamond.appcliente.dto.valoracion.ValoracionRequest
import com.diamond.appcliente.util.PreferenciasHelper
import com.diamond.appcliente.viewmodel.GestionarValoracionViewModel

class ValoracionActivity : AppCompatActivity() {

    private lateinit var radioGroupValoracion: RadioGroup
    private lateinit var radioGroupFacilidad: RadioGroup
    private lateinit var editTextComentario: EditText
    private lateinit var btnEnviarValoracion: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_valoracion)

        val token = PreferenciasHelper(application).obtenerToken()
        Log.d("ValoracionActivity", "Token recibido: $token")

        val nombreCliente = intent.getStringExtra("nombre")
        val apellidoCliente = intent.getStringExtra("apellido")
        val imagenUrlCliente = intent.getStringExtra("urlUsuario")

        Log.d("ValoracionActivity", "Nombre Cliente: $nombreCliente")
        Log.d("ValoracionActivity", "Apellido Cliente: $apellidoCliente")
        Log.d("ValoracionActivity", "URL Cliente: $imagenUrlCliente")

        radioGroupValoracion = findViewById(R.id.radioGroupValoracion)
        radioGroupFacilidad = findViewById(R.id.radioGroupFacilidad)
        editTextComentario = findViewById(R.id.editTextComentario)
        btnEnviarValoracion = findViewById(R.id.btnEnviarValoracion)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = Color.BLACK
        }

        findViewById<Button>(R.id.btnCancelar).setOnClickListener {
            startActivity(Intent(this, ClienteHomeActivity::class.java))
            finish()
        }

        btnEnviarValoracion.setOnClickListener { enviarValoracion() }
    }

    private fun enviarValoracion() {
        val valoracion = getValoracionSeleccionada()
        val utilidad = getFacilidadSeleccionada()
        val mensaje = editTextComentario.text.toString()

        if (valoracion != -1 && utilidad != null && mensaje.isNotEmpty()) {
            val request = ValoracionRequest(valoracion, utilidad, mensaje)

            GestionarValoracionViewModel().enviarValoracion(this, request, object : GestionarValoracionViewModel.ValoracionCallback {
                override fun onSuccess(message: String?) {
                    val dialogView = layoutInflater.inflate(R.layout.dialog_valoracion_exitosa, null)
                    val dialog = AlertDialog.Builder(this@ValoracionActivity, R.style.ReservaDialogTheme)
                        .setView(dialogView)
                        .setCancelable(true)
                        .create()

                    dialogView.findViewById<Button>(R.id.btnOk).setOnClickListener {
                        dialog.dismiss()
                        startActivity(Intent(this@ValoracionActivity, ClienteHomeActivity::class.java))
                        finish()
                    }

                    dialog.setOnCancelListener {
                        startActivity(Intent(this@ValoracionActivity, ClienteHomeActivity::class.java))
                        finish()
                    }

                    dialog.show()
                }
                override fun onError(message: String?) {
                    Toast.makeText(this@ValoracionActivity, "Error: $message", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getValoracionSeleccionada(): Int {
        val selectedId = radioGroupValoracion.checkedRadioButtonId
        if (selectedId != -1) {
            return (findViewById<RadioButton>(selectedId)).text.toString().toInt()
        }
        return -1
    }

    private fun getFacilidadSeleccionada(): Boolean? {
        val selectedId = radioGroupFacilidad.checkedRadioButtonId
        if (selectedId != -1) {
            return (findViewById<RadioButton>(selectedId)).text.toString() == "Sí"
        }
        return null
    }
}
