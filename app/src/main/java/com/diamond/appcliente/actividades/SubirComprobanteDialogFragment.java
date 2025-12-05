package com.diamond.appcliente.actividades;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import com.diamond.appcliente.R;
import com.diamond.appcliente.viewmodel.ListarReservaViewModel;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class SubirComprobanteDialogFragment extends DialogFragment {
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imagenSeleccionadaUri;
    private ListarReservaViewModel listarReservaViewModel;
    private Long reservaId;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        this.reservaId = getArguments().getLong("reservaId");
        this.listarReservaViewModel = new ViewModelProvider(requireActivity()).get(ListarReservaViewModel.class);
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        View view = requireActivity().getLayoutInflater().inflate(R.layout.activity_subir_comprobante, null);

        Button btnSeleccionarImagen = view.findViewById(R.id.btnSeleccionarImagen);
        Button btnSubirComprobante = view.findViewById(R.id.btnSubirComprobante);

        // Simplificado: usando clases anónimas
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
                    Toast.makeText(getContext(), "Por favor, seleccione una imagen", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.setContentView(view);
        return dialog;
    }

    private void seleccionarImagen() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK && requestCode == PICK_IMAGE_REQUEST && data != null) {
            imagenSeleccionadaUri = data.getData();
        }
    }

    private void subirComprobante(Long reservaId2, Uri imagenUri) {
        try {
            InputStream inputStream = getActivity().getContentResolver().openInputStream(imagenUri);
            File file = new File(getActivity().getCacheDir(), "user_image.jpg");
            OutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            while (true) {
                int read = inputStream.read(buffer);
                if (read == -1) break;
                outputStream.write(buffer, 0, read);
            }
            inputStream.close();
            outputStream.close();

            Log.d("SubirComprobante", "Archivo creado en: " + file.getAbsolutePath());
            Log.d("SubirComprobante", "Tamaño del archivo: " + file.length());

            // Subir imagen
            listarReservaViewModel.subirComprobante(reservaId2, MultipartBody.Part.createFormData("imagen", file.getName(), RequestBody.create(MediaType.parse("image/*"), file)), new ListarReservaViewModel.ActualizarCallback() {
                @Override
                public void onSuccess(String mensaje) {
                    Toast.makeText(getActivity(), mensaje, Toast.LENGTH_SHORT).show();
                    dismiss();
                }

                @Override
                public void onError(String mensaje) {
                    Toast.makeText(getActivity(), mensaje, Toast.LENGTH_SHORT).show();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Error al manejar la imagen", Toast.LENGTH_SHORT).show();
        }
    }
}
