package com.example.piazza.recyclerView.missatges;

import com.example.piazza.classes.Usuario;
import com.example.piazza.commons.*;
import com.google.firebase.Timestamp;

import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.ExecutionException;

public class Missatge {

    int id;
    Usuario usuari;
    String missatge;
    Timestamp hora;

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


        this.hora = new Timestamp(Date.from(getCurrTimeGMT.zdt.toInstant()));

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

    public Timestamp getHora() {
        return hora;
    }

    public void setHora(Timestamp hora) {
        this.hora = hora;
    }
}
