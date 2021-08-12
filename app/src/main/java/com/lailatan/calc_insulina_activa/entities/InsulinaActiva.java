package com.lailatan.calc_insulina_activa.entities;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.lailatan.calc_insulina_activa.db.InsulinaActivaSQLiteHelper;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class InsulinaActiva implements Serializable {
    private Integer insulina_activa_id;
    private Insulina insulina;
    private Double unidades;
    private Integer dia_desde;
    private Integer mes_desde;
    private Integer anio_desde;
    private Integer hora_desde; // 24hs
    private Integer minuto_desde;
    private Integer activa;
    private String descripcion;

    public InsulinaActiva() {
    }

    public InsulinaActiva(Insulina insulina, Double unidades, Integer dia_desde, Integer mes_desde, Integer anio_desde, Integer hora_desde, Integer minuto_desde, Integer activa, String descripcion) {
        this.insulina_activa_id = 0;
        this.insulina = insulina;
        this.unidades = unidades;
        this.dia_desde = dia_desde;
        this.mes_desde = mes_desde;
        this.anio_desde = anio_desde;
        this.hora_desde = hora_desde;
        this.minuto_desde = minuto_desde;
        this.activa = activa;
        this.descripcion = descripcion;
    }

    public InsulinaActiva(Integer insulina_activa_id, Insulina insulina, Double unidades, Integer dia_desde, Integer mes_desde, Integer anio_desde, Integer hora_desde, Integer minuto_desde, Integer activa, String descripcion) {
        this.insulina_activa_id = insulina_activa_id;
        this.insulina = insulina;
        this.unidades = unidades;
        this.dia_desde = dia_desde;
        this.mes_desde = mes_desde;
        this.anio_desde = anio_desde;
        this.hora_desde = hora_desde;
        this.minuto_desde = minuto_desde;
        this.activa = activa;
        this.descripcion = descripcion;
    }

    public Integer getInsulina_activa_id() {
        return insulina_activa_id;
    }

    public void setInsulina_activa_id(Integer insulina_activa_id) {
        this.insulina_activa_id = insulina_activa_id;
    }

    public Insulina getInsulina() {
        return insulina;
    }

    public void setInsulina(Insulina insulina) {
        this.insulina = insulina;
    }

    public Double getUnidades() {
        return unidades;
    }

    public void setUnidades(Double unidades) {
        this.unidades = unidades;
    }

    public Integer getDia_desde() {
        return dia_desde;
    }

    public void setDia_desde(Integer dia_desde) {
        this.dia_desde = dia_desde;
    }

    public Integer getMes_desde() {
        return mes_desde;
    }

    public void setMes_desde(Integer mes_desde) {
        this.mes_desde = mes_desde;
    }

    public Integer getAnio_desde() {
        return anio_desde;
    }

    public void setAnio_desde(Integer anio_desde) {
        this.anio_desde = anio_desde;
    }

    public Integer getHora_desde() {
        return hora_desde;
    }

    public void setHora_desde(Integer hora_desde) {
        this.hora_desde = hora_desde;
    }

    public Integer getMinuto_desde() {
        return minuto_desde;
    }

    public void setMinuto_desde(Integer minuto_desde) {
        this.minuto_desde = minuto_desde;
    }

    public Integer getActiva() {
        return activa;
    }

    public void setActiva(Integer activa) {
        this.activa = activa;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return "InsulinaActiva{" +
                "insulina_activa_id=" + insulina_activa_id +
                ", insulina=" + insulina +
                ", unidades=" + unidades +
                ", dia_desde=" + dia_desde +
                ", mes_desde=" + mes_desde +
                ", anio_desde=" + anio_desde +
                ", hora_desde=" + hora_desde +
                ", minuto_desde=" + minuto_desde +
                ", activa=" + activa +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public LocalDateTime getFechaDesde(){
        return LocalDateTime.of(anio_desde, mes_desde, dia_desde, hora_desde, minuto_desde);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Long calcularTiempoQueRestaActivayDesactivar(Context contexto) {
        Long diferencia = null;
        if (this.getActiva()==1) {
            diferencia = ChronoUnit.MINUTES.between(this.getFechaDesde(), LocalDateTime.now());
            if ((diferencia < 0) || (diferencia > this.getInsulina().getDuracion_minutos())) {
                this.setActiva(0);
                diferencia = null;
                if (this.getInsulina_activa_id()!=0) {
                    InsulinaActivaSQLiteHelper insuActivaHelper = new InsulinaActivaSQLiteHelper(contexto);
                    insuActivaHelper.desactivarInsulinaActiva(this.insulina_activa_id);
                    insuActivaHelper.close();
                }
            }
        }
        return (diferencia==null)?0:(this.getInsulina().getDuracion_minutos()-diferencia);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public Double calcularInsuActivaActual() {
        double insulinaActivaActual = 0.00;
        if (this.getActiva()==1) {
            Long diferencia = ChronoUnit.MINUTES.between(this.getFechaDesde(), LocalDateTime.now());
            if ((diferencia >= 0) && (diferencia < this.getInsulina().getDuracion_minutos())) {
                insulinaActivaActual = (this.getInsulina().getDuracion_minutos()- diferencia) * this.getUnidades() / this.getInsulina().getDuracion_minutos();
            }
        }
        return insulinaActivaActual;
    }


}
