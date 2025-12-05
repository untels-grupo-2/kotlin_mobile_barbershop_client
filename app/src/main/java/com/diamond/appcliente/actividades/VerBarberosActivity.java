package com.diamond.appcliente.actividades;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.diamond.appcliente.R;
import com.diamond.appcliente.adapters.BarberoDisponibleAdapter;
import com.diamond.appcliente.dto.barbero.DtoBarberoDisponible;
import com.diamond.appcliente.viewmodel.HorarioBarberoInstanciaViewModel;

public class VerBarberosActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BarberoDisponibleAdapter adapter;
    private HorarioBarberoInstanciaViewModel viewModel;

    private String fechaReserva, nombreServicio, descripcionServicio, imagenUrlServicio, nombreCliente, apellidoCliente, nombreBarberoSeleccionado, tipoHorario;
    private double precioServicio;
    private int servicio_id;

    private String fecha; // Fecha seleccionada (de ListarRangoHorarios)
    private Long tipoHorarioId; // Se asignará usando el tipoHorario transformado
    private Long horarioRangoId; // Se asignará usando el turnoReserva transformado (convertido a Long)

    private TextView textTituloServicio, textPrecioServicio, textDescripcionServicio, textSedeServicio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_barberos);

        Intent intent = getIntent();
        recyclerView = findViewById(R.id.recyclerViewBarberos);
        nombreServicio = intent.getStringExtra("nombreServicio");
        descripcionServicio = intent.getStringExtra("descripcionServicio");
        precioServicio = intent.getDoubleExtra("precioServicio", 0);
        imagenUrlServicio = intent.getStringExtra("imagenServicio");
        nombreCliente = intent.getStringExtra("nombreCliente");
        apellidoCliente = intent.getStringExtra("apellidoCliente");
        servicio_id = intent.getIntExtra("servicio_id", -1);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ImageView imageView = findViewById(R.id.imagenServicio);  // El ID del ImageView en tu XML
        Glide.with(this)  // Usar Glide para cargar la imagen desde la URL
                .load(imagenUrlServicio)
                .into(imageView);

        textTituloServicio = findViewById(R.id.textTituloServicio);
        textPrecioServicio = findViewById(R.id.textPrecioServicio);
        textDescripcionServicio = findViewById(R.id.textDescripcionServicio);
        textSedeServicio = findViewById(R.id.textSedeServicio);

        // Configurar UI con los datos del servicio
        textTituloServicio.setText(nombreServicio);
        textPrecioServicio.setText("S/ " + precioServicio);
        textDescripcionServicio.setText(descripcionServicio);
        textSedeServicio.setText("SEDE: VILLA MARIA");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.BLACK);  // Cambiar la barra de estado a negro
        }

        // Inicializamos el ViewModel
        viewModel = new ViewModelProvider(this).get(HorarioBarberoInstanciaViewModel.class);

        // Obtener los datos pasados de la actividad anterior
        fecha = getIntent().getStringExtra("fechaReserva"); // Recibe la fecha seleccionada
        String tipoHorario = getIntent().getStringExtra("tipoHorario"); // Recibe el tipoHorario como "MAÑANA", "TARDE", "NOCHE"

        // Recibe el horarioRangoId que se envió desde la actividad anterior y lo convierte a Long
        horarioRangoId = (long) getIntent().getIntExtra("horarioRangoId", -1); // Convertir el int a Long
        Log.d("VerBarberosActivity", "horarioRangoId recibido: " + horarioRangoId);  // Verificar el valor recibido

        // Transformar los datos recibidos
        tipoHorarioId = obtenerTipoHorarioValor(tipoHorario); // Transformamos el tipoHorario a tipoHorarioId

        // Agregar logs para verificar los datos recibidos y transformados
        Log.d("VerBarberosActivity", "Fecha: " + fecha);
        Log.d("VerBarberosActivity", "TipoHorarioId: " + tipoHorarioId);
        Log.d("VerBarberosActivity", "HorarioRangoId: " + horarioRangoId); // Verificar el valor del horarioRangoId
        Log.d("VerBarberosActivity", "servicioID: " + servicio_id);

        // Observamos el LiveData que contiene los barberos disponibles
        viewModel.getBarberos().observe(this, barberos -> {
            if (barberos != null && !barberos.isEmpty()) {
                adapter = new BarberoDisponibleAdapter(barberos, this);
                recyclerView.setAdapter(adapter);
            } else {
                Toast.makeText(this, "No hay barberos disponibles para esta fecha y horario", Toast.LENGTH_SHORT).show();
            }
        });

        // Llamamos al método para obtener los barberos disponibles según el tipo de horario y rango de horario
        obtenerBarberosDisponibles();

        // Configurar el botón para reservar el barbero seleccionado
        Button btnReservar = findViewById(R.id.btnReservar);
        btnReservar.setOnClickListener(v -> {
            // Obtener el barbero seleccionado
            DtoBarberoDisponible barberoSeleccionado = adapter.getBarberoSeleccionado();
            if (barberoSeleccionado != null) {
                Long barberoId = barberoSeleccionado.getBarberoId(); // Obtener el ID del barbero
                nombreBarberoSeleccionado = barberoSeleccionado.getNombre(); // Obtener el nombre del barbero

                // Agregar log para verificar los valores
                Log.d("VerBarberosActivity", "Barbero ID: " + barberoId);
                Log.d("VerBarberosActivity", "Nombre Barbero: " + nombreBarberoSeleccionado);
                Log.d("VerBarberosActivity", "Horario: " + tipoHorario);
                Log.d("VerBarberosActivity", "Servicio: " + nombreServicio);
                Log.d("VerBarberosActivity", "Fecha Reserva: " + fecha);
                Log.d("VerBarberosActivity", "servicio_id: " + servicio_id);
                Log.d("VerBarberosActivity", "horarioRangoId antes de enviar: " + horarioRangoId);

                // Abrir la actividad de reserva
                Intent intentReserva = new Intent(VerBarberosActivity.this, ReservaActivity.class);
                intentReserva.putExtra("barbero", nombreBarberoSeleccionado);  // Pasar el barbero seleccionado
                intentReserva.putExtra("barberoId", barberoId);  // Pasar el ID del barbero
                intentReserva.putExtra("horario", tipoHorario);  // Pasar el tipo de horario (mañana/tarde/noche)
                intentReserva.putExtra("servicio", nombreServicio);  // Pasar el servicio seleccionado
                intentReserva.putExtra("servicio_id", servicio_id);  // Pasar el servicio_id
                intentReserva.putExtra("fechaReserva", fecha);  // Pasar la fecha seleccionada

                // Enviar también el horarioRangoId
                intentReserva.putExtra("horarioRangoId", horarioRangoId); // Pasar el horarioRangoId a la siguiente actividad

                startActivity(intentReserva);
            } else {
                Toast.makeText(VerBarberosActivity.this, "Por favor, selecciona un barbero", Toast.LENGTH_SHORT).show();
            }
        });

        Button btnCancelar = findViewById(R.id.btnCancelar);
        btnCancelar.setOnClickListener(v -> {
            // Crear el Intent para redirigir a ClienteHomeActivity
            Intent intentCancelar = new Intent(VerBarberosActivity.this, ClienteHomeActivity.class);
            startActivity(intentCancelar);
            finish();  // Finalizar la actividad actual para evitar que el usuario regrese a ella con el botón atrás
        });

    }

    // Método para obtener los barberos disponibles según el tipoHorarioId y horarioRangoId
    private void obtenerBarberosDisponibles() {
        if (tipoHorarioId == -1 || horarioRangoId == -1 || fecha == null) {
            Log.e("VerBarberosActivity", "Los valores tipoHorarioId, horarioRangoId o fecha no son válidos.");
            return;
        }

        // Llamamos al método para obtener los barberos disponibles de acuerdo a los parámetros
        viewModel.obtenerBarberosDisponibles(fecha, tipoHorarioId, horarioRangoId);
    }

    // Método para obtener el valor numérico de tipoHorario basado en el string recibido
    private Long obtenerTipoHorarioValor(String tipoHorario) {
        switch (tipoHorario) {
            case "MAÑANA":
                return 1L; // Retornar Long
            case "TARDE":
                return 2L;
            case "NOCHE":
                return 3L;
            default:
                return -1L; // En caso de un valor inesperado
        }
    }
}
