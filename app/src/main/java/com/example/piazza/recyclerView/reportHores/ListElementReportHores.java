package com.example.piazza.recyclerView.reportHores;

import com.example.piazza.classes.Horario;

import java.io.Serializable;

public class ListElementReportHores implements Serializable {

    Horario horario;

    public ListElementReportHores(Horario horario) {
        this.horario = horario;
    }

    public Horario getHorario() {
        return horario;
    }

    public void setHorario(Horario horario) {
        this.horario = horario;
    }

}
