package com.diamond.appcliente.actividades;

import android.app.DatePickerDialog;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.diamond.appcliente.R;
import com.diamond.appcliente.adapters.HorarioRangoAdapter;
import com.diamond.appcliente.dto.horariorango.HorarioRangoDto;
import com.diamond.appcliente.dto.servicio.ServicioRequest;
import com.diamond.appcliente.viewmodel.GestionarHorarioRangoViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class ListarRangoHorarios extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HorarioRangoAdapter horarioRangoAdapter;
    private List<HorarioRangoDto> listaHorarios;
    private TextView textFechaSeleccionada;

    private ImageView btnSeleccionarFecha;
    private Button btnConfirmarFechayHora, btnCancelarReserva;

    private GestionarHorarioRangoViewModel gestionarHorarioRangoViewModel;

    private String botonSeleccionado = null; // Guardará el botón seleccionado (hora del turno)
    private int horarioRangoId = -1; // Variable para almacenar el horarioRangoId

    // Este es el objeto ServicioRequest que debes recibir correctamente
    private ServicioRequest servicio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_rango_horarios);
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        textFechaSeleccionada = findViewById(R.id.textFechaSeleccionada);
        btnSeleccionarFecha = findViewById(R.id.btnSeleccionarFecha);
        btnConfirmarFechayHora = findViewById(R.id.btnConfirmarFechayHora);
        btnCancelarReserva = findViewById(R.id.btnCancelarReserva);

        // Obtener datos de la actividad anterior
        String nombreCliente = getIntent().getStringExtra("nombreCliente");
        String apellidoCliente = getIntent().getStringExtra("apellidoCliente");
        String nombreServicio = getIntent().getStringExtra("nombreServicio");
        String descripcionServicio = getIntent().getStringExtra("descripcionServicio");
        String imagenUrlServicio = getIntent().getStringExtra("imagenServicio");
        double precioServicio = getIntent().getDoubleExtra("precioServicio", 0.0);
        int servicio_id = getIntent().getIntExtra("servicio_id", -1);

        // Verifica si el servicio está correctamente recibido
        Log.d("ListarRangoHorarios", "Servicio ID recibido: " + servicio_id);

        // Verifica que el objeto servicio no sea nulo
        servicio = new ServicioRequest(nombreServicio, precioServicio, imagenUrlServicio, servicio_id);

        // Verifica si el objeto servicio está bien asignado antes de usarlo
        if (servicio != null) {
            Log.d("ListarRangoHorarios", "Servicio: " + servicio.getNombre());
            Log.d("ListarRangoHorarios", "Descripción del servicio: " + servicio.getDescripcion());
            Log.d("ListarRangoHorarios", "Precio del servicio: " + servicio.getPrecio());
        } else {
            Log.e("ListarRangoHorarios", "El objeto servicio está nulo");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.BLACK);  // Cambiar la barra de estado a negro
        }

        // Configurar la UI con los datos del servicio recibido
        btnSeleccionarFecha.setOnClickListener(v -> mostrarCalendario());
        btnConfirmarFechayHora.setOnClickListener(v -> confirmarReserva(nombreCliente, apellidoCliente, nombreServicio, descripcionServicio, imagenUrlServicio, precioServicio, servicio_id));
        btnCancelarReserva.setOnClickListener(v -> finish());

        // Inicializar el RecyclerView
        recyclerView = findViewById(R.id.recyclerViewHorarios);

        // Crear GridLayoutManager para 1 columna
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1); // 1 columna, para filas
        recyclerView.setLayoutManager(layoutManager);

        listaHorarios = new ArrayList<>();
        horarioRangoAdapter = new HorarioRangoAdapter(this, listaHorarios, new HorarioRangoAdapter.OnHorarioSelectedListener() {
            @Override
            public void onHorarioSelected(String horario, int horarioRangoId) {
                botonSeleccionado = horario; // Guardar el turno seleccionado
                ListarRangoHorarios.this.horarioRangoId = horarioRangoId; // Guardar el id del horario seleccionado
                Log.d("ListarRangoHorarios", "horarioRangoId seleccionado: " + horarioRangoId);  // Log para verificar el id
            }
        });

        // Establecer el adaptador para el RecyclerView
        recyclerView.setAdapter(horarioRangoAdapter);

        gestionarHorarioRangoViewModel = new GestionarHorarioRangoViewModel();

        obtenerHorarios();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(ListarRangoHorarios.this, ClienteHomeActivity.class));
                overridePendingTransition(0, 0); // Evitar animación de transición
                return true;
            } else if (id == R.id.nav_servicios) {
                startActivity(new Intent(ListarRangoHorarios.this, SeccionServiciosActivity.class));
                overridePendingTransition(0, 0); // Evitar animación de transición
                return true;
            } else if (id == R.id.historial) {
                startActivity(new Intent(ListarRangoHorarios.this, ListarReservaActivity.class));
                overridePendingTransition(0, 0); // Evitar animación de transición
                return true;
            } else if (id == R.id.nav_perfil) {
                startActivity(new Intent(ListarRangoHorarios.this, UsuarioActivity.class));
                overridePendingTransition(0, 0); // Evitar animación de transición
                return true;
            }
            return false;
        });
    }

    private void mostrarCalendario() {
        final Calendar calendario = Calendar.getInstance();
        int anio = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);

        // Cambiar el idioma del calendario a español
        Locale locale = new Locale("es", "ES");  // Español (España)
        Locale.setDefault(locale);  // Establecer el idioma global de la aplicación

        // Establecer el primer día de la semana (lunes)
        Calendar startOfWeekCalendar = Calendar.getInstance();
        startOfWeekCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        startOfWeekCalendar.set(Calendar.HOUR_OF_DAY, 0);
        startOfWeekCalendar.set(Calendar.MINUTE, 0);
        startOfWeekCalendar.set(Calendar.SECOND, 0);
        startOfWeekCalendar.set(Calendar.MILLISECOND, 0);
        long startOfWeek = startOfWeekCalendar.getTimeInMillis();

        // Obtener el día de la semana actual
        int dayOfWeek = calendario.get(Calendar.DAY_OF_WEEK);
        int daysToAdd;

        // Ajustar el valor de daysToAdd dependiendo del día actual
        if (dayOfWeek == Calendar.MONDAY) {
            daysToAdd = 6;  // Si es lunes, añadimos 6 días
        } else if (dayOfWeek == Calendar.TUESDAY) {
            daysToAdd = 5;  // Si es martes, añadimos 5 días
        } else if (dayOfWeek == Calendar.WEDNESDAY) {
            daysToAdd = 4;  // Si es miércoles, añadimos 4 días
        } else if (dayOfWeek == Calendar.THURSDAY) {
            daysToAdd = 3;  // Si es jueves, añadimos 3 días
        } else if (dayOfWeek == Calendar.FRIDAY) {
            daysToAdd = 2;  // Si es viernes, añadimos 2 días
        } else if (dayOfWeek == Calendar.SATURDAY) {
            daysToAdd = 1;  // Si es sábado, añadimos 1 día
        } else { // Si es domingo
            daysToAdd = 0;  // Si es domingo, no agregamos días
        }

        // Establecer el último día de la semana (domingo)
        Calendar endOfWeekCalendar = Calendar.getInstance();
        endOfWeekCalendar.add(Calendar.DAY_OF_WEEK, daysToAdd);  // Moverse al último día de la semana
        long endOfWeek = endOfWeekCalendar.getTimeInMillis();

        // Aplicando el tema personalizado al DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                R.style.CustomDatePickerDialogTheme, // Estilo personalizado
                (view, year, month, dayOfMonth) -> {
                    Calendar fechaSeleccionada = Calendar.getInstance();
                    fechaSeleccionada.set(year, month, dayOfMonth);
                    SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd", locale);  // Usar el locale para español
                    textFechaSeleccionada.setText(formatoFecha.format(fechaSeleccionada.getTime()));
                },
                anio, mes, dia
        );

        // Establecer el rango de fechas (solo dentro de la semana actual, pero no fechas pasadas)
        long today = System.currentTimeMillis();
        datePickerDialog.getDatePicker().setMinDate(today); // Establecer el día de hoy como fecha mínima
        datePickerDialog.getDatePicker().setMaxDate(endOfWeek); // Final de la semana actual
        datePickerDialog.show();
    }

    private void obtenerHorarios() {
        gestionarHorarioRangoViewModel.obtenerHorariosRangos(this, 1, new GestionarHorarioRangoViewModel.HorarioRangoCallback() {
            @Override
            public void onSuccess(List<HorarioRangoDto> horarioRangos) {
                List<HorarioRangoDto> todosHorarios = new ArrayList<>();

                // Añadir encabezados de turnos
                todosHorarios.add(new HorarioRangoDto("🌅 Turno mañana", "Encabezado"));
                for (HorarioRangoDto rango : horarioRangos) {
                    if (rango.getTipoHorario().equals("MAÑANA")) {
                        todosHorarios.add(rango);
                    }
                }

                todosHorarios.add(new HorarioRangoDto("☀️ Turno tarde", "Encabezado"));
                for (HorarioRangoDto rango : horarioRangos) {
                    if (rango.getTipoHorario().equals("TARDE")) {
                        todosHorarios.add(rango);
                    }
                }

                todosHorarios.add(new HorarioRangoDto("🌙 Turno noche", "Encabezado"));
                for (HorarioRangoDto rango : horarioRangos) {
                    if (rango.getTipoHorario().equals("NOCHE")) {
                        todosHorarios.add(rango);
                    }
                }

                // Ordenar los horarios antes de mostrarlos
                ordenarHorarios(todosHorarios);

                // Establecer los horarios ordenados en el RecyclerView
                setUpRecyclerView(todosHorarios);
            }

            @Override
            public void onError(String mensaje) {
                Toast.makeText(ListarRangoHorarios.this, mensaje, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUpRecyclerView(List<HorarioRangoDto> horarioRangos) {
        horarioRangoAdapter = new HorarioRangoAdapter(this, horarioRangos, new HorarioRangoAdapter.OnHorarioSelectedListener() {
            @Override
            public void onHorarioSelected(String horario, int horarioRangoId) {
                botonSeleccionado = horario; // Guardar el turno seleccionado
                ListarRangoHorarios.this.horarioRangoId = horarioRangoId; // Guardar el id del horario seleccionado
                Log.d("ListarRangoHorarios", "horarioRangoId seleccionado: " + horarioRangoId);  // Log para verificar el id
            }
        });
        recyclerView.setAdapter(horarioRangoAdapter);
    }

    private String obtenerTipoHorario(String botonSeleccionado) {
        // Extraer la hora del botón seleccionado
        String hora = botonSeleccionado.split(" ")[0];  // Obtén la hora antes de AM/PM
        String periodo = botonSeleccionado.split(" ")[1];  // Obtén AM o PM

        // Convertir la hora a un número entero para la comparación
        int horaInt = Integer.parseInt(hora);

        // Ajustar la hora para que sea válida en el formato de 24 horas
        if (periodo.equals("PM") && horaInt != 12) {
            horaInt += 12;  // Convertir la hora PM a formato de 24 horas (excepto para las 12 PM)
        } else if (periodo.equals("AM") && horaInt == 12) {
            horaInt = 0;  // Convertir la medianoche (12 AM) a 0 horas
        }

        // Lógica para determinar el tipo de horario
        if (horaInt >= 6 && horaInt < 13) {
            return "MAÑANA";
        } else if (horaInt >= 13 && horaInt < 18) {
            return "TARDE";
        } else {
            return "NOCHE";
        }
    }

    private void ordenarHorarios(List<HorarioRangoDto> todosHorarios) {
        // Ordenar la lista de horarios
        Collections.sort(todosHorarios, new Comparator<HorarioRangoDto>() {
            @Override
            public int compare(HorarioRangoDto o1, HorarioRangoDto o2) {
                // Si ambos son encabezados, no los comparamos
                if (o1.getTipoHorario().equals("Encabezado") && o2.getTipoHorario().equals("Encabezado")) {
                    return 0;
                }

                // Extraer las horas de los rangos de horario
                String horaInicio1 = o1.getRango().split(" - ")[0];  // Ejemplo: "9 AM"
                String horaInicio2 = o2.getRango().split(" - ")[0];  // Ejemplo: "10 AM"

                // Convertir las horas a un formato comparable
                SimpleDateFormat format = new SimpleDateFormat("h a");  // Formato de 12 horas con AM/PM
                try {
                    return format.parse(horaInicio1).compareTo(format.parse(horaInicio2));
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });
    }

    private void confirmarReserva(String nombreCliente, String apellidoCliente, String nombreServicio, String descripcionServicio, String imagenUrlServicio, double precioServicio, int servicio_id) {
        if (textFechaSeleccionada.getText().toString().equals("Selecciona una fecha")) {
            Toast.makeText(this, "Por favor, selecciona una fecha", Toast.LENGTH_SHORT).show();
            return;
        }
        if (botonSeleccionado == null) {
            Toast.makeText(this, "Por favor, selecciona un horario", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verifica el valor de horarioRangoId antes de enviarlo
        Log.d("ListarRangoHorarios", "horarioRangoId antes de enviar: " + horarioRangoId); // Verifica que tiene el valor correcto

        // Agregar tipoHorario a la siguiente actividad
        String fecha = textFechaSeleccionada.getText().toString();
        String tipoHorario = obtenerTipoHorario(botonSeleccionado); // Método para obtener tipoHorario basado en el turno

        Intent intent = new Intent(ListarRangoHorarios.this, VerBarberosActivity.class);

        intent.putExtra("nombreCliente", nombreCliente);
        intent.putExtra("apellidoCliente", apellidoCliente);
        intent.putExtra("fechaReserva", fecha);
        intent.putExtra("turnoReserva", botonSeleccionado); // Enviar el turno seleccionado
        intent.putExtra("nombreServicio", nombreServicio);
        intent.putExtra("descripcionServicio", descripcionServicio);
        intent.putExtra("precioServicio", precioServicio); // Enviarlo nuevamente como double
        intent.putExtra("imagenServicio", imagenUrlServicio);
        intent.putExtra("tipoHorario", tipoHorario);
        intent.putExtra("servicio_id", servicio_id);

        // Enviar el horarioRangoId también
        intent.putExtra("horarioRangoId", horarioRangoId); // Enviar el horarioRangoId

        startActivity(intent); // Iniciar la siguiente actividad
    }

}
