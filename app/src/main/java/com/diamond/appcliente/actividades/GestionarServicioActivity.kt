package com.diamond.appcliente.actividades

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.diamond.appcliente.R
import com.diamond.appcliente.adapters.ServicioAdapter
import com.diamond.appcliente.dto.servicio.ServicioDto
import com.diamond.appcliente.dto.servicio.ServicioRequest
import com.diamond.appcliente.viewmodel.GestionarServicioViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GestionarServicioActivity : AppCompatActivity() {

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

        viewModel.obtenerServicios(object : GestionarServicioViewModel.ServicioCallback {
            override fun onSuccess(servicios: List<ServicioDto>?) {
                servicios ?: return
                if (adapter == null) {
                    adapter = ServicioAdapter(servicios, object : ServicioAdapter.OnServicioClickListener {
                        override fun onAviso(servicio: ServicioDto, imagenUrl: String?) {}
                    })
                    recyclerView.adapter = adapter
                } else {
                    adapter!!.notifyDataSetChanged()
                }
            }
            override fun onError(mensaje: String?) {
                Toast.makeText(this@GestionarServicioActivity, "Error: $mensaje", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun crearNuevoServicio(nombre: String, precio: Double, descripcion: String, tipoServicioId: Int) {
        viewModel.crearServicio(ServicioRequest(nombre, precio, descripcion, tipoServicioId), object : GestionarServicioViewModel.ServicioOperacionCallback {
            override fun onSuccess(mensaje: String?) {
                Toast.makeText(this@GestionarServicioActivity, mensaje, Toast.LENGTH_SHORT).show()
                viewModel.obtenerServicios(object : GestionarServicioViewModel.ServicioCallback {
                    override fun onSuccess(servicios: List<ServicioDto>?) { adapter?.notifyDataSetChanged() }
                    override fun onError(mensaje: String?) { Toast.makeText(this@GestionarServicioActivity, "Error: $mensaje", Toast.LENGTH_SHORT).show() }
                })
            }
            override fun onError(mensaje: String?) {
                Toast.makeText(this@GestionarServicioActivity, "Error: $mensaje", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun eliminarServicio(id: Int) {
        viewModel.eliminarServicio(id, object : GestionarServicioViewModel.ServicioOperacionCallback {
            override fun onSuccess(mensaje: String?) {
                Toast.makeText(this@GestionarServicioActivity, mensaje, Toast.LENGTH_SHORT).show()
                viewModel.obtenerServicios(object : GestionarServicioViewModel.ServicioCallback {
                    override fun onSuccess(servicios: List<ServicioDto>?) { adapter?.notifyDataSetChanged() }
                    override fun onError(mensaje: String?) { Toast.makeText(this@GestionarServicioActivity, "Error: $mensaje", Toast.LENGTH_SHORT).show() }
                })
            }
            override fun onError(mensaje: String?) {
                Toast.makeText(this@GestionarServicioActivity, "Error: $mensaje", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
