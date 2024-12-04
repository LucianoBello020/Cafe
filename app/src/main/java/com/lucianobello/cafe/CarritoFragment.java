package com.lucianobello.cafe;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class CarritoFragment extends Fragment {
    private RecyclerView recyclerView;
    private CarritoAdapter carritoAdapter;
    private ArrayList<Carrito> carritoList;
    private DBHelper dbHelper;
    private TextView totalTextView;
    private Button buttonPagar;

    // Firebase DatabaseReference
    private DatabaseReference databaseReference;

    // MQTT Handler
    private MqttHandler mqttHandler;

    // Configuración MQTT
    private static final String MQTT_BROKER_URL = "tcp://broker.emqx.io:1883"; // URL del broker
    private static final String MQTT_TOPIC = "cafe/pago";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflar el layout
        View view = inflater.inflate(R.layout.fragment_carrito, container, false);

        // Inicializar el RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view_carrito);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inicializar DBHelper
        dbHelper = new DBHelper(getContext());

        // Inicializar el TextView para mostrar el total
        totalTextView = view.findViewById(R.id.text_view_total);

        // Inicializar el botón de pagar
        buttonPagar = view.findViewById(R.id.button_pagar);

        // Inicializar Firebase DatabaseReference
        databaseReference = FirebaseDatabase.getInstance().getReference("Pagos");

        // Inicializar MQTT Handler
        mqttHandler = new MqttHandler();
        mqttHandler.connect(MQTT_BROKER_URL, "AndroidClient" + System.currentTimeMillis());

        // Cargar los productos del carrito desde la base de datos
        carritoList = obtenerProductosCarrito();

        // Configurar el adapter
        carritoAdapter = new CarritoAdapter(carritoList, new CarritoAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClick(int carritoId) {
                eliminarProductoDelCarrito(carritoId);
            }
        }, getContext());

        recyclerView.setAdapter(carritoAdapter);

        // Mostrar el total del carrito
        mostrarTotalCarrito();

        // Configurar el botón de pago
        buttonPagar.setOnClickListener(v -> realizarCompra());

        return view;
    }

    private ArrayList<Carrito> obtenerProductosCarrito() {
        ArrayList<Carrito> productosCarrito = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DBHelper.TABLE_CARRITO, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CARRITO_ID));
                int productoId = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CARRITO_PRODUCTO_ID));
                int cantidad = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CARRITO_CANTIDAD));

                // Recuperar los detalles del producto
                Cursor productoCursor = db.rawQuery("SELECT * FROM " + DBHelper.TABLE_PRODUCTOS + " WHERE " +
                        DBHelper.COLUMN_PRODUCTO_ID + " = ?", new String[]{String.valueOf(productoId)});
                if (productoCursor.moveToFirst()) {
                    String nombre = productoCursor.getString(productoCursor.getColumnIndexOrThrow(DBHelper.COLUMN_PRODUCTO_NOMBRE));
                    double precio = productoCursor.getDouble(productoCursor.getColumnIndexOrThrow(DBHelper.COLUMN_PRODUCTO_PRECIO));
                    String nombreImagen = productoCursor.getString(productoCursor.getColumnIndexOrThrow(DBHelper.COLUMN_PRODUCTO_IMAGEN));

                    int imagenId = getContext().getResources().getIdentifier(nombreImagen, "drawable", getContext().getPackageName());

                    Carrito carrito = new Carrito(id, nombre, precio, imagenId, cantidad);
                    productosCarrito.add(carrito);
                }
                productoCursor.close();
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return productosCarrito;
    }

    private void eliminarProductoDelCarrito(int carritoId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DBHelper.TABLE_CARRITO, DBHelper.COLUMN_CARRITO_ID + " = ?", new String[]{String.valueOf(carritoId)});
        db.close();

        carritoList.clear();
        carritoList.addAll(obtenerProductosCarrito());
        carritoAdapter.notifyDataSetChanged();
        mostrarTotalCarrito();
    }

    private double calcularTotalCarrito() {
        double total = 0;
        for (Carrito carrito : carritoList) {
            total += carrito.getPrecioProducto() * carrito.getCantidad();
        }
        return total;
    }

    private void mostrarTotalCarrito() {
        double total = calcularTotalCarrito();
        totalTextView.setText("Total: $" + String.format("%.2f", total));
    }

    private void realizarCompra() {
        double total = calcularTotalCarrito();
        ArrayList<String> productosComprados = new ArrayList<>();
        for (Carrito carrito : carritoList) {
            String productoDetalle = carrito.getNombreProducto() + " x" + carrito.getCantidad();
            productosComprados.add(productoDetalle);
        }

        // Crear el mensaje JSON para MQTT
        String mensajeMQTT = "{ \"productos\": " + productosComprados.toString() + ", \"total\": " + total + " }";
        mqttHandler.publish(MQTT_TOPIC, mensajeMQTT);

        // Enviar a Firebase
        String idPago = databaseReference.push().getKey();
        if (idPago != null) {
            databaseReference.child(idPago).setValue(new Pago(productosComprados, total));
            Toast.makeText(getContext(), "Pago enviado. Total: $" + String.format("%.2f", total), Toast.LENGTH_LONG).show();
            limpiarCarrito();
        }
    }

    private void limpiarCarrito() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DBHelper.TABLE_CARRITO, null, null);
        db.close();

        carritoList.clear();
        carritoAdapter.notifyDataSetChanged();
        mostrarTotalCarrito();
    }

    public static class Pago {
        public ArrayList<String> productosComprados;
        public double totalPagado;

        public Pago() {}

        public Pago(ArrayList<String> productosComprados, double totalPagado) {
            this.productosComprados = productosComprados;
            this.totalPagado = totalPagado;
        }
    }
}
