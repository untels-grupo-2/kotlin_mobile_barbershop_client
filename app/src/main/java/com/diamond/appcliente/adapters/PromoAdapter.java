package com.diamond.appcliente.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.diamond.appcliente.R;

import java.util.List;

public class PromoAdapter extends RecyclerView.Adapter<PromoAdapter.PromoViewHolder> {

    private List<Integer> promoImageList;

    public PromoAdapter(List<Integer> promoImageList) {
        this.promoImageList = promoImageList;
    }

    @NonNull
    @Override
    public PromoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_promo, parent, false);
        return new PromoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PromoViewHolder holder, int position) {
        holder.promoImage.setImageResource(promoImageList.get(position));
    }

    @Override
    public int getItemCount() {
        return promoImageList.size();
    }

    public static class PromoViewHolder extends RecyclerView.ViewHolder {

        ImageView promoImage;

        public PromoViewHolder(View itemView) {
            super(itemView);
            promoImage = itemView.findViewById(R.id.promoImage);
        }
    }
}
