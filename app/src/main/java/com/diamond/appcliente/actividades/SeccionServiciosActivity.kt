package com.diamond.appcliente.actividades

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.diamond.appcliente.R
import com.diamond.appcliente.adapters.ServicioAdapter
import com.diamond.barbershop.shared.dto.servicio.ServicioDto
import com.shared.models.ui.state.UiState
import com.diamond.appcliente.viewmodel.GestionarServicioViewModel
import com.diamond.appcliente.viewmodel.UsuarioViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SeccionServiciosActivity : AuthActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBarServicios: ProgressBar
    private var adapter: ServicioAdapter? = null
    private var listaServicios: List<ServicioDto> = emptyList()
    private val viewModel1: GestionarServicioViewModel by viewModels()
    private val usuarioViewModel: UsuarioViewModel by viewModels()
    private var staleSnackbar: Snackbar? = null

    private var nombreCliente: String? = null
    private var apellidoCliente: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_area_servicios)

        recyclerView = findViewById(R.id.recyclerViewServicios)
        progressBarServicios = findViewById(R.id.progressBarServicios)
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
        bottomNavigationView.selectedItemId = R.id.nav_servicios
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
                    usuarioViewModel.usuario.collect { state ->
                        if (state is UiState.Success) {
                            nombreCliente = state.data.nombre
                            apellidoCliente = state.data.apellido
                        }
                    }
                }
                launch {
                    viewModel1.servicios.collect { state ->
                        when (state) {
                            is UiState.Loading -> progressBarServicios.visibility = View.VISIBLE
                            is UiState.Success -> {
                                progressBarServicios.visibility = View.GONE
                                mostrarIndicadorDatos(state.isStale)
                                if (state.data.isNotEmpty()) {
                                    listaServicios = state.data
                                    adapter = ServicioAdapter(state.data, object : ServicioAdapter.OnServicioClickListener {
                                        override fun onAviso(servicio: ServicioDto, imagenUrl: String?) {
                                            Log.d("IntentData", "nombreServicio: ${servicio.nombre} | servicio_id: ${servicio.servicio_id}")
                                            val intent = Intent(this@SeccionServiciosActivity, ListarRangoHorarios::class.java)
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
                            is UiState.Error -> {
                                progressBarServicios.visibility = View.GONE
                                Toast.makeText(this@SeccionServiciosActivity, state.message, Toast.LENGTH_SHORT).show()
                            }
                            else -> {}
                        }
                    }
                }
            }
        }

        usuarioViewModel.obtenerMiUsuario()
        viewModel1.cargarServicios()
    }

    private fun mostrarIndicadorDatos(isStale: Boolean) {
        if (isStale) {
            if (staleSnackbar?.isShown != true) {
                staleSnackbar = Snackbar.make(
                    findViewById(android.R.id.content),
                    "Sin conexión – mostrando datos guardados",
                    Snackbar.LENGTH_INDEFINITE
                )
                staleSnackbar?.show()
            }
        } else {
            staleSnackbar?.dismiss()
            staleSnackbar = null
        }
    }

    private fun filterServiciosByType(tipo: String): List<ServicioDto> =
        listaServicios.filter { it.nombre_tipoServicio.equals(tipo, ignoreCase = true) }

    private fun updateRecyclerView(serviciosFiltrados: List<ServicioDto>) {
        adapter?.updateServicios(serviciosFiltrados)
        if (serviciosFiltrados.isEmpty()) {
            Toast.makeText(this, "No se encontraron servicios para esta categoría", Toast.LENGTH_SHORT).show()
        }
    }
}
