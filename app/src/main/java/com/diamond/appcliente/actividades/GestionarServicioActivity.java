package com.diamond.appcliente.actividades;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.diamond.appcliente.R;
import com.diamond.appcliente.adapters.ServicioAdapter;
import com.diamond.appcliente.dto.servicio.ServicioDto;
import com.diamond.appcliente.dto.servicio.ServicioRequest;
import com.diamond.appcliente.viewmodel.GestionarServicioViewModel;

import java.util.List;

public class GestionarServicioActivity extends AppCompatActivity {

    private GestionarServicioViewModel viewModel;
    private RecyclerView recyclerView;
    private Button btnAgregarServicio;
    private ServicioAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestionar_servicio);

        recyclerView = findViewById(R.id.recyclerViewServicios);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // 2 columnas

        btnAgregarServicio = findViewById(R.id.btnAgregarServicio);

        // Inicializa el ViewModel
        viewModel = new ViewModelProvider(this).get(GestionarServicioViewModel.class);

        // Observa el LiveData para actualizar la UI cuando los servicios cambien
        // Cargar los servicios
        viewModel.obtenerServicios(this, new GestionarServicioViewModel.ServicioCallback() {
            @Override
            public void onSuccess(List<ServicioDto> servicios) {
                // Si la lista se actualiza, actualizamos el adaptador
                if (adapter == null) {
                    adapter = new ServicioAdapter(servicios, new ServicioAdapter.OnServicioClickListener() {
                        @Override
                        public void onAviso(ServicioDto servicio, String imagenUrl) {

                        }
                    });
                    recyclerView.setAdapter(adapter);
                } else {
                    // Si ya existe el adaptador, solo actualizamos la lista
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(String mensaje) {
                // Si ocurre un error, muestra un mensaje en el toast
                Toast.makeText(GestionarServicioActivity.this, "Error: " + mensaje, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void crearNuevoServicio(String nombre, double precio, String descripcion, int tipoServicioId) {
        ServicioRequest request = new ServicioRequest(nombre, precio, descripcion, tipoServicioId);
        viewModel.crearServicio(this, request, new GestionarServicioViewModel.ServicioOperacionCallback() {
            @Override
            public void onSuccess(String mensaje) {
                Toast.makeText(GestionarServicioActivity.this, mensaje, Toast.LENGTH_SHORT).show();
                // Refrescar la lista de servicios después de crear uno nuevo
                viewModel.obtenerServicios(GestionarServicioActivity.this, new GestionarServicioViewModel.ServicioCallback() {
                    @Override
                    public void onSuccess(List<ServicioDto> servicios) {
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(String mensaje) {
                        Toast.makeText(GestionarServicioActivity.this, "Error: " + mensaje, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String mensaje) {
                Toast.makeText(GestionarServicioActivity.this, "Error: " + mensaje, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void eliminarServicio(int id) {
        viewModel.eliminarServicio(this, id, new GestionarServicioViewModel.ServicioOperacionCallback() {
            @Override
            public void onSuccess(String mensaje) {
                Toast.makeText(GestionarServicioActivity.this, mensaje, Toast.LENGTH_SHORT).show();
                // Refrescar la lista después de eliminar un servicio
                viewModel.obtenerServicios(GestionarServicioActivity.this, new GestionarServicioViewModel.ServicioCallback() {
                    @Override
                    public void onSuccess(List<ServicioDto> servicios) {
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(String mensaje) {
                        Toast.makeText(GestionarServicioActivity.this, "Error: " + mensaje, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String mensaje) {
                Toast.makeText(GestionarServicioActivity.this, "Error: " + mensaje, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
