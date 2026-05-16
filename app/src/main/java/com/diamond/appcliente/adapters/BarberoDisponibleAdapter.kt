package com.diamond.appcliente.adapters

import android.content.Context
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
        Glide.with(context).load(barbero.urlBarbero).into(holder.imagenBarbero)

        if (barbero.disponible) {
            holder.radioButton.isEnabled = true
            holder.itemView.setBackgroundColor(context.resources.getColor(R.color.black))
            holder.imagenBarbero.colorFilter = null
        } else {
            holder.radioButton.isEnabled = false
            holder.radioButton.isChecked = false
            holder.itemView.setBackgroundColor(context.resources.getColor(R.color.gray))
            val colorMatrix = ColorMatrix().apply { setSaturation(0f) }
            holder.imagenBarbero.colorFilter = ColorMatrixColorFilter(colorMatrix)
        }

        holder.radioButton.isChecked = position == selectedPosition

        holder.radioButton.setOnClickListener {
            if (barbero.disponible) {
                selectedPosition = position
                notifyDataSetChanged()
                Toast.makeText(context, "Seleccionaste a ${barbero.nombre}", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "${barbero.nombre} no está disponible", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount() = barberos.size

    fun getBarberoSeleccionado(): DtoBarberoDisponible? =
        if (selectedPosition >= 0) barberos[selectedPosition] else null

    inner class BarberoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreTextView: TextView = itemView.findViewById(R.id.nombreBarbero)
        val radioButton: RadioButton = itemView.findViewById(R.id.radioButton)
        val imagenBarbero: ImageView = itemView.findViewById(R.id.imagenBarbero)
    }
}
