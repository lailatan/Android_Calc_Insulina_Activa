package com.lailatan.calc_insulina_activa.entities;

import java.io.Serializable;

public class Insulina implements Serializable {
    private Integer insulina_id;
    private String nombre;
    private String laboratorio;
    private Integer rapida;
    private Integer duracion_minutos; //minutos
    private String descripcion;

    public Insulina() {
    }

    public Insulina(String nombre, String laboratorio, Integer rapida, Integer duracion_minutos, String descripcion) {
        this.insulina_id = 0;
        this.nombre = nombre;
        this.laboratorio = laboratorio;
        this.rapida = rapida;
        this.duracion_minutos = duracion_minutos;
        this.descripcion = descripcion;
    }

    public Insulina(Integer insulina_id, String nombre, String laboratorio, Integer rapida, Integer duracion_minutos, String descripcion) {
        this.insulina_id = insulina_id;
        this.nombre = nombre;
        this.laboratorio = laboratorio;
        this.rapida = rapida;
        this.duracion_minutos = duracion_minutos;
        this.descripcion = descripcion;
    }

    public Integer getInsulina_id() {
        return insulina_id;
    }

    public void setInsulina_id(Integer insulina_id) {
        this.insulina_id = insulina_id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getLaboratorio() {
        return laboratorio;
    }

    public void setLaboratorio(String laboratorio) {
        this.laboratorio = laboratorio;
    }

    public Integer getRapida() {
        return rapida;
    }

    public void setRapida(Integer rapida) {
        this.rapida = rapida;
    }

    public Integer getDuracion_minutos() {
        return duracion_minutos;
    }

    public void setDuracion_minutos(Integer duracion_minutos) {
        this.duracion_minutos = duracion_minutos;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return "Insulina{" +
                "insulina_id=" + insulina_id +
                ", nombre='" + nombre + '\'' +
                ", laboratorio='" + laboratorio + '\'' +
                ", rapida=" + rapida +
                ", duracion_minutos=" + duracion_minutos +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
}
