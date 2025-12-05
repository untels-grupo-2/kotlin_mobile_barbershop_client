package com.diamond.appcliente.actividades;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.diamond.appcliente.R;
import com.diamond.appcliente.adapters.BarberoAdapter;
import com.diamond.appcliente.dto.barbero.BarberoDto;
import com.diamond.appcliente.viewmodel.GestionarBarberoViewModel;

import java.util.List;

public class GestionarBarberoActivity extends AppCompatActivity {

    private GestionarBarberoViewModel viewModel;
    private RecyclerView recyclerView;
    private BarberoAdapter adapter;

    // Variables que recibimos de la actividad anterior
    private String fechaReserva, turnoReserva, nombreServicio, descripcionServicio, imagenUrlServicio, nombreCliente, apellidoCliente, nombreBarberoSeleccionado, tipoHorario;
    private double precioServicio;
    private int tipoHorarioValor, turnoReservaValor; // Para almacenar los valores numéricos
    private TextView textTituloServicio, textPrecioServicio, textDescripcionServicio, textSedeServicio;

    private Button btnReservar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestionar_barbero);

        // Recibiendo los datos de la actividad anterior
        Intent intent = getIntent();
        fechaReserva = intent.getStringExtra("fechaReserva");
        turnoReserva = intent.getStringExtra("turnoReserva");
        nombreServicio = intent.getStringExtra("nombreServicio");
        descripcionServicio = intent.getStringExtra("descripcionServicio");
        precioServicio = intent.getDoubleExtra("precioServicio", 0);
        imagenUrlServicio = intent.getStringExtra("imagenServicio");
        nombreCliente = intent.getStringExtra("nombreCliente");
        apellidoCliente = intent.getStringExtra("apellidoCliente");
        tipoHorario = intent.getStringExtra("tipoHorario");  // Recibimos el tipoHorario

        // Convertir tipoHorario a número (1: MAÑANA, 2: TARDE, 3: NOCHE)
        tipoHorarioValor = obtenerTipoHorarioValor(tipoHorario);

        // Convertir turnoReserva a número con base en la tabla
        turnoReservaValor = obtenerTurnoReservaValor(turnoReserva);

        Log.d("GestionarBarberoActivity", "Tipo Horario Valor: " + tipoHorarioValor);
        Log.d("GestionarBarberoActivity", "Turno Reserva Valor: " + turnoReservaValor);

        ImageView imageView = findViewById(R.id.imagenServicio);  // El ID del ImageView en tu XML
        Glide.with(this)  // Usar Glide para cargar la imagen desde la URL
                .load(imagenUrlServicio)
                .into(imageView);

        // Inicializar las vistas
        recyclerView = findViewById(R.id.recyclerViewBarberos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        textTituloServicio = findViewById(R.id.textTituloServicio);
        textPrecioServicio = findViewById(R.id.textPrecioServicio);
        textDescripcionServicio = findViewById(R.id.textDescripcionServicio);
        textSedeServicio = findViewById(R.id.textSedeServicio);

        // Configurar UI con los datos del servicio
        textTituloServicio.setText(nombreServicio);
        textPrecioServicio.setText("S/ " + precioServicio);
        textDescripcionServicio.setText(descripcionServicio);
        textSedeServicio.setText("SEDE: VILLA MARIA");  // Esto se podría pasar también desde el Intent si fuera necesario

        // Configurar el comportamiento del botón "Reservar"
        Button btnReservar = findViewById(R.id.btnReservar);
        btnReservar.setOnClickListener(v -> mostrarPopupConfirmacion());

        // Cargar la lista de barberos
        viewModel = new GestionarBarberoViewModel();
        cargarLista();
    }

    private void cargarLista() {
        viewModel.obtenerBarberos(this, new GestionarBarberoViewModel.BarberoCallback() {
            @Override
            public void onSuccess(List<BarberoDto> barberos) {
                adapter = new BarberoAdapter(barberos, new BarberoAdapter.OnBarberoClickListener() {
                    @Override
                    public void onBarberoSeleccionado(BarberoDto barbero, String nombreBarbero) {
                        // Aquí se actualiza el nombre del barbero seleccionado
                        nombreBarberoSeleccionado = nombreBarbero;
                        Toast.makeText(GestionarBarberoActivity.this, "Barbero seleccionado: " + nombreBarbero, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onActualizar(BarberoDto barbero) {
                        // Si es necesario actualizar un barbero, agregar el código aquí
                    }

                    @Override
                    public void onEliminar(BarberoDto barbero) {
                        // Si es necesario eliminar un barbero, agregar el código aquí
                    }

                    public void onValoracion(BarberoDto barbero) {
                        // Si es necesario eliminar un barbero, agregar el código aquí
                    }
                });
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onError(String mensaje) {
                Toast.makeText(GestionarBarberoActivity.this, mensaje, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Función para mostrar el popup de confirmación con los detalles
    private void mostrarPopupConfirmacion() {
        // Crear un nuevo Dialog
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.popup_reserva_confirmada); // Crear layout para el popup
        dialog.setCancelable(false);

        // Referenciar las vistas del popup
        TextView textTitulo = dialog.findViewById(R.id.textTitulo);
        TextView textDetalleReserva = dialog.findViewById(R.id.textDetalleReserva);
        Button btnContinuar = dialog.findViewById(R.id.btnContinuar);

        // Crear el mensaje personalizado
        String mensaje = nombreCliente + " " + apellidoCliente + " se ha realizado tu reserva con el barbero: " +
                nombreBarberoSeleccionado + " para el " + fechaReserva + " a las " + turnoReserva + " en el turno de: " + tipoHorario +
                " (Valor: " + tipoHorarioValor + ", Turno valor: " + turnoReservaValor + ")";

        // Asignar el mensaje al TextView en el popup
        textTitulo.setText("¡Reserva confirmada!");
        textDetalleReserva.setText(mensaje);

        // Configurar el botón "Continuar"
        btnContinuar.setOnClickListener(v -> {
            // Cerrar el popup
            dialog.dismiss();

            // Crear un Intent para abrir ValoracionActivity
            Intent intent = new Intent(GestionarBarberoActivity.this, ValoracionActivity.class);

            // Pasar solo el dato de nombreCliente a la actividad de valoración
            intent.putExtra("nombreCliente", nombreCliente);

            // Iniciar la actividad
            startActivity(intent);
        });

        // Mostrar el popup
        dialog.show();
    }

    // Método para obtener el valor numérico basado en el tipo de horario
    private int obtenerTipoHorarioValor(String tipoHorario) {
        switch (tipoHorario) {
            case "MAÑANA":
                return 1;
            case "TARDE":
                return 2;
            case "NOCHE":
                return 3;
            default:
                return -1; // En caso de un valor inesperado
        }
    }

    // Método para obtener el valor numérico basado en el turno seleccionado
    private int obtenerTurnoReservaValor(String botonSeleccionado) {
        switch (botonSeleccionado) {
            case "9 AM - 10 AM":
                return 1;
            case "10 AM - 11 AM":
                return 2;
            case "11 AM - 12 PM":
                return 3;
            case "12 PM - 1 PM":
                return 4;
            case "1 PM - 2 PM":
                return 5;
            case "2 PM - 3 PM":
                return 6;
            case "3 PM - 4 PM":
                return 7;
            case "4 PM - 5 PM":
                return 8;
            case "5 PM - 6 PM":
                return 9;
            case "6 PM - 7 PM":
                return 10;
            case "7 PM - 8 PM":
                return 11;
            case "8 PM - 9 PM":
                return 12;
            case "9 PM - 10 PM":
                return 13;
            default:
                return -1; // Si no se encuentra el turno
        }
    }
}
