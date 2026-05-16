package com.diamond.appcliente.actividades

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.diamond.appcliente.R
import com.diamond.appcliente.adapters.ServicioAdapter
import com.diamond.appcliente.dto.servicio.ServicioDto
import com.diamond.appcliente.dto.servicio.ServicioRequest
import com.diamond.appcliente.viewmodel.GestionarServicioViewModel

class GestionarServicioActivity : AppCompatActivity() {

    private lateinit var viewModel: GestionarServicioViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnAgregarServicio: Button
    private var adapter: ServicioAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestionar_servicio)

        recyclerView = findViewById(R.id.recyclerViewServicios)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        btnAgregarServicio = findViewById(R.id.btnAgregarServicio)

        viewModel = ViewModelProvider(this).get(GestionarServicioViewModel::class.java)

        viewModel.obtenerServicios(this, object : GestionarServicioViewModel.ServicioCallback {
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
        val request = ServicioRequest(nombre, precio, descripcion, tipoServicioId)
        viewModel.crearServicio(this, request, object : GestionarServicioViewModel.ServicioOperacionCallback {
            override fun onSuccess(mensaje: String?) {
                Toast.makeText(this@GestionarServicioActivity, mensaje, Toast.LENGTH_SHORT).show()
                viewModel.obtenerServicios(this@GestionarServicioActivity, object : GestionarServicioViewModel.ServicioCallback {
                    override fun onSuccess(servicios: List<ServicioDto>?) { adapter?.notifyDataSetChanged() }
                    override fun onError(mensaje: String?) {
                        Toast.makeText(this@GestionarServicioActivity, "Error: $mensaje", Toast.LENGTH_SHORT).show()
                    }
                })
            }
            override fun onError(mensaje: String?) {
                Toast.makeText(this@GestionarServicioActivity, "Error: $mensaje", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun eliminarServicio(id: Int) {
        viewModel.eliminarServicio(this, id, object : GestionarServicioViewModel.ServicioOperacionCallback {
            override fun onSuccess(mensaje: String?) {
                Toast.makeText(this@GestionarServicioActivity, mensaje, Toast.LENGTH_SHORT).show()
                viewModel.obtenerServicios(this@GestionarServicioActivity, object : GestionarServicioViewModel.ServicioCallback {
                    override fun onSuccess(servicios: List<ServicioDto>?) { adapter?.notifyDataSetChanged() }
                    override fun onError(mensaje: String?) {
                        Toast.makeText(this@GestionarServicioActivity, "Error: $mensaje", Toast.LENGTH_SHORT).show()
                    }
                })
            }
            override fun onError(mensaje: String?) {
                Toast.makeText(this@GestionarServicioActivity, "Error: $mensaje", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
