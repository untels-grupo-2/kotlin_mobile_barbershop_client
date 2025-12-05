package com.diamond.appcliente.actividades;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.diamond.appcliente.R;
import com.diamond.appcliente.adapters.ListarReservaAdapter;
import com.diamond.appcliente.viewmodel.ListarReservaViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.List;

public class ListarReservaActivity extends AppCompatActivity {
    private ListarReservaAdapter listarReservaAdapter;
    private ListarReservaViewModel listarReservaViewModel;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_reserva);
        this.recyclerView = findViewById(R.id.recycler_view_reservas);
        this.progressBar = findViewById(R.id.progress_bar);
        this.listarReservaAdapter = new ListarReservaAdapter();
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.recyclerView.setAdapter(this.listarReservaAdapter);
        this.listarReservaViewModel = new ViewModelProvider(this).get(ListarReservaViewModel.class);
        loadReservations();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.historial);
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    startActivity(new Intent(ListarReservaActivity.this, ClienteHomeActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (id == R.id.nav_servicios) {
                    startActivity(new Intent(ListarReservaActivity.this, SeccionServiciosActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (id == R.id.historial || id != R.id.nav_perfil) {
                    return false;
                } else {
                    startActivity(new Intent(ListarReservaActivity.this, UsuarioActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
            }
        });

        getWindow().setStatusBarColor(-16777216); // Establecer color de la barra de estado
    }

    private void loadReservations() {
        this.progressBar.setVisibility(0);
        this.listarReservaViewModel.getReservas().observe(this, new androidx.lifecycle.Observer<List>() {
            @Override
            public void onChanged(List reservas) {
                progressBar.setVisibility(8); // Ocultar el progress bar al recibir datos
                if (reservas == null || reservas.isEmpty()) {
                    Toast.makeText(ListarReservaActivity.this, "No se encontraron reservas", Toast.LENGTH_SHORT).show();
                } else {
                    listarReservaAdapter.submitList(reservas); // Actualizar la lista de reservas
                }
            }
        });
    }
}
