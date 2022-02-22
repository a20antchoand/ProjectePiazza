package com.example.piazza.Classes;

public class Horario {

    Usuario usuario;
    Registro entrada;
    Registro salida;

    public Horario(Usuario usuario, Registro entrada, Registro salida) {
        this.usuario = usuario;
        this.entrada = entrada;
        this.salida = salida;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Registro getEntrada() {
        return entrada;
    }

    public void setEntrada(Registro entrada) {
        this.entrada = entrada;
    }

    public Registro getSalida() {
        return salida;
    }

    public void setSalida(Registro salida) {
        this.salida = salida;
    }
}
