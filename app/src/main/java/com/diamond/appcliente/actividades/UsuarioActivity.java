package com.diamond.appcliente.actividades;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.auth0.android.jwt.JWT;
import com.bumptech.glide.Glide;
import com.diamond.appcliente.R;
import com.diamond.appcliente.dto.usuario.UsuarioDto;
import com.diamond.appcliente.util.PreferenciasHelper;
import com.diamond.appcliente.viewmodel.UsuarioViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class UsuarioActivity extends AppCompatActivity {
    private Button btnLogoutUser;
    private ImageView imageUsuario;
    private Uri imagenSeleccionadaUri;
    private TextView textCelular;
    private TextView textEmail;
    private TextView textNombre;
    private UsuarioViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario);
        imageUsuario = findViewById(R.id.imageUsuario);
        textNombre = findViewById(R.id.textNombre);
        textEmail = findViewById(R.id.textEmail);
        textCelular = findViewById(R.id.textCelular);
        btnLogoutUser = findViewById(R.id.btnLogoutUser);
        viewModel = new UsuarioViewModel();

        String token = new PreferenciasHelper(getApplication()).obtenerToken();
        if (token != null) {
            Log.d("UsuarioActivity", "Token recibido: " + token);
            new JWT(token);
            viewModel.obtenerMiUsuario(this, new UsuarioViewModel.UsuarioCallback() {
                @Override
                public void onSuccess(UsuarioDto usuario) {
                    textNombre.setText(usuario.getNombre() + " " + usuario.getApellido());
                    textEmail.setText(usuario.getEmail());
                    textCelular.setText(usuario.getCelular());
                    Glide.with(UsuarioActivity.this).load(usuario.getUrlUsuario()).placeholder(R.drawable.perfil_default).into(imageUsuario);
                }

                @Override
                public void onError(String mensaje) {
                    Toast.makeText(UsuarioActivity.this, "Error al cargar usuario: " + mensaje, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.d("UsuarioActivity", "Token no encontrado");
        }

        getWindow().setStatusBarColor(-16777216);

        // Usando clases anónimas en lugar de lambdas
        findViewById(R.id.buttonActualizarUsuario).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarPopupActualizar();
            }
        });

        btnLogoutUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarSesion();
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_perfil);
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    startActivity(new Intent(UsuarioActivity.this, ClienteHomeActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (id == R.id.nav_servicios) {
                    startActivity(new Intent(UsuarioActivity.this, SeccionServiciosActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (id == R.id.historial) {
                    startActivity(new Intent(UsuarioActivity.this, ListarReservaActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (id != R.id.nav_perfil) {
                    return false;
                } else {
                    startActivity(new Intent(UsuarioActivity.this, UsuarioActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
            }
        });
    }

    private void mostrarPopupActualizar() {
        View popupView = getLayoutInflater().inflate(R.layout.popup_actualizar_usuario, null);
        PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        final EditText editTextNombre = popupView.findViewById(R.id.etNombreUsuario);
        final EditText editTextApellido = popupView.findViewById(R.id.etApellidoUsuario);
        final EditText editTextEmail = popupView.findViewById(R.id.etEmailUsuario);
        final EditText editTextCelular = popupView.findViewById(R.id.etCelularUsuario);
        Button btnActualizarImagen = popupView.findViewById(R.id.btnSeleccionarImagenUsuario);
        Button btnActualizarDatos = popupView.findViewById(R.id.btnActualizarUsuario);
        Button btnCancelarActualizarUsuario = popupView.findViewById(R.id.btnCancelarActualizarUsuario);

        if (new PreferenciasHelper(getApplication()).obtenerToken() != null) {
            viewModel.obtenerMiUsuario(this, new UsuarioViewModel.UsuarioCallback() {
                @Override
                public void onSuccess(UsuarioDto usuario) {
                    editTextNombre.setText(usuario.getNombre());
                    editTextApellido.setText(usuario.getApellido());
                    editTextEmail.setText(usuario.getEmail());
                    editTextCelular.setText(usuario.getCelular());
                }

                @Override
                public void onError(String mensaje) {
                    Toast.makeText(UsuarioActivity.this, "Error al cargar usuario: " + mensaje, Toast.LENGTH_SHORT).show();
                }
            });
        }

        btnActualizarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionarImagen();
            }
        });

        btnActualizarDatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre = editTextNombre.getText().toString();
                String apellido = editTextApellido.getText().toString();
                String email = editTextEmail.getText().toString();
                String celular = editTextCelular.getText().toString();

                if (celular.length() == 9) {
                    UsuarioDto dtoUsuario = new UsuarioDto();
                    dtoUsuario.setNombre(nombre);
                    dtoUsuario.setApellido(apellido);
                    dtoUsuario.setEmail(email);
                    dtoUsuario.setCelular(celular);

                    if (imagenSeleccionadaUri != null) {
                        actualizarUsuarioConImagen(dtoUsuario, imagenSeleccionadaUri);
                    } else {
                        actualizarUsuario(dtoUsuario);
                    }

                    popupWindow.dismiss();
                } else {
                    Toast.makeText(UsuarioActivity.this, "El número de celular debe tener 9 dígitos.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCancelarActualizarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                startActivity(new Intent(UsuarioActivity.this, UsuarioActivity.class));
            }
        });

        popupWindow.showAtLocation(findViewById(R.id.activity_usuario), 17, 0, 0);
    }

    private void seleccionarImagen() {
        startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            imagenSeleccionadaUri = data.getData();
        }
    }

    private void actualizarUsuario(UsuarioDto dtoUsuario) {
        if (new PreferenciasHelper(getApplication()).obtenerToken() != null) {
            viewModel.actualizarMiPerfil(this, dtoUsuario, null, new UsuarioViewModel.ActualizarCallback() {
                @Override
                public void onSuccess(String mensaje) {
                    Toast.makeText(UsuarioActivity.this, mensaje, Toast.LENGTH_SHORT).show();
                    cargarUsuario();
                }

                @Override
                public void onError(String mensaje) {
                    Toast.makeText(UsuarioActivity.this, mensaje, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void actualizarUsuarioConImagen(UsuarioDto dtoUsuario, Uri imagenUri) {
        if (new PreferenciasHelper(getApplication()).obtenerToken() != null) {
            viewModel.actualizarMiPerfil(this, dtoUsuario, imagenUri, new UsuarioViewModel.ActualizarCallback() {
                @Override
                public void onSuccess(String mensaje) {
                    Toast.makeText(UsuarioActivity.this, mensaje, Toast.LENGTH_SHORT).show();
                    cargarUsuario();
                }

                @Override
                public void onError(String mensaje) {
                    Toast.makeText(UsuarioActivity.this, mensaje, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void cerrarSesion() {
        new PreferenciasHelper(getApplication()).limpiarPreferencias();
        Toast.makeText(this, "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void cargarUsuario() {
        if (new PreferenciasHelper(getApplication()).obtenerToken() != null) {
            viewModel.obtenerMiUsuario(this, new UsuarioViewModel.UsuarioCallback() {
                @Override
                public void onSuccess(UsuarioDto usuario) {
                    textNombre.setText(usuario.getNombre() + " " + usuario.getApellido());
                    textEmail.setText(usuario.getEmail());
                    textCelular.setText(usuario.getCelular());
                    Glide.with(UsuarioActivity.this).load(usuario.getUrlUsuario()).into(imageUsuario);
                }

                @Override
                public void onError(String mensaje) {
                    Toast.makeText(UsuarioActivity.this, "Error al cargar usuario: " + mensaje, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
