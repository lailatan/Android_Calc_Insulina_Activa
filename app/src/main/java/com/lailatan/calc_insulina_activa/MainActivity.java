package com.lailatan.calc_insulina_activa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void clickConfiguracion(View view) {
        Intent lanzarConfiguracion = new Intent(MainActivity.this, InsulinaBuscarActivity.class);
        startActivity(lanzarConfiguracion);
    }

    public void clickCalcularInsuActiva(View view) {
        Intent lanzarCalcular = new Intent(MainActivity.this, InsulinaActivaCalcularActivity.class);
        startActivity(lanzarCalcular);
    }
}