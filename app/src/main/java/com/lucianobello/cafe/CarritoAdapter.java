package com.lucianobello.cafe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class CarritoAdapter extends RecyclerView.Adapter<CarritoAdapter.CarritoViewHolder> {

    private List<Carrito> carritoList;
    private OnDeleteClickListener onDeleteClickListener;
    private Context context;

    public interface OnDeleteClickListener {
        void onDeleteClick(int carritoId);
    }

    public CarritoAdapter(List<Carrito> carritoList, OnDeleteClickListener onDeleteClickListener, Context context) {
        this.carritoList = carritoList;
        this.onDeleteClickListener = onDeleteClickListener;
        this.context = context;  // Recibir el contexto
    }

    @Override
    public CarritoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_carrito, parent, false);
        return new CarritoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarritoViewHolder holder, int position) {
        Carrito carrito = carritoList.get(position);

        // Establecer los datos
        holder.nombreProducto.setText(carrito.getNombreProducto());
        holder.precioProducto.setText(String.format("$ %.2f", carrito.getPrecioProducto()));
        holder.cantidadProducto.setText("Cantidad: " + carrito.getCantidad());
        holder.totalUnidad.setText(String.format("$ %.2f", carrito.getPrecioProducto() * carrito.getCantidad())); // Total por unidad

        // Obtener el nombre de la imagen desde el carrito
        String nombreImagen = carrito.getImagenProducto();
        Log.d("CarritoAdapter", "Nombre de la imagen: " + nombreImagen);

        // Verificar si el nombre de la imagen es válido
        if (nombreImagen != null && !nombreImagen.isEmpty()) {
            // Usar Glide para cargar la imagen desde los recursos
            int idImagen = context.getResources().getIdentifier(nombreImagen, "drawable", context.getPackageName());

            // Si no se encuentra, asigna una imagen por defecto
            if (idImagen == 0) {
                idImagen = R.drawable.default_image;  // Imagen default
            }

            // Cargar la imagen usando Glide
            Glide.with(context)
                    .load(idImagen)
                    .into(holder.imagenProducto);
        } else {
            // Si no hay imagen o el nombre es inválido, usar la imagen por defecto
            Glide.with(context)
                    .load(R.drawable.default_image)
                    .into(holder.imagenProducto);
        }

        // Botón de eliminar producto
        holder.eliminarProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDeleteClickListener.onDeleteClick(carrito.getId()); // Eliminar el producto
            }
        });
    }

    @Override
    public int getItemCount() {
        return carritoList.size();
    }

    public static class CarritoViewHolder extends RecyclerView.ViewHolder {
        public TextView nombreProducto, precioProducto, cantidadProducto, totalUnidad;
        public ImageView imagenProducto;
        public Button eliminarProducto;

        public CarritoViewHolder(View itemView) {
            super(itemView);
            nombreProducto = itemView.findViewById(R.id.text_nombre_producto_carrito);
            precioProducto = itemView.findViewById(R.id.text_precio_producto_carrito);
            cantidadProducto = itemView.findViewById(R.id.text_cantidad_producto_carrito);
            totalUnidad = itemView.findViewById(R.id.text_total_unidad);
            imagenProducto = itemView.findViewById(R.id.image_producto);
            eliminarProducto = itemView.findViewById(R.id.button_eliminar_producto);
        }
    }
}
