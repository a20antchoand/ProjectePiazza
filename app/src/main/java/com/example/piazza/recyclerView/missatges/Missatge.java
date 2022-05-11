package com.example.piazza.recyclerView.missatges;

import com.example.piazza.classes.Usuario;
import com.example.piazza.commons.*;

import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.ExecutionException;

public class Missatge {

    int id;
    Usuario usuari;
    String missatge;
    GregorianCalendar hora;

    public Missatge () {}

    public Missatge(Usuario usuari, String missatge) {
        this.usuari = usuari;
        this.missatge = missatge;

        String stringTemps = null;
        try {
            stringTemps = new getCurrTimeGMT().execute().get();
            getCurrTimeGMT.zdt = getCurrTimeGMT.getZoneDateTime(stringTemps);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ZonedDateTime zdt = getCurrTimeGMT.zdt;
        this.hora = GregorianCalendar.from(zdt);
    }

    public Usuario getUsuari() {
        return usuari;
    }

    public void setUsuari(Usuario usuari) {
        this.usuari = usuari;
    }

    public String getMissatge() {
        return missatge;
    }

    public void setMissatge(String missatge) {
        this.missatge = missatge;
    }

    public GregorianCalendar getHora() {
        return hora;
    }

    public void setHora(GregorianCalendar hora) {
        this.hora = hora;
    }
}
