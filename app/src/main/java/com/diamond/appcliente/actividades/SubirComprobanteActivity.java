package com.diamond.appcliente.actividades;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import com.diamond.appcliente.R;
import com.diamond.appcliente.viewmodel.ListarReservaViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class SubirComprobanteActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_CODE_PERMISSIONS = 1001;
    private ImageView imagePreview;
    private Uri imagenSeleccionadaUri;
    private ListarReservaViewModel listarReservaViewModel;
    private Long reservaId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subir_comprobante);
        this.reservaId = Long.valueOf(getIntent().getLongExtra("reservaId", -1));
        this.listarReservaViewModel = new ViewModelProvider(this).get(ListarReservaViewModel.class);

        Button btnSeleccionarImagen = findViewById(R.id.btnSeleccionarImagen);
        Button btnSubirComprobante = findViewById(R.id.btnSubirComprobante);
        Button btnCancelar = findViewById(R.id.btnCancelar);
        this.imagePreview = findViewById(R.id.imagePreview);

        // Request permissions if needed
        if (ContextCompat.checkSelfPermission(this, "android.permission.READ_EXTERNAL_STORAGE") != 0) {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, REQUEST_CODE_PERMISSIONS);
        }

        getWindow().setStatusBarColor(-16777216);

        btnSeleccionarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionarImagen();
            }
        });

        btnSubirComprobante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imagenSeleccionadaUri != null) {
                    subirComprobante(reservaId, imagenSeleccionadaUri);
                } else {
                    Toast.makeText(SubirComprobanteActivity.this, "Por favor, seleccione una imagen", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SubirComprobanteActivity.this, ListarReservaActivity.class));
                finish();
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.historial);
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    startActivity(new Intent(SubirComprobanteActivity.this, ClienteHomeActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (id == R.id.nav_servicios) {
                    startActivity(new Intent(SubirComprobanteActivity.this, SeccionServiciosActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (id == R.id.historial || id != R.id.nav_perfil) {
                    return false;
                } else {
                    startActivity(new Intent(SubirComprobanteActivity.this, UsuarioActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
            }
        });
    }

    // Request permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS && grantResults.length > 0 && grantResults[0] == 0) {
            Toast.makeText(this, "Permiso concedido", Toast.LENGTH_SHORT).show();
        }
    }

    // Seleccionar imagen de la galería
    private void seleccionarImagen() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE_REQUEST && data != null) {
            imagenSeleccionadaUri = data.getData();
            mostrarVistaPrevia(imagenSeleccionadaUri);
        }
    }

    private void mostrarVistaPrevia(Uri imagenUri) {
        try {
            imagePreview.setImageBitmap(BitmapFactory.decodeStream(getContentResolver().openInputStream(imagenUri)));
            imagePreview.setVisibility(View.VISIBLE);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("SubirComprobanteActivity", "Error al mostrar la vista previa de la imagen", e);
        }
    }

    // Subir el comprobante a la nube o servidor
    private void subirComprobante(Long reservaId2, Uri imagenUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imagenUri);
            File file = new File(getCacheDir(), "user_image.jpg");
            OutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            while (true) {
                int read = inputStream.read(buffer);
                if (read == -1) {
                    break;
                }
                outputStream.write(buffer, 0, read);
            }
            inputStream.close();
            outputStream.close();

            // Enviar la imagen al servidor
            listarReservaViewModel.subirComprobante(reservaId2, MultipartBody.Part.createFormData("imagen", file.getName(), RequestBody.create(MediaType.parse("image/*"), file)), new ListarReservaViewModel.ActualizarCallback() {
                @Override
                public void onSuccess(String mensaje) {
                    Log.d("SubirComprobanteActivity", "Comprobante subido exitosamente.");
                    Toast.makeText(SubirComprobanteActivity.this, mensaje, Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onError(String mensaje) {
                    Log.e("SubirComprobanteActivity", "Error al subir el comprobante: " + mensaje);
                    Toast.makeText(SubirComprobanteActivity.this, mensaje, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("SubirComprobanteActivity", "Error al manejar la imagen", e);
            Toast.makeText(this, "Error al manejar la imagen", Toast.LENGTH_SHORT).show();
        }
    }
}
