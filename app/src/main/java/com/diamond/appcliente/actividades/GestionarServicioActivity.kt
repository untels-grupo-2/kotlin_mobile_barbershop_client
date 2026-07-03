package com.diamond.appcliente.actividades

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.diamond.appcliente.R
import com.diamond.appcliente.adapters.ServicioAdapter
import com.shared.models.ui.state.UiState
import com.diamond.appcliente.viewmodel.GestionarServicioViewModel
import com.diamond.barbershop.shared.dto.servicio.ServicioDto
import com.diamond.barbershop.shared.dto.servicio.ServicioRequest
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GestionarServicioActivity : AuthActivity() {

    private val viewModel: GestionarServicioViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnAgregarServicio: Button
    private var adapter: ServicioAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestionar_servicio)

        recyclerView = findViewById(R.id.recyclerViewServicios)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        btnAgregarServicio = findViewById(R.id.btnAgregarServicio)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.servicios.collect { state ->
                        when (state) {
                            is UiState.Success -> {
                                val servicios = state.data
                                if (adapter == null) {
                                    adapter = ServicioAdapter(servicios, object : ServicioAdapter.OnServicioClickListener {
                                        override fun onAviso(servicio: ServicioDto, imagenUrl: String?) {}
                                    })
                                    recyclerView.adapter = adapter
                                } else {
                                    adapter!!.notifyDataSetChanged()
                                }
                            }
                            is UiState.Error -> Toast.makeText(
                                this@GestionarServicioActivity, state.message, Toast.LENGTH_SHORT
                            ).show()
                            else -> {}
                        }
                    }
                }
                launch {
                    viewModel.mensaje.collect { msg ->
                        Toast.makeText(this@GestionarServicioActivity, msg, Toast.LENGTH_SHORT).show()
                    }
                }
                launch {
                    viewModel.crudError.collect { msg ->
                        Toast.makeText(this@GestionarServicioActivity, "Error: $msg", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        viewModel.cargarServicios()
    }

    private fun crearNuevoServicio(nombre: String, precio: Double, descripcion: String, tipoServicioId: Int) {
        viewModel.crearServicio(ServicioRequest(nombre, precio, descripcion, tipoServicioId))
    }

    private fun eliminarServicio(id: Int) {
        viewModel.eliminarServicio(id)
    }
}
