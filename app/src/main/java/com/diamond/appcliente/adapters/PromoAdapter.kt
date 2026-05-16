package com.diamond.appcliente.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.diamond.appcliente.R

class PromoAdapter(private val promoImageList: List<Int>) :
    RecyclerView.Adapter<PromoAdapter.PromoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_promo, parent, false)
        return PromoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PromoViewHolder, position: Int) {
        holder.promoImage.setImageResource(promoImageList[position])
    }

    override fun getItemCount() = promoImageList.size

    class PromoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val promoImage: ImageView = itemView.findViewById(R.id.promoImage)
    }
}
