package com.example.piazza.classes;

import java.time.ZonedDateTime;

public class Horario {

    Usuario usuario;

    int AnioEntrada;
    int mesEntrada;
    int diaEntrada;
    int horaEntrada;
    int minutEntrada;

    int AnioSalida;
    int mesSalida;
    int diaSalida;
    int horaSalida;
    int minutSalida;

    long totalMinutsTreballats;

    public Horario() {
    }

    public int getAnioEntrada() {
        return AnioEntrada;
    }

    public void setAnioEntrada(int anioEntrada) {
        AnioEntrada = anioEntrada;
    }

    public int getMesEntrada() {
        return mesEntrada;
    }

    public void setMesEntrada(int mesEntrada) {
        this.mesEntrada = mesEntrada;
    }

    public int getDiaEntrada() {
        return diaEntrada;
    }

    public void setDiaEntrada(int diaEntrada) {
        this.diaEntrada = diaEntrada;
    }

    public int getHoraEntrada() {
        return horaEntrada;
    }

    public void setHoraEntrada(int horaEntrada) {
        this.horaEntrada = horaEntrada;
    }

    public int getMinutEntrada() {
        return minutEntrada;
    }

    public void setMinutEntrada(int minutEntrada) {
        this.minutEntrada = minutEntrada;
    }

    public int getAnioSalida() {
        return AnioSalida;
    }

    public void setAnioSalida(int anioSalida) {
        AnioSalida = anioSalida;
    }

    public int getMesSalida() {
        return mesSalida;
    }

    public void setMesSalida(int mesSalida) {
        this.mesSalida = mesSalida;
    }

    public int getDiaSalida() {
        return diaSalida;
    }

    public void setDiaSalida(int diaSalida) {
        this.diaSalida = diaSalida;
    }

    public int getHoraSalida() {
        return horaSalida;
    }

    public void setHoraSalida(int horaSalida) {
        this.horaSalida = horaSalida;
    }

    public int getMinutSalida() {
        return minutSalida;
    }

    public void setMinutSalida(int minutSalida) {
        this.minutSalida = minutSalida;
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

}
