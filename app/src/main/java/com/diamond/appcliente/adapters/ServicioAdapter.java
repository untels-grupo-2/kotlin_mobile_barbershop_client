package com.diamond.appcliente.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.diamond.appcliente.R;
import com.diamond.appcliente.dto.servicio.ServicioDto;

import java.util.List;

public class ServicioAdapter extends RecyclerView.Adapter<ServicioAdapter.ServicioViewHolder> {

    public interface OnServicioClickListener {
        void onAviso(ServicioDto servicio, String imagenUrl);
    }

    private List<ServicioDto> servicios;  // Lista de servicios que se muestra en el RecyclerView
    private final OnServicioClickListener listener;

    // Constructor del adapter
    public ServicioAdapter(List<ServicioDto> servicios, OnServicioClickListener listener) {
        this.servicios = servicios;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ServicioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_servicio, parent, false);
        return new ServicioViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ServicioViewHolder holder, int position) {
        ServicioDto servicio = servicios.get(position); // Obtener el servicio en la posición
        holder.textNombre.setText(servicio.getNombre());  // Establecer nombre del servicio
        holder.textPrecio.setText("S/ " + servicio.getPrecio());  // Establecer precio del servicio
        holder.textDescripcion.setText(servicio.getDescripcion());  // Establecer nombre del servicio

        // Obtener la URL de la imagen desde el backend (campo urlServicio)
        String imagenUrl = servicio.getUrlServicio();

        // Usar Glide para cargar la imagen desde la URL obtenida del backend
        Glide.with(holder.itemView.getContext())
                .load(imagenUrl)
                .into(holder.imageServicio);

        // Establecer el click listener para el botón de aviso
        holder.btnAviso.setOnClickListener(v -> listener.onAviso(servicio, imagenUrl));
    }

    @Override
    public int getItemCount() {
        return servicios.size();  // Devuelve el tamaño de la lista de servicios
    }

    // Método para actualizar los servicios mostrados en el RecyclerView
    public void updateServicios(List<ServicioDto> nuevosServicios) {
        this.servicios = nuevosServicios;  // Actualiza la lista de servicios
        notifyDataSetChanged();  // Notifica al adaptador que los datos han cambiado
    }

    // ViewHolder para el adaptador
    public static class ServicioViewHolder extends RecyclerView.ViewHolder {

        // Elementos UI para cada servicio
        TextView textNombre, textPrecio, textDescripcion;
        Button btnActualizar, btnEliminar, btnAviso;
        ImageView imageServicio;

        public ServicioViewHolder(@NonNull View itemView) {
            super(itemView);
            // Inicialización de los elementos UI dentro de cada item del RecyclerView
            textNombre = itemView.findViewById(R.id.textNombreServicio);
            textPrecio = itemView.findViewById(R.id.textPrecioServicio);
            textDescripcion = itemView.findViewById(R.id.textDescripcionServicio);
            btnAviso = itemView.findViewById(R.id.btnAviso);
            imageServicio = itemView.findViewById(R.id.imageServicio);
        }
    }
}
