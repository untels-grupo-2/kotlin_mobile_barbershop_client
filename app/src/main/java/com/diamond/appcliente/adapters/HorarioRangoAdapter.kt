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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HorarioRangoAdapter(
    private val context: Context,
    private val horarios: List<HorarioRangoDto>,
    private val listener: OnHorarioSelectedListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface OnHorarioSelectedListener {
        fun onHorarioSelected(horario: String?, horarioRangoId: Int)
    }

    private var selectedPosition = -1
    private var selectedDate: String = ""

    fun updateSelectedDate(date: String) {
        selectedDate = date
        notifyDataSetChanged()
    }

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
            val isPast = isPastSlot(horario.rango)
            val isSelected = position == selectedPosition && !isPast

            if (isPast) {
                holder.button.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#111111"))
                holder.button.strokeColor = ColorStateList.valueOf(Color.parseColor("#2A2A2A"))
                holder.button.strokeWidth = 1
                holder.button.setTextColor(Color.parseColor("#555555"))
                holder.button.isEnabled = false
                holder.button.alpha = 0.6f
            } else {
                holder.button.isEnabled = true
                holder.button.alpha = 1.0f
                holder.button.setTextColor(Color.parseColor("#FFFFFF"))
                holder.button.backgroundTintList = ColorStateList.valueOf(
                    if (isSelected) Color.parseColor("#251800") else Color.parseColor("#1E1E1E")
                )
                holder.button.strokeColor = ColorStateList.valueOf(
                    if (isSelected) Color.parseColor("#FF9800") else Color.parseColor("#444444")
                )
                holder.button.strokeWidth = if (isSelected) 3 else 1
            }

            val displayText = horario.rango?.replace("12 AM", "12 PM") ?: ""
            holder.button.text = displayText

            holder.button.setOnClickListener {
                if (!isPast) {
                    selectedPosition = holder.adapterPosition
                    notifyDataSetChanged()
                    listener.onHorarioSelected(horario.rango, horario.horarioRango_id)
                }
            }
        }
    }

    override fun getItemCount() = horarios.size

    private fun isPastSlot(rango: String?): Boolean {
        if (rango == null) return false
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        if (selectedDate.isEmpty() || selectedDate != todayStr) return false

        val startStr = rango.split(" - ").firstOrNull()?.trim() ?: return false
        val parts = startStr.split(" ")
        if (parts.size < 2) return false

        var hour = parts[0].toIntOrNull() ?: return false
        val period = parts[1].uppercase(Locale.US)

        when {
            period == "PM" && hour != 12 -> hour += 12
            period == "AM" && hour == 12 -> hour = 12 // backend "12 AM" = noon
        }

        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return hour <= currentHour
    }

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
