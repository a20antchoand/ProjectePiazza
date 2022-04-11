package com.example.piazza.classes;

public class Usuario {

    String uid;
    String email;
    String telefono;
    String nom;
    String cognom;
    String horesMensuals;
    String rol;
    String urlPerfil;


    public Usuario () {
    }

    public Usuario(String uid, String email, String nom, String cognom, String telefono, String rol, String horesMensuals, String urlPerfil) {
        this.uid = uid;
        this.email = email;
        this.telefono = telefono;
        this.rol = rol;
        this.nom = nom;
        this.cognom = cognom;
        this.horesMensuals = horesMensuals;
        this.urlPerfil = urlPerfil;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHoresMensuals() {
        return horesMensuals;
    }

    public void setHoresMensuals(String horesMensuals) {
        this.horesMensuals = horesMensuals;
    }

    public String getUrlPerfil() {
        return urlPerfil;
    }

    public void setUrlPerfil(String urlPerfil) {
        this.urlPerfil = urlPerfil;
    }
}
