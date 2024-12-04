package com.lucianobello.cafe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder> {

    private ArrayList<Producto> productos;
    private Context context;
    private DBHelper dbHelper;

    public ProductoAdapter(ArrayList<Producto> productos, Context context) {
        this.productos = productos;
        this.context = context;
        dbHelper = new DBHelper(context);
    }

    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_producto, parent, false);
        return new ProductoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoViewHolder holder, int position) {
        Producto producto = productos.get(position);

        // Establecer los datos del producto
        holder.nombreProducto.setText(producto.getNombre());
        holder.precioProducto.setText(String.format("$ %.2f", producto.getPrecio()));
        holder.imagenProducto.setImageResource(producto.getImagen());

        // Botón para agregar al carrito
        holder.agregarCarritoButton.setOnClickListener(v -> {
            // Establecer una cantidad predeterminada, por ejemplo 1
            int cantidad = 1;

            // Agregar producto al carrito usando el ID del producto y la cantidad
            dbHelper.addToCarrito(producto.getId(), cantidad);

            // Mostrar notificación de que el producto fue agregado
            Toast.makeText(context, producto.getNombre() + " ha sido agregado al carrito.", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    public static class ProductoViewHolder extends RecyclerView.ViewHolder {
        public TextView nombreProducto, precioProducto;
        public ImageView imagenProducto;
        public Button agregarCarritoButton;

        public ProductoViewHolder(View itemView) {
            super(itemView);
            nombreProducto = itemView.findViewById(R.id.text_nombre_producto);
            precioProducto = itemView.findViewById(R.id.text_precio_producto);
            imagenProducto = itemView.findViewById(R.id.image_producto);
            agregarCarritoButton = itemView.findViewById(R.id.button_agregar_carrito);
        }
    }
}
