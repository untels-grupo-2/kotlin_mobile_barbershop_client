package com.diamond.appcliente.adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.diamond.appcliente.R
import com.diamond.appcliente.actividades.SubirComprobanteActivity
import com.diamond.appcliente.dto.reserva.ReservaResponse

class ListarReservaAdapter : ListAdapter<ReservaResponse, ListarReservaAdapter.ListarReservaViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListarReservaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_listar_reserva, parent, false)
        return ListarReservaViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListarReservaViewHolder, position: Int) {
        val reserva = getItem(position) ?: return
        holder.barberoNombre.text = "Barbero: ${reserva.barberoNombre}"
        holder.servicioNombre.text = "Servicio: ${reserva.servicioNombre}"
        holder.horarioRango.text = "Horario: ${reserva.horarioRango}"
        holder.estado.text = "Estado: ${reserva.estado}"
        holder.fechaReserva.text = "Fecha: ${reserva.fechaReserva}"

        val context = holder.itemView.context
        if (reserva.estado == "REALIZADA") {
            holder.btnSubirComprobante.isEnabled = false
            holder.btnSubirComprobante.setBackgroundColor(context.resources.getColor(R.color.gray))
        } else {
            holder.btnSubirComprobante.isEnabled = true
            holder.btnSubirComprobante.setBackgroundColor(context.resources.getColor(R.color.custom_orange))
        }

        holder.whatsappIcon.setOnClickListener {
            val mensaje = "Hola, me gustaría consultar acerca de mi reserva para el servicio de " +
                "${reserva.servicioNombre} con el barbero ${reserva.barberoNombre} " +
                "en el horario ${reserva.horarioRango} en la fecha ${reserva.fechaReserva}"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/+51963066495?text=${Uri.encode(mensaje)}"))
            intent.setPackage("com.whatsapp")
            context.startActivity(intent)
        }

        holder.btnSubirComprobante.setOnClickListener {
            val intent = Intent(context, SubirComprobanteActivity::class.java)
            intent.putExtra("reservaId", reserva.reservaId)
            context.startActivity(intent)
        }
    }

    class ListarReservaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val barberoNombre: TextView = itemView.findViewById(R.id.tv_barbero_nombre)
        val servicioNombre: TextView = itemView.findViewById(R.id.tv_servicio_nombre)
        val horarioRango: TextView = itemView.findViewById(R.id.tv_horario_rango)
        val estado: TextView = itemView.findViewById(R.id.tv_estado)
        val fechaReserva: TextView = itemView.findViewById(R.id.tv_fecha_reserva)
        val whatsappIcon: ImageView = itemView.findViewById(R.id.whatsappIcon)
        val btnSubirComprobante: Button = itemView.findViewById(R.id.btnSubirComprobante)
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ReservaResponse>() {
            override fun areItemsTheSame(oldItem: ReservaResponse, newItem: ReservaResponse) =
                oldItem.reservaId == newItem.reservaId

            override fun areContentsTheSame(oldItem: ReservaResponse, newItem: ReservaResponse) =
                oldItem == newItem
        }
    }
}
