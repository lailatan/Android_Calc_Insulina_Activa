package com.lailatan.calc_insulina_activa;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.lailatan.calc_insulina_activa.db.InsulinaActivaSQLiteHelper;
import com.lailatan.calc_insulina_activa.entities.InsulinaActiva;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    CheckBox notificacionesCB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        notificacionesCB = findViewById(R.id.notificacionesCB);
        cargarDatosNotificaciones();
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
            alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
            alertDialog.setCancelable(false);
            alertDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                public void onClick(DialogInterface dialog, int which) {
                    Utils.crearAlarmasTodasInsulinasActivas(getApplicationContext(),listaDeInsulinaActivas);
                    Toast.makeText(MainActivity.this, getString(R.string.notif_all_active_insulin_created), Toast.LENGTH_LONG).show();
                }
            });
            alertDialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(MainActivity.this, getString(R.string.notif_from_now), Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }
            });
            alertDialog.create();
            alertDialog.show();
        }
    }
}