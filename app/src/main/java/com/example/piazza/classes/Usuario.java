package com.example.piazza.classes;

public class Usuario {

    String email;
    String telefono;
    String nom;
    String cognom;
    String salario;


    public Usuario () {
    }

    public Usuario(String email, String nom, String cognom, String telefono, String salario) {
        this.email = email;
        this.telefono = telefono;
        this.salario = salario;
        this.nom = nom;
        this.cognom = cognom;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getCognom() {
        return cognom;
    }

    public void setCognom(String cognom) {
        this.cognom = cognom;
    }

    public String getSalario() {
        return salario;
    }

    public void setSalario(String salario) {
        this.salario = salario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
