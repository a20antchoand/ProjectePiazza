package com.example.piazza.Classes;

public class Registro {

    long anio;
    long mes;
    long dia;
    long hora;
    long minut;

    public Registro(long anio, long mes, long dia, long hora, long minut) {
        this.anio = anio;
        this.mes = mes;
        this.dia = dia;
        this.hora = hora;
        this.minut = minut;
    }

    public long getAnio() {
        return anio;
    }

    public void setAnio(long anio) {
        this.anio = anio;
    }

    public long getMes() {
        return mes;
    }

    public void setMes(long mes) {
        this.mes = mes;
    }

    public long getDia() {
        return dia;
    }

    public void setDia(long dia) {
        this.dia = dia;
    }

    public long getHora() {
        return hora;
    }

    public void setHora(long hora) {
        this.hora = hora;
    }

    public long getMinut() {
        return minut;
    }

    public void setMinut(long minut) {
        this.minut = minut;
    }
}
