package com.lucianobello.cafe;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageView;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        // Obtener el ImageView de la imagen
        ImageView imageView = findViewById(R.id.imageView);

        // Cargar la animación de rotación
        Animation rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate);

        // Aplicar la animación al ImageView
        imageView.startAnimation(rotateAnimation);

        // Redirigir a la MainActivity después de 3 segundos
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Iniciar MainActivity
                Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Finalizar la actividad de Splash Screen
            }
        }, 3000); // 3 segundos de duración del Splash Screen
    }
}
