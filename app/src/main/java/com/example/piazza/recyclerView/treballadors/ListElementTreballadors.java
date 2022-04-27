package com.example.piazza.recyclerView.treballadors;

import android.graphics.Bitmap;

import java.io.Serializable;

public class ListElementTreballadors implements Serializable {

    String uid;
    String nom;
    String cognom;

    public ListElementTreballadors(String nom, String cognom, String uid) {
        this.nom = nom;
        this.cognom = cognom;
        this.uid = uid;
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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


}
