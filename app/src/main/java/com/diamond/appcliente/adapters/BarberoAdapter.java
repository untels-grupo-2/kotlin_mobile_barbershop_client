package com.diamond.appcliente.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.diamond.appcliente.R;
import com.diamond.appcliente.dto.barbero.BarberoDto;

import java.util.List;

public class BarberoAdapter extends RecyclerView.Adapter<BarberoAdapter.BarberoViewHolder> {

    public interface OnBarberoClickListener {
        void onBarberoSeleccionado(BarberoDto barbero, String nombreBarbero);
        void onActualizar(BarberoDto barbero);
        void onEliminar(BarberoDto barbero);
    }

    private final List<BarberoDto> barberos;
    private final OnBarberoClickListener listener;

    // Variable para saber cuál barbero está seleccionado
    private int barberoSeleccionado = -1;

    public BarberoAdapter(List<BarberoDto> barberos, OnBarberoClickListener listener) {
        this.barberos = barberos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BarberoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_barbero, parent, false);
        return new BarberoViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull BarberoViewHolder holder, int position) {
        BarberoDto barbero = barberos.get(position);
        holder.textNombre.setText(barbero.getNombre());

        // Cargar la imagen del barbero usando Glide y la URL proporcionada por el backend
        String imagenUrl = barbero.getUrlBarbero();
        Glide.with(holder.itemView.getContext())
                .load(imagenUrl)
                .into(holder.imageBarbero);

        // Desmarcar todos los RadioButtons
        holder.radioButton.setChecked(false);

        // Si el barbero es el seleccionado, marcarlo
        if (holder.getAdapterPosition() == barberoSeleccionado) {
            holder.radioButton.setChecked(true);
        }

        // Establecer el evento de selección del barbero
        holder.radioButton.setOnClickListener(v -> {
            barberoSeleccionado = holder.getAdapterPosition();
            listener.onBarberoSeleccionado(barbero, barbero.getNombre());
            notifyDataSetChanged(); // Notificar que se ha actualizado la selección
        });
    }

    @Override
    public int getItemCount() {
        return barberos.size();
    }

    public static class BarberoViewHolder extends RecyclerView.ViewHolder {
        TextView textNombre;
        ImageView imageBarbero;
        RadioButton radioButton;

        public BarberoViewHolder(@NonNull View itemView) {
            super(itemView);
            textNombre = itemView.findViewById(R.id.textNombre);
            imageBarbero = itemView.findViewById(R.id.imageBarbero);
            radioButton = itemView.findViewById(R.id.radioButton);
        }
    }
}
