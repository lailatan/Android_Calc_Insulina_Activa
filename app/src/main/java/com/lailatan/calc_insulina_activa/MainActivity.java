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
    CheckBox notificacionesCB;
    CheckBox archivoCB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        notificacionesCB = findViewById(R.id.notificacionesCB);
        archivoCB = findViewById(R.id.archivoCB);
        cargarDatosNotificaciones();
        cargarDatosBackupArchivo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarDatosNotificaciones();
        cargarDatosBackupArchivo();
    }

    public void clickConfiguracion(View view) {
        Intent lanzarConfiguracion = new Intent(MainActivity.this, InsulinaBuscarActivity.class);
        startActivity(lanzarConfiguracion);
    }

    public void clickCalcularInsuActiva(View view) {
        Intent lanzarCalcular = new Intent(MainActivity.this, InsulinaActivaCalcularActivity.class);
        startActivity(lanzarCalcular);
    }

    private void cargarDatosNotificaciones() {
        if (Utils.cargarConfigRecibirNotificaciones(this)) {
            if (Utils.tienePermisoParaNotificaciones(this)) {
                notificacionesCB.setChecked(true);
                Utils.createNotificationChannel(this);
                Utils.habilitarAlarmasEnBoot(getApplicationContext());
                //Utils.crearNotificacion(this, "PRUEBA");
            }else{
                Utils.desHabilitarAlarmasEnBoot(getApplicationContext());
                Utils.guardarConfigRecibirNotificaciones(this,false);
                notificacionesCB.setChecked(false);
            }
        } else {
            Utils.desHabilitarAlarmasEnBoot(getApplicationContext());
            notificacionesCB.setChecked(false);
        }
    }

    private void cargarDatosBackupArchivo() {
        if (Utils.cargarConfigBackupArchivo(this)) {
            if (Utils.tienePermisoParaArchivos(this)) {
                archivoCB.setChecked(true);
            }else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 225);
            }
        } else {
            archivoCB.setChecked(false);
        }
    }

    public void clickRecibirNotificaciones(View view) {
        if (notificacionesCB.isChecked()){
            if (Utils.tienePermisoParaNotificaciones(this)) {
                Utils.guardarConfigRecibirNotificaciones(this,true);
                Utils.createNotificationChannel(this);
                Utils.habilitarAlarmasEnBoot(getApplicationContext());
                crearAlarmasViejas();
            }
            else {
                Utils.guardarConfigRecibirNotificaciones(this,false);
                notificacionesCB.setChecked(false);
            }
        }else {
            Utils.guardarConfigRecibirNotificaciones(this,false);
            borrarAlarmasViejas();
            Utils.desHabilitarAlarmasEnBoot(getApplicationContext());
        }
    }

    public void clickGuardarEnArchivo(View view){
        if (archivoCB.isChecked()){
            if (!Utils.tienePermisoParaArchivos(this))
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 225);
            else {
                Utils.guardarConfigBackupArchivo(this, true);
                String path_usuario = Utils.DeterminarPathArchivoSegunVersionAndroidCorto(this);
                Toast.makeText(this, getString(R.string.backup_path) + path_usuario, Toast.LENGTH_LONG).show();
            }
        }else {
            Utils.guardarConfigBackupArchivo(this,false);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (Utils.tienePermisoParaArchivos(this)) {
            Utils.guardarConfigBackupArchivo(this,true);
            String path= getString(R.string.app_name) + "/";
            Toast.makeText(this, getString(R.string.backup_path) + path, Toast.LENGTH_LONG).show();
        }
        else {
            Utils.guardarConfigBackupArchivo(this,false);
            archivoCB.setChecked(false);
        }
    }

    private void borrarAlarmasViejas() {
        InsulinaActivaSQLiteHelper insuActivaHelper = new InsulinaActivaSQLiteHelper(this);
        ArrayList<InsulinaActiva> listaDeInsulinaActivas = insuActivaHelper.buscarInsulinaActivas(true);
        insuActivaHelper.close();
        if (listaDeInsulinaActivas.size()!=0) {
            Utils.borrarTodasAlarmasInsulinaActiva(getApplicationContext(), listaDeInsulinaActivas);
            Toast.makeText(MainActivity.this, getString(R.string.notif_all_active_insulin_deleted), Toast.LENGTH_LONG).show();
        }
    }

    private void crearAlarmasViejas() {
        InsulinaActivaSQLiteHelper insuActivaHelper = new InsulinaActivaSQLiteHelper(this);
        ArrayList<InsulinaActiva> listaDeInsulinaActivas = insuActivaHelper.buscarInsulinaActivas(true);
        insuActivaHelper.close();
        if (listaDeInsulinaActivas.size()!=0) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.MyDialogTheme);
            alertDialog.setMessage(R.string.create_notif_confirmation);
            alertDialog.setTitle(R.string.notifications);
            alertDialog.setIcon(R.drawable.ic_question);
            alertDialog.setCancelable(false);
            alertDialog.setPositiveButton(R.string.yes, (dialog, which) -> {
                Utils.crearAlarmasTodasInsulinasActivas(getApplicationContext(),listaDeInsulinaActivas);
                Toast.makeText(MainActivity.this, getString(R.string.notif_all_active_insulin_created), Toast.LENGTH_LONG).show();
            });
            alertDialog.setNegativeButton(R.string.no, (dialog, which) -> {
                Toast.makeText(MainActivity.this, getString(R.string.notif_from_now), Toast.LENGTH_LONG).show();
                dialog.dismiss();
            });
            alertDialog.create();
            alertDialog.show();
        }
    }


}