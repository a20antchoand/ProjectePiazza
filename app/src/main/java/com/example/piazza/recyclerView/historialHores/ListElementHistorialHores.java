package com.example.piazza.recyclerView.historialHores;

import com.example.piazza.classes.Horari;

import java.io.Serializable;

public class ListElementHistorialHores implements Serializable {

    Horari Horari;
    String id;

    public ListElementHistorialHores(Horari Horari, String id) {
        this.Horari = Horari;
        this.id = id;
    }

    public Horari getHorari() {
        return Horari;
    }

    public void setHorari(Horari Horari) {
        this.Horari = Horari;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
