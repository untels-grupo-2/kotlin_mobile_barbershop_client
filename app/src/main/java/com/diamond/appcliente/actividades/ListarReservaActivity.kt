package com.diamond.appcliente.actividades

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.diamond.appcliente.R
import com.diamond.appcliente.adapters.ListarReservaAdapter
import com.diamond.appcliente.viewmodel.ListarReservaViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class ListarReservaActivity : AppCompatActivity() {

    private lateinit var listarReservaAdapter: ListarReservaAdapter
    private lateinit var listarReservaViewModel: ListarReservaViewModel
    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listar_reserva)

        recyclerView = findViewById(R.id.recycler_view_reservas)
        progressBar = findViewById(R.id.progress_bar)
        listarReservaAdapter = ListarReservaAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = listarReservaAdapter
        listarReservaViewModel = ViewModelProvider(this).get(ListarReservaViewModel::class.java)
        loadReservations()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.historial
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> { startActivity(Intent(this, ClienteHomeActivity::class.java)); overridePendingTransition(0, 0); true }
                R.id.nav_servicios -> { startActivity(Intent(this, SeccionServiciosActivity::class.java)); overridePendingTransition(0, 0); true }
                R.id.nav_perfil -> { startActivity(Intent(this, UsuarioActivity::class.java)); overridePendingTransition(0, 0); true }
                else -> false
            }
        }

        window.statusBarColor = -16777216
    }

    private fun loadReservations() {
        progressBar.visibility = View.VISIBLE
        listarReservaViewModel.getReservas().observe(this) { reservas ->
            progressBar.visibility = View.GONE
            if (reservas.isNullOrEmpty()) {
                Toast.makeText(this, "No se encontraron reservas", Toast.LENGTH_SHORT).show()
            } else {
                listarReservaAdapter.submitList(reservas)
            }
        }
    }
}
