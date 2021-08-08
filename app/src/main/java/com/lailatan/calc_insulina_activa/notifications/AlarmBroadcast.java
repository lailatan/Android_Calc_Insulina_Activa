package com.lailatan.calc_insulina_activa.notifications;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;

import com.lailatan.calc_insulina_activa.InsulinaActivaCalcularActivity;
import com.lailatan.calc_insulina_activa.R;
import com.lailatan.calc_insulina_activa.entities.Insulina;

import static com.lailatan.calc_insulina_activa.Utils.*;

public class AlarmBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String channelId = "${context.packageName} - ${context.getString(R.string.app_name)}";

        Bundle datos =intent.getExtras();
        String insulina=datos.getString(C_INSULINA);
        String duracion=datos.getString(C_DURACION);
        String horaInicio=datos.getString(C_HORAINICIO);
        String unidades=datos.getString(C_UNIDADES);

        if (Integer.valueOf(duracion) > 180) {
            Double valorhoras = Math.round((Integer.valueOf(duracion) / 60.0) * 100.0) / 100.0; //solo 2 decimales
            duracion = valorhoras.toString() +  context.getString(R.string.hours_short);
        } else {
            duracion +=  context.getString(R.string.minutes_short);
        }

        //String texto = context.getString(R.string.active_insulin_expired_info) +  "\n" +
        String texto = "\t" + context.getString(R.string.insulin) + " " +insulina + "\n" +
                "\t" + context.getString(R.string.hour) + " " + horaInicio +  "\n" +
                "\t" + context.getString(R.string.units) + " " + unidades +  context.getString(R.string.IU) + "\n" +
                "\t" + context.getString(R.string.duration) + " " + duracion +  "\n";


        // Create an Intent for the activity you want to start
        Intent intentProximo = new Intent(context, InsulinaActivaCalcularActivity.class);
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(intentProximo);
        // Get the PendingIntent containing the entire back stack
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT);

        //intentProximo.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentProximo, 0);


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder (context, channelId)
                .setSmallIcon(android.R.drawable.ic_menu_my_calendar)
                //.setSmallIcon(R.drawable.insulina_activa) // 3
                .setContentTitle (context.getString(R.string.active_insulin_expired)) // 4
                //.setContentText ("mensaje") // 5
                .setStyle(new NotificationCompat.BigTextStyle().bigText(texto)) // 6
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                .setPriority(NotificationCompat.PRIORITY_HIGH) // 7
                .setContentIntent(pendingIntent)
                .setAutoCancel(true); // 8


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1001, notificationBuilder.build());

    }
}
