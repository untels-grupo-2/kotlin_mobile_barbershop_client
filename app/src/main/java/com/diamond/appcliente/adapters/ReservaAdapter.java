package com.diamond.appcliente.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.diamond.appcliente.R;
import com.diamond.appcliente.dto.reserva.ReservaResponse;

import java.util.List;

public class ReservaAdapter extends RecyclerView.Adapter<ReservaAdapter.ReservaViewHolder> {

    private List<ReservaResponse> reservas;
    private Context context;

    public ReservaAdapter(List<ReservaResponse> reservas, Context context) {
        this.reservas = reservas;
        this.context = context;
    }

    @Override
    public ReservaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reserva, parent, false);
        return new ReservaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReservaViewHolder holder, int position) {
        ReservaResponse reserva = reservas.get(position);
        holder.reservaId.setText(String.valueOf(reserva.getReservaId()));
        holder.barberoNombre.setText(reserva.getBarberoNombre());
        holder.horarioRango.setText(reserva.getHorarioRango());
        holder.estado.setText(reserva.getEstado());
    }

    @Override
    public int getItemCount() {
        return reservas.size();
    }

    public class ReservaViewHolder extends RecyclerView.ViewHolder {

        TextView reservaId, barberoNombre, horarioRango, estado;

        public ReservaViewHolder(View itemView) {
            super(itemView);
            reservaId = itemView.findViewById(R.id.reservaId);
            barberoNombre = itemView.findViewById(R.id.barberoNombre);
            horarioRango = itemView.findViewById(R.id.horarioRango);
        }
    }
}
