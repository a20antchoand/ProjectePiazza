package com.example.piazza.recyclerView.historialHores;

import java.io.Serializable;

public class ListElementHistorialHores implements Serializable {

    String data;
    String entrada;
    String sortida;
    String total;

    public ListElementHistorialHores(String data, String entrada, String sortida, String total) {
        this.data = data;
        this.entrada = entrada;
        this.sortida = sortida;
        this.total = total;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getEntrada() {
        return entrada;
    }

    public void setEntrada(String entrada) {
        this.entrada = entrada;
    }

    public String getSortida() {
        return sortida;
    }

    public void setSortida(String sortida) {
        this.sortida = sortida;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}
