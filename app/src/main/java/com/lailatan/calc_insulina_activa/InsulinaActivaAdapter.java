package com.lailatan.calc_insulina_activa;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.lailatan.calc_insulina_activa.entities.Insulina;
import com.lailatan.calc_insulina_activa.entities.InsulinaActiva;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class InsulinaActivaAdapter extends ArrayAdapter<InsulinaActiva> {
    Context context;

    public InsulinaActivaAdapter(Context contexto, List<InsulinaActiva> listaInsuActiva){
        super(contexto, 0, listaInsuActiva);
        this.context=contexto;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View ItemLista = convertView;

        if (ItemLista == null) {
            ItemLista = LayoutInflater
                    .from(getContext())
                    .inflate(R.layout.fila_insulina_activa, parent, false);
        }

        InsulinaActiva insuActivaActual = getItem(position);

        TextView nombreInsulinaTV = ItemLista.findViewById(R.id.nombreInsulinaTV);
        TextView unidadesTV = ItemLista.findViewById(R.id.unidadesTV);
        TextView fechaHoraAplicacionTV = ItemLista.findViewById(R.id.fechaHoraAplicacionTV);
        TextView activaTV = ItemLista.findViewById(R.id.activaTV);
        TextView tiempoActivaTV = ItemLista.findViewById(R.id.tiempoActivaTV);
        TextView unidadesActivaTV = ItemLista.findViewById(R.id.unidadesActivaTV);
        TextView rapidaTV = ItemLista.findViewById(R.id.rapidaTV);
        LinearLayout insulinaActivaLL = ItemLista.findViewById(R.id.insulinaActivaLL);


        Insulina insulina = insuActivaActual.getInsulina();
        //nombreInsulinaTV.setText(String.format("%s - %s", insulina.getNombre(), insulina.getLaboratorio()));
        nombreInsulinaTV.setText(String.format("%s ", insulina.getNombre()));
        rapidaTV.setText(insulina.getRapida()==1?R.string.quick:R.string.slow);
        unidadesTV.setText(insuActivaActual.getUnidades().toString());

        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        LocalDateTime fechaHoraAplicacion = insuActivaActual.getFechaDesde();
        fechaHoraAplicacionTV.setText(fechaHoraAplicacion.format(dateTimeFormat));



        if (insuActivaActual.getActiva()==1) {
            Long tiempoRestante = insuActivaActual.calcularTiempoQueRestaActivayDesactivar(context);
            Double unidadesRestante  = Math.round((insuActivaActual.calcularInsuActivaActual()) * 100.0) / 100.0; //solo 2 decimales

            activaTV.setTextColor(Color.parseColor("#52e038"));
            activaTV.setText(R.string.active);
            String unidadesTxt = context.getString(R.string.IU);

            if (tiempoRestante>180){
                Double valorhoras = Math.round((tiempoRestante / 60.0) * 100.0) / 100.0; //solo 2 decimales
                String tiempoTxt = context.getString(R.string.hours_short);
                tiempoActivaTV.setText(String.format("  %s%s ", valorhoras.toString(), tiempoTxt));
            } else {
                String tiempoTxt = context.getString(R.string.minutes_short);
                tiempoActivaTV.setText(String.format("  %s %s ", tiempoRestante.toString(), tiempoTxt));
            }
            unidadesActivaTV.setText(String.format(" %s %s / ", unidadesRestante.toString(), unidadesTxt));
            insulinaActivaLL.setVisibility(View.VISIBLE);
        }else {
            activaTV.setTextColor(Color.parseColor("#ff0000"));
            activaTV.setText( R.string.inactive);
            insulinaActivaLL.setVisibility(View.INVISIBLE);
        }
        return ItemLista;
    }
}
