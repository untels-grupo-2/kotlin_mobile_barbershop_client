package com.diamond.appcliente.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.diamond.appcliente.R
import com.diamond.appcliente.dto.barbero.BarberoDto

class BarberoAdapter(
    private val barberos: List<BarberoDto>,
    private val listener: OnBarberoClickListener
) : RecyclerView.Adapter<BarberoAdapter.BarberoViewHolder>() {

    interface OnBarberoClickListener {
        fun onBarberoSeleccionado(barbero: BarberoDto, nombreBarbero: String?)
        fun onActualizar(barbero: BarberoDto)
        fun onEliminar(barbero: BarberoDto)
    }

    private var barberoSeleccionado = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarberoViewHolder {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.item_barbero, parent, false)
        return BarberoViewHolder(vista)
    }

    override fun onBindViewHolder(holder: BarberoViewHolder, position: Int) {
        val barbero = barberos[position]
        holder.textNombre.text = barbero.nombre
        Glide.with(holder.itemView.context).load(barbero.urlBarbero).into(holder.imageBarbero)

        holder.radioButton.isChecked = holder.adapterPosition == barberoSeleccionado

        holder.radioButton.setOnClickListener {
            barberoSeleccionado = holder.adapterPosition
            listener.onBarberoSeleccionado(barbero, barbero.nombre)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount() = barberos.size

    class BarberoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textNombre: TextView = itemView.findViewById(R.id.textNombre)
        val imageBarbero: ImageView = itemView.findViewById(R.id.imageBarbero)
        val radioButton: RadioButton = itemView.findViewById(R.id.radioButton)
    }
}
