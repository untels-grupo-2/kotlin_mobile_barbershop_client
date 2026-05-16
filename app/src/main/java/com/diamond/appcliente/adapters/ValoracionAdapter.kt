package com.diamond.appcliente.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.diamond.appcliente.R
import com.diamond.appcliente.dto.valoracion.ValoracionResponse

class ValoracionAdapter(
    private val valoraciones: List<ValoracionResponse>,
    private val listener: OnValoracionClickListener
) : RecyclerView.Adapter<ValoracionAdapter.ValoracionViewHolder>() {

    interface OnValoracionClickListener {
        fun onValoracionSeleccionada(valoracion: ValoracionResponse)
        fun onEnviarValoracion(valoracion: ValoracionResponse)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ValoracionViewHolder {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.item_valoracion, parent, false)
        return ValoracionViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ValoracionViewHolder, position: Int) {
        val valoracion = valoraciones[position]
        holder.textValoracion.text = valoracion.valoracion.toString()
        holder.textComentario.text = valoracion.mensaje
        holder.textUsuario.text = valoracion.usuario_nombre
        holder.checkBoxUtil.isChecked = valoracion.util == true

        holder.btnEnviar.setOnClickListener {
            valoracion.util = holder.checkBoxUtil.isChecked
            listener.onEnviarValoracion(valoracion)
        }
    }

    override fun getItemCount() = valoraciones.size

    class ValoracionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textValoracion: TextView = itemView.findViewById(R.id.textValoracion)
        val textComentario: TextView = itemView.findViewById(R.id.textComentario)
        val textUsuario: TextView = itemView.findViewById(R.id.textUsuario)
        val checkBoxUtil: CheckBox = itemView.findViewById(R.id.checkBoxUtil)
        val btnEnviar: Button = itemView.findViewById(R.id.btnEnviarValoracion)
    }
}
