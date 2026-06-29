package com.diamond.appcliente.adapters

import android.content.Context
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.diamond.appcliente.R
import com.diamond.appcliente.dto.barbero.DtoBarberoDisponible
import com.google.android.material.card.MaterialCardView

class BarberoDisponibleAdapter(
    private val barberos: List<DtoBarberoDisponible>,
    private val context: Context
) : RecyclerView.Adapter<BarberoDisponibleAdapter.BarberoViewHolder>() {

    private var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarberoViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_barbero_disponible, parent, false)
        return BarberoViewHolder(view)
    }

    override fun onBindViewHolder(holder: BarberoViewHolder, position: Int) {
        val barbero = barberos[position]
        holder.nombreTextView.text = barbero.nombre
        Glide.with(context).load(barbero.urlBarbero)
            .placeholder(R.drawable.perfil_default)
            .error(R.drawable.perfil_default)
            .into(holder.imagenBarbero)

        val isSelected = position == selectedPosition

        if (barbero.disponible) {
            holder.radioButton.isEnabled = true
            holder.radioButton.isChecked = isSelected
            holder.imagenBarbero.colorFilter = null
            holder.textViewDisponibilidad.visibility = View.GONE

            holder.cardView.setCardBackgroundColor(
                if (isSelected) Color.parseColor("#251800") else Color.parseColor("#1E1E1E")
            )
            holder.cardView.strokeColor =
                if (isSelected) Color.parseColor("#FF9800") else Color.parseColor("#333333")
        } else {
            holder.radioButton.isEnabled = false
            holder.radioButton.isChecked = false
            holder.imagenBarbero.colorFilter = ColorMatrixColorFilter(
                ColorMatrix().apply { setSaturation(0f) }
            )
            holder.textViewDisponibilidad.visibility = View.VISIBLE
            holder.cardView.setCardBackgroundColor(Color.parseColor("#141414"))
            holder.cardView.strokeColor = Color.parseColor("#222222")
        }

        // Todo el card es clickeable para seleccionar
        holder.cardView.setOnClickListener {
            if (barbero.disponible) {
                val prev = selectedPosition
                selectedPosition = holder.adapterPosition
                notifyItemChanged(prev)
                notifyItemChanged(selectedPosition)
                Toast.makeText(context, "Seleccionaste a ${barbero.nombre}", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "${barbero.nombre} no está disponible", Toast.LENGTH_SHORT).show()
            }
        }

        holder.radioButton.setOnClickListener {
            if (barbero.disponible) {
                val prev = selectedPosition
                selectedPosition = holder.adapterPosition
                notifyItemChanged(prev)
                notifyItemChanged(selectedPosition)
                Toast.makeText(context, "Seleccionaste a ${barbero.nombre}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount() = barberos.size

    fun getBarberoSeleccionado(): DtoBarberoDisponible? =
        if (selectedPosition >= 0) barberos[selectedPosition] else null

    inner class BarberoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: MaterialCardView = itemView as MaterialCardView
        val nombreTextView: TextView = itemView.findViewById(R.id.nombreBarbero)
        val radioButton: RadioButton = itemView.findViewById(R.id.radioButton)
        val imagenBarbero: ImageView = itemView.findViewById(R.id.imagenBarbero)
        val textViewDisponibilidad: TextView = itemView.findViewById(R.id.textViewDisponibilidad)
    }
}
