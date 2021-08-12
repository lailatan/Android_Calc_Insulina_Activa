package com.lailatan.calc_insulina_activa;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.lailatan.calc_insulina_activa.db.InsulinaActivaSQLiteHelper;
import com.lailatan.calc_insulina_activa.db.InsulinaSQLiteHelper;
import com.lailatan.calc_insulina_activa.entities.Insulina;


public class InsulinaActivity extends AppCompatActivity {
    static final String C_INSULINA = "insulina";
    private Integer insulinaId;
    EditText nombreET;
    EditText laboET;
    EditText descripcionET;
    EditText duracionMinET;
    EditText duracionHorET;
    RadioGroup minHorasRG;
    RadioButton minutosRB;
    RadioButton horasRB;
    CheckBox rapidaCB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insulina);
        nombreET=  findViewById(R.id.nombreET);
        descripcionET=  findViewById(R.id.descripcionET);
        duracionMinET=  findViewById(R.id.duracionMinET);
        duracionHorET=  findViewById(R.id.duracionHorET);
        laboET=  findViewById(R.id.laboET);
        minHorasRG=  findViewById(R.id.minHorasRG);
        rapidaCB=  findViewById(R.id.rapidaCB);
        minutosRB=  findViewById(R.id.minutosRB);
        horasRB=  findViewById(R.id.horasRB);

        Bundle datos = this.getIntent().getExtras();
        Insulina insulina = (Insulina) datos.getSerializable(C_INSULINA);

        if (insulina==null) {
            insulinaId = 0;
        }else {
            insulinaId = insulina.getInsulina_id();
            cargarDatosInsulina(insulina);
        }

        //Valido que las horas solo puedan tener ##.##
        duracionHorET.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,int after) {}

            @Override
            public void afterTextChanged(Editable s) {
                String valor= duracionHorET.getText().toString();
                try{
                    Utils.esNumero(valor, 2, 2, false);
                }catch(UtilException e){
                    if(valor.length() != 0){
                        duracionHorET.getText().delete(valor.length() - 1, valor.length());
                    }
                }
            }
        });
    }

    private void cargarDatosInsulina(Insulina insulina) {
        nombreET.setText(insulina.getNombre());
        laboET.setText(insulina.getLaboratorio());
        descripcionET.setText(insulina.getDescripcion());
        duracionMinET.setText(insulina.getDuracion_minutos().toString());
        duracionHorET.setText("");
        duracionHorET.setVisibility(View.GONE);
        minutosRB.setChecked(true);
        rapidaCB.setChecked(insulina.getRapida()==1);
    }

    private Boolean guardarInsulina() {
        boolean guardado=false;
        if (insulinaEnUso()) {
            Toast.makeText(getApplicationContext(), R.string.cant_modif_in_use_insulin,Toast.LENGTH_LONG).show();
        } else {
            if (datosValidos()) {
                String nombre = nombreET.getText().toString();
                String laboratorio = laboET.getText().toString();
                String descripcion = descripcionET.getText().toString();
                Integer rapida = rapidaCB.isChecked() ? 1 : 0;

                Integer duracion = Integer.parseInt(duracionMinET.getText().toString());
                InsulinaSQLiteHelper insulinaHelper = new InsulinaSQLiteHelper(this);
                insulinaId = insulinaHelper.guardarInsulina(new Insulina(insulinaId, nombre, laboratorio, rapida, duracion, descripcion));
                insulinaHelper.close();
                guardado = true;
            }
        }
        return guardado;
    }

    private boolean insulinaEnUso() {
        boolean enUso=false;
        if (!(insulinaId==0)) {
            InsulinaActivaSQLiteHelper insulinaActivaHelper = new InsulinaActivaSQLiteHelper(InsulinaActivity.this);
            enUso = insulinaActivaHelper.insulinaEstaUsadaComoActivaPorId(insulinaId);
            insulinaActivaHelper.close();
        }
        return enUso;
    }

    private boolean datosValidos() {
        boolean validos=false;

        if (nombreET.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), R.string.must_enter_name,Toast.LENGTH_LONG).show();
            nombreET.requestFocus();
        } else if (minutosRB.isChecked()) {
            if (duracionMinET.getText().toString().isEmpty()) {
                Toast.makeText(getApplicationContext(), R.string.must_enter_duration, Toast.LENGTH_LONG).show();
                duracionMinET.requestFocus();
            } else if (Integer.parseInt(duracionMinET.getText().toString())<=0){
                Toast.makeText(getApplicationContext(), R.string.must_duration_bigger_than_zero, Toast.LENGTH_LONG).show();
                duracionMinET.requestFocus();
            } else {
                validos = true;
            }
        } else if (horasRB.isChecked()) {
            if (duracionHorET.getText().toString().isEmpty()) {
                Toast.makeText(getApplicationContext(), R.string.must_enter_duration, Toast.LENGTH_LONG).show();
                duracionHorET.requestFocus();
            } else if (Double.parseDouble(duracionHorET.getText().toString()) <= 0) {
                Toast.makeText(getApplicationContext(), R.string.must_duration_bigger_than_zero, Toast.LENGTH_LONG).show();
                duracionHorET.requestFocus();
            } else {
                double valorhoras = Double.parseDouble(duracionHorET.getText().toString());
                int valorminutos = (int) (valorhoras * 60);
                duracionMinET.setText(Integer.toString(valorminutos));
                duracionHorET.setVisibility(View.GONE);
                duracionMinET.setVisibility(View.VISIBLE);
                duracionHorET.setText("");
                validos = true;
            }
        }
        return  validos;
    }


    private void confirmarYBorrar() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this,R.style.MyDialogTheme);
        alertDialog.setMessage(R.string.delete_insulin_confirmation);
        alertDialog.setTitle(R.string.delete);
        alertDialog.setIcon(R.drawable.ic_question);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                if (!(insulinaId==0)) {
                    if (!insulinaEnUso()) {
                        InsulinaSQLiteHelper insulinaHelper = new InsulinaSQLiteHelper(InsulinaActivity.this);
                        insulinaHelper.eliminarInsulina(insulinaId);
                        insulinaHelper.close();
                        onBackPressed();
                    } else Toast.makeText(getApplicationContext(), R.string.cant_delete_in_use_insulin, Toast.LENGTH_LONG).show();
                }
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

    public void clickGuardar(View view) {
        Utils.hideKeyboard(this);
        if (guardarInsulina()) {
            onBackPressed();
        }
    }

    public void clickBorrar(View view) {
        Utils.hideKeyboard(this);
        confirmarYBorrar();
    }

    public void clickCancelar(View view) {
        Utils.hideKeyboard(this);
        onBackPressed();
    }

    public void clickMinHorasRB(View view) {
        double valorhoras;
        int valorminutos;
        Utils.hideKeyboard(this);
        if (minutosRB.isChecked() && duracionMinET.getVisibility()==View.GONE) {
            if (!duracionHorET.getText().toString().isEmpty()) {
                valorhoras = Double.parseDouble(duracionHorET.getText().toString());
                valorminutos = (int) (valorhoras * 60);
                duracionMinET.setText(Integer.toString(valorminutos));
                duracionHorET.setText("");
            }
        } else if (horasRB.isChecked() && duracionHorET.getVisibility()==View.GONE) {
            if (!duracionMinET.getText().toString().isEmpty()) {
                valorminutos = Integer.parseInt(duracionMinET.getText().toString());
                if (valorminutos>5999) valorminutos=5999; // tope de minutos para ##,## horas
                valorhoras = valorminutos / 60.0;
                //valorhoras = Math.round(valorhoras * 100.0) / 100.0; //solo 2 decimales
                duracionMinET.setText("");
                duracionHorET.setText(Double.toString(valorhoras));
            }
        }

        duracionMinET.setVisibility(minutosRB.isChecked() ? View.VISIBLE : View.GONE);
        duracionHorET.setVisibility(horasRB.isChecked() ? View.VISIBLE : View.GONE);
    }
}