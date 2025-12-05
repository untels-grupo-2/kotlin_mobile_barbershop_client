package com.diamond.appcliente.actividades;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.diamond.appcliente.R;
import com.diamond.appcliente.dto.valoracion.ValoracionRequest;
import com.diamond.appcliente.util.PreferenciasHelper;
import com.diamond.appcliente.viewmodel.GestionarValoracionViewModel;

public class ValoracionActivity extends AppCompatActivity {

    private RadioGroup radioGroupValoracion;
    private RadioGroup radioGroupFacilidad;
    private EditText editTextComentario;
    private Button btnEnviarValoracion;
    private String nombreCliente, apellidoCliente, imagenUrlCliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_valoracion);

        PreferenciasHelper prefs = new PreferenciasHelper(getApplication());
        String token = prefs.obtenerToken();

        Log.d("ValoracionActivity", "Token recibido: " + token);

        Intent intent = getIntent();
        nombreCliente = intent.getStringExtra("nombre");
        apellidoCliente = intent.getStringExtra("apellido");
        imagenUrlCliente = intent.getStringExtra("urlUsuario");

        Log.d("ValoracionActivity", "Nombre Cliente: " + nombreCliente);
        Log.d("ValoracionActivity", "Apellido Cliente: " + apellidoCliente);
        Log.d("ValoracionActivity", "URL Cliente: " + imagenUrlCliente);

        radioGroupValoracion = findViewById(R.id.radioGroupValoracion);
        radioGroupFacilidad = findViewById(R.id.radioGroupFacilidad);
        editTextComentario = findViewById(R.id.editTextComentario);
        btnEnviarValoracion = findViewById(R.id.btnEnviarValoracion);

        Button btnCancelar = findViewById(R.id.btnCancelar);
        btnCancelar.setOnClickListener(v -> {
            Intent intentCancelar = new Intent(ValoracionActivity.this, ClienteHomeActivity.class);
            startActivity(intentCancelar);
            finish();
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.BLACK);  // Cambiar la barra de estado a negro
        }

        btnEnviarValoracion.setOnClickListener(v -> enviarValoracion());
    }

    private void enviarValoracion() {
        int valoracion = getValoracionSeleccionada();
        Boolean utilidad = getFacilidadSeleccionada();
        String mensaje = editTextComentario.getText().toString();

        if (valoracion != -1 && utilidad != null && !mensaje.isEmpty()) {
            ValoracionRequest request = new ValoracionRequest(valoracion, utilidad, mensaje);

            GestionarValoracionViewModel viewModel = new GestionarValoracionViewModel();
            viewModel.enviarValoracion(this, request, new GestionarValoracionViewModel.ValoracionCallback() {
                @Override
                public void onSuccess(String message) {
                    // Inflar layout personalizado
                    View dialogView = getLayoutInflater().inflate(R.layout.dialog_valoracion_exitosa, null);

                    AlertDialog dialog = new AlertDialog.Builder(ValoracionActivity.this, R.style.ReservaDialogTheme)
                            .setView(dialogView)
                            .setCancelable(true)
                            .create();

                    // Botón OK
                    Button btnOk = dialogView.findViewById(R.id.btnOk);
                    btnOk.setOnClickListener(v -> {
                        dialog.dismiss();
                        Intent intent = new Intent(ValoracionActivity.this, ClienteHomeActivity.class);
                        startActivity(intent);
                        finish();
                    });

                    // Si tocan fuera del diálogo
                    dialog.setOnCancelListener(d -> {
                        Intent intent = new Intent(ValoracionActivity.this, ClienteHomeActivity.class);
                        startActivity(intent);
                        finish();
                    });

                    dialog.show();
                }


                @Override
                public void onError(String message) {
                    Toast.makeText(ValoracionActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
        }
    }

    private int getValoracionSeleccionada() {
        int selectedId = radioGroupValoracion.getCheckedRadioButtonId();
        if (selectedId != -1) {
            RadioButton radioButton = findViewById(selectedId);
            return Integer.parseInt(radioButton.getText().toString());
        }
        return -1;
    }

    private Boolean getFacilidadSeleccionada() {
        int selectedId = radioGroupFacilidad.getCheckedRadioButtonId();
        if (selectedId != -1) {
            RadioButton radioButton = findViewById(selectedId);
            return radioButton.getText().toString().equals("Sí");
        }
        return null;
    }
}
