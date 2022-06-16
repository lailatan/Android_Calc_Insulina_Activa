package com.lailatan.calc_insulina_activa;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.lailatan.calc_insulina_activa.db.InsulinaActivaSQLiteHelper;
import com.lailatan.calc_insulina_activa.entities.InsulinaActiva;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class InsulinaActivaCalcularActivity extends AppCompatActivity {
    public static final long PERIODO = 60000; // 60 segundos (60 * 1000 millisegundos) // 5 minutos 60000 * 5 = 300000
    private static final Integer C_TOPE_INACTIVAS = 4;
    private Handler handler;
    private Runnable runnable;
    private static final String C_INSULINA_ACTIVA = "insulina_activa";
    private Boolean hacerBackup;
    InsulinaActivaAdapter insuActivaAdapter;
    ArrayList<InsulinaActiva> listaDeInsulinaActivas;
    ListView insulinaActivaLV;
    TextView vacioTV;
    TextView horaCalculoTV;
    TextView insulinaTotalTV;
    TextView insulinaRapidaTotalTV;
    TextView insulinaLentaTotalTV;
    TextView limpiarBT;

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
        limpiarBT=findViewById(R.id.limpiarBT);
        vacioTV=findViewById(R.id.vacioTV);

        configurarBackup();
        calcularInsulinaActiva();
        borrarInsuActivayControles(true,false);

    }

    private void configurarBackup() {
        hacerBackup = Utils.cargarConfigBackupArchivo(InsulinaActivaCalcularActivity.this);
        limpiarBT.setText(getString(hacerBackup ? R.string.store_data: R.string.erase_data));
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
        borrarInsuActivayControles(true,true);
    }

    private void borrarInsuActivayControles(boolean soloInactivas, boolean porEleccionUsuario) {
        String mensaje="";
        InsulinaActivaSQLiteHelper insuActivaHelper = new InsulinaActivaSQLiteHelper(this);
        Integer cantidadInactivas = insuActivaHelper.buscarCantidadInsulinasActivas(false,true);
        insuActivaHelper.close();
        //Log.i("InsulinaActivaCalcular","Insulinas inactivas --> " + cantidadInactivas.toString());

        if (cantidadInactivas>C_TOPE_INACTIVAS||(cantidadInactivas>0 && porEleccionUsuario)) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.MyDialogTheme);
            if (hacerBackup){
                mensaje = getString(porEleccionUsuario ? R.string.store_all_inactive_insulin_confirmation : R.string.store_all_stores_inactive_insulin_confirmation);
                alertDialog.setTitle(R.string.store_data);

            }else {
                mensaje = getString(porEleccionUsuario ? R.string.delete_all_inactive_insulin_confirmation : R.string.delete_all_stores_inactive_insulin_confirmation);
                alertDialog.setTitle(R.string.erase_data);
            }
            alertDialog.setMessage(mensaje);
            alertDialog.setIcon(R.drawable.ic_question);
            alertDialog.setCancelable(false);
            alertDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                @RequiresApi(api = Build.VERSION_CODES.O)
                public void onClick(DialogInterface dialog, int which) {
                    boolean tienePermiso = hacerBackup && Utils.tienePermisoParaArchivos(InsulinaActivaCalcularActivity.this);
                    if ((!hacerBackup) || (hacerBackup && tienePermiso)) {
                        if (hacerBackup) Utils.escribirEnArchivo(InsulinaActivaCalcularActivity.this, listaDeInsulinaActivas);
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
                            MostrarTotales(1);
                            insuActivaHelper.eliminarTodasInsulinaActiva();
                            if (Utils.cargarConfigRecibirNotificaciones(InsulinaActivaCalcularActivity.this))
                                Utils.borrarTodasAlarmasInsulinaActiva(getApplicationContext(), listaDeInsulinaActivas);
                            listaDeInsulinaActivas.clear();
                        }
                        insuActivaHelper.close();
                    } else {
                        ActivityCompat.requestPermissions(InsulinaActivaCalcularActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 225);
                    }

                }
            });
            alertDialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertDialog.create();
            alertDialog.show();
        } else {
            horaCalculoTV.setText("");
            insulinaTotalTV.setText("");
            insulinaLentaTotalTV.setText("");
            insulinaRapidaTotalTV.setText("");
            MostrarTotales(1);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void clickCalcular(View view) {
        MostrarTotales(1);
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
        configurarBackup();

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

    public void clickMostrarTotales(View view) {
        Utils.hideKeyboard(this);
        MostrarTotales(Integer.parseInt(((RadioButton)view).getTag().toString()   ));
    }

    private void MostrarTotales(Integer tag) {
        LinearLayout tituloLL=findViewById(R.id.tituloLL);
        LinearLayout tablaLL=findViewById(R.id.tablaLL);
        if (tag==0) {
            tituloLL.setVisibility(View.GONE);
            tablaLL.setVisibility(View.GONE);
            ((RadioButton)findViewById(R.id.ocultarRB)).setChecked(true);

        } else{
            tituloLL.setVisibility(View.VISIBLE);
            tablaLL.setVisibility(View.VISIBLE);
            ((RadioButton)findViewById(R.id.mostrarRB)).setChecked(true);
        }
    }

}
