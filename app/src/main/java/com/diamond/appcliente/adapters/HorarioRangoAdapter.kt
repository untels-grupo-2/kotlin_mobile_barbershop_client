package com.diamond.appcliente.adapters

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.diamond.appcliente.R
import com.diamond.appcliente.dto.horariorango.HorarioRangoDto
import com.google.android.material.button.MaterialButton

class HorarioRangoAdapter(
    private val context: Context,
    private val horarios: List<HorarioRangoDto>,
    private val listener: OnHorarioSelectedListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface OnHorarioSelectedListener {
        fun onHorarioSelected(horario: String?, horarioRangoId: Int)
    }

    private var selectedPosition = -1

    override fun getItemViewType(position: Int) =
        if (horarios[position].tipoHorario == "Encabezado") TYPE_HEADER else TYPE_ITEM

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_HEADER) {
            HeaderViewHolder(LayoutInflater.from(context).inflate(R.layout.item_header, parent, false))
        } else {
            HorarioRangoViewHolder(LayoutInflater.from(context).inflate(R.layout.item_horario_rango, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HeaderViewHolder) {
            holder.textHeader.text = horarios[position].rango
        } else if (holder is HorarioRangoViewHolder) {
            val horario = horarios[position]
            val isSelected = position == selectedPosition

            holder.button.backgroundTintList = ColorStateList.valueOf(
                if (isSelected) Color.parseColor("#251800") else Color.parseColor("#1E1E1E")
            )
            holder.button.strokeColor = ColorStateList.valueOf(
                if (isSelected) Color.parseColor("#FF9800") else Color.parseColor("#444444")
            )
            holder.button.strokeWidth = if (isSelected) 3 else 1

            val displayText = horario.rango?.replace("12 AM", "12 PM") ?: ""
            holder.button.text = displayText

            holder.button.setOnClickListener {
                selectedPosition = holder.adapterPosition
                notifyDataSetChanged()
                listener.onHorarioSelected(horario.rango, horario.horarioRango_id)
            }
        }
    }

    override fun getItemCount() = horarios.size

    class HorarioRangoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val button: MaterialButton = itemView.findViewById(R.id.btnHorarioRango)
    }

    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textHeader: TextView = itemView.findViewById(R.id.textHeader)
    }

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1
    }
}
