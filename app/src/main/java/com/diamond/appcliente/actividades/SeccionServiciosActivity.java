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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.auth0.android.jwt.JWT;
import com.diamond.appcliente.R;
import com.diamond.appcliente.adapters.ServicioAdapter;
import com.diamond.appcliente.dto.servicio.ServicioDto;
import com.diamond.appcliente.util.PreferenciasHelper;
import com.diamond.appcliente.viewmodel.GestionarServicioViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class SeccionServiciosActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ServicioAdapter adapter;
    private List<ServicioDto> listaServicios;
    private GestionarServicioViewModel viewModel1;

    // UI Elements
    private TextView clienteNombre;
    private ImageView clienteFoto;

    // Variables de instancia para almacenar los datos del cliente
    private String nombreCliente;
    private String apellidoCliente;
    private String imagenUrlCliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_servicios);

        // Obtener el token JWT desde las preferencias
        PreferenciasHelper prefs = new PreferenciasHelper(getApplication());
        String token = prefs.obtenerToken();
        JWT jwt = new JWT(token);

        // Extraemos los datos del JWT
        nombreCliente = jwt.getClaim("nombre").asString();
        apellidoCliente = jwt.getClaim("apellido").asString();
        imagenUrlCliente = jwt.getClaim("urlUsuario").asString();




        // Configuración del RecyclerView
        recyclerView = findViewById(R.id.recyclerViewServicios);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        viewModel1 = new GestionarServicioViewModel();
        cargarServicios();

        // Botones de filtro para los diferentes tipos de servicio
        Button serviciosButton = findViewById(R.id.serviciosButton);
        Button cortesButton = findViewById(R.id.cortesButton);
        Button skincareButton = findViewById(R.id.SkincareButton);
        Button afeitadoButton = findViewById(R.id.AfeitadoButton);
        Button coloracionButton = findViewById(R.id.ColoracionButton);

        serviciosButton.setOnClickListener(v -> {
            // Mostrar todos los servicios
            updateRecyclerView(listaServicios);
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.BLACK);  // Cambiar la barra de estado a negro
        }

        cortesButton.setOnClickListener(v -> {
            // Filtrar por tipo "Corte de Cabello"
            List<ServicioDto> corteServicios = filterServiciosByType("CORTES");
            updateRecyclerView(corteServicios);
        });

        coloracionButton.setOnClickListener(v -> {
            // Filtrar por tipo "Coloracion"
            List<ServicioDto> coloracionServicios = filterServiciosByType("COLORACIÓN");
            updateRecyclerView(coloracionServicios);
        });

        skincareButton.setOnClickListener(v -> {
            // Filtrar por tipo "Skincare"
            List<ServicioDto> skincareServicios = filterServiciosByType("SKINCARE");
            updateRecyclerView(skincareServicios);
        });

        afeitadoButton.setOnClickListener(v -> {
            // Filtrar por tipo "Afeitado"
            List<ServicioDto> afeitadoServicios = filterServiciosByType("AFEITADO DE BARBA");
            updateRecyclerView(afeitadoServicios);
        });
    }

    private void cargarServicios() {
        viewModel1.obtenerServicios(this, new GestionarServicioViewModel.ServicioCallback() {
            @Override
            public void onSuccess(List<ServicioDto> servicios) {
                listaServicios = servicios;
                adapter = new ServicioAdapter(servicios, new ServicioAdapter.OnServicioClickListener() {
                    @Override
                    public void onAviso(ServicioDto servicio, String imagenUrl) {
                        Log.d("IntentData", "nombreServicio: " + servicio.getNombre());
                        Log.d("IntentData", "descripcionServicio: " + servicio.getDescripcion());
                        Log.d("IntentData", "precioServicio: " + servicio.getPrecio());
                        Log.d("IntentData", "imagenServicio: " + imagenUrl);
                        Log.d("IntentData", "nombre: " + nombreCliente);
                        Log.d("IntentData", "apellido: " + apellidoCliente);
                        Log.d("IntentData", "imagenUsuario: " + imagenUrlCliente);
                        Log.d("IntentData", "servicio_id: " + servicio.getServicio_id());
                        // Enviar los datos directamente a la siguiente actividad sin popup
                        Intent intent = new Intent(SeccionServiciosActivity.this, ListarRangoHorarios.class);
                        intent.putExtra("nombreServicio", servicio.getNombre());
                        intent.putExtra("servicio_id", servicio.getServicio_id());
                        intent.putExtra("descripcionServicio", servicio.getDescripcion());
                        intent.putExtra("precioServicio", servicio.getPrecio());  // Como double
                        intent.putExtra("imagenServicio", imagenUrl);
                        intent.putExtra("nombre", nombreCliente);
                        intent.putExtra("apellido", apellidoCliente);
                        startActivity(intent);
                    }
                });
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onError(String mensaje) {
                Toast.makeText(SeccionServiciosActivity.this, mensaje, Toast.LENGTH_SHORT).show();
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_servicios);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(SeccionServiciosActivity.this, ClienteHomeActivity.class));
                overridePendingTransition(0, 0); // Evitar animación de transición
                return true;
            } else if (id == R.id.nav_servicios) {
                startActivity(new Intent(SeccionServiciosActivity.this, SeccionServiciosActivity.class));
                overridePendingTransition(0, 0); // Evitar animación de transición
                return true;
            } else if (id == R.id.historial) {
                startActivity(new Intent(SeccionServiciosActivity.this, ListarReservaActivity.class));
                overridePendingTransition(0, 0); // Evitar animación de transición
                return true;
            } else if (id == R.id.nav_perfil) {
                startActivity(new Intent(SeccionServiciosActivity.this, UsuarioActivity.class));
                overridePendingTransition(0, 0); // Evitar animación de transición
                return true;
            }
            return false;
        });
    }

    // Método para filtrar los servicios por tipo
    private List<ServicioDto> filterServiciosByType(String tipo) {
        List<ServicioDto> filteredServicios = new ArrayList<>();
        for (ServicioDto servicio : listaServicios) {
            if (servicio.getNombre_tipoServicio().equalsIgnoreCase(tipo)) {
                filteredServicios.add(servicio);
            }
        }
        return filteredServicios;
    }

    // Método para actualizar el RecyclerView con los servicios filtrados
    private void updateRecyclerView(List<ServicioDto> serviciosFiltrados) {
        if (adapter != null) {
            adapter.updateServicios(serviciosFiltrados);
        }
        if (serviciosFiltrados.isEmpty()) {
            Toast.makeText(SeccionServiciosActivity.this, "No se encontraron servicios para esta categoría", Toast.LENGTH_SHORT).show();
        }
    }
}
