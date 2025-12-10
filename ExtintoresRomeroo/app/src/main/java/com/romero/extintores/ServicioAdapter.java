package com.romero.extintores;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ServicioAdapter extends RecyclerView.Adapter<ServicioAdapter.ViewHolder> {
    private Context context;
    private List<Servicio> serviciosList;
    private OnServicioClickListener listener;

    public interface OnServicioClickListener {
        void onServicioClick(Servicio servicio);
    }

    public ServicioAdapter(Context context, List<Servicio> serviciosList, OnServicioClickListener listener) {
        this.context = context;
        this.serviciosList = serviciosList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_servicio, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Servicio servicio = serviciosList.get(position);

        holder.tvCliente.setText("Cliente: " + servicio.getClientName());
        holder.tvEmail.setText("Email: " + servicio.getClientEmail());
        holder.tvExtintor.setText("Extintor: " + servicio.getTipo() + " - " + servicio.getCapacidad());
        holder.tvUbicacion.setText("Ubicación: " + servicio.getUbicacion());
        holder.tvFechaServicio.setText("Fecha: " + servicio.getFechaServicio());
        holder.tvEstado.setText("Estado: " + servicio.getEstado());
        holder.tvTecnico.setText("Técnico: " + servicio.getTecnicoName());


        switch (servicio.getEstado()) {
            case "Pendiente":
                holder.cardView.setCardBackgroundColor(Color.parseColor("#FFF9C4"));
                holder.tvEstado.setText("⏳ Estado: " + servicio.getEstado());
                holder.tvEstado.setTextColor(Color.parseColor("#F57C00"));
                break;
            case "Asignado":
                holder.cardView.setCardBackgroundColor(Color.parseColor("#BBDEFB"));
                holder.tvEstado.setText("🔔 Estado: " + servicio.getEstado());
                holder.tvEstado.setTextColor(Color.parseColor("#1976D2"));
                break;
            case "En Proceso":
                holder.cardView.setCardBackgroundColor(Color.parseColor("#FFE0B2"));
                holder.tvEstado.setText("🔧 Estado: " + servicio.getEstado());
                holder.tvEstado.setTextColor(Color.parseColor("#E65100"));
                break;
            case "Completado":
                holder.cardView.setCardBackgroundColor(Color.parseColor("#C8E6C9"));
                holder.tvEstado.setText("✅ Estado: " + servicio.getEstado());
                holder.tvEstado.setTextColor(Color.parseColor("#388E3C"));
                break;
        }

        holder.itemView.setOnClickListener(v -> listener.onServicioClick(servicio));
    }

    @Override
    public int getItemCount() {
        return serviciosList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvCliente, tvEmail, tvExtintor, tvUbicacion, tvFechaServicio, tvEstado, tvTecnico;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            tvCliente = itemView.findViewById(R.id.tvCliente);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvExtintor = itemView.findViewById(R.id.tvExtintor);
            tvUbicacion = itemView.findViewById(R.id.tvUbicacion);
            tvFechaServicio = itemView.findViewById(R.id.tvFechaServicio);
            tvEstado = itemView.findViewById(R.id.tvEstado);
            tvTecnico = itemView.findViewById(R.id.tvTecnico);
        }
    }
}