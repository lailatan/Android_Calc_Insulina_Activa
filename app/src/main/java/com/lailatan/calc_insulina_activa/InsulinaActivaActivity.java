package com.lailatan.calc_insulina_activa;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lailatan.calc_insulina_activa.db.InsulinaActivaSQLiteHelper;
import com.lailatan.calc_insulina_activa.db.InsulinaSQLiteHelper;
import com.lailatan.calc_insulina_activa.entities.Insulina;
import com.lailatan.calc_insulina_activa.entities.InsulinaActiva;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class InsulinaActivaActivity extends AppCompatActivity {
    private static final String C_INSULINABUSQUEDA = "insulina";
    private static final String C_INSULINA_ACTIVA = "insulina_activa";

    private Integer insulinaActivaId;
    private Insulina insulinaAplicada;

    TextView rapidaTV;
    TextView duracionMinTV;
    TextView duracionHorasTV;
    EditText insulinaET;
    EditText unidadesET;
    EditText descripcionET;
    EditText diaET;
    EditText mesET;
    EditText anioET;
    EditText horaET;
    EditText minutoET;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insulina_activa);

        rapidaTV=findViewById(R.id.rapidaTV);
        duracionMinTV=findViewById(R.id.duracionMinTV);
        duracionHorasTV=findViewById(R.id.duracionHorasTV);
        insulinaET=findViewById(R.id.insulinaET);
        unidadesET=findViewById(R.id.unidadesET);
        descripcionET=findViewById(R.id.descripcionET);
        diaET=findViewById(R.id.diaET);
        mesET=findViewById(R.id.mesET);
        anioET=findViewById(R.id.anioET);
        horaET=findViewById(R.id.horaET);
        minutoET=findViewById(R.id.minutoET);

        Bundle datos = this.getIntent().getExtras();
        InsulinaActiva insulinaActiva = (InsulinaActiva) datos.getSerializable(C_INSULINA_ACTIVA);
        if (insulinaActiva==null) {
            insulinaActivaId =0;
            cargarFechaHoraActual();
        }
        else cargarDatosInsulinaActiva(insulinaActiva);

        //Valido que las unidades solo puedan tener ##.##
        unidadesET.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,int after) {}

            @Override
            public void afterTextChanged(Editable s) {
                String valor= unidadesET.getText().toString();
                try{
                    Utils.esNumero(valor, 2, 2, false);
                }catch(UtilException e){
                    if(valor.length() != 0){
                        unidadesET.getText().delete(valor.length() - 1, valor.length());
                    }
                }
            }
        });
    }

    private void cargarDatosInsulinaActiva(InsulinaActiva insulinaActiva) {
        insulinaActivaId = insulinaActiva.getInsulina_activa_id();
        cargarDatosInsulina(insulinaActiva.getInsulina());
        unidadesET.setText(String.valueOf(insulinaActiva.getUnidades()));
        descripcionET.setText(insulinaActiva.getDescripcion());
        diaET.setText(String.format("%02d", insulinaActiva.getDia_desde()));
        mesET.setText(String.format("%02d", insulinaActiva.getMes_desde()));
        anioET.setText(String.valueOf(insulinaActiva.getAnio_desde()));
        horaET.setText(String.format("%02d", insulinaActiva.getHora_desde()));
        minutoET.setText(String.format("%02d", insulinaActiva.getMinuto_desde()));
        insulinaET.setEnabled(false);
    }

    private void cargarDatosInsulina(Insulina insulina) {
        insulinaAplicada=insulina;
        duracionMinTV.setText(insulina.getDuracion_minutos().toString());
        Double valorhoras = Math.round((insulina.getDuracion_minutos() / 60.0) * 100.0) / 100.0; //solo 2 decimales
        duracionHorasTV.setText(valorhoras.toString());
        insulinaET.setText(insulina.getNombre() +" - " + insulina.getLaboratorio());
        rapidaTV.setText(insulina.getRapida()==1?R.string.quick:R.string.slow);
    }

    private void CargarDatosInsulinaconId(Integer insulinaSeleccionadaId) {
        InsulinaSQLiteHelper insulinaHelper = new InsulinaSQLiteHelper(this);
        Insulina insulinaSeleccionada = insulinaHelper.buscarInsulinaPorId(insulinaSeleccionadaId);
        insulinaHelper.close();
        cargarDatosInsulina(insulinaSeleccionada);
    }

    public void clickBuscarInsulina(View view) {
        Utils.hideKeyboard(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.MyDialogTheme);
        builder.setTitle(R.string.select_one_insulin);

        InsulinaSQLiteHelper insulinaHelper = new InsulinaSQLiteHelper(this);
        List<Insulina> listaInsulinas = insulinaHelper.buscarInsulinas("");
        insulinaHelper.close();

        final List<String> insulinas = new ArrayList<>();
        final List<Integer> insulinasId = new ArrayList<>();

        for (Integer i=0;i<listaInsulinas.size();i=i+1) {
            insulinas.add(listaInsulinas.get(i).getNombre() +" - " + listaInsulinas.get(i).getLaboratorio());
            insulinasId.add(listaInsulinas.get(i).getInsulina_id());
        }

        if (insulinas.size()==0){
            Toast.makeText(getApplicationContext(), R.string.no_insulin_msg, Toast.LENGTH_LONG).show();
        } else {
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_dropdown_item_1line, insulinas);
            builder.setAdapter(dataAdapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //if (which != 0) CargarDatosInsulinaconId(insulinasId.get(which));
                    CargarDatosInsulinaconId(insulinasId.get(which));
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            Rect displayRectangle = new Rect();
            Window window = this.getWindow();
            window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
            dialog.getWindow().setLayout((int) (displayRectangle.width() *
                    0.8f), (int) (displayRectangle.height() * 0.8f));
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void clickFABGuardar(View view) {
        Utils.hideKeyboard(this);
        if (validoDatos()) {
            if (guardarInsulinaActiva()) {
                onBackPressed();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private Boolean guardarInsulinaActiva() {
        Boolean guardado=false;
        if (validoDatos()) {
            Double unidades = Double.valueOf(unidadesET.getText().toString());
            Integer dia = Integer.valueOf(diaET.getText().toString());
            Integer mes = Integer.valueOf(mesET.getText().toString());
            Integer anio = Integer.valueOf(anioET.getText().toString());
            Integer hora = Integer.valueOf(horaET.getText().toString());
            Integer minuto = Integer.valueOf(minutoET.getText().toString());
            String descripcion = descripcionET.getText().toString();
            Integer activa = 1;

            InsulinaActiva insulinaActivaActual = new InsulinaActiva(insulinaActivaId,insulinaAplicada,unidades,
                    dia,mes,anio,hora,minuto,activa,descripcion);

            insulinaActivaActual.calcularTiempoQueRestaActivayDesactivar(this);

            InsulinaActivaSQLiteHelper insulinaActivaHelper = new InsulinaActivaSQLiteHelper(this);
            insulinaActivaId = insulinaActivaHelper.guardarInsulinaActiva(insulinaActivaActual);
            insulinaActivaHelper.close();
            guardado=true;
        }
        return guardado;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean validoDatos() {
        Boolean datosValidos=false;
        if (insulinaET.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), R.string.must_select_insulin, Toast.LENGTH_LONG).show();
            insulinaET.requestFocus();
        }else if (unidadesET.getText().toString().isEmpty()){
                Toast.makeText(getApplicationContext(), R.string.must_enter_units,Toast.LENGTH_LONG).show();
                unidadesET.requestFocus();
        } else if (fechaValida()){
            datosValidos=true;
        }

        return datosValidos;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean fechaValida() {
        Boolean datosValidos=false;
         if (FechaIngresada()) {
             if (!Utils.fechaHoraValidaNoFuturoPasado24Horas(diaET.getText().toString(), mesET.getText().toString(), anioET.getText().toString(),
                     horaET.getText().toString(), minutoET.getText().toString())) {
                 Toast.makeText(getApplicationContext(), R.string.must_enter_valid_date, Toast.LENGTH_LONG).show();
                 diaET.requestFocus();
             } else {
                 diaET.setText(String.format("%02d", Integer.valueOf(diaET.getText().toString())));
                 mesET.setText(String.format("%02d", Integer.valueOf(mesET.getText().toString())));
                 horaET.setText(String.format("%02d", Integer.valueOf(horaET.getText().toString())));
                 minutoET.setText(String.format("%02d", Integer.valueOf(minutoET.getText().toString())));
                 datosValidos = true;
             }
         }
        return datosValidos;
    }

    private boolean FechaIngresada() {
        boolean fechaingresada=false;
        if (!Utils.validarNumero(diaET.getText().toString(),1,31)) {
            Toast.makeText(getApplicationContext(), R.string.must_enter_valid_day, Toast.LENGTH_LONG).show();
            diaET.requestFocus();
        }else if (!Utils.validarNumero(mesET.getText().toString(),1,12)) {
            Toast.makeText(getApplicationContext(), R.string.must_enter_valid_month, Toast.LENGTH_LONG).show();
            mesET.requestFocus();
        }else if (!Utils.validarNumero(anioET.getText().toString(),2000,3000)) {
            Toast.makeText(getApplicationContext(), R.string.must_enter_valid_year, Toast.LENGTH_LONG).show();
            anioET.requestFocus();
        }else if (!Utils.validarNumero(horaET.getText().toString(),0,23)) {
            Toast.makeText(getApplicationContext(), R.string.must_enter_valid_hour, Toast.LENGTH_LONG).show();
            horaET.requestFocus();
        }else if (!Utils.validarNumero(minutoET.getText().toString(),0,59)) {
            Toast.makeText(getApplicationContext(), R.string.must_enter_valid_minute, Toast.LENGTH_LONG).show();
            minutoET.requestFocus();
        }else fechaingresada=true;
        return fechaingresada;
    }

    public void clickFABBorrar(View view) {
        Utils.hideKeyboard(this);
        confirmarYBorrar();
    }

    private void confirmarYBorrar() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this,R.style.MyDialogTheme);
        alertDialog.setMessage(R.string.delete_active_insulin_confirmation);
        alertDialog.setTitle(R.string.delete);
        alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                InsulinaActivaSQLiteHelper insulinaActivaHelper = new InsulinaActivaSQLiteHelper(InsulinaActivaActivity.this);
                if (!(insulinaActivaId==0)) insulinaActivaHelper.eliminarInsulinaActiva(insulinaActivaId);
                onBackPressed();
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


    public void clickFABCancelar(View view) {
        Utils.hideKeyboard(this);
        onBackPressed();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void clickFechaHoraAhora(View view) {
        cargarFechaHoraActual();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void cargarFechaHoraActual() {
        LocalDateTime fechaHoraActual = LocalDateTime.now();
        diaET.setText(String.format("%02d", fechaHoraActual.getDayOfMonth()));
        mesET.setText(String.format("%02d", fechaHoraActual.getMonthValue()));
        anioET.setText(String.valueOf(fechaHoraActual.getYear()));
        horaET.setText(String.format("%02d", fechaHoraActual.getHour()));
        minutoET.setText(String.format("%02d", fechaHoraActual.getMinute()));
    }
}