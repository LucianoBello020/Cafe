package com.lucianobello.cafe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class CrearCafeFragment extends Fragment {

    private EditText editTextNombreCafe;
    private Button btnAgregarCafe;
    private DBHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crear_cafe, container, false);

        editTextNombreCafe = view.findViewById(R.id.editTextNombreCafe);
        btnAgregarCafe = view.findViewById(R.id.btnAgregarCafe);
        dbHelper = new DBHelper(requireContext());

        btnAgregarCafe.setOnClickListener(v -> agregarCafePersonalizado());

        return view;
    }

    private void agregarCafePersonalizado() {
        String nombreCafe = editTextNombreCafe.getText().toString().trim();

        if (nombreCafe.isEmpty()) {
            Toast.makeText(getContext(), "Por favor, ingresa el nombre del café", Toast.LENGTH_SHORT).show();
        } else {
            // Agregar el café personalizado a la base de datos con un precio fijo de 5.55
            Producto productoPersonalizado = new Producto(0, nombreCafe, 5.55, R.drawable.cafe_personalizado);
            long idProducto = dbHelper.insertarProductoPersonalizado(productoPersonalizado);

            if (idProducto != -1) {
                Toast.makeText(getContext(), "Café personalizado agregado al carrito", Toast.LENGTH_SHORT).show();
                dbHelper.addToCarrito((int) idProducto, 1);  // Agregar directamente al carrito
            } else {
                Toast.makeText(getContext(), "Error al agregar el café", Toast.LENGTH_SHORT).show();
            }

            // Limpiar el campo de texto después de agregar
            editTextNombreCafe.setText("");
        }
    }
}
