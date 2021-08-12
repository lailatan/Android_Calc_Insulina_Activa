package com.lailatan.calc_insulina_activa.notifications;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import com.lailatan.calc_insulina_activa.InsulinaActivaCalcularActivity;
import com.lailatan.calc_insulina_activa.R;
import static com.lailatan.calc_insulina_activa.Utils.*;

public class AlarmBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //String channelId = "${context.packageName} - ${context.getString(R.string.app_name)}";
        String channelId = context.getString(R.string.app_name);

        Bundle datos =intent.getExtras();
        String texto=datos.getString(C_TEXTO);
        int idAlarma=datos.getInt(C_ID_MENSAJE);

        // Create an Intent for the activity you want to start
        Intent intentProximo = new Intent(context, InsulinaActivaCalcularActivity.class);
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(intentProximo);
        // Get the PendingIntent containing the entire back stack
        //PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLA.FLAG_CANCEL_CURRENT);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT);

        //intentProximo.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentProximo, 0);

        NotificationCompat.Builder notificationBuilder =  new NotificationCompat.Builder( context, channelId)
                //.setSmallIcon(android.R.drawable.ic_menu_my_calendar)
                .setSmallIcon(R.drawable.ic_medic_white)
                .setContentTitle (context.getString(R.string.active_insulin_expired))
                //.setContentText ("mensaje")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(texto))
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(idAlarma, notificationBuilder.build());

        Log.i("AlarmBroadcast", "Crear notificacion");
        Log.i("AlarmBroadcast", texto);
        Log.i("AlarmBroadcast", Integer.toString(idAlarma));
        Log.i("AlarmBroadcast", intent.getData().toString());

    }
}
