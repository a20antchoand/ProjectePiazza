package com.example.piazza.Classes;

public class Usuario {

    Registro registroEntrada;
    Registro registroSalida;
    String email;

    public Usuario(String email) {
        this.email = email;
        this.registroEntrada = new Registro(0,0,0,0,0);
        this.registroSalida = new Registro(0,0,0,0,0);
    }

    public Usuario (String email, Registro entrada, Registro sortida) {

        this.email = email;

        this.registroEntrada = entrada;
        this.registroSalida = sortida;

    }

    public Registro getRegistroEntrada() {
        return registroEntrada;
    }

    public void setRegistroEntrada(Registro registroEntrada) {
        this.registroEntrada = registroEntrada;
    }

    public Registro getRegistroSalida() {
        return registroSalida;
    }

    public void setRegistroSalida(Registro registroSalida) {
        this.registroSalida = registroSalida;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
