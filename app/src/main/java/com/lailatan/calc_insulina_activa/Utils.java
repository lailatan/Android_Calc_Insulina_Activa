package com.lailatan.calc_insulina_activa;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.lailatan.calc_insulina_activa.db.InsulinaActivaSQLiteHelper;
import com.lailatan.calc_insulina_activa.db.InsulinaSQLiteHelper;
import com.lailatan.calc_insulina_activa.entities.InsulinaActiva;
import com.lailatan.calc_insulina_activa.notifications.AlarmBroadcast;

import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.content.Context.ALARM_SERVICE;

public class Utils {
    public static final String C_INSULINA = "insulina";
    public static final String C_DURACION = "duracion";
    public static final String C_HORAINICIO = "hora";
    public static final String C_UNIDADES = "unidades";

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Boolean fechaHoraValidaNoFuturoPasado24Horas(String dia, String mes, String anio, String hora, String minuto) {
        boolean valida = false;
        try {
            //Formato de fecha (día/mes/año)
            SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
            formatoFecha.setLenient(false);
            //Comprobación de la fecha
            formatoFecha.parse(dia + "/" + mes + "/" + anio);
            valida = true;

            //Fecha no a futuro ni mas de 24 horas (1440 minutos)
            LocalDateTime fechaIngresada = LocalDateTime.of(Integer.parseInt(anio), Integer.parseInt(mes), Integer.parseInt(dia), Integer.parseInt(hora), Integer.parseInt(minuto));
            Long diferencia = ChronoUnit.MINUTES.between(fechaIngresada, LocalDateTime.now());
            valida = (diferencia<=0)&&(diferencia>-1440);
            valida = (diferencia>=0)&&(diferencia<1440);

        } catch (ParseException e) {
            //Si la fecha no es correcta, pasará por aquí
            valida = false;
        }
        return valida;
    }

    public static boolean validarNumero(String texto, int nroDesde, int nroHAsta) {
        return (!texto.isEmpty() && Integer.valueOf(texto)>=nroDesde && Integer.valueOf(texto)<=nroHAsta);
    }

    public static double esNumero(String valor, long cantidadDeEnteros, long cantidadDeDecimales, boolean admiteSignoNegativo)throws UtilException{
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
        //char separadorDecimal= decimalFormatSymbols.getDecimalSeparator();
        char separadorDecimal= ".".charAt(0);
        try{
            double retorno = 0.0;
            //Valido que el valor no este en blanco
            if (valor == null || valor.trim().length() == 0)
                throw new UtilException ("Debe indicar el valor");

            //Valido que si ingresaron un punto, no sea el primer valor
            //if (valor.indexOf(".") != -1 && (valor.length() == 1)){
            if (valor.indexOf(separadorDecimal) != -1 && (valor.length() == 1)){
                throw new UtilException ("Debe indicar la cantidad de enteros y dedcimales. Ej.: 3.5");
            }

            //En caso que el valor ingresado en la primera posición sea el signo menos asigno
            //-0 para que no de error por no ser numero.
            if (valor.equals("-") && (valor.length() == 1))
                valor = "-0";

            //Valido que no sea un número
            try{
                retorno = Double.valueOf(valor);
            }catch(NumberFormatException e){
                //retorno = 0.0
                throw new UtilException ("El valor no es válido");
            }

            //Valido que no sea un valor menor a cero
            if (admiteSignoNegativo == false){
                if (retorno < 0.0)
                    throw new UtilException ("El valor no puede ser negativo");
            }

            //Valido la cantidad de enteros y decimales. Para saber si existen
            //decimales busco el punto decimal. En caso que el punto decimal no
            //este es por que no existen decimales.
            //if (valor.indexOf(".") != -1){
            if (valor.indexOf(separadorDecimal) != -1){

                //Valido la cantidad de enteros
                if (cantidadDeEnteros == 0)
                    throw new UtilException ("El valor no permite decimales.");

                //Valido la cantidad de decimales
                if (cantidadDeDecimales == 0)
                    throw new UtilException ("El valor no permite decimales.");

                //Si la longitud es uno significa que ingreso solo el punto. Si es
                //distinta de uno asumo que ingreso enteros y decimales.
                if (valor.length() != 1){

                    //int cantidadEntero = valor.substring(0, valor.indexOf(".")).length();
                    int cantidadEntero = valor.substring(0, valor.indexOf(separadorDecimal)).length();
                    //int cantidadDecimales = valor.substring(valor.indexOf(".") + 1, valor.length()).length();
                    int cantidadDecimales = valor.substring(valor.indexOf(separadorDecimal) + 1, valor.length()).length();

                    if (cantidadEntero > cantidadDeEnteros){
                        String ent = "enteros";
                        if (String.valueOf(cantidadEntero).length() == 1)
                            ent = " entero";
                        throw new UtilException ("El valor debe tener "+ cantidadDeEnteros + ent);
                    }
                    if (cantidadDecimales > cantidadDeDecimales){
                        String dec =" decimales";
                        if (String.valueOf(cantidadDecimales).length() == 1)
                            dec = " decimal";
                        throw new UtilException ("El valor debe tener "+ cantidadDeDecimales + dec);
                    }
                }
            }else{
                int cantidadEntero = valor.length();
                if (cantidadEntero > cantidadDeEnteros){
                    String ent = "enteros";
                    if (String.valueOf(cantidadEntero).length() == 1)
                        ent = " entero";
                    throw new UtilException ("El valor debe tener "+ cantidadDeEnteros + ent);
                }
            }
            return retorno;
        }catch(UtilException e){
            throw e;
        }catch(Exception e){
            throw new UtilException ("Error de sistema");
        }
    }

