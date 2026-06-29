package com.diamond.appcliente.actividades

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.AnimationUtils
import android.graphics.PorterDuff
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.diamond.appcliente.R
import com.diamond.appcliente.adapters.ServicioAdapter
import com.diamond.barbershop.shared.dto.servicio.ServicioDto
import com.diamond.appcliente.ui.state.UiState
import com.diamond.appcliente.viewmodel.GestionarServicioViewModel
import com.diamond.appcliente.viewmodel.ListarReservaViewModel
import com.diamond.appcliente.viewmodel.UsuarioViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ClienteHomeActivity : AuthActivity() {

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
    private lateinit var progressBarHome: ProgressBar

    private var nombreCliente: String? = null
    private var apellidoCliente: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cliente_home)

        clienteNombre = findViewById(R.id.clienteNombre)
        clienteFoto = findViewById(R.id.clienteFoto)
        metaProgresoTexto = findViewById(R.id.metaProgresoTexto)
        metaProgresoImagen = findViewById(R.id.metaProgresoImagen)
        progressBarHome = findViewById(R.id.progressBarHome)

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
                    usuarioViewModel.usuario.collect { state ->
                        when (state) {
                            is UiState.Success -> {
                                val usuario = state.data
                                nombreCliente = usuario.nombre
                                apellidoCliente = usuario.apellido
                                clienteNombre.text = "${usuario.nombre} ${usuario.apellido}"
                                clienteFoto.setImageResource(R.drawable.perfil_default)
                                if (!usuario.urlUsuario.isNullOrEmpty()) {
                                    Glide.with(this@ClienteHomeActivity)
                                        .load(usuario.urlUsuario)
                                        .skipMemoryCache(true)
                                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                                        .placeholder(R.drawable.perfil_default)
                                        .error(R.drawable.perfil_default)
                                        .into(clienteFoto)
                                }
                            }
                            else -> {}
                        }
                    }
                }
                launch {
                    viewModel1.servicios.collect { state ->
                        when (state) {
                            is UiState.Loading -> progressBarHome.visibility = View.VISIBLE
                            is UiState.Success -> {
                                progressBarHome.visibility = View.GONE
                                if (state.data.isNotEmpty()) {
                                    listaServicios = state.data
                                    adapter = ServicioAdapter(state.data, object : ServicioAdapter.OnServicioClickListener {
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
                            is UiState.Error -> {
                                progressBarHome.visibility = View.GONE
                                Toast.makeText(this@ClienteHomeActivity, state.message, Toast.LENGTH_SHORT).show()
                            }
                            else -> {}
                        }
                    }
                }
                launch {
                    listarReservaViewModel.reservas.collect { state ->
                        if (state is UiState.Success) {
                            var count = state.data.count { it.estRecompensa == 0 }
                            if (count > 7) count = 7
                            updateMetaProgress(count)
                            val prefs = getSharedPreferences("diamond_prefs", MODE_PRIVATE)
                            if (!prefs.getBoolean("welcome_shown", false)) {
                                prefs.edit().putBoolean("welcome_shown", true).apply()
                                mostrarBienvenidaRacha(count)
                            }
                        }
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

    private fun mostrarBienvenidaRacha(racha: Int) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_bienvenida_racha)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog.setCancelable(true)

        val ivScissors = dialog.findViewById<ImageView>(R.id.ivScissors)
        val tvNumero = dialog.findViewById<TextView>(R.id.tvRachaNumero)
        val tvLabel = dialog.findViewById<TextView>(R.id.tvRachaLabel)

        val badgeRes = when (racha) {
            0 -> R.drawable.ic_racha_1
            1 -> R.drawable.ic_racha_1
            2 -> R.drawable.ic_racha_2
            3 -> R.drawable.ic_racha_3
            4 -> R.drawable.ic_racha_4
            5 -> R.drawable.ic_racha_5
            6 -> R.drawable.ic_racha_6
            7 -> R.drawable.ic_racha_7
            8 -> R.drawable.ic_racha_8
            9 -> R.drawable.ic_racha_9
            else -> R.drawable.ic_racha_10
        }
        ivScissors.setImageResource(badgeRes)

        when {
            racha == 0 -> {
                tvNumero.text = "✂"
                tvLabel.text = "¡EMPIEZA TU RACHA!"
            }
            racha >= 10 -> {
                tvNumero.text = "★"
                tvLabel.text = "¡META ALCANZADA!"
            }
            else -> {
                tvNumero.text = racha.toString()
                tvLabel.text = "CORTES EN TU RACHA"
            }
        }

        val scissorsAnim = AnimationUtils.loadAnimation(this, R.anim.scissors_pop_in)
        val numberAnim = AnimationUtils.loadAnimation(this, R.anim.fade_in_up)
        ivScissors.startAnimation(scissorsAnim)
        tvNumero.startAnimation(numberAnim)

        dialog.show()

        Handler(Looper.getMainLooper()).postDelayed({
            if (dialog.isShowing) dialog.dismiss()
        }, 3000)
    }

    private fun filterServiciosByType(tipo: String): List<ServicioDto> =
        listaServicios.filter { it.nombre_tipoServicio.equals(tipo, ignoreCase = true) }

    private fun updateRecyclerView(serviciosFiltrados: List<ServicioDto>) {
        adapter?.updateServicios(serviciosFiltrados)
        if (serviciosFiltrados.isEmpty()) {
            Toast.makeText(this, "No se encontraron servicios para esta categoría", Toast.LENGTH_SHORT).show()
        }
    }

    private val rachaDotIds = intArrayOf(
        R.id.rachaDot1, R.id.rachaDot2, R.id.rachaDot3, R.id.rachaDot4,
        R.id.rachaDot5, R.id.rachaDot6, R.id.rachaDot7
    )

    private fun updateMetaProgress(count: Int) {
        metaProgresoTexto.text = "$count/7 Cortes"

        rachaDotIds.forEachIndexed { index, id ->
            val dot = findViewById<ImageView>(id)
            dot.setColorFilter(
                if (index < count) Color.parseColor("#FF9800") else Color.parseColor("#333333"),
                PorterDuff.Mode.SRC_IN
            )
        }

        val badgeRes = when (count) {
            0 -> R.drawable.ic_racha_1
            1 -> R.drawable.ic_racha_1
            2 -> R.drawable.ic_racha_2
            3 -> R.drawable.ic_racha_3
            4 -> R.drawable.ic_racha_4
            5 -> R.drawable.ic_racha_5
            6 -> R.drawable.ic_racha_6
            else -> R.drawable.ic_racha_7
        }
        metaProgresoImagen.setImageResource(badgeRes)
    }
}
