package com.diamond.appcliente.actividades

import android.app.DatePickerDialog
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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.diamond.appcliente.R
import com.diamond.appcliente.adapters.HorarioRangoAdapter
import com.diamond.appcliente.dto.horariorango.HorarioRangoDto
import com.diamond.barbershop.shared.dto.servicio.ServicioRequest
import com.diamond.appcliente.ui.state.UiState
import com.diamond.appcliente.viewmodel.GestionarHorarioRangoViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class ListarRangoHorarios : AuthActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var horarioRangoAdapter: HorarioRangoAdapter
    private lateinit var textFechaSeleccionada: TextView
    private val gestionarHorarioRangoViewModel: GestionarHorarioRangoViewModel by viewModels()
    private lateinit var servicio: ServicioRequest

    private var botonSeleccionado: String? = null
    private var horarioRangoId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listar_rango_horarios)

        findViewById<MaterialToolbar>(R.id.toolbar).setNavigationOnClickListener { finish() }

        textFechaSeleccionada = findViewById(R.id.textFechaSeleccionada)

        val nombreCliente = intent.getStringExtra("nombreCliente")
        val apellidoCliente = intent.getStringExtra("apellidoCliente")
        val nombreServicio = intent.getStringExtra("nombreServicio")
        val descripcionServicio = intent.getStringExtra("descripcionServicio")
        val imagenUrlServicio = intent.getStringExtra("imagenServicio")
        val precioServicio = intent.getDoubleExtra("precioServicio", 0.0)
        val servicio_id = intent.getIntExtra("servicio_id", -1)

        Log.d("ListarRangoHorarios", "Servicio ID recibido: $servicio_id")
        servicio = ServicioRequest(nombreServicio ?: "", precioServicio, imagenUrlServicio ?: "", servicio_id)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = Color.BLACK
        }

        findViewById<ImageView>(R.id.btnSeleccionarFecha).setOnClickListener { mostrarCalendario() }
        findViewById<Button>(R.id.btnConfirmarFechayHora).setOnClickListener {
            confirmarReserva(nombreCliente, apellidoCliente, nombreServicio, descripcionServicio, imagenUrlServicio, precioServicio, servicio_id)
        }
        findViewById<Button>(R.id.btnCancelarReserva).setOnClickListener { finish() }

        recyclerView = findViewById(R.id.recyclerViewHorarios)
        recyclerView.layoutManager = GridLayoutManager(this, 1)

        val listaHorarios = mutableListOf<HorarioRangoDto>()
        horarioRangoAdapter = HorarioRangoAdapter(this, listaHorarios, object : HorarioRangoAdapter.OnHorarioSelectedListener {
            override fun onHorarioSelected(horario: String?, horarioRangoId: Int) {
                botonSeleccionado = horario
                this@ListarRangoHorarios.horarioRangoId = horarioRangoId
                Log.d("ListarRangoHorarios", "horarioRangoId seleccionado: $horarioRangoId")
            }
        })
        recyclerView.adapter = horarioRangoAdapter

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.nav_home
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
                    gestionarHorarioRangoViewModel.horarios.collect { state ->
                        when (state) {
                            is UiState.Success -> {
                                val horarios = state.data
                                if (horarios.isNotEmpty()) {
                                    val todosHorarios = mutableListOf<HorarioRangoDto>()
                                    todosHorarios.add(HorarioRangoDto(rango = "🌅 Turno mañana", tipoHorario = "Encabezado"))
                                    horarios.filter { it.tipoHorario == "MAÑANA" }.forEach { todosHorarios.add(it) }
                                    todosHorarios.add(HorarioRangoDto(rango = "☀️ Turno tarde", tipoHorario = "Encabezado"))
                                    horarios.filter { it.tipoHorario == "TARDE" }.forEach { todosHorarios.add(it) }
                                    todosHorarios.add(HorarioRangoDto(rango = "🌙 Turno noche", tipoHorario = "Encabezado"))
                                    horarios.filter { it.tipoHorario == "NOCHE" }.forEach { todosHorarios.add(it) }
                                    ordenarHorarios(todosHorarios)
                                    setUpRecyclerView(todosHorarios)
                                }
                            }
                            is UiState.Error -> Toast.makeText(this@ListarRangoHorarios, state.message, Toast.LENGTH_SHORT).show()
                            else -> {}
                        }
                    }
                }
            }
        }

        gestionarHorarioRangoViewModel.cargarHorarios(1)
    }

    private fun mostrarCalendario() {
        val calendario = Calendar.getInstance()
        val locale = Locale("es", "ES")
        Locale.setDefault(locale)

        val dayOfWeek = calendario.get(Calendar.DAY_OF_WEEK)
        val daysToAdd = when (dayOfWeek) {
            Calendar.MONDAY -> 6; Calendar.TUESDAY -> 5; Calendar.WEDNESDAY -> 4
            Calendar.THURSDAY -> 3; Calendar.FRIDAY -> 2; Calendar.SATURDAY -> 1
            else -> 0
        }

        val endOfWeekCalendar = Calendar.getInstance()
        endOfWeekCalendar.add(Calendar.DAY_OF_WEEK, daysToAdd)

        val datePickerDialog = DatePickerDialog(
            this, R.style.CustomDatePickerDialogTheme,
            { _, year, month, dayOfMonth ->
                val fechaSeleccionada = Calendar.getInstance()
                fechaSeleccionada.set(year, month, dayOfMonth)
                textFechaSeleccionada.text = SimpleDateFormat("yyyy-MM-dd", locale).format(fechaSeleccionada.time)
            },
            calendario.get(Calendar.YEAR), calendario.get(Calendar.MONTH), calendario.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        datePickerDialog.datePicker.maxDate = endOfWeekCalendar.timeInMillis
        datePickerDialog.show()
    }

    private fun setUpRecyclerView(horarioRangos: List<HorarioRangoDto>) {
        horarioRangoAdapter = HorarioRangoAdapter(this, horarioRangos, object : HorarioRangoAdapter.OnHorarioSelectedListener {
            override fun onHorarioSelected(horario: String?, horarioRangoId: Int) {
                botonSeleccionado = horario
                this@ListarRangoHorarios.horarioRangoId = horarioRangoId
                Log.d("ListarRangoHorarios", "horarioRangoId seleccionado: $horarioRangoId")
            }
        })
        recyclerView.adapter = horarioRangoAdapter
    }

    private fun obtenerTipoHorario(botonSeleccionado: String): String {
        val parts = botonSeleccionado.split(" ")
        var horaInt = parts[0].toIntOrNull() ?: return "MAÑANA"
        val periodo = if (parts.size > 1) parts[1].uppercase() else "AM"
        if (periodo == "PM" && horaInt != 12) horaInt += 12
        else if (periodo == "AM" && horaInt == 12) horaInt = 12 // backend sends 12 AM for noon
        return when {
            horaInt in 6..12 -> "MAÑANA"
            horaInt in 13..17 -> "TARDE"
            else -> "NOCHE"
        }
    }

    private fun ordenarHorarios(todosHorarios: MutableList<HorarioRangoDto>) {
        val fmt = SimpleDateFormat("h a", java.util.Locale.US)

        fun parseTime(rango: String?): Long {
            val hora = rango?.split(" - ")?.get(0)?.trim() ?: return Long.MAX_VALUE
            return try {
                val fixed = if (hora == "12 AM") "12 PM" else hora
                fmt.parse(fixed)?.time ?: Long.MAX_VALUE
            } catch (e: Exception) { Long.MAX_VALUE }
        }

        val mananaHeader = todosHorarios.firstOrNull { it.tipoHorario == "Encabezado" && it.rango?.contains("mañana") == true }
        val tardeHeader  = todosHorarios.firstOrNull { it.tipoHorario == "Encabezado" && it.rango?.contains("tarde")  == true }
        val nocheHeader  = todosHorarios.firstOrNull { it.tipoHorario == "Encabezado" && it.rango?.contains("noche")  == true }

        val manana = todosHorarios.filter { it.tipoHorario == "MAÑANA" }.sortedBy { parseTime(it.rango) }
        val tarde  = todosHorarios.filter { it.tipoHorario == "TARDE"  }.sortedBy { parseTime(it.rango) }
        val noche  = todosHorarios.filter { it.tipoHorario == "NOCHE"  }.sortedBy { parseTime(it.rango) }

        todosHorarios.clear()
        if (mananaHeader != null) { todosHorarios.add(mananaHeader); todosHorarios.addAll(manana) }
        if (tardeHeader  != null) { todosHorarios.add(tardeHeader);  todosHorarios.addAll(tarde)  }
        if (nocheHeader  != null) { todosHorarios.add(nocheHeader);  todosHorarios.addAll(noche)  }
    }

    private fun confirmarReserva(
        nombreCliente: String?, apellidoCliente: String?, nombreServicio: String?,
        descripcionServicio: String?, imagenUrlServicio: String?,
        precioServicio: Double, servicio_id: Int
    ) {
        if (textFechaSeleccionada.text.toString() == "Selecciona una fecha") {
            Toast.makeText(this, "Por favor, selecciona una fecha", Toast.LENGTH_SHORT).show(); return
        }
        if (botonSeleccionado == null) {
            Toast.makeText(this, "Por favor, selecciona un horario", Toast.LENGTH_SHORT).show(); return
        }
        Log.d("ListarRangoHorarios", "horarioRangoId antes de enviar: $horarioRangoId")

        val intent = Intent(this, VerBarberosActivity::class.java)
        intent.putExtra("nombreCliente", nombreCliente)
        intent.putExtra("apellidoCliente", apellidoCliente)
        intent.putExtra("fechaReserva", textFechaSeleccionada.text.toString())
        intent.putExtra("turnoReserva", botonSeleccionado)
        intent.putExtra("nombreServicio", nombreServicio)
        intent.putExtra("descripcionServicio", descripcionServicio)
        intent.putExtra("precioServicio", precioServicio)
        intent.putExtra("imagenServicio", imagenUrlServicio)
        intent.putExtra("tipoHorario", obtenerTipoHorario(botonSeleccionado!!))
        intent.putExtra("servicio_id", servicio_id)
        intent.putExtra("horarioRangoId", horarioRangoId)
        startActivity(intent)
    }
}
