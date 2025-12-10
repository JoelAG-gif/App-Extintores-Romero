package com.romero.extintores;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ExtintorAdapter extends RecyclerView.Adapter<ExtintorAdapter.ViewHolder> {
    private Context context;
    private List<Extintor> extintoresList;

    public ExtintorAdapter(Context context, List<Extintor> extintoresList) {
        this.context = context;
        this.extintoresList = extintoresList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_extintor, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Extintor extintor = extintoresList.get(position);

        holder.tvTipo.setText(extintor.getTipo());
        holder.tvCapacidad.setText("Capacidad: " + extintor.getCapacidad());
        holder.tvUbicacion.setText("Ubicación: " + extintor.getUbicacion());
        holder.tvVencimiento.setText("Vence: " + extintor.getFechaVencimiento());

        // Cargar imagen
        String imageUrl = ApiConfig.IMAGE_URL + extintor.getImagenUrl();
        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .into(holder.ivExtintor);

        // Cambiar color según estado
        if (extintor.isVencido()) {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#FFCDD2"));
            holder.tvEstado.setText("❌ VENCIDO");
            holder.tvEstado.setTextColor(Color.RED);
        } else if (extintor.isAlerta()) {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#FFF9C4"));
            holder.tvEstado.setText("⚠️ Por vencer (" + extintor.getDiasRestantes() + " días)");
            holder.tvEstado.setTextColor(Color.parseColor("#FF6F00"));
        } else {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#C8E6C9"));
            holder.tvEstado.setText("✅ Vigente (" + extintor.getDiasRestantes() + " días)");
            holder.tvEstado.setTextColor(Color.parseColor("#2E7D32"));
        }
    }

    @Override
    public int getItemCount() {
        return extintoresList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView ivExtintor;
        TextView tvTipo, tvCapacidad, tvUbicacion, tvVencimiento, tvEstado;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            ivExtintor = itemView.findViewById(R.id.ivExtintor);
            tvTipo = itemView.findViewById(R.id.tvTipo);
            tvCapacidad = itemView.findViewById(R.id.tvCapacidad);
            tvUbicacion = itemView.findViewById(R.id.tvUbicacion);
            tvVencimiento = itemView.findViewById(R.id.tvVencimiento);
            tvEstado = itemView.findViewById(R.id.tvEstado);
        }
    }
}