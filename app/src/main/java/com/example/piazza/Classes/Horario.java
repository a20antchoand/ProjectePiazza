package com.example.piazza.Classes;

import java.time.ZonedDateTime;

public class Horario {

    Usuario usuario;
    ZonedDateTime entrada;
    ZonedDateTime salida;
    long totalMinutsTreballats;

    public Horario() {
    }

    public Horario(Usuario usuario, ZonedDateTime entrada, ZonedDateTime salida) {
        this.usuario = usuario;
        this.entrada = entrada;
        this.salida = salida;
    }

    public long getTotalMinutsTreballats() {
        return totalMinutsTreballats;
    }

    public void setTotalMinutsTreballats(long totalMinutsTreballats) {
        this.totalMinutsTreballats = totalMinutsTreballats;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public ZonedDateTime getEntrada() {
        return entrada;
    }

    public void setEntrada(ZonedDateTime entrada) {
        this.entrada = entrada;
    }

    public ZonedDateTime getSalida() {
        return salida;
    }

    public void setSalida(ZonedDateTime salida) {
        this.salida = salida;
    }
}
