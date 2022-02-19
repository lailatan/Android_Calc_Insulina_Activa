package com.lailatan.calc_insulina_activa.entities;

import java.io.Serializable;

public class Ratio implements Serializable {
    private Integer ratio_id;
    private Integer hora;
    private Double valor;

    public Ratio() {
    }

    public Ratio(Integer ratio_id, Integer hora, Double valor) {
        this.ratio_id = ratio_id;
        this.hora = hora;
        this.valor = valor;
    }

    public Ratio(Integer hora, Double valor) {
        this.hora = hora;
        this.valor = valor;
    }

    public Integer getRatio_id() {
        return ratio_id;
    }

    public void setRatio_id(Integer ratio_id) {
        this.ratio_id = ratio_id;
    }

    public Integer getHora() {
        return hora;
    }

    public void setHora(Integer hora) {
        this.hora = hora;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }


    @Override
    public String toString() {
        return "Ratio{" +
                "insulina_id=" + ratio_id +
                ", hora=" + hora +
                ", valor=" + valor +
                '}';
    }
}
