package com.diamond.appcliente.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.diamond.appcliente.util.PreferenciasHelper;

public class ClienteHomeViewModel extends AndroidViewModel {

    // LiveData para manejar los datos del cliente
    public final MutableLiveData<String> nombreCompleto = new MutableLiveData<>();
    public final MutableLiveData<String> imagenUrlCliente = new MutableLiveData<>();

    private final PreferenciasHelper preferenciasHelper;

    public ClienteHomeViewModel(@NonNull Application application) {
        super(application);
        preferenciasHelper = new PreferenciasHelper(application);
    }

    // Establece el nombre y apellido del cliente
    public void setNombreYApellido(String nombre, String apellido) {
        nombreCompleto.setValue(nombre + " " + apellido);
    }

    // Establece la URL de la imagen del cliente
    public void setImagenUrlCliente(String imagenUrl) {
        imagenUrlCliente.setValue(imagenUrl);
    }

    // Métodos getter para acceder a los valores del LiveData
    public String getNombreCliente() {
        return nombreCompleto.getValue() != null ? nombreCompleto.getValue() : "Desconocido";
    }

    public String getApellidoCliente() {
        return nombreCompleto.getValue() != null ? nombreCompleto.getValue().split(" ")[1] : "Desconocido";
    }

    public String getImagenUrlCliente() {
        return imagenUrlCliente.getValue() != null ? imagenUrlCliente.getValue() : "default";
    }

    // Método para cerrar sesión y limpiar las preferencias
    public void cerrarSesion() {
        preferenciasHelper.limpiarPreferencias();
    }
}
