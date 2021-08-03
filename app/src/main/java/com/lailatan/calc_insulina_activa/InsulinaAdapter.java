package com.lailatan.calc_insulina_activa;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lailatan.calc_insulina_activa.entities.Insulina;

import java.util.List;

public class InsulinaAdapter extends ArrayAdapter<Insulina> {

    public InsulinaAdapter(Context contexto, List<Insulina> listaInsulina){
        super(contexto, 0, listaInsulina);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View ItemLista = convertView;

        if (ItemLista == null) {
            ItemLista = LayoutInflater
                    .from(getContext())
                    .inflate(R.layout.fila_insulina, parent, false);
        }

        Insulina insulinaActual = getItem(position);

        TextView nombreInsulinaTV = ItemLista.findViewById(R.id.nombreInsulinaTV);
        TextView rapidaTV = ItemLista.findViewById(R.id.rapidaTV);
        TextView laboratorioTV = ItemLista.findViewById(R.id.laboratorioTV);
        TextView duracionMinTV = ItemLista.findViewById(R.id.duracionMinTV);
        TextView duracionHorasTV = ItemLista.findViewById(R.id.duracionHorasTV);


        nombreInsulinaTV.setText(insulinaActual.getNombre());
        rapidaTV.setText(insulinaActual.getRapida()==1?R.string.quick:R.string.slow);

        duracionMinTV.setText(insulinaActual.getDuracion_minutos().toString());
        Double valorhoras = Math.round((insulinaActual.getDuracion_minutos() / 60.0) * 100.0) / 100.0; //solo 2 decimales
        duracionHorasTV.setText(valorhoras.toString());

        laboratorioTV.setText(insulinaActual.getLaboratorio());

        return ItemLista;
    }

}
