package com.example.piazza.recyclerView.historialHores;

import com.example.piazza.classes.Horario;

import java.io.Serializable;

public class ListElementHistorialHores implements Serializable {

    Horario horario;
    String id;
    int diaSetmana;

    public ListElementHistorialHores(Horario horario, String id, int diaSetmana) {
        this.horario = horario;
        this.id = id;
        this.diaSetmana = diaSetmana;
    }

    public Horario getHorario() {
        return horario;
    }

    public void setHorario(Horario horario) {
        this.horario = horario;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getDiaSetmana() {
        return diaSetmana;
    }

    public void setDiaSetmana(int diaSetmana) {
        this.diaSetmana = diaSetmana;
    }
}
