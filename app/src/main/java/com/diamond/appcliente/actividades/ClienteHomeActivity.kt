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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.diamond.appcliente.R
import com.diamond.appcliente.adapters.ServicioAdapter
import com.diamond.appcliente.dto.servicio.ServicioDto
import com.diamond.appcliente.viewmodel.GestionarServicioViewModel
import com.diamond.appcliente.viewmodel.ListarReservaViewModel
import com.diamond.appcliente.viewmodel.UsuarioViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ClienteHomeActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private var adapter: ServicioAdapter? = null
    private var listaServicios: List<ServicioDto> = emptyList()
    private val viewModel1: GestionarServicioViewModel by viewModels()
    private val listarReservaViewModel: ListarReservaViewModel by viewModels()
    private val usuarioViewModel: UsuarioViewModel by viewModels()

    private lateinit var clienteNombre: TextView
    private lateinit var clienteFoto: ImageView
    private lateinit var metaProgresoTexto: TextView
    private lateinit var metaProgresoImagen: ImageView

    private var nombreCliente: String? = null
    private var apellidoCliente: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cliente_home)

        clienteNombre = findViewById(R.id.clienteNombre)
        clienteFoto = findViewById(R.id.clienteFoto)
        metaProgresoTexto = findViewById(R.id.metaProgresoTexto)
        metaProgresoImagen = findViewById(R.id.metaProgresoImagen)

        recyclerView = findViewById(R.id.recyclerViewServicios)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = Color.BLACK
        }

        findViewById<Button>(R.id.serviciosButton).setOnClickListener { updateRecyclerView(listaServicios) }
        findViewById<Button>(R.id.cortesButton).setOnClickListener { updateRecyclerView(filterServiciosByType("CORTES")) }
        findViewById<Button>(R.id.ColoracionButton).setOnClickListener { updateRecyclerView(filterServiciosByType("COLORACIÓN")) }
        findViewById<Button>(R.id.SkincareButton).setOnClickListener { updateRecyclerView(filterServiciosByType("SKINCARE")) }
        findViewById<Button>(R.id.AfeitadoButton).setOnClickListener { updateRecyclerView(filterServiciosByType("AFEITADO DE BARBA")) }

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
                    usuarioViewModel.usuario.collect { usuario ->
                        usuario ?: return@collect
                        nombreCliente = usuario.nombre
                        apellidoCliente = usuario.apellido
                        clienteNombre.text = "${usuario.nombre} ${usuario.apellido}"
                        if (!usuario.urlUsuario.isNullOrEmpty()) {
                            Glide.with(this@ClienteHomeActivity).load(usuario.urlUsuario).into(clienteFoto)
                        } else {
                            clienteFoto.setImageResource(R.drawable.perfil_default)
                        }
                    }
                }
                launch {
                    viewModel1.servicios.collect { servicios ->
                        if (servicios.isEmpty()) return@collect
                        listaServicios = servicios
                        adapter = ServicioAdapter(servicios, object : ServicioAdapter.OnServicioClickListener {
                            override fun onAviso(servicio: ServicioDto, imagenUrl: String?) {
                                Log.d("IntentData", "nombreServicio: ${servicio.nombre} | servicio_id: ${servicio.servicio_id}")
                                val intent = Intent(this@ClienteHomeActivity, ListarRangoHorarios::class.java)
                                intent.putExtra("nombreServicio", servicio.nombre)
                                intent.putExtra("servicio_id", servicio.servicio_id)
                                intent.putExtra("descripcionServicio", servicio.descripcion)
                                intent.putExtra("precioServicio", servicio.precio)
                                intent.putExtra("imagenServicio", imagenUrl)
                                intent.putExtra("nombre", nombreCliente)
                                intent.putExtra("apellido", apellidoCliente)
                                startActivity(intent)
                            }
                        })
                        recyclerView.adapter = adapter
                    }
                }
                launch {
                    viewModel1.error.collect { msg ->
                        Toast.makeText(this@ClienteHomeActivity, msg, Toast.LENGTH_SHORT).show()
                    }
                }
                launch {
                    listarReservaViewModel.reservas.collect { reservas ->
                        reservas ?: return@collect
                        var count = reservas.count { it.estRecompensa == 0 }
                        if (count > 7) count = 7
                        updateMetaProgress(count)
                    }
                }
            }
        }

        usuarioViewModel.obtenerMiUsuario()
        viewModel1.cargarServicios()
    }

    override fun onResume() {
        super.onResume()
        listarReservaViewModel.cargarReservas()
    }

    private fun filterServiciosByType(tipo: String): List<ServicioDto> =
        listaServicios.filter { it.nombre_tipoServicio.equals(tipo, ignoreCase = true) }

    private fun updateRecyclerView(serviciosFiltrados: List<ServicioDto>) {
        adapter?.updateServicios(serviciosFiltrados)
        if (serviciosFiltrados.isEmpty()) {
            Toast.makeText(this, "No se encontraron servicios para esta categoría", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateMetaProgress(reservasRealizadas: Int) {
        metaProgresoTexto.text = "$reservasRealizadas/7 Cortes"
        val progresoImagenId = when (reservasRealizadas) {
            1 -> R.drawable.racha_1
            2 -> R.drawable.racha_2
            3 -> R.drawable.racha_3
            4 -> R.drawable.racha_4
            5 -> R.drawable.racha_5
            6 -> R.drawable.racha_6
            7 -> R.drawable.racha_7
            else -> R.drawable.racha_0
        }
        metaProgresoImagen.setImageResource(progresoImagenId)
    }
}
