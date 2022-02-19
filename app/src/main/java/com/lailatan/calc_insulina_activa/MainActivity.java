package com.lailatan.calc_insulina_activa;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;
import com.lailatan.calc_insulina_activa.db.InsulinaActivaSQLiteHelper;
import com.lailatan.calc_insulina_activa.entities.InsulinaActiva;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void clickConfiguracion(View view) {
        Intent lanzarConfiguracion = new Intent(MainActivity.this, MainConfiguracionActivity.class);
        startActivity(lanzarConfiguracion);
    }

    public void clickCalcularInsuActiva(View view) {
        Intent lanzarCalcular = new Intent(MainActivity.this, InsulinaActivaCalcularActivity.class);
        startActivity(lanzarCalcular);
    }


}