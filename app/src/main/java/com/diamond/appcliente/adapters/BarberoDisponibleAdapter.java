package com.diamond.appcliente.adapters;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.diamond.appcliente.R;
import com.diamond.appcliente.dto.barbero.DtoBarberoDisponible;

import java.util.List;

public class BarberoDisponibleAdapter extends RecyclerView.Adapter<BarberoDisponibleAdapter.BarberoViewHolder> {

    private List<DtoBarberoDisponible> barberos;
    private Context context;
    private int selectedPosition = -1;  // Para hacer que solo se pueda seleccionar uno

    public BarberoDisponibleAdapter(List<DtoBarberoDisponible> barberos, Context context) {
        this.barberos = barberos;
        this.context = context;
    }

    @Override
    public BarberoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_barbero_disponible, parent, false);
        return new BarberoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BarberoViewHolder holder, int position) {
        DtoBarberoDisponible barbero = barberos.get(position);

        // Seteamos los valores en la UI
        holder.nombreTextView.setText(barbero.getNombre());
        Glide.with(context).load(barbero.getUrlBarbero()).into(holder.imagenBarbero);

        // Verificar si el barbero está disponible
        if (barbero.isDisponible()) {
            holder.radioButton.setEnabled(true);  // Habilitar el RadioButton si está disponible
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.black)); // Fondo negro si está disponible
            // Eliminamos el filtro de gris de la imagen si está disponible
            holder.imagenBarbero.setColorFilter(null);  // Sin filtro de color
        } else {
            holder.radioButton.setEnabled(false);  // Deshabilitar el RadioButton si no está disponible
            holder.radioButton.setChecked(false);  // Desmarcar el RadioButton si no está disponible
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.gray)); // Fondo gris si no está disponible

            // Aplicar filtro blanco y negro a la imagen
            ColorMatrix colorMatrix = new ColorMatrix();
            colorMatrix.setSaturation(0);  // Esto convierte la imagen a blanco y negro
            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);
            holder.imagenBarbero.setColorFilter(filter);  // Aplica el filtro
        }

        // Si este barbero es el seleccionado, seleccionamos el radio button
        holder.radioButton.setChecked(position == selectedPosition);

        // Acción al seleccionar un barbero
        holder.radioButton.setOnClickListener(v -> {
            if (barbero.isDisponible()) {
                selectedPosition = position;  // Actualizamos la posición seleccionada
                notifyDataSetChanged(); // Refrescamos el RecyclerView
                Toast.makeText(context, "Seleccionaste a " + barbero.getNombre(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, barbero.getNombre() + " no está disponible", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return barberos.size();
    }

    // Método para obtener el barbero seleccionado
    public DtoBarberoDisponible getBarberoSeleccionado() {
        if (selectedPosition >= 0) {
            return barberos.get(selectedPosition);
        } else {
            return null;  // Ningún barbero seleccionado
        }
    }

    public class BarberoViewHolder extends RecyclerView.ViewHolder {
        TextView nombreTextView;
        RadioButton radioButton;
        ImageView imagenBarbero;

        public BarberoViewHolder(View itemView) {
            super(itemView);
            nombreTextView = itemView.findViewById(R.id.nombreBarbero);
            radioButton = itemView.findViewById(R.id.radioButton);
            imagenBarbero = itemView.findViewById(R.id.imagenBarbero);
        }
    }
}
