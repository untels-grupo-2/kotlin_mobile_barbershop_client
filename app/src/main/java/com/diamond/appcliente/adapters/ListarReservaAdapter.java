package com.diamond.appcliente.adapters;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.diamond.appcliente.R;
import com.diamond.appcliente.actividades.SubirComprobanteActivity;
import com.diamond.appcliente.dto.reserva.ReservaResponse;

public class ListarReservaAdapter extends ListAdapter<ReservaResponse, ListarReservaAdapter.ListarReservaViewHolder> {
    public ListarReservaAdapter() {
        super(new DiffUtil.ItemCallback<ReservaResponse>() {
            public boolean areItemsTheSame(ReservaResponse oldItem, ReservaResponse newItem) {
                return oldItem.getReservaId().equals(newItem.getReservaId());
            }

            public boolean areContentsTheSame(ReservaResponse oldItem, ReservaResponse newItem) {
                return oldItem.equals(newItem);
            }
        });
    }

    @Override
    public ListarReservaAdapter.ListarReservaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ListarReservaAdapter.ListarReservaViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_listar_reserva, parent, false));
    }

    @Override
    public void onBindViewHolder(ListarReservaAdapter.ListarReservaViewHolder holder, int position) {
        ReservaResponse reserva = getItem(position);
        if (reserva != null) {
            holder.barberoNombre.setText("Barbero: " + reserva.getBarberoNombre());
            holder.servicioNombre.setText("Servicio: " + reserva.getServicioNombre());
            holder.horarioRango.setText("Horario: " + reserva.getHorarioRango());
            holder.estado.setText("Estado: " + reserva.getEstado());
            holder.fechaReserva.setText("Fecha: " + reserva.getFechaReserva());

            if ("REALIZADA".equals(reserva.getEstado())) {
                holder.btnSubirComprobante.setEnabled(false);
                holder.btnSubirComprobante.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.gray));
            } else {
                holder.btnSubirComprobante.setEnabled(true);
                holder.btnSubirComprobante.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.custom_orange));
            }

            // Aquí simplificamos el manejo de los clicks
            holder.whatsappIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/" + "+51963066495" + "?text=" + Uri.encode("Hola, me gustaría consultar acerca de mi reserva para el servicio de " + reserva.getServicioNombre() + " con el barbero " + reserva.getBarberoNombre() + " en el horario " + reserva.getHorarioRango() + " en la fecha " + reserva.getFechaReserva())));
                    intent.setPackage("com.whatsapp");
                    v.getContext().startActivity(intent);
                }
            });

            holder.btnSubirComprobante.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), SubirComprobanteActivity.class);
                    intent.putExtra("reservaId", reserva.getReservaId());
                    v.getContext().startActivity(intent);
                }
            });
        }
    }

    public static class ListarReservaViewHolder extends RecyclerView.ViewHolder {
        private TextView barberoNombre;
        private Button btnSubirComprobante;
        private TextView estado;
        private TextView fechaReserva;
        private TextView horarioRango;
        private TextView servicioNombre;
        private ImageView whatsappIcon;

        public ListarReservaViewHolder(View itemView) {
            super(itemView);
            this.barberoNombre = itemView.findViewById(R.id.tv_barbero_nombre);
            this.servicioNombre = itemView.findViewById(R.id.tv_servicio_nombre);
            this.horarioRango = itemView.findViewById(R.id.tv_horario_rango);
            this.estado = itemView.findViewById(R.id.tv_estado);
            this.fechaReserva = itemView.findViewById(R.id.tv_fecha_reserva);
            this.whatsappIcon = itemView.findViewById(R.id.whatsappIcon);
            this.btnSubirComprobante = itemView.findViewById(R.id.btnSubirComprobante);
        }
    }
}
