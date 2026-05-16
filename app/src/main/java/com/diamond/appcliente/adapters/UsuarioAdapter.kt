package com.diamond.appcliente.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.diamond.appcliente.R
import com.diamond.appcliente.dto.usuario.UsuarioDto

class UsuarioAdapter(private val usuarios: List<UsuarioDto>) :
    RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_usuario, parent, false)
        return UsuarioViewHolder(view)
    }

    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
        val usuario = usuarios[position]
        holder.textNombre.text = "${usuario.nombre} ${usuario.apellido}"
        holder.textEmail.text = usuario.email
        holder.textCelular.text = usuario.celular
        Glide.with(holder.itemView.context).load(usuario.urlUsuario).into(holder.imageUsuario)
    }

    override fun getItemCount() = usuarios.size

    class UsuarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textNombre: TextView = itemView.findViewById(R.id.textNombre)
        val textEmail: TextView = itemView.findViewById(R.id.textEmail)
        val textCelular: TextView = itemView.findViewById(R.id.textCelular)
        val imageUsuario: ImageView = itemView.findViewById(R.id.imageUsuario)
    }
}
