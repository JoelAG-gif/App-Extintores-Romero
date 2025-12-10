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

public class ProductoAdminAdapter extends RecyclerView.Adapter<ProductoAdminAdapter.ViewHolder> {
    private Context context;
    private List<Producto> productosList;
    private OnProductoClickListener listener;

    public interface OnProductoClickListener {
        void onProductoClick(Producto producto);
    }

    public ProductoAdminAdapter(Context context, List<Producto> productosList, OnProductoClickListener listener) {
        this.context = context;
        this.productosList = productosList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_producto_admin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Producto producto = productosList.get(position);

        holder.tvNombre.setText(producto.getNombre());
        holder.tvCapacidad.setText("Capacidad: " + producto.getCapacidad());
        holder.tvPrecio.setText("Precio: S/ " + String.format("%.2f", producto.getPrecio()));
        holder.tvStock.setText("Stock: " + producto.getStock());
        holder.tvCategoria.setText("Categoría: " + producto.getCategoria());

        // Cargar imagen
        String imageUrl = ApiConfig.IMAGE_URL + producto.getImagenUrl();
        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .into(holder.ivProducto);

        // Color según stock
        if (producto.isAgotado()) {
            holder.tvStock.setTextColor(Color.RED);
            holder.cardView.setCardBackgroundColor(Color.parseColor("#FFCDD2"));
        } else if (producto.getStock() < 5) {
            holder.tvStock.setTextColor(Color.parseColor("#FF6F00"));
            holder.cardView.setCardBackgroundColor(Color.parseColor("#FFF9C4"));
        } else {
            holder.tvStock.setTextColor(Color.parseColor("#2E7D32"));
            holder.cardView.setCardBackgroundColor(Color.parseColor("#C8E6C9"));
        }

        holder.itemView.setOnClickListener(v -> listener.onProductoClick(producto));
    }

    @Override
    public int getItemCount() {
        return productosList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView ivProducto;
        TextView tvNombre, tvCapacidad, tvPrecio, tvStock, tvCategoria;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            ivProducto = itemView.findViewById(R.id.ivProducto);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvCapacidad = itemView.findViewById(R.id.tvCapacidad);
            tvPrecio = itemView.findViewById(R.id.tvPrecio);
            tvStock = itemView.findViewById(R.id.tvStock);
            tvCategoria = itemView.findViewById(R.id.tvCategoria);
        }
    }
}