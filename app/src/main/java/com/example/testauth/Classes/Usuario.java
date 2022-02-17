package com.example.testauth.Classes;

import java.util.HashMap;
import java.util.Map;

public class Usuario {

    Registro registroEntrada;
    Registro registroSalida;
    String email;

    public Usuario(String email) {
        this.email = email;
        this.registroEntrada = new Registro(0,0,0,0,0);
        this.registroSalida = new Registro(0,0,0,0,0);
    }

    public Usuario (Map<String, Object> info) {

        this.email = (String) info.get("email");

        this.registroEntrada = (Registro) info.get("registroEntrada");
        this.registroSalida = (Registro) info.get("registroSalida");

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
