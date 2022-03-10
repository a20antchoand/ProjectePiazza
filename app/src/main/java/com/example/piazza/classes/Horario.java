package com.example.piazza.classes;

public class Horario {

    Usuario usuario;

    int anioEntrada;
    int mesEntrada;
    int diaEntrada;
    int horaEntrada;
    int minutEntrada;

    int anioSalida;
    int mesSalida;
    int diaSalida;
    int horaSalida;
    int minutSalida;

    long totalMinutsTreballats;

    public Horario(Usuario usuario, int anioEntrada, int mesEntrada, int diaEntrada, int horaEntrada, int minutEntrada, int anioSalida, int mesSalida, int diaSalida, int horaSalida, int minutSalida, long totalMinutsTreballats) {
        this.usuario = usuario;
        this.anioEntrada = anioEntrada;
        this.mesEntrada = mesEntrada;
        this.diaEntrada = diaEntrada;
        this.horaEntrada = horaEntrada;
        this.minutEntrada = minutEntrada;
        this.anioSalida = anioSalida;
        this.mesSalida = mesSalida;
        this.diaSalida = diaSalida;
        this.horaSalida = horaSalida;
        this.minutSalida = minutSalida;
        this.totalMinutsTreballats = totalMinutsTreballats;
    }

    public Horario() {
        anioEntrada = -1;
        anioSalida = -1;
        mesEntrada = -1;
        mesSalida = -1;
        diaEntrada = -1;
        diaSalida = -1;
        horaEntrada = -1;
        horaSalida = -1;
        minutEntrada = -1;
        minutSalida = -1;
    }

    public int getAnioEntrada() {
        return anioEntrada;
    }

    public void setAnioEntrada(int anioEntrada) {
        this.anioEntrada = anioEntrada;
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
        return anioSalida;
    }

    public void setAnioSalida(int anioSalida) {
        this.anioSalida = anioSalida;
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
