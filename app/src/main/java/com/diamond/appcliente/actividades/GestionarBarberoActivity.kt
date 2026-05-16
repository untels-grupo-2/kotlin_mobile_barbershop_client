package com.diamond.appcliente.actividades

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.diamond.appcliente.R
import com.diamond.appcliente.adapters.BarberoAdapter
import com.diamond.appcliente.dto.barbero.BarberoDto
import com.diamond.appcliente.viewmodel.GestionarBarberoViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GestionarBarberoActivity : AppCompatActivity() {

    private val viewModel: GestionarBarberoViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private var adapter: BarberoAdapter? = null

    private var fechaReserva: String? = null
    private var turnoReserva: String? = null
    private var nombreServicio: String? = null
    private var descripcionServicio: String? = null
    private var imagenUrlServicio: String? = null
    private var nombreCliente: String? = null
    private var apellidoCliente: String? = null
    private var nombreBarberoSeleccionado: String? = null
    private var tipoHorario: String? = null
    private var precioServicio: Double = 0.0
    private var tipoHorarioValor: Int = -1
    private var turnoReservaValor: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestionar_barbero)

        fechaReserva = intent.getStringExtra("fechaReserva")
        turnoReserva = intent.getStringExtra("turnoReserva")
        nombreServicio = intent.getStringExtra("nombreServicio")
        descripcionServicio = intent.getStringExtra("descripcionServicio")
        precioServicio = intent.getDoubleExtra("precioServicio", 0.0)
        imagenUrlServicio = intent.getStringExtra("imagenServicio")
        nombreCliente = intent.getStringExtra("nombreCliente")
        apellidoCliente = intent.getStringExtra("apellidoCliente")
        tipoHorario = intent.getStringExtra("tipoHorario")

        tipoHorarioValor = obtenerTipoHorarioValor(tipoHorario)
        turnoReservaValor = obtenerTurnoReservaValor(turnoReserva)

        Log.d("GestionarBarberoActivity", "Tipo Horario Valor: $tipoHorarioValor | Turno Reserva Valor: $turnoReservaValor")

        Glide.with(this).load(imagenUrlServicio).into(findViewById<ImageView>(R.id.imagenServicio))

        recyclerView = findViewById(R.id.recyclerViewBarberos)
        recyclerView.layoutManager = LinearLayoutManager(this)

        findViewById<TextView>(R.id.textTituloServicio).text = nombreServicio
        findViewById<TextView>(R.id.textPrecioServicio).text = "S/ $precioServicio"
        findViewById<TextView>(R.id.textDescripcionServicio).text = descripcionServicio
        findViewById<TextView>(R.id.textSedeServicio).text = "SEDE: VILLA MARIA"

        findViewById<Button>(R.id.btnReservar).setOnClickListener { mostrarPopupConfirmacion() }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.barberos.collect { barberos ->
                        if (barberos.isEmpty()) return@collect
                        adapter = BarberoAdapter(barberos, object : BarberoAdapter.OnBarberoClickListener {
                            override fun onBarberoSeleccionado(barbero: BarberoDto, nombreBarbero: String?) {
                                nombreBarberoSeleccionado = nombreBarbero
                                Toast.makeText(this@GestionarBarberoActivity, "Barbero seleccionado: $nombreBarbero", Toast.LENGTH_SHORT).show()
                            }
                            override fun onActualizar(barbero: BarberoDto) {}
                            override fun onEliminar(barbero: BarberoDto) {}
                        })
                        recyclerView.adapter = adapter
                    }
                }
                launch {
                    viewModel.error.collect { msg ->
                        Toast.makeText(this@GestionarBarberoActivity, msg, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        viewModel.cargarBarberos()
    }

    private fun mostrarPopupConfirmacion() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.popup_reserva_confirmada)
        dialog.setCancelable(false)

        val mensaje = "$nombreCliente $apellidoCliente se ha realizado tu reserva con el barbero: " +
                "$nombreBarberoSeleccionado para el $fechaReserva a las $turnoReserva en el turno de: $tipoHorario" +
                " (Valor: $tipoHorarioValor, Turno valor: $turnoReservaValor)"

        dialog.findViewById<TextView>(R.id.textTitulo).text = "¡Reserva confirmada!"
        dialog.findViewById<TextView>(R.id.textDetalleReserva).text = mensaje

        dialog.findViewById<Button>(R.id.btnContinuar).setOnClickListener {
            dialog.dismiss()
            val intent = Intent(this, ValoracionActivity::class.java)
            intent.putExtra("nombreCliente", nombreCliente)
            startActivity(intent)
        }

        dialog.show()
    }

    private fun obtenerTipoHorarioValor(tipoHorario: String?): Int = when (tipoHorario) {
        "MAÑANA" -> 1; "TARDE" -> 2; "NOCHE" -> 3; else -> -1
    }

    private fun obtenerTurnoReservaValor(boton: String?): Int = when (boton) {
        "9 AM - 10 AM" -> 1; "10 AM - 11 AM" -> 2; "11 AM - 12 PM" -> 3; "12 PM - 1 PM" -> 4
        "1 PM - 2 PM" -> 5; "2 PM - 3 PM" -> 6; "3 PM - 4 PM" -> 7; "4 PM - 5 PM" -> 8
        "5 PM - 6 PM" -> 9; "6 PM - 7 PM" -> 10; "7 PM - 8 PM" -> 11; "8 PM - 9 PM" -> 12
        "9 PM - 10 PM" -> 13; else -> -1
    }
}
