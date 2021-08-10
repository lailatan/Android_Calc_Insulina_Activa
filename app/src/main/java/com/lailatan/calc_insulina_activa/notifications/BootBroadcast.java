package com.lailatan.calc_insulina_activa.notifications;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;

import com.lailatan.calc_insulina_activa.InsulinaActivaCalcularActivity;
import com.lailatan.calc_insulina_activa.MainActivity;
import com.lailatan.calc_insulina_activa.R;
import com.lailatan.calc_insulina_activa.Utils;
import com.lailatan.calc_insulina_activa.db.InsulinaActivaSQLiteHelper;
import com.lailatan.calc_insulina_activa.entities.InsulinaActiva;

import java.util.ArrayList;

import static com.lailatan.calc_insulina_activa.Utils.C_ID_MENSAJE;
import static com.lailatan.calc_insulina_activa.Utils.C_TEXTO;

public class BootBroadcast extends BroadcastReceiver {
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            if (Utils.cargarConfigRecibirNotificaciones(context)) {
                if (Utils.tienePermisoParaNotificaciones(context)) {
                    InsulinaActivaSQLiteHelper insuActivaHelper = new InsulinaActivaSQLiteHelper(context);
                    ArrayList<InsulinaActiva> listaDeInsulinaActivas = insuActivaHelper.buscarInsulinaActivas(true);
                    insuActivaHelper.close();
                    if (listaDeInsulinaActivas.size() != 0) {
                        Utils.crearAlarmasTodasInsulinasActivas(context, listaDeInsulinaActivas);
                        Toast.makeText(context, "BootBroadcast " + listaDeInsulinaActivas.size() + " alarmas creadas", Toast.LENGTH_LONG).show();
                        Log.i("BootBroadcast", "Crear notificacion");
                    }
                }
            }
            Log.i("BootBroadcast", "contexto " + context.toString());
        }
        Toast.makeText(context,  "BootBroadcast saliendo", Toast.LENGTH_LONG).show();
    }
}
