package com.diamond.appcliente.actividades

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.diamond.appcliente.R
import com.diamond.appcliente.dto.reserva.DtoReserva
import com.diamond.appcliente.dto.reserva.ReservaResponse
import com.diamond.appcliente.util.PreferenciasHelper
import com.diamond.appcliente.viewmodel.GestionarReservaViewModel
import com.diamond.appcliente.viewmodel.ListarReservaViewModel
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Date

class ReservaActivity : AppCompatActivity() {

    private lateinit var textViewBarbero: TextView
    private lateinit var textViewHorario: TextView
    private lateinit var textViewServicio: TextView
    private lateinit var textViewFecha: TextView
    private lateinit var editTextAdicionales: EditText
    private lateinit var btnEnviarReserva: Button
    private lateinit var btnEnviarReservaRecompensa: Button
    private var dialog: AlertDialog? = null

    private var horarioRangoId: Long = -1L
    private var servicio_id: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reserva)

        val token = PreferenciasHelper(application).obtenerToken()
        Log.d("ReservaActivity", "Token recibido: $token")

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

        Log.d("ReservaActivity", "Servicio ID recibido: $servicio_id")
        Log.d("ReservaActivity", "HorarioRangoId recibido: $horarioRangoId")
        Log.d("ReservaActivity", "Barbero ID: $barberoId")
        Log.d("ReservaActivity", "Horario: $horario")
        Log.d("ReservaActivity", "Servicio: $servicio")
        Log.d("ReservaActivity", "Fecha Reserva: $fecha")

        textViewBarbero.text = "Barbero: $barbero"
        textViewHorario.text = "Turno: $horario"
        textViewServicio.text = "Servicio: $servicio"

        if (!fecha.isNullOrEmpty()) {
            textViewFecha.text = fecha
        } else {
            textViewFecha.text = "Fecha: ${SimpleDateFormat("yyyy-MM-dd").format(Date())}"
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = Color.BLACK
        }

        verificarReservas()

        btnEnviarReserva.setOnClickListener { enviarReserva(barberoId, horario, servicio_id) }
        btnEnviarReservaRecompensa.setOnClickListener { enviarReservaRecompensa(barberoId, horario, servicio_id) }

        findViewById<Button>(R.id.btnCancelar).setOnClickListener {
            startActivity(Intent(this, ClienteHomeActivity::class.java))
            finish()
        }
    }

    private fun verificarReservas() {
        ListarReservaViewModel(application).getReservas().observe(this) { reservas ->
            if (reservas != null) {
                val count = reservas.count { it.estRecompensa == 0 }
                activarBotonReservaRecompensa(count >= 7)
            }
        }
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
        dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

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

    private fun enviarReserva(barberoId: Long, horario: String?, servicio_id: Long) {
        val fechaReserva = textViewFecha.text.toString()
        val adicionales = editTextAdicionales.text.toString()
        val request = DtoReserva(barberoId, horarioRangoId, fechaReserva, servicio_id, adicionales)
        Log.d("Reserva", "Datos de la reserva: ${Gson().toJson(request)}")

        GestionarReservaViewModel().enviarReserva(this, request, object : GestionarReservaViewModel.ReservaCallback {
            override fun onSuccess(message: String?) { mostrarDialogoExito() }
            override fun onError(message: String?) {
                Toast.makeText(this@ReservaActivity, "Error: $message", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun enviarReservaRecompensa(barberoId: Long, horario: String?, servicio_id: Long) {
        val fechaReserva = textViewFecha.text.toString()
        val adicionales = editTextAdicionales.text.toString()
        val request = DtoReserva(barberoId, horarioRangoId, fechaReserva, servicio_id, adicionales)
        Log.d("Reserva", "Datos de la reserva con recompensa: ${Gson().toJson(request)}")

        GestionarReservaViewModel().crearReservaRecompensa(this, request, object : GestionarReservaViewModel.ReservaCallback {
            override fun onSuccess(message: String?) { mostrarDialogoExito() }
            override fun onError(message: String?) {
                Toast.makeText(this@ReservaActivity, "Error: $message", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
