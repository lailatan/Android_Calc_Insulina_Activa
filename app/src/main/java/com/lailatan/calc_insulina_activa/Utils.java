package com.lailatan.calc_insulina_activa;


import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Utils {
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

}
