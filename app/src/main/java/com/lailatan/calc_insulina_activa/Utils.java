package com.lailatan.calc_insulina_activa;


import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.lailatan.calc_insulina_activa.entities.InsulinaActiva;
import com.lailatan.calc_insulina_activa.notifications.AlarmBroadcast;
import com.lailatan.calc_insulina_activa.notifications.BootBroadcast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;


import static android.content.Context.ALARM_SERVICE;

public class Utils {
    public static final String C_TEXTO = "texto";
    public static final String C_ID_MENSAJE = "id_mensaje";

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
        boolean valida;
        try {
            //Formato de fecha (día/mes/año)
            SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
            formatoFecha.setLenient(false);
            //Comprobación de la fecha
            formatoFecha.parse(dia + "/" + mes + "/" + anio);
            valida = true;

            //Fecha no a futuro ni mas de 24 horas (1440 minutos)
            LocalDateTime fechaIngresada = LocalDateTime.of(Integer.parseInt(anio), Integer.parseInt(mes), Integer.parseInt(dia), Integer.parseInt(hora), Integer.parseInt(minuto));
            long diferencia = ChronoUnit.MINUTES.between(fechaIngresada, LocalDateTime.now());
            //valida = (diferencia<=0)&&(diferencia>-1440);
            valida = (diferencia>=0)&&(diferencia<1440);

        } catch (ParseException e) {
            //Si la fecha no es correcta, pasará por aquí
            valida = false;
        }
        return valida;
    }

    public static boolean validarNumero(String texto, int nroDesde, int nroHAsta) {
        return (!texto.isEmpty() && Integer.parseInt(texto)>=nroDesde && Integer.parseInt(texto)<=nroHAsta);
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
        //String channelId = "${contexto.packageName} - ${contexto.getString(R.string.app_name)}";
        String channelId = contexto.getString(R.string.app_name);
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.O) {
            int importancia = NotificationManager.IMPORTANCE_HIGH;
            //int importance = NotificationManager.IMPORTANCE_DEFAULT;
            String nombre=contexto.getString(R.string.app_name) + " Channel";
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


    public static void crearNotificacion(Context context,String texto){
        String channelId = context.getString(R.string.app_name);
        NotificationCompat.Builder mbuilder= new NotificationCompat.Builder( context, channelId);
        mbuilder.setSmallIcon(android.R.drawable.ic_menu_my_calendar)
                //.setSmallIcon(R.drawable.insulina_activa)
                .setContentTitle (context.getString(R.string.active_insulin_expired))
                //.setContentText ("mensaje")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(texto))
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                //.setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManagerCompat mNotificationManager = NotificationManagerCompat.from(context);
        mNotificationManager.notify(1, mbuilder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void crearAlarmaInsulinaActiva(Context context, InsulinaActiva insulinaActiva){
        String texto = crearTextoAlarmaIsulinaActiva(context,insulinaActiva);
        Integer idAlarma=insulinaActiva.getInsulina_activa_id();
        Intent intent = new Intent(context, AlarmBroadcast.class);
        intent.setData(Uri.parse("InsActiveAlarms://" + idAlarma.toString()));
        intent.putExtra(C_TEXTO, texto);
        intent.putExtra(C_ID_MENSAJE, idAlarma);
        //PendingIntent  pendingIntent = PendingIntent.getBroadcast(context,idAlarma, intent, PendingIntent.FLAG_ONE_SHOT);
        PendingIntent  pendingIntent = PendingIntent.getBroadcast(context,idAlarma, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        LocalDateTime fechaHoraAlarma = insulinaActiva.getFechaDesde().plusMinutes(insulinaActiva.getInsulina().getDuracion_minutos());
        Long timestamp = fechaHoraAlarma.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        Log.i("Utils", "Crear alarma");
        Log.i("AlarmBroadcast", texto);
        Log.i("AlarmBroadcast", idAlarma.toString());
        Log.i("AlarmBroadcast", timestamp.toString());

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, timestamp, pendingIntent);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static String crearTextoAlarmaIsulinaActiva(Context context, InsulinaActiva insulinaActiva) {
        String duracionTxt;
        Integer duracion = insulinaActiva.getInsulina().getDuracion_minutos();
        if (duracion > 180) {            double valorhoras = Math.round((duracion / 60.0) * 100.0) / 100.0; //solo 2 decimales
            duracionTxt = valorhoras +  context.getString(R.string.hours_short);
        } else {
            duracionTxt =  duracion.toString() +  context.getString(R.string.minutes_short);
        }

        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        String texto = "\t" + context.getString(R.string.insulin) + " " + insulinaActiva.getInsulina().getNombre() + "\n" +
                "\t" + context.getString(R.string.hour) + " " + insulinaActiva.getFechaDesde().format(dateTimeFormat) + "\n" +
                "\t" + context.getString(R.string.units) + " " + insulinaActiva.getUnidades().toString() + context.getString(R.string.IU) + "\n" +
                "\t" + context.getString(R.string.duration) + " " + duracionTxt + "\n";
        return texto;
    }

    public static void borrarAlarmaInsulinaActiva(Context context, Integer insulinaActivaId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        if (alarmManager!= null) {
            try {
                Intent intent = new Intent(context, AlarmBroadcast.class);
                intent.putExtra(C_ID_MENSAJE, insulinaActivaId);
                intent.setData(Uri.parse("InsActiveAlarms://" + insulinaActivaId.toString()));
                PendingIntent sender = PendingIntent.getBroadcast(context, insulinaActivaId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                //FLAG_CANCEL_CURRENT???
                alarmManager.cancel(sender);
                Log.i("Utils", "Borrar alarma");
                Log.i("AlarmBroadcast", insulinaActivaId.toString());
            } catch (Exception e) {
                Log.i("Utils", "Borrar alarma");
                Log.i("AlarmBroadcast", "ERROR BORRANDO ALARMA");
                Toast.makeText(context, context.getString(R.string.notif_not_found), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static void borrarTodasAlarmasInsulinaActiva(Context context, ArrayList<InsulinaActiva> listaDeInsulinaActivas) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        if (alarmManager!= null) {
            if (listaDeInsulinaActivas.size()!=0)
                for (InsulinaActiva insuActiva:listaDeInsulinaActivas) {
                    if (insuActiva.getActiva() == 1)
                        borrarAlarmaInsulinaActiva(context,insuActiva.getInsulina_activa_id());
                }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void crearAlarmasTodasInsulinasActivas(Context context,ArrayList<InsulinaActiva> listaDeInsulinaActivas) {
        if (listaDeInsulinaActivas.size()!=0) {
            for (InsulinaActiva insuActiva : listaDeInsulinaActivas) {
                if (insuActiva.getActiva() == 1) {
                    if (insuActiva.calcularTiempoQueRestaActivayDesactivar(context) > 0) {
                        crearAlarmaInsulinaActiva(context, insuActiva);
                    }
                }
            }
        }
    }

    public static void habilitarAlarmasEnBoot(Context context){
        ComponentName receiver = new ComponentName(context, BootBroadcast.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);    }

    public static void desHabilitarAlarmasEnBoot(Context context){
        ComponentName receiver = new ComponentName(context, BootBroadcast.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

    public static boolean tienePermisoParaArchivos(Context contexto) {
        boolean permiso=false;
        int permissionCheck = ContextCompat.checkSelfPermission(contexto, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED)
            Toast.makeText(contexto, contexto.getString(R.string.must_have_backup_permissions), Toast.LENGTH_SHORT).show();
        else permiso=true;
        return permiso;
    }

    //guardar configuración aplicación Android usando SharedPreferences
    public static void guardarConfigBackupArchivo(Context contexto, Boolean guardarEnArchivo) {
        SharedPreferences prefs = contexto.getSharedPreferences(contexto.getString(R.string.app_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("BackupFile", guardarEnArchivo);
        editor.commit();
    }

    //cargar configuración aplicación Android usando SharedPreferences
    public static Boolean cargarConfigBackupArchivo(Context contexto) {
        SharedPreferences prefs = contexto.getSharedPreferences(contexto.getString(R.string.app_name), Context.MODE_PRIVATE);
        return prefs.getBoolean("BackupFile", false);
    }

    public static void escribirEnArchivo(Context contexto, ArrayList<InsulinaActiva> listaDeInsulinaActivas) {
        String archivoNombre=contexto.getString(R.string.backup_file_name);
        String mensaje="";
        String texto="";
        boolean archivoNuevo=false;
        if (tienePermisoParaArchivos(contexto)) {
            try {
                String carpeta_backup_path = Environment.getExternalStorageDirectory() + "/" + contexto.getString(R.string.app_name) + "/";
                File carpeta_backup_file = new File(carpeta_backup_path);

                if (!carpeta_backup_file.exists()) carpeta_backup_file.mkdir();

                if (carpeta_backup_file.canWrite()) {
                    File backupDB = new File(carpeta_backup_file, archivoNombre);
                    if (!backupDB.exists()) {
                        backupDB.createNewFile();
                        archivoNuevo=true;
                    }

                    //for (InsulinaActiva insulinaActiva : listaDeInsulinaActivas)
                    //    if (insulinaActiva.getActiva() == 0) texto += insulinaActiva.toString() + "\n";
                    for (int i= listaDeInsulinaActivas.size()-1; i>=0 ; i--)
                        if (listaDeInsulinaActivas.get(i).getActiva() == 0) texto += listaDeInsulinaActivas.get(i).toString() + "\n";


                    FileWriter archivo = new FileWriter(backupDB, true);
                    if (archivoNuevo) archivo.write(contexto.getString(R.string.backup_file_headers) + "\n");
                    archivo.write(texto);
                    archivo.flush();
                    archivo.close();

                    mensaje = mensaje + contexto.getString(R.string.backup_success) + " " + contexto.getString(R.string.app_name) + "/" + archivoNombre;
                } else {
                    mensaje = contexto.getString(R.string.backup_fail_permission);
                }

            } catch (Exception e) {
                mensaje = contexto.getString(R.string.backup_fail_space_or_permission);
                e.printStackTrace();
            }

        }
        Toast.makeText(contexto, mensaje, Toast.LENGTH_SHORT).show();
    }
}