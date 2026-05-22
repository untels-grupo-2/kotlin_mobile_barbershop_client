package com.diamond.appcliente.actividades

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.diamond.appcliente.R
import com.diamond.appcliente.dto.valoracion.ValoracionRequest
import com.diamond.appcliente.viewmodel.GestionarValoracionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ValoracionActivity : AppCompatActivity() {

    private lateinit var radioGroupValoracion: RadioGroup
    private lateinit var radioGroupFacilidad: RadioGroup
    private lateinit var editTextComentario: EditText
    private val viewModel: GestionarValoracionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_valoracion)

        Log.d("ValoracionActivity", "Nombre: ${intent.getStringExtra("nombre")} | Apellido: ${intent.getStringExtra("apellido")}")

        radioGroupValoracion = findViewById(R.id.radioGroupValoracion)
        radioGroupFacilidad = findViewById(R.id.radioGroupFacilidad)
        editTextComentario = findViewById(R.id.editTextComentario)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = Color.BLACK
        }

        findViewById<Button>(R.id.btnCancelar).setOnClickListener {
            startActivity(Intent(this, ClienteHomeActivity::class.java))
            finish()
        }

        findViewById<Button>(R.id.btnEnviarValoracion).setOnClickListener { enviarValoracion() }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.evento.collect { evento ->
                    when (evento) {
                        is GestionarValoracionViewModel.ValoracionUiEvent.Exito -> {
                            val dialogView = layoutInflater.inflate(R.layout.dialog_valoracion_exitosa, null)
                            val dialog = AlertDialog.Builder(this@ValoracionActivity, R.style.ReservaDialogTheme)
                                .setView(dialogView).setCancelable(true).create()

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
                        is GestionarValoracionViewModel.ValoracionUiEvent.Error ->
                            Toast.makeText(this@ValoracionActivity, "Error: ${evento.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun enviarValoracion() {
        val valoracion = getValoracionSeleccionada()
        val utilidad = getFacilidadSeleccionada()
        val mensaje = editTextComentario.text.toString()

        if (valoracion != -1 && utilidad != null && mensaje.isNotEmpty()) {
            viewModel.enviarValoracion(ValoracionRequest(valoracion, utilidad, mensaje))
        } else {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getValoracionSeleccionada(): Int {
        val selectedId = radioGroupValoracion.checkedRadioButtonId
        return if (selectedId != -1) (findViewById<RadioButton>(selectedId)).text.toString().toInt() else -1
    }

    private fun getFacilidadSeleccionada(): Boolean? {
        val selectedId = radioGroupFacilidad.checkedRadioButtonId
        return if (selectedId != -1) (findViewById<RadioButton>(selectedId)).text.toString() == "Sí" else null
    }
}
