package com.lailatan.calc_insulina_activa.notifications;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import androidx.annotation.RequiresApi;
import com.lailatan.calc_insulina_activa.Utils;
import com.lailatan.calc_insulina_activa.db.InsulinaActivaSQLiteHelper;
import com.lailatan.calc_insulina_activa.entities.InsulinaActiva;
import java.util.ArrayList;


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
                        //Toast.makeText(context, "BootBroadcast " + listaDeInsulinaActivas.size() + " alarmas creadas", Toast.LENGTH_LONG).show();
                        Log.i("BootBroadcast", "Crear notificacion");
                    }
                }
            }
            //Log.i("BootBroadcast", "contexto " + context.toString());
        }
        //Toast.makeText(context,  "BootBroadcast saliendo", Toast.LENGTH_LONG).show();
    }
}
