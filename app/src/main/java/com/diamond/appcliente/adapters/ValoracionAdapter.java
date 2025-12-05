package com.diamond.appcliente.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.diamond.appcliente.R;
import com.diamond.appcliente.dto.valoracion.ValoracionResponse;

import java.util.List;

public class ValoracionAdapter extends RecyclerView.Adapter<ValoracionAdapter.ValoracionViewHolder> {

    public interface OnValoracionClickListener {
        void onValoracionSeleccionada(ValoracionResponse valoracion);
        void onEnviarValoracion(ValoracionResponse valoracion);
    }

    private final List<ValoracionResponse> valoraciones;
    private final OnValoracionClickListener listener;

    public ValoracionAdapter(List<ValoracionResponse> valoraciones, OnValoracionClickListener listener) {
        this.valoraciones = valoraciones;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ValoracionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_valoracion, parent, false);
        return new ValoracionViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ValoracionViewHolder holder, int position) {
        ValoracionResponse valoracion = valoraciones.get(position);
        holder.textValoracion.setText(String.valueOf(valoracion.getValoracion()));
        holder.textComentario.setText(valoracion.getMensaje());
        holder.textUsuario.setText(valoracion.getUsuario_nombre());

        // Obtener el estado del CheckBox para marcar si la valoración fue útil
        holder.checkBoxUtil.setChecked(valoracion.getUtil() != null && valoracion.getUtil());

        holder.btnEnviar.setOnClickListener(v -> {
            // Actualizar el valor de "util" con el estado del CheckBox
            valoracion.setUtil(holder.checkBoxUtil.isChecked());
            listener.onEnviarValoracion(valoracion);
        });
    }

    @Override
    public int getItemCount() {
        return valoraciones.size();
    }

    public static class ValoracionViewHolder extends RecyclerView.ViewHolder {

        TextView textValoracion, textComentario, textUsuario;
        CheckBox checkBoxUtil;
        Button btnEnviar;

        public ValoracionViewHolder(@NonNull View itemView) {
            super(itemView);
            textValoracion = itemView.findViewById(R.id.textValoracion);
            textComentario = itemView.findViewById(R.id.textComentario);
            textUsuario = itemView.findViewById(R.id.textUsuario);
            checkBoxUtil = itemView.findViewById(R.id.checkBoxUtil); // Referenciar el CheckBox
            btnEnviar = itemView.findViewById(R.id.btnEnviarValoracion);
        }
    }
}
