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
import com.lailatan.calc_insulina_activa.entities.Ratio;

import java.util.List;

public class RatioAdapter extends ArrayAdapter<Ratio> {

    public RatioAdapter(Context contexto, List<Ratio> listaRatio){
        super(contexto, 0, listaRatio);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View ItemLista = convertView;

        if (ItemLista == null) {
            ItemLista = LayoutInflater
                    .from(getContext())
                    .inflate(R.layout.fila_ratio, parent, false);
        }

        Ratio ratioActual = getItem(position);

        TextView horaTV = ItemLista.findViewById(R.id.horaTV);
        TextView valorTV = ItemLista.findViewById(R.id.valorTV);


        horaTV.setText(String.format("%02d:00",ratioActual.getHora()));
        valorTV.setText(ratioActual.getValor().toString());

        return ItemLista;
    }
}
