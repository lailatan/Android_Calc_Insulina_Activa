package com.lailatan.calc_insulina_activa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.lailatan.calc_insulina_activa.db.InsulinaSQLiteHelper;
import com.lailatan.calc_insulina_activa.entities.Insulina;

import java.util.List;

public class InsulinaBuscarActivity extends AppCompatActivity {
    static final String C_INSULINA = "insulina";
    ListView insulinaListaLV;
    EditText nombreBuscarET;
    TextView view_vacio;
    TextView limpiarBT;
    TextView buscarBT;
    private InsulinaAdapter insulinaAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insulina_buscar);
        insulinaListaLV = findViewById(R.id.insulinaListaLV);
        nombreBuscarET=findViewById(R.id.nombreBuscarET);
        view_vacio = findViewById(R.id.view_vacio);
        limpiarBT = findViewById(R.id.limpiarBT);
        buscarBT = findViewById(R.id.buscarBT);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BuscarInsulinas();
    }

    public void clickBuscarInsulinas(View view) {
        Utils.hideKeyboard(this);
        BuscarInsulinas();
    }

    private void BuscarInsulinas(){
        String nombre= nombreBuscarET.getText().toString();

        InsulinaSQLiteHelper insulinaHelper = new InsulinaSQLiteHelper(this);
        List<Insulina> listaInsulinas = insulinaHelper.buscarInsulinas(nombre);
        insulinaHelper.close();

        insulinaAdapter = new InsulinaAdapter(getApplicationContext(), listaInsulinas);
        insulinaListaLV.setAdapter(insulinaAdapter);

        if(listaInsulinas.size()==0){
            view_vacio.setText(R.string.not_found);

        } else {
            view_vacio.setText("");
            insulinaListaLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    Intent abrirInsulina = new Intent(getApplicationContext(), InsulinaActivity.class);
                    abrirInsulina.putExtra(C_INSULINA, ((Insulina) insulinaAdapter.getItem(position)));
                    startActivity(abrirInsulina);
                }
            });
        }
    }


    public void clickLimpiar(View view) {
        Utils.hideKeyboard(this);
        LimpiarControles();
    }

    public void clickAgregar(View view) {
        Intent intent = new Intent(this, InsulinaActivity.class);
        intent.putExtra(C_INSULINA, (Insulina) null);
        startActivity(intent);
    }

    public void LimpiarControles() {
        insulinaListaLV.setAdapter(null);
        nombreBuscarET.setText("");
        view_vacio.setText("");
    }

    public void clickVolver(View view) {
        onBackPressed();
    }
}