package com.diamond.appcliente.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.diamond.appcliente.R
import com.diamond.appcliente.dto.reserva.ReservaResponse

class ReservaAdapter(
    private val reservas: List<ReservaResponse>,
    private val context: Context
) : RecyclerView.Adapter<ReservaAdapter.ReservaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservaViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_reserva, parent, false)
        return ReservaViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReservaViewHolder, position: Int) {
        val reserva = reservas[position]
        holder.reservaId.text = reserva.reservaId.toString()
        holder.barberoNombre.text = reserva.barberoNombre
        holder.horarioRango.text = reserva.horarioRango
        holder.estado.text = reserva.estado
    }

    override fun getItemCount() = reservas.size

    inner class ReservaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val reservaId: TextView = itemView.findViewById(R.id.reservaId)
        val barberoNombre: TextView = itemView.findViewById(R.id.barberoNombre)
        val horarioRango: TextView = itemView.findViewById(R.id.horarioRango)
        val estado: TextView = itemView.findViewById(R.id.estado)
    }
}
