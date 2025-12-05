package com.diamond.appcliente.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.diamond.appcliente.R;
import com.diamond.appcliente.dto.horariorango.HorarioRangoDto;

import java.util.List;

public class HorarioRangoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;  // Tipo de vista para el encabezado
    private static final int TYPE_ITEM = 1;    // Tipo de vista para los ítems normales (botones de horario)

    private Context context;
    private List<HorarioRangoDto> horarios;
    private int selectedPosition = -1;  // Variable global para toda la página

    private OnHorarioSelectedListener onHorarioSelectedListener; // Listener para comunicar la selección

    public interface OnHorarioSelectedListener {
        void onHorarioSelected(String horario, int horarioRangoId); // Ahora también pasamos el ID
    }

    public HorarioRangoAdapter(Context context, List<HorarioRangoDto> horarios, OnHorarioSelectedListener listener) {
        this.context = context;
        this.horarios = horarios;
        this.onHorarioSelectedListener = listener; // Inicializar el listener
    }

    // Este método es usado para diferenciar entre los tipos de vista: encabezado o ítem normal
    @Override
    public int getItemViewType(int position) {
        // Si el ítem es un encabezado (Turno)
        if (horarios.get(position).getTipoHorario().equals("Encabezado")) {
            return TYPE_HEADER;
        } else {
            return TYPE_ITEM;
        }
    }

    // Crea las vistas para cada tipo de ítem (encabezado o item normal)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            // Si es un encabezado, inflamos el layout del encabezado
            View view = LayoutInflater.from(context).inflate(R.layout.item_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            // Si es un ítem normal (botón de horario), inflamos el layout del horario
            View view = LayoutInflater.from(context).inflate(R.layout.item_horario_rango, parent, false);
            return new HorarioRangoViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            // Si es un encabezado
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            headerHolder.textHeader.setText(horarios.get(position).getRango()); // Establecemos el texto del encabezado
        } else {
            // Si es un botón de horario
            HorarioRangoDto horario = horarios.get(position);
            HorarioRangoViewHolder itemHolder = (HorarioRangoViewHolder) holder;

            // Cambiar el fondo del botón según si está seleccionado o no
            if (position == selectedPosition) {
                itemHolder.button.setBackgroundColor(Color.parseColor("#FF9800"));  // Naranja para el seleccionado
            } else {
                itemHolder.button.setBackgroundColor(Color.parseColor("#212121"));  // Color original para los no seleccionados
            }

            itemHolder.button.setText(horario.getRango());

            // Al hacer clic, actualizamos la posición seleccionada
            itemHolder.button.setOnClickListener(v -> {
                // Si el botón ya está seleccionado, lo deseleccionamos
                selectedPosition = position; // Actualizamos el botón seleccionado
                notifyDataSetChanged();  // Notificar que se actualice la vista

                // Enviar el horario seleccionado y el horarioRango_id a la actividad principal
                if (onHorarioSelectedListener != null) {
                    onHorarioSelectedListener.onHorarioSelected(horario.getRango(), horario.getHorarioRango_id()); // Llamamos al listener con el ID correcto
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return horarios.size();
    }

    // ViewHolder para los botones de horario
    public static class HorarioRangoViewHolder extends RecyclerView.ViewHolder {
        Button button;

        public HorarioRangoViewHolder(@NonNull View itemView) {
            super(itemView);
            button = itemView.findViewById(R.id.btnHorarioRango);  // Encontramos el botón
        }
    }

    // ViewHolder para los encabezados (Turnos)
    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView textHeader;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            textHeader = itemView.findViewById(R.id.textHeader);  // Encontramos el TextView para el encabezado
        }
    }
}
