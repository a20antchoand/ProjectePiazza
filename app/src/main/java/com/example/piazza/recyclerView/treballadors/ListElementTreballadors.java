package com.example.piazza.recyclerView.treballadors;

import java.io.Serializable;

public class ListElementTreballadors implements Serializable {

    String uid;
    String color;
    String nom;
    String hores;
    String sou;

    public ListElementTreballadors(String color, String nom, String hores, String sou, String uid) {
        this.color = color;
        this.nom = nom;
        this.hores = hores;
        this.sou = sou;
        this.uid = uid;
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

    public String getSou() {
        return sou;
    }

    public void setSou(String sou) {
        this.sou = sou;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
