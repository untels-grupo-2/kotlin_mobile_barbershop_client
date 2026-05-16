package com.diamond.appcliente.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.diamond.appcliente.R
import com.diamond.appcliente.dto.servicio.ServicioDto

class ServicioAdapter(
    private var servicios: List<ServicioDto>,
    private val listener: OnServicioClickListener
) : RecyclerView.Adapter<ServicioAdapter.ServicioViewHolder>() {

    interface OnServicioClickListener {
        fun onAviso(servicio: ServicioDto, imagenUrl: String?)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServicioViewHolder {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.item_servicio, parent, false)
        return ServicioViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ServicioViewHolder, position: Int) {
        val servicio = servicios[position]
        holder.textNombre.text = servicio.nombre
        holder.textPrecio.text = "S/ ${servicio.precio}"
        holder.textDescripcion.text = servicio.descripcion
        Glide.with(holder.itemView.context).load(servicio.urlServicio).into(holder.imageServicio)
        holder.btnAviso.setOnClickListener { listener.onAviso(servicio, servicio.urlServicio) }
    }

    override fun getItemCount() = servicios.size

    fun updateServicios(nuevosServicios: List<ServicioDto>) {
        servicios = nuevosServicios
        notifyDataSetChanged()
    }

    class ServicioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textNombre: TextView = itemView.findViewById(R.id.textNombreServicio)
        val textPrecio: TextView = itemView.findViewById(R.id.textPrecioServicio)
        val textDescripcion: TextView = itemView.findViewById(R.id.textDescripcionServicio)
        val btnAviso: Button = itemView.findViewById(R.id.btnAviso)
        val imageServicio: ImageView = itemView.findViewById(R.id.imageServicio)
    }
}
