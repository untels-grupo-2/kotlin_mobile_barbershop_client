package com.diamond.appcliente.actividades;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.diamond.appcliente.R;
import com.diamond.appcliente.dto.reserva.DtoReserva;
import com.diamond.appcliente.dto.reserva.ReservaResponse;
import com.diamond.appcliente.viewmodel.GestionarReservaViewModel;
import com.diamond.appcliente.viewmodel.ListarReservaViewModel;
import com.diamond.appcliente.util.PreferenciasHelper;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ReservaActivity extends AppCompatActivity {

    private TextView textViewBarbero, textViewHorario, textViewServicio, textViewFecha;
    private EditText editTextAdicionales;
    private AlertDialog dialog;

    private Button btnEnviarReserva, btnEnviarReservaRecompensa;

    private Long tipoHorarioId, horarioRangoId; // Para guardar los valores transformados
    private Long servicio_id; // Para recibir el servicio_id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserva);

        // Recuperar el token JWT desde las preferencias
        PreferenciasHelper prefs = new PreferenciasHelper(getApplication());
        String token = prefs.obtenerToken();  // Asegúrate de que tienes un método para obtener el token

        // Log para verificar el token recibido
        Log.d("ReservaActivity", "Token recibido: " + token);

        // Inicializando las vistas
        textViewBarbero = findViewById(R.id.textViewBarbero);
        textViewHorario = findViewById(R.id.textViewHorario);
        textViewServicio = findViewById(R.id.textViewServicio);
        textViewFecha = findViewById(R.id.textViewFecha);
        editTextAdicionales = findViewById(R.id.editTextAdicionales);
        btnEnviarReserva = findViewById(R.id.btnEnviarReserva);
        btnEnviarReservaRecompensa = findViewById(R.id.btnEnviarReservaRecompensa);

        // Obtener los datos pasados de la actividad anterior
        Intent intent = getIntent();
        String barbero = intent.getStringExtra("barbero");
        String horario = intent.getStringExtra("horario");
        String servicio = intent.getStringExtra("servicio");
        Long barberoId = intent.getLongExtra("barberoId", -1);
        String fecha = intent.getStringExtra("fechaReserva");

        // Obtener el servicio_id que fue enviado desde VerBarberosActivity
        servicio_id = (long) intent.getIntExtra("servicio_id", -1);

        // Obtener el horarioRangoId que fue enviado desde ListarRangoHorarios
        // Asegurarnos de recibir el valor como Long (usamos getLongExtra en vez de getIntExtra)
        horarioRangoId = intent.getLongExtra("horarioRangoId", -1L);  // Aquí usamos getLongExtra para recibirlo como Long

        // Verificar que el servicio_id y horarioRangoId se reciben correctamente
        Log.d("ReservaActivity", "Servicio ID recibido: " + servicio_id);
        Log.d("ReservaActivity", "HorarioRangoId recibido: " + horarioRangoId);  // Verificar que el horarioRangoId se recibe correctamente

        // Log para verificar los datos recibidos
        Log.d("ReservaActivity", "Barbero ID: " + barberoId);
        Log.d("ReservaActivity", "Nombre Barbero: " + servicio);
        Log.d("ReservaActivity", "Horario: " + horario);
        Log.d("ReservaActivity", "Servicio: " + servicio);
        Log.d("ReservaActivity", "Fecha Reserva: " + fecha);
        Log.d("ReservaActivity", "Servicio ID: " + servicio_id); // Agregar log para verificar servicio_id

        // Mostrar los datos en los TextViews
        textViewBarbero.setText("Barbero: " + barbero);
        textViewHorario.setText("Turno: " + horario); // Mostrar el valor de horario en el TextView correspondiente
        textViewServicio.setText("Servicio: " + servicio);

        // Establecer una fecha predeterminada (fecha actual) en el TextView de fecha
        if (fecha != null && !fecha.isEmpty()) {
            textViewFecha.setText(fecha);
        } else {
            String fechaPredeterminada = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            textViewFecha.setText("Fecha: " + fechaPredeterminada);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.BLACK);  // Cambiar la barra de estado a negro
        }

        // Verificar las reservas al iniciar la actividad
        verificarReservas();

        // Configurar el botón para enviar la reserva
        btnEnviarReserva.setOnClickListener(v -> enviarReserva(barberoId, horario, servicio_id));  // Cambié 'servicio' por 'servicio_id'

        // Configurar el botón para enviar la reserva con recompensa
        btnEnviarReservaRecompensa.setOnClickListener(v -> enviarReservaRecompensa(barberoId, horario, servicio_id));

        Button btnCancelar = findViewById(R.id.btnCancelar);
        btnCancelar.setOnClickListener(v -> {
            // Crear el Intent para redirigir a ClienteHomeActivity
            Intent intentCancelar = new Intent(ReservaActivity.this, ClienteHomeActivity.class);
            startActivity(intentCancelar);
            finish();  // Finalizar la actividad actual para evitar que el usuario regrese a ella con el botón atrás
        });
    }

    // Método para verificar las reservas
    private void verificarReservas() {
        ListarReservaViewModel listarReservaViewModel = new ListarReservaViewModel(getApplication());
        listarReservaViewModel.getReservas().observe(this, reservas -> {
            if (reservas != null) {
                int reservasConRecompensa = 0;

                // Contamos las reservas con estRecompensa == 0
                for (ReservaResponse reserva : reservas) {
                    if (reserva.getEstRecompensa() == 0) { // Filtrar por estRecompensa == 0
                        reservasConRecompensa++;
                    }
                }

                // Limitar las reservas realizadas a un máximo de 7
                if (reservasConRecompensa >= 7) {
                    // Activar el botón de recompensa y ponerlo en naranja si tiene 7 o más reservas
                    activarBotonReservaRecompensa(true);
                } else {
                    // Desactivar el botón de recompensa y ponerlo en gris si tiene menos de 7 reservas
                    activarBotonReservaRecompensa(false);
                }
            }
        });
    }

    // Método para activar/desactivar el botón de reserva con recompensa
    private void activarBotonReservaRecompensa(boolean activar) {
        if (activar) {
            // Activar el botón y ponerlo en naranja
            btnEnviarReservaRecompensa.setEnabled(true);
            btnEnviarReservaRecompensa.setBackgroundColor(getResources().getColor(R.color.orange));  // Establecer color naranja
        } else {
            // Desactivar el botón y ponerlo en gris
            btnEnviarReservaRecompensa.setEnabled(false);
            btnEnviarReservaRecompensa.setBackgroundColor(getResources().getColor(R.color.gray));  // Establecer color gris
        }
    }

    private void enviarReserva(Long barberoId, String horario, Long servicio_id) {
        String fechaReserva = textViewFecha.getText().toString();
        String adicionales = editTextAdicionales.getText().toString();

        DtoReserva request = new DtoReserva(barberoId, horarioRangoId, fechaReserva, servicio_id, adicionales);

        Log.d("Reserva", "Datos de la reserva: " + new Gson().toJson(request));

        GestionarReservaViewModel viewModel = new GestionarReservaViewModel();
        viewModel.enviarReserva(this, request, new GestionarReservaViewModel.ReservaCallback() {
            @Override
            public void onSuccess(String message) {
                // Asegurarse de que no haya diálogos previos abiertos
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }

                // Inflar layout personalizado para el AlertDialog
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_reserva_exitosa, null);

                // Crear un nuevo AlertDialog con el layout inflado
                dialog = new AlertDialog.Builder(ReservaActivity.this)
                        .setView(dialogView)
                        .setCancelable(true)  // Permitir que se cierre tocando fuera
                        .create();

                // Configurar botón OK
                Button btnOk = dialogView.findViewById(R.id.btnOk);
                btnOk.setOnClickListener(v -> {
                    dialog.dismiss(); // Cerrar el diálogo
                    Intent intent = new Intent(ReservaActivity.this, ValoracionActivity.class);
                    startActivity(intent); // Iniciar la actividad de valoración
                    finish();
                });

                Button btnNo = dialogView.findViewById(R.id.btnNO);
                btnNo.setOnClickListener(v -> {
                    dialog.dismiss(); // Cerrar el diálogo
                    Intent intent = new Intent(ReservaActivity.this, ClienteHomeActivity.class);
                    startActivity(intent); // Redirigir al ClienteHomeActivity
                    finish(); // Finalizar la actividad actual para evitar que el usuario regrese a ella con el botón atrás
                });

                // Si tocan fuera del diálogo, se redirige a ClienteHomeActivity
                dialog.setOnCancelListener(d -> {
                    Intent intent = new Intent(ReservaActivity.this, ClienteHomeActivity.class);
                    startActivity(intent); // Regresar a la pantalla principal
                    finish();
                });

                dialog.show(); // Mostrar el diálogo
            }

            @Override
            public void onError(String message) {
                Toast.makeText(ReservaActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void enviarReservaRecompensa(Long barberoId, String horario, Long servicio_id) {
        String fechaReserva = textViewFecha.getText().toString();
        String adicionales = editTextAdicionales.getText().toString();

        DtoReserva request = new DtoReserva(barberoId, horarioRangoId, fechaReserva, servicio_id, adicionales);

        Log.d("Reserva", "Datos de la reserva con recompensa: " + new Gson().toJson(request));

        GestionarReservaViewModel viewModel = new GestionarReservaViewModel();
        viewModel.crearReservaRecompensa(this, request, new GestionarReservaViewModel.ReservaCallback() {
            @Override
            public void onSuccess(String message) {
                // Asegurarse de que no haya diálogos previos abiertos
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }

                // Inflar layout personalizado para el AlertDialog
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_reserva_exitosa, null);

                // Crear un nuevo AlertDialog con el layout inflado
                dialog = new AlertDialog.Builder(ReservaActivity.this)
                        .setView(dialogView)
                        .setCancelable(true) // Permitir que se cierre tocando fuera
                        .create();

                // Configurar botón OK
                Button btnOk = dialogView.findViewById(R.id.btnOk);
                btnOk.setOnClickListener(v -> {
                    dialog.dismiss(); // Cerrar el diálogo
                    Intent intent = new Intent(ReservaActivity.this, ValoracionActivity.class);
                    startActivity(intent); // Iniciar la actividad de valoración
                    finish();
                });

                Button btnNo = dialogView.findViewById(R.id.btnNO);
                btnNo.setOnClickListener(v -> {
                    dialog.dismiss(); // Cerrar el diálogo
                    Intent intent = new Intent(ReservaActivity.this, ClienteHomeActivity.class);
                    startActivity(intent); // Redirigir al ClienteHomeActivity
                    finish(); // Finalizar la actividad actual para evitar que el usuario regrese a ella con el botón atrás
                });

                // Si tocan fuera del diálogo, se redirige a ClienteHomeActivity
                dialog.setOnCancelListener(d -> {
                    Intent intent = new Intent(ReservaActivity.this, ClienteHomeActivity.class);
                    startActivity(intent); // Regresar a la pantalla principal
                    finish();
                });

                dialog.show(); // Mostrar el diálogo
            }

            @Override
            public void onError(String message) {
                Toast.makeText(ReservaActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