    //guardar configuración aplicación Android usando SharedPreferences
    public static void guardarConfigRecibirNotificaciones(Context contexto, Boolean recibirNotificaciones) {
        SharedPreferences prefs = contexto.getSharedPreferences(contexto.getString(R.string.app_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("Notifications", recibirNotificaciones);
        editor.commit();
    }

    //cargar configuración aplicación Android usando SharedPreferences
    public static Boolean cargarConfigRecibirNotificaciones(Context contexto) {
        SharedPreferences prefs = contexto.getSharedPreferences(contexto.getString(R.string.app_name), Context.MODE_PRIVATE);
        return prefs.getBoolean("Notifications", false);
    }

    public static boolean tienePermisoParaNotificaciones(Context contexto) {
        NotificationManagerCompat manager = NotificationManagerCompat.from(contexto);
        // La validez del método areNotificationsEnabled solo se admite oficialmente hasta la API mínima 19,
        // y este método aún se puede llamar si es menor que 19, pero solo devolverá verdadero, es decir,
        // el usuario ha habilitado las notificaciones de forma predeterminada.
        boolean isOpened = manager.areNotificationsEnabled();
        if (!isOpened) Toast.makeText(contexto, contexto.getString(R.string.must_have_notificaion_permissions), Toast.LENGTH_LONG).show();
        return isOpened;
    }

    public  static String createNotificationChannel (Context contexto) {
        String channelId = "${contexto.packageName} - ${contexto.getString(R.string.app_name)}";
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.O) {
            int importancia = NotificationManager.IMPORTANCE_HIGH;
            //int importance = NotificationManager.IMPORTANCE_DEFAULT;
            String nombre=contexto.getString(R.string.app_name) + "Channel";
            String descripcion="Notification Channel for " + contexto.getString(R.string.app_name);
            NotificationChannel channel = new NotificationChannel(channelId, nombre, importancia);
            channel.setDescription(descripcion);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = contexto.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        return channelId;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void crearAlarmaInsulinaActiva(Context context, InsulinaActiva insulinaActiva){
        Intent intent = new Intent(context, AlarmBroadcast.class);
        intent.putExtra(C_INSULINA, insulinaActiva.getInsulina().getNombre());
        intent.putExtra(C_DURACION, insulinaActiva.getInsulina().getDuracion_minutos().toString());
        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        LocalDateTime fechaHoraAplicacion = insulinaActiva.getFechaDesde();
        intent.putExtra(C_HORAINICIO, fechaHoraAplicacion.format(dateTimeFormat));
        intent.putExtra(C_UNIDADES, insulinaActiva.getUnidades().toString());
        PendingIntent  pendingIntent = PendingIntent.getBroadcast(context, insulinaActiva.getInsulina_activa_id(), intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        /*
        long ahora=System.currentTimeMillis();
        long diezSegundosEnMillis = 1000*10;
        */
        //alarmManager.set(AlarmManager.RTC_WAKEUP, ahora+diezSegundosEnMillis, pendingIntent);

        LocalDateTime fechaHoraAlarma = insulinaActiva.getFechaDesde().plusMinutes(insulinaActiva.getInsulina().getDuracion_minutos());
        Long timestamp = fechaHoraAlarma.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        /*
        Calendar datetimeToAlarm = Calendar.getInstance(Locale.getDefault());
        //datetimeToAlarm.timeInMillis = System.currentTimeMillis ()
        datetimeToAlarm.set(Calendar.DAY_OF_MONTH, fechaHoraAlarma.getDayOfMonth());
        datetimeToAlarm.set(Calendar.MONTH, fechaHoraAlarma.getMonthValue());
        datetimeToAlarm.set(Calendar.YEAR, fechaHoraAlarma.getYear());
        datetimeToAlarm.set(Calendar.HOUR_OF_DAY, fechaHoraAlarma.getHour());
        datetimeToAlarm.set(Calendar.MINUTE, fechaHoraAlarma.getMinute());
        datetimeToAlarm.set(Calendar.SECOND, 0);
        datetimeToAlarm.set(Calendar.MILLISECOND, 0);
        Long timestamp2 = datetimeToAlarm.getTimeInMillis();
         */
        alarmManager.set(AlarmManager.RTC_WAKEUP, timestamp, pendingIntent);
        Toast.makeText(context, context.getString(R.string.notif_created),Toast.LENGTH_LONG).show();
    }

    public static void borrarAlarmaInsulinaActiva(Context context, Integer insulinaActivaId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        if (alarmManager!= null) {
            //alarmManager..cancel();
        }
    }

    public static void borrarTodasAlarmasInsulinaActiva(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        if (alarmManager!= null) {
            //alarmManager..cancel();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void crearAlarmasTodasInsulinasActivas(Context context) {
        InsulinaActivaSQLiteHelper insulinaActivaHelper = new InsulinaActivaSQLiteHelper(context);
        List<InsulinaActiva> listaDeInsulinaActivas = insulinaActivaHelper.buscarInsulinaActivas();
        for (InsulinaActiva insuActiva: listaDeInsulinaActivas) {
            if (insuActiva.getActiva()==1) {
                if (insuActiva.calcularTiempoQueRestaActivayDesactivar(context) > 0) {
                    crearAlarmaInsulinaActiva(context,insuActiva);
                }
            }
        }
        insulinaActivaHelper.close();
    }
}
