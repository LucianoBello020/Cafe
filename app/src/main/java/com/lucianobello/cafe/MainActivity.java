package com.lucianobello.cafe;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Usando setOnItemSelectedListener con lambda
        bottomNavigationView.setOnItemSelectedListener( this::onNavigationItemSelected );

        // Cargar el fragment por defecto si no hay un estado guardado
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, new TiendaFragment()).commit();
        }
    }

    private boolean onNavigationItemSelected(MenuItem item) {
        Fragment selectedFragment;

        // Usar if-else en lugar de switch
        if (item.getItemId() == R.id.nav_tienda) {
            selectedFragment = new TiendaFragment();
        } else if (item.getItemId() == R.id.nav_crear_cafe) {
            selectedFragment = new CrearCafeFragment();
        } else if (item.getItemId() == R.id.nav_carrito) {
            selectedFragment = new CarritoFragment();
        } else {
            selectedFragment = new TiendaFragment();
        }

        // Reemplazar el fragment seleccionado
        getSupportFragmentManager().beginTransaction().replace( R.id.nav_host_fragment, selectedFragment ).commit();
        return true;
    }
}
