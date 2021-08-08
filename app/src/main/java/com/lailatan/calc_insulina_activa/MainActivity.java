package com.lailatan.calc_insulina_activa;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.lailatan.calc_insulina_activa.db.InsulinaActivaSQLiteHelper;
import com.lailatan.calc_insulina_activa.db.InsulinaSQLiteHelper;
import com.lailatan.calc_insulina_activa.entities.Insulina;
import com.lailatan.calc_insulina_activa.entities.InsulinaActiva;

import java.time.LocalDateTime;

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
                //Utils.createNotificationChannel(this,
                //        NotificationManagerCompat.IMPORTANCE_HIGH, false,
                //        getString(R.string.app_name), "Canal de notificación de aplicaciones");
                //Utils.crearNotificacion(this, null);
            }else{
                Utils.guardarConfigRecibirNotificaciones(this,false);
                notificacionesCB.setChecked(false);
            }
        } else notificacionesCB.setChecked(false);
    }

    public void clickRecibirNotificaciones(View view) {
        if (notificacionesCB.isChecked()){
            if (Utils.tienePermisoParaNotificaciones(this)) {
                Utils.guardarConfigRecibirNotificaciones(this,true);
                Utils.createNotificationChannel(this);
                crearAlarmasViejas();
            }
            else {
                Utils.guardarConfigRecibirNotificaciones(this,false);
                notificacionesCB.setChecked(false);
            }
        }else {
            Utils.guardarConfigRecibirNotificaciones(this,false);
            Utils.borrarTodasAlarmasInsulinaActiva(this);
        }
    }

    private void crearAlarmasViejas() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this,R.style.MyDialogTheme);
        alertDialog.setMessage(R.string.create_notif_confirmation);
        alertDialog.setTitle(R.string.notifications);
        alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
        {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(DialogInterface dialog, int which)
            {
                Toast.makeText(MainActivity.this, getString(R.string.notif_all_active_insulin_created),Toast.LENGTH_LONG).show();
                Utils.crearAlarmasTodasInsulinasActivas(MainActivity.this);
            }
        });
        alertDialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                Toast.makeText(MainActivity.this, getString(R.string.notif_from_now),Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
        alertDialog.create();
        alertDialog.show();
    }

}