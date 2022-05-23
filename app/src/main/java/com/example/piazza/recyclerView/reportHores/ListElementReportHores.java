package com.example.piazza.recyclerView.reportHores;

import com.example.piazza.classes.Horari;

import java.io.Serializable;

public class ListElementReportHores implements Serializable {

    Horari Horari;

    public ListElementReportHores(Horari Horari) {
        this.Horari = Horari;
    }

    public Horari getHorari() {
        return Horari;
    }

    public void setHorari(Horari Horari) {
        this.Horari = Horari;
    }

}
