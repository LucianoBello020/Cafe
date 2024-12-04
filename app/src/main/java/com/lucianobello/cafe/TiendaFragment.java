package com.lucianobello.cafe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TiendaFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProductoAdapter productoAdapter;
    private ArrayList<Producto> productos;
    private DBHelper dbHelper;

    public TiendaFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tienda, container, false);

        // Inicialización del RecyclerView y el adaptador
        recyclerView = view.findViewById(R.id.recycler_view_tienda);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2)); // Usando un GridLayoutManager con 2 columnas

        // Inicializar la base de datos
        dbHelper = new DBHelper(getContext());

        // Cargar productos desde la base de datos
        productos = dbHelper.getProductos(); // Método que puedes implementar para obtener productos

        // Configuración del adaptador
        productoAdapter = new ProductoAdapter(productos, getContext());
        recyclerView.setAdapter(productoAdapter);

        return view;
    }
}
