package com.diamond.appcliente.actividades

import android.content.Intent
import android.graphics.Color
import android.os.Build
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
import com.diamond.appcliente.adapters.BarberoDisponibleAdapter
import com.diamond.appcliente.viewmodel.HorarioBarberoInstanciaViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class VerBarberosActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private var adapter: BarberoDisponibleAdapter? = null
    private val viewModel: HorarioBarberoInstanciaViewModel by viewModels()

    private var nombreServicio: String? = null
    private var descripcionServicio: String? = null
    private var imagenUrlServicio: String? = null
    private var precioServicio: Double = 0.0
    private var servicio_id: Int = -1
    private var nombreBarberoSeleccionado: String? = null
    private var tipoHorario: String? = null

    private var fecha: String? = null
    private var tipoHorarioId: Long = -1L
    private var horarioRangoId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_barberos)

        recyclerView = findViewById(R.id.recyclerViewBarberos)
        nombreServicio = intent.getStringExtra("nombreServicio")
        descripcionServicio = intent.getStringExtra("descripcionServicio")
        precioServicio = intent.getDoubleExtra("precioServicio", 0.0)
        imagenUrlServicio = intent.getStringExtra("imagenServicio")
        servicio_id = intent.getIntExtra("servicio_id", -1)
        recyclerView.layoutManager = LinearLayoutManager(this)

        Glide.with(this).load(imagenUrlServicio).into(findViewById<ImageView>(R.id.imagenServicio))

        findViewById<TextView>(R.id.textTituloServicio).text = nombreServicio
        findViewById<TextView>(R.id.textPrecioServicio).text = "S/ $precioServicio"
        findViewById<TextView>(R.id.textDescripcionServicio).text = descripcionServicio
        findViewById<TextView>(R.id.textSedeServicio).text = "SEDE: VILLA MARIA"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = Color.BLACK
        }

        fecha = intent.getStringExtra("fechaReserva")
        tipoHorario = intent.getStringExtra("tipoHorario")
        horarioRangoId = intent.getIntExtra("horarioRangoId", -1).toLong()
        Log.d("VerBarberosActivity", "horarioRangoId recibido: $horarioRangoId")

        tipoHorarioId = obtenerTipoHorarioValor(tipoHorario)

        Log.d("VerBarberosActivity", "Fecha: $fecha | TipoHorarioId: $tipoHorarioId | HorarioRangoId: $horarioRangoId | servicioID: $servicio_id")

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.barberosDisponibles.collect { barberos ->
                    if (barberos.isNotEmpty()) {
                        adapter = BarberoDisponibleAdapter(barberos, this@VerBarberosActivity)
                        recyclerView.adapter = adapter
                    } else {
                        Toast.makeText(this@VerBarberosActivity, "No hay barberos disponibles para esta fecha y horario", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        obtenerBarberosDisponibles()

        findViewById<Button>(R.id.btnReservar).setOnClickListener {
            val barberoSeleccionado = adapter?.getBarberoSeleccionado()
            if (barberoSeleccionado != null) {
                nombreBarberoSeleccionado = barberoSeleccionado.nombre
                Log.d("VerBarberosActivity", "Barbero ID: ${barberoSeleccionado.barberoId} | Nombre: $nombreBarberoSeleccionado")

                val intentReserva = Intent(this, ReservaActivity::class.java)
                intentReserva.putExtra("barbero", nombreBarberoSeleccionado)
                intentReserva.putExtra("barberoId", barberoSeleccionado.barberoId)
                intentReserva.putExtra("horario", tipoHorario)
                intentReserva.putExtra("servicio", nombreServicio)
                intentReserva.putExtra("servicio_id", servicio_id)
                intentReserva.putExtra("fechaReserva", fecha)
                intentReserva.putExtra("horarioRangoId", horarioRangoId)
                startActivity(intentReserva)
            } else {
                Toast.makeText(this, "Por favor, selecciona un barbero", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.btnCancelar).setOnClickListener {
            startActivity(Intent(this, ClienteHomeActivity::class.java))
            finish()
        }
    }

    private fun obtenerBarberosDisponibles() {
        if (tipoHorarioId == -1L || horarioRangoId == -1L || fecha == null) {
            Log.e("VerBarberosActivity", "Los valores tipoHorarioId, horarioRangoId o fecha no son válidos.")
            return
        }
        viewModel.obtenerBarberosDisponibles(fecha!!, tipoHorarioId, horarioRangoId)
    }

    private fun obtenerTipoHorarioValor(tipoHorario: String?): Long = when (tipoHorario) {
        "MAÑANA" -> 1L
        "TARDE" -> 2L
        "NOCHE" -> 3L
        else -> -1L
    }
}
