package com.diamond.appcliente.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.diamond.appcliente.R;
import com.diamond.appcliente.dto.usuario.UsuarioDto;
import java.util.List;

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder> {
    private final List<UsuarioDto> usuarios;

    public UsuarioAdapter(List<UsuarioDto> usuarios2) {
        this.usuarios = usuarios2;
    }

    public UsuarioViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new UsuarioViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usuario, parent, false));
    }

    public void onBindViewHolder(UsuarioViewHolder holder, int position) {
        UsuarioDto usuario = this.usuarios.get(position);
        holder.textNombre.setText(usuario.getNombre() + " " + usuario.getApellido());
        holder.textEmail.setText(usuario.getEmail());
        holder.textCelular.setText(usuario.getCelular());
        Glide.with(holder.itemView.getContext()).load(usuario.getUrlUsuario()).into(holder.imageUsuario);
    }

    public int getItemCount() {
        return this.usuarios.size();
    }

    public static class UsuarioViewHolder extends RecyclerView.ViewHolder {
        ImageView imageUsuario;
        TextView textCelular;
        TextView textEmail;
        TextView textNombre;

        public UsuarioViewHolder(View itemView) {
            super(itemView);
            this.textNombre = (TextView) itemView.findViewById(R.id.textNombre);
            this.textEmail = (TextView) itemView.findViewById(R.id.textEmail);
            this.textCelular = (TextView) itemView.findViewById(R.id.textCelular);
            this.imageUsuario = (ImageView) itemView.findViewById(R.id.imageUsuario);
        }
    }
}