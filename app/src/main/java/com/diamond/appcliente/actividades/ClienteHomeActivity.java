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
import com.bumptech.glide.Glide;
import com.diamond.appcliente.R;
import com.diamond.appcliente.adapters.ServicioAdapter;
import com.diamond.appcliente.dto.reserva.ReservaResponse;
import com.diamond.appcliente.dto.servicio.ServicioDto;
import com.diamond.appcliente.dto.usuario.UsuarioDto;
import com.diamond.appcliente.viewmodel.GestionarServicioViewModel;
import com.diamond.appcliente.viewmodel.ListarReservaViewModel;
import com.diamond.appcliente.viewmodel.UsuarioViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class ClienteHomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ServicioAdapter adapter;
    private List<ServicioDto> listaServicios;
    private GestionarServicioViewModel viewModel1;

    // UI Elements
    private TextView clienteNombre;
    private ImageView clienteFoto;
    private TextView metaProgresoTexto;
    private ImageView metaProgresoImagen;

    // Variables de instancia para almacenar los datos del cliente
    private String nombreCliente;
    private String apellidoCliente;
    private String imagenUrlCliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente_home);

        // Inicializamos las vistas
        clienteNombre = findViewById(R.id.clienteNombre);
        clienteFoto = findViewById(R.id.clienteFoto);
        metaProgresoTexto = findViewById(R.id.metaProgresoTexto);
        metaProgresoImagen = findViewById(R.id.metaProgresoImagen);

        // Carrusel de promociones
        List<Integer> promoImages = new ArrayList<>();
        promoImages.add(R.drawable.promo1);
        promoImages.add(R.drawable.promo1);
        promoImages.add(R.drawable.promo1);


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

        // Cargar datos del usuario
        cargarDatosUsuario();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Obtener las reservas realizadas desde el ViewModel
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
                if (reservasConRecompensa > 7) {
                    reservasConRecompensa = 7;
                }

                // Actualizar el progreso en la UI según la cantidad de reservas con recompensa
                updateMetaProgress(reservasConRecompensa);
            }
        });
    }


    // Método para cargar los datos del usuario
    private void cargarDatosUsuario() {
        UsuarioViewModel usuarioViewModel = new UsuarioViewModel();
        usuarioViewModel.obtenerMiUsuario(getApplicationContext(), new UsuarioViewModel.UsuarioCallback() {
            @Override
            public void onSuccess(UsuarioDto usuario) {
                // Asignar los datos obtenidos a las variables de instancia
                nombreCliente = usuario.getNombre();
                apellidoCliente = usuario.getApellido();
                imagenUrlCliente = usuario.getUrlUsuario();

                // Actualizar la UI con los datos del cliente
                clienteNombre.setText(nombreCliente + " " + apellidoCliente);

                // Cargar la imagen del cliente usando Glide
                if (imagenUrlCliente != null && !imagenUrlCliente.isEmpty()) {
                    Glide.with(ClienteHomeActivity.this)
                            .load(imagenUrlCliente)
                            .into(clienteFoto);
                } else {
                    clienteFoto.setImageResource(R.drawable.perfil_default);
                }
            }

            @Override
            public void onError(String mensaje) {
                // Manejo de errores
            }
        });
    }

    // Método para cargar los datos del token (recargará los datos en la interfaz de usuario)
    private void loadDataFromToken(String token) {
        JWT jwt = new JWT(token);
        nombreCliente = jwt.getClaim("nombre").asString();
        apellidoCliente = jwt.getClaim("apellido").asString();
        imagenUrlCliente = jwt.getClaim("urlUsuario").asString();

        // Asignamos los valores a la UI
        clienteNombre.setText(nombreCliente + " " + apellidoCliente);

        // Cargar la imagen del cliente usando Glide
        if (imagenUrlCliente != null && !imagenUrlCliente.isEmpty()) {
            Glide.with(this)
                    .load(imagenUrlCliente)
                    .into(clienteFoto);
        } else {
            clienteFoto.setImageResource(R.drawable.perfil_default);
        }
    }

    private void cargarServicios() {
        viewModel1.obtenerServicios(this, new GestionarServicioViewModel.ServicioCallback() {
            @Override
            public void onSuccess(List<ServicioDto> servicios) {
                listaServicios = servicios;
                adapter = new ServicioAdapter(servicios, new ServicioAdapter.OnServicioClickListener() {
                    @Override
                    public void onAviso(ServicioDto servicio, String imagenUrl) {
                        // Enviar los datos directamente a la siguiente actividad sin popup
                        Log.d("IntentData", "nombreServicio: " + servicio.getNombre());
                        Log.d("IntentData", "descripcionServicio: " + servicio.getDescripcion());
                        Log.d("IntentData", "precioServicio: " + servicio.getPrecio());
                        Log.d("IntentData", "imagenServicio: " + imagenUrl);
                        Log.d("IntentData", "nombre: " + nombreCliente);
                        Log.d("IntentData", "apellido: " + apellidoCliente);
                        Log.d("IntentData", "imagenUsuario: " + imagenUrlCliente);
                        Log.d("IntentData", "servicio_id: " + servicio.getServicio_id());

                        Intent intent = new Intent(ClienteHomeActivity.this, ListarRangoHorarios.class);
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
                Toast.makeText(ClienteHomeActivity.this, mensaje, Toast.LENGTH_SHORT).show();
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(ClienteHomeActivity.this, ClienteHomeActivity.class));
                overridePendingTransition(0, 0); // Evitar animación de transición
                return true;
            } else if (id == R.id.nav_servicios) {
                startActivity(new Intent(ClienteHomeActivity.this, SeccionServiciosActivity.class));
                overridePendingTransition(0, 0); // Evitar animación de transición
                return true;
            } else if (id == R.id.historial) {
                startActivity(new Intent(ClienteHomeActivity.this, ListarReservaActivity.class));
                overridePendingTransition(0, 0); // Evitar animación de transición
                return true;
            } else if (id == R.id.nav_perfil) {
                startActivity(new Intent(ClienteHomeActivity.this, UsuarioActivity.class));
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
            Toast.makeText(ClienteHomeActivity.this, "No se encontraron servicios para esta categoría", Toast.LENGTH_SHORT).show();
        }
    }

    // Método para actualizar el progreso de la meta
    private void updateMetaProgress(int reservasRealizadas) {
        // Determinar el texto (de 0/7 a 7/7)
        String progresoTexto = reservasRealizadas + "/7 Cortes";
        metaProgresoTexto.setText(progresoTexto);

        // Determinar la imagen según el progreso
        int progresoImagenId = R.drawable.racha_0; // Imagen predeterminada (0/7)

        switch (reservasRealizadas) {
            case 1:
                progresoImagenId = R.drawable.racha_1;
                break;
            case 2:
                progresoImagenId = R.drawable.racha_2;
                break;
            case 3:
                progresoImagenId = R.drawable.racha_3;
                break;
            case 4:
                progresoImagenId = R.drawable.racha_4;
                break;
            case 5:
                progresoImagenId = R.drawable.racha_5;
                break;
            case 6:
                progresoImagenId = R.drawable.racha_6;
                break;
            case 7:
                progresoImagenId = R.drawable.racha_7;
                break;
        }

        // Actualizar la imagen de progreso
        metaProgresoImagen.setImageResource(progresoImagenId);
    }
}
