package com.diamond.appcliente.actividades

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.auth0.android.jwt.JWT
import com.diamond.appcliente.R
import com.diamond.appcliente.adapters.ServicioAdapter
import com.diamond.appcliente.dto.servicio.ServicioDto
import com.diamond.appcliente.util.PreferenciasHelper
import com.diamond.appcliente.viewmodel.GestionarServicioViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class SeccionServiciosActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private var adapter: ServicioAdapter? = null
    private var listaServicios: List<ServicioDto> = emptyList()
    private lateinit var viewModel1: GestionarServicioViewModel

    private var nombreCliente: String? = null
    private var apellidoCliente: String? = null
    private var imagenUrlCliente: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_area_servicios)

        val token = PreferenciasHelper(application).obtenerToken()
        val jwt = JWT(token!!)
        nombreCliente = jwt.getClaim("nombre").asString()
        apellidoCliente = jwt.getClaim("apellido").asString()
        imagenUrlCliente = jwt.getClaim("urlUsuario").asString()

        recyclerView = findViewById(R.id.recyclerViewServicios)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        viewModel1 = GestionarServicioViewModel()
        cargarServicios()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = Color.BLACK
        }

        findViewById<Button>(R.id.serviciosButton).setOnClickListener { updateRecyclerView(listaServicios) }
        findViewById<Button>(R.id.cortesButton).setOnClickListener { updateRecyclerView(filterServiciosByType("CORTES")) }
        findViewById<Button>(R.id.ColoracionButton).setOnClickListener { updateRecyclerView(filterServiciosByType("COLORACIÓN")) }
        findViewById<Button>(R.id.SkincareButton).setOnClickListener { updateRecyclerView(filterServiciosByType("SKINCARE")) }
        findViewById<Button>(R.id.AfeitadoButton).setOnClickListener { updateRecyclerView(filterServiciosByType("AFEITADO DE BARBA")) }
    }

    private fun cargarServicios() {
        viewModel1.obtenerServicios(this, object : GestionarServicioViewModel.ServicioCallback {
            override fun onSuccess(servicios: List<ServicioDto>?) {
                servicios ?: return
                listaServicios = servicios
                adapter = ServicioAdapter(servicios, object : ServicioAdapter.OnServicioClickListener {
                    override fun onAviso(servicio: ServicioDto, imagenUrl: String?) {
                        Log.d("IntentData", "nombreServicio: ${servicio.nombre}")
                        Log.d("IntentData", "servicio_id: ${servicio.servicio_id}")
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
            override fun onError(mensaje: String?) {
                Toast.makeText(this@SeccionServiciosActivity, mensaje, Toast.LENGTH_SHORT).show()
            }
        })

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
