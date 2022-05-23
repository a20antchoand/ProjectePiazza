package com.example.piazza.classes;

public class Usuari {

    String uid;
    String email;
    String telefono;
    String nom;
    String cognom;
    String horesMensuals;
    String diesSetmana;
    String rol;
    Boolean treballant = false;
    String urlPerfil;
    String empresa;

    /**
     * Constructor per defecte d'Usuari
     */
    public Usuari () {
    }

    /**
     * Constructor per la classe Usuari
     *
     * @param uid ID de l'usuari
     * @param email Email de l'usuari
     * @param nom Nom de l'usuari
     * @param cognom Cognom de l'usuari
     * @param telefono Telefon de l'usuari
     * @param rol Rol de l'usuari
     * @param horesMensuals Hores que realitza mensuals
     * @param diesSetmana Dies que treballa setmanals
     * @param urlPerfil URL per la foto de perfil (DEPRECAT)
     * @param empresa Empresa en la que treballa
     */

    public Usuari(String uid, String email, String nom, String cognom, String telefono, String rol, String horesMensuals, String diesSetmana, String urlPerfil, String empresa) {
        this.uid = uid;
        this.email = email;
        this.telefono = telefono;
        this.rol = rol;
        this.nom = nom;
        this.cognom = cognom;
        this.horesMensuals = horesMensuals;
        this.diesSetmana = diesSetmana;
        this.urlPerfil = urlPerfil;
        this.empresa = empresa;
    }

    public Usuari(String uid, String email, String nom, String cognom, String telefono, String rol, String empresa) {
        this.uid = uid;
        this.email = email;
        this.telefono = telefono;
        this.nom = nom;
        this.cognom = cognom;
        this.rol = rol;
        this.empresa = empresa;
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

    public String getDiesSetmana() {
        return diesSetmana;
    }

    public void setDiesSetmana(String diesSetmana) {
        this.diesSetmana = diesSetmana;
    }

    public String getUrlPerfil() {
        return urlPerfil;
    }

    public void setUrlPerfil(String urlPerfil) {
        this.urlPerfil = urlPerfil;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public Boolean getTreballant() {
        return treballant;
    }

    public void setTreballant(Boolean treballant) {
        this.treballant = treballant;
    }
}
