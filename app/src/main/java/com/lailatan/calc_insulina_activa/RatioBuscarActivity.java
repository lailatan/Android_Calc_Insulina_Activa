package com.lailatan.calc_insulina_activa;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.lailatan.calc_insulina_activa.db.InsulinaSQLiteHelper;
import com.lailatan.calc_insulina_activa.db.RatioSQLiteHelper;
import com.lailatan.calc_insulina_activa.entities.Insulina;
import com.lailatan.calc_insulina_activa.entities.Ratio;

import java.util.ArrayList;
import java.util.List;

public class RatioBuscarActivity extends AppCompatActivity {
    static final String C_RATIO = "ratio";
    ListView ratioListaLV;
    TextView view_vacio;
    private RatioAdapter ratioAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ratio_buscar);
        ratioListaLV = findViewById(R.id.ratioListaLV);
        view_vacio = findViewById(R.id.view_vacio);
        View header = getLayoutInflater().inflate(R.layout.fila_ratio_header, null);
        ratioListaLV.addHeaderView(header);
        //actualizarParaTestear();
    }

    @Override
    protected void onResume() {
        super.onResume();
        BuscarRatios();
    }

    private void BuscarRatios(){
        RatioSQLiteHelper ratioHelper = new RatioSQLiteHelper(this);
        List<Ratio> listaRatios = ratioHelper.buscarRatios();
        ratioHelper.close();

        ratioAdapter = new RatioAdapter(getApplicationContext(), listaRatios);
        ratioListaLV.setAdapter(ratioAdapter);

        if(listaRatios.size()==0){
            view_vacio.setText(R.string.not_found);

        } else {
            view_vacio.setText("");
            ratioListaLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    if (position!=0) editarRatio((Ratio) ratioAdapter.getItem(position-1));
                }
            });
        }
    }

    public void editarRatio(Ratio ratioActual) {
        AlertDialog.Builder builder = new AlertDialog.Builder(RatioBuscarActivity.this);
        // Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();
        View myLayout = inflater.inflate(R.layout.dialog_editar_ratio, null);

        ((TextView) myLayout.findViewById(R.id.horaTV)).setText(String.format("%02d:00",ratioActual.getHora()));
        ((TextView) myLayout.findViewById(R.id.horaTV)).setTag(ratioActual.getHora());
        ((EditText) myLayout.findViewById(R.id.ratioET)).setText(ratioActual.getValor().toString());
        ((EditText) myLayout.findViewById(R.id.ratioET)).setTag(ratioActual.getRatio_id());
        ((EditText) myLayout.findViewById(R.id.ratioET)).requestFocus();

        builder.setView(myLayout)
                // Add action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Dialog f = (Dialog) dialog;
                        String nuevoRatio = ((EditText) f.findViewById(R.id.ratioET)).getText().toString();
                        //String nuevoRatio =  ratioET.getText().toString();
                        Double valor=(!nuevoRatio.isEmpty())?Double.parseDouble((nuevoRatio)):0;
                        Integer hora=(Integer)((TextView) f.findViewById(R.id.horaTV)).getTag();
                        Integer ratio_id=(Integer)((EditText) f.findViewById(R.id.ratioET)).getTag();
                        guardarRatio(new Ratio(ratio_id,hora,valor));
                        BuscarRatios();
                        //dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //LoginDialogFragment.this.getDialog().cancel();
                        dialog.dismiss();
                    }
                });
        builder.setCancelable(true);
        builder.create();
        builder.show();
    }

    private void guardarRatio(Ratio ratio) {
        RatioSQLiteHelper ratioHelper = new RatioSQLiteHelper(this);
        ratioHelper.guardarRatio(ratio);
        ratioHelper.close();
    }

    private void guardarTodosRatiosIgual(Double ratio_valor) {
        RatioSQLiteHelper ratioHelper = new RatioSQLiteHelper(this);
        ratioHelper.actualizarTodosRatiosIguales(ratio_valor);
        ratioHelper.close();
    }

    private void actualizarParaTestear() {
        RatioSQLiteHelper ratioHelper = new RatioSQLiteHelper(this);
        ratioHelper.actualizarParaTest();
        ratioHelper.close();
    }

    public void clickVolver(View view) {
        onBackPressed();
    }

    public void clickConsultaRatio(View view) {
        Toast toast = Toast.makeText(this,R.string.ratio_msg,Toast.LENGTH_LONG);
        toast.show();
    }

    public void clickConsultaHoraRatio(View view) {
        Toast toast = Toast.makeText(this,R.string.hora_msg,Toast.LENGTH_LONG);
        toast.show();
    }

    public void clickUnSoloRatio(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(RatioBuscarActivity.this);
        // Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();
        View myLayout = inflater.inflate(R.layout.dialog_editar_todos_ratios_igual, null);

        ((EditText) myLayout.findViewById(R.id.ratioET)).requestFocus();

        builder.setView(myLayout)
                // Add action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Dialog f = (Dialog) dialog;
                        String nuevoRatio = ((EditText) f.findViewById(R.id.ratioET)).getText().toString();
                        //String nuevoRatio =  ratioET.getText().toString();
                        Double valor=(!nuevoRatio.isEmpty())?Double.parseDouble((nuevoRatio)):0;
                        guardarTodosRatiosIgual(valor);
                        BuscarRatios();
                        //dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //LoginDialogFragment.this.getDialog().cancel();
                        dialog.dismiss();
                    }
                });
        builder.setCancelable(true);
        builder.create();
        builder.show();
    }

    public void clickRangoRatio(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(RatioBuscarActivity.this);
        // Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();
        View myLayout = inflater.inflate(R.layout.dialog_editar_rango_ratios, null);

        ((EditText) myLayout.findViewById(R.id.ratioET)).requestFocus();
        Spinner hora_desdeSP = ((Spinner) myLayout.findViewById(R.id.hora_desdeSP));
        Spinner hora_hastaSP = ((Spinner) myLayout.findViewById(R.id.hora_hastaSP));
        llenarSpinners(hora_desdeSP,hora_hastaSP);

        builder.setView(myLayout)
                // Add action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Dialog f = (Dialog) dialog;
                        String nuevoRatio = ((EditText) f.findViewById(R.id.ratioET)).getText().toString();
                        //String nuevoRatio =  ratioET.getText().toString();
                        Double valor=(!nuevoRatio.isEmpty())?Double.parseDouble((nuevoRatio)):0;
                        Long nuevaHoraDesde = ((Spinner) f.findViewById(R.id.hora_desdeSP)).getSelectedItemId();
                        Long nuevaHoraHasta = ((Spinner) f.findViewById(R.id.hora_hastaSP)).getSelectedItemId();
                        guardarRangoRatio(valor, nuevaHoraDesde.intValue(), nuevaHoraHasta.intValue());
                        BuscarRatios();
                        //dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //LoginDialogFragment.this.getDialog().cancel();
                        dialog.dismiss();
                    }
                });
        builder.setCancelable(true);
        builder.create();
        builder.show();
    }

    private void guardarRangoRatio(Double valor, Integer horaDesde, Integer horaHasta) {
        RatioSQLiteHelper ratioHelper = new RatioSQLiteHelper(this);
        ratioHelper.actualizarRangoRatiosIgual(valor,horaDesde,horaHasta);
        ratioHelper.close();
    }

    private void llenarSpinners(Spinner hora_desdeSP, Spinner hora_hastaSP) {
        List<String> items = new ArrayList<>();
        for (int i=0;i<24;i++){
            items.add(String.format("%02d:00",i));
        }

        hora_desdeSP.setAdapter(new ArrayAdapter<String>(this, R.layout.ratio_spinner_item, items));
        hora_desdeSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // Get the spinner selected item text
                //String selectedItemText = (String) adapterView.getItemAtPosition(i);
                //limpiarResultado();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //Toast.makeText(mContext,"No selection",Toast.LENGTH_LONG).show();
            }
        });
        hora_hastaSP.setAdapter(new ArrayAdapter<String>(this, R.layout.ratio_spinner_item, items));
        hora_hastaSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // Get the spinner selected item text
                //String selectedItemText = (String) adapterView.getItemAtPosition(i);
                //limpiarResultado();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //Toast.makeText(mContext,"No selection",Toast.LENGTH_LONG).show();
            }
        });
        hora_desdeSP.setSelection(0);
        hora_hastaSP.setSelection(23);
    }

}