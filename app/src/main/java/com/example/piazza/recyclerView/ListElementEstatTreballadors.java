package com.example.piazza.recyclerView;

import java.io.Serializable;

public class ListElementEstatTreballadors implements Serializable {

    String color;
    String nom;
    String hores;
    String estat;

    public ListElementEstatTreballadors(String color, String nom, String hores, String estat) {
        this.color = color;
        this.nom = nom;
        this.hores = hores;
        this.estat = estat;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getHores() {
        return hores;
    }

    public void setHores(String hores) {
        this.hores = hores;
    }

    public String getEstat() {
        return estat;
    }

    public void setEstat(String estat) {
        this.estat = estat;
    }
}
