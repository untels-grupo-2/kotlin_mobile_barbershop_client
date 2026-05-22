package com.diamond.appcliente.actividades

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.diamond.appcliente.R
import com.diamond.appcliente.dto.reserva.DtoReserva
import com.diamond.appcliente.viewmodel.GestionarReservaViewModel
import com.diamond.appcliente.viewmodel.ListarReservaViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date

@AndroidEntryPoint
class ReservaActivity : AppCompatActivity() {

    private lateinit var textViewBarbero: TextView
    private lateinit var textViewHorario: TextView
    private lateinit var textViewServicio: TextView
    private lateinit var textViewFecha: TextView
    private lateinit var editTextAdicionales: EditText
    private lateinit var btnEnviarReserva: Button
    private lateinit var btnEnviarReservaRecompensa: Button
    private var dialog: AlertDialog? = null

    private val reservaViewModel: GestionarReservaViewModel by viewModels()
    private val listarReservaViewModel: ListarReservaViewModel by viewModels()

    private var horarioRangoId: Long = -1L
    private var servicio_id: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reserva)

        textViewBarbero = findViewById(R.id.textViewBarbero)
        textViewHorario = findViewById(R.id.textViewHorario)
        textViewServicio = findViewById(R.id.textViewServicio)
        textViewFecha = findViewById(R.id.textViewFecha)
        editTextAdicionales = findViewById(R.id.editTextAdicionales)
        btnEnviarReserva = findViewById(R.id.btnEnviarReserva)
        btnEnviarReservaRecompensa = findViewById(R.id.btnEnviarReservaRecompensa)

        val barbero = intent.getStringExtra("barbero")
        val horario = intent.getStringExtra("horario")
        val servicio = intent.getStringExtra("servicio")
        val barberoId = intent.getLongExtra("barberoId", -1L)
        val fecha = intent.getStringExtra("fechaReserva")

        servicio_id = intent.getIntExtra("servicio_id", -1).toLong()
        horarioRangoId = intent.getLongExtra("horarioRangoId", -1L)

        Log.d("ReservaActivity", "Servicio ID: $servicio_id | HorarioRangoId: $horarioRangoId | BarberoId: $barberoId")

        textViewBarbero.text = "Barbero: $barbero"
        textViewHorario.text = "Turno: $horario"
        textViewServicio.text = "Servicio: $servicio"

        textViewFecha.text = if (!fecha.isNullOrEmpty()) fecha
        else "Fecha: ${SimpleDateFormat("yyyy-MM-dd").format(Date())}"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = Color.BLACK
        }

        btnEnviarReserva.setOnClickListener { enviarReserva(barberoId) }
        btnEnviarReservaRecompensa.setOnClickListener { enviarReservaRecompensa(barberoId) }

        findViewById<Button>(R.id.btnCancelar).setOnClickListener {
            startActivity(Intent(this, ClienteHomeActivity::class.java))
            finish()
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    listarReservaViewModel.reservas.collect { reservas ->
                        reservas ?: return@collect
                        activarBotonReservaRecompensa(reservas.count { it.estRecompensa == 0 } >= 7)
                    }
                }
                launch {
                    reservaViewModel.evento.collect { evento ->
                        when (evento) {
                            is GestionarReservaViewModel.ReservaUiEvent.Exito -> mostrarDialogoExito()
                            is GestionarReservaViewModel.ReservaUiEvent.Error -> Toast.makeText(this@ReservaActivity, evento.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        listarReservaViewModel.cargarReservas()
    }

    private fun activarBotonReservaRecompensa(activar: Boolean) {
        btnEnviarReservaRecompensa.isEnabled = activar
        btnEnviarReservaRecompensa.setBackgroundColor(
            resources.getColor(if (activar) R.color.orange else R.color.gray)
        )
    }

    private fun mostrarDialogoExito() {
        dialog?.takeIf { it.isShowing }?.dismiss()
        val dialogView = layoutInflater.inflate(R.layout.dialog_reserva_exitosa, null)
        dialog = AlertDialog.Builder(this).setView(dialogView).setCancelable(true).create()

        dialogView.findViewById<Button>(R.id.btnOk).setOnClickListener {
            dialog?.dismiss()
            startActivity(Intent(this, ValoracionActivity::class.java))
            finish()
        }
        dialogView.findViewById<Button>(R.id.btnNO).setOnClickListener {
            dialog?.dismiss()
            startActivity(Intent(this, ClienteHomeActivity::class.java))
            finish()
        }
        dialog?.setOnCancelListener {
            startActivity(Intent(this, ClienteHomeActivity::class.java))
            finish()
        }
        dialog?.show()
    }

    private fun enviarReserva(barberoId: Long) {
        val request = DtoReserva(barberoId, horarioRangoId, textViewFecha.text.toString(), servicio_id, editTextAdicionales.text.toString())
        Log.d("Reserva", "Datos de la reserva: ${Gson().toJson(request)}")
        reservaViewModel.enviarReserva(request)
    }

    private fun enviarReservaRecompensa(barberoId: Long) {
        val request = DtoReserva(barberoId, horarioRangoId, textViewFecha.text.toString(), servicio_id, editTextAdicionales.text.toString())
        Log.d("Reserva", "Datos de la reserva con recompensa: ${Gson().toJson(request)}")
        reservaViewModel.crearReservaRecompensa(request)
    }
}
