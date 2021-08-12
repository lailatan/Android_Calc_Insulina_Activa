package com.lailatan.calc_insulina_activa;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.lailatan.calc_insulina_activa.db.InsulinaActivaSQLiteHelper;
import com.lailatan.calc_insulina_activa.entities.InsulinaActiva;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class InsulinaActivaCalcularActivity extends AppCompatActivity {
    public static final long PERIODO = 60000; // 60 segundos (60 * 1000 millisegundos) // 5 minutos 60000 * 5 = 300000
    private static final Integer C_TOPE_INACTIVAS = 10;
    private Handler handler;
    private Runnable runnable;
    private static final String C_INSULINA_ACTIVA = "insulina_activa";
    InsulinaActivaAdapter insuActivaAdapter;
    ArrayList<InsulinaActiva> listaDeInsulinaActivas;
    ListView insulinaActivaLV;
    TextView vacioTV;
    TextView horaCalculoTV;
    TextView insulinaTotalTV;
    TextView insulinaRapidaTotalTV;
    TextView insulinaLentaTotalTV;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insulina_activa_calcular);
        insulinaActivaLV=findViewById(R.id.insulinaActivaLV);
        horaCalculoTV=findViewById(R.id.horaCalculoTV);
        insulinaTotalTV=findViewById(R.id.insulinaTotalTV);
        insulinaRapidaTotalTV=findViewById(R.id.insulinaRapidaTotalTV);
        insulinaLentaTotalTV=findViewById(R.id.insulinaLentaTotalTV);
        vacioTV=findViewById(R.id.vacioTV);

        //calcularInsulinaActiva();
        verificarCantidadInsulinasInactivas();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void calcularInsulinaActiva() {
        Double insulinaTotalActiva = 0.00;
        Double insulinaRapidaTotalActiva = 0.00;
        Double insulinaLentaTotalActiva = 0.00;
        cargarInsulinasActivas();
        for (InsulinaActiva insuActiva: listaDeInsulinaActivas) {
            if (insuActiva.getActiva()==1) {
                Double cantidadInsuActivaActual=insuActiva.calcularInsuActivaActual();
                insulinaTotalActiva+=cantidadInsuActivaActual;
                if (insuActiva.getInsulina().getRapida()==1) insulinaRapidaTotalActiva+=cantidadInsuActivaActual;
                else  insulinaLentaTotalActiva+=cantidadInsuActivaActual;
            }
        }

        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        horaCalculoTV.setText(LocalDateTime.now().format(dateTimeFormat));
        insulinaTotalTV.setText(String.valueOf(Math.round(insulinaTotalActiva * 100.0) / 100.0));
        insulinaRapidaTotalTV.setText(String.valueOf(Math.round(insulinaRapidaTotalActiva * 100.0) / 100.0));
        insulinaLentaTotalTV.setText(String.valueOf(Math.round(insulinaLentaTotalActiva* 100.0) / 100.0));
    }

    private void cargarInsulinasActivas() {

        InsulinaActivaSQLiteHelper insuActivaHelper = new InsulinaActivaSQLiteHelper(this);
        listaDeInsulinaActivas = insuActivaHelper.buscarInsulinaActivas(false);
        insuActivaHelper.close();

        insuActivaAdapter = new InsulinaActivaAdapter(this, listaDeInsulinaActivas);
        insulinaActivaLV.setAdapter(insuActivaAdapter);

        if(listaDeInsulinaActivas.size()==0){
            vacioTV.setText(R.string.not_found_insulin);

        } else {
            //tengoDatos=true;
            vacioTV.setText("");
            insulinaActivaLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    Intent abrirInsulinaActiva = new Intent(getApplicationContext(), InsulinaActivaActivity.class);
                    abrirInsulinaActiva.putExtra(C_INSULINA_ACTIVA, ((InsulinaActiva) insuActivaAdapter.getItem(position)));
                    startActivity(abrirInsulinaActiva);
                }
            });
        }
    }


    //Botones
    public void clickLimpiar(View view) {
        borrarInsuActivayControles(false);
    }

    private void verificarCantidadInsulinasInactivas() {

        InsulinaActivaSQLiteHelper insuActivaHelper = new InsulinaActivaSQLiteHelper(this);
        Integer cantidad = insuActivaHelper.buscarCantidadInsulinasActivas(false,true);
        insuActivaHelper.close();
        Log.i("InsulinaActivaCalcular","Insulinas inactivas --> " + cantidad.toString());
        if (cantidad>C_TOPE_INACTIVAS)
            borrarInsuActivayControles(true);
    }

    private void borrarInsuActivayControles(boolean soloInactivas) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this,R.style.MyDialogTheme);
        String mensaje = getString(soloInactivas?R.string.delete_all_inactive_insulin_confirmation:R.string.delete_all_active_insulin_confirmation);
        alertDialog.setMessage(mensaje);
        alertDialog.setTitle(R.string.delete);
        alertDialog.setIcon(R.drawable.ic_question);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
        {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(DialogInterface dialog, int which)
            {
                InsulinaActivaSQLiteHelper insuActivaHelper = new InsulinaActivaSQLiteHelper(InsulinaActivaCalcularActivity.this);
                if (soloInactivas) {
                    insuActivaHelper.eliminarTodasInsulinInaActiva();
                    calcularInsulinaActiva();
                } else {
                    horaCalculoTV.setText("");
                    insulinaTotalTV.setText("");
                    insulinaLentaTotalTV.setText("");
                    insulinaRapidaTotalTV.setText("");
                    insulinaActivaLV.setAdapter(null);
                    insuActivaHelper.eliminarTodasInsulinaActiva();
                    if (Utils.cargarConfigRecibirNotificaciones(InsulinaActivaCalcularActivity.this))
                        Utils.borrarTodasAlarmasInsulinaActiva(getApplicationContext(), listaDeInsulinaActivas);
                    listaDeInsulinaActivas.clear();
                }
                insuActivaHelper.close();
            }
        });
        alertDialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });
        alertDialog.create();
        alertDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void clickCalcular(View view) {
        calcularInsulinaActiva();
    }

    public void clickFABAgregar(View view) {
        Intent abrirInsulinaActiva = new Intent(getApplicationContext(), InsulinaActivaActivity.class);
        abrirInsulinaActiva.putExtra(C_INSULINA_ACTIVA, ((InsulinaActiva) null));
        startActivity(abrirInsulinaActiva);
    }

    public void clickFABCancelar(View view) {
        onBackPressed();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onResume() {
        super.onResume();
        calcularInsulinaActiva();

        handler = new Handler(Looper.getMainLooper());
        runnable = new Runnable(){
            @Override
            public void run(){
                //Toast.makeText(InsulinaActivaCalcularActivity.this,  "onResume", Toast.LENGTH_SHORT).show();
                calcularInsulinaActiva();
                handler.postDelayed(this, PERIODO);
            }
        };
        handler.postDelayed(runnable, PERIODO);

    }

    @Override
    protected void onPause() {
        super.onPause();
        //Toast.makeText(this,  "onPause", Toast.LENGTH_SHORT).show();
        handler.removeCallbacks(runnable);
    }

}
