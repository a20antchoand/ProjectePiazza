package com.example.piazza.recyclerView.estatTreballadors;

import java.io.Serializable;


/** Classe constructora per els elements de la recycler view amb la informació del estat
 * de les hores dels treballadors.
 */

public class ListElementEstatTreballadors implements Serializable {

    String color;
    String nom;
    String hores;
    String estat;

    /** Classe constructora per els elements de la recycler view amb la informació del estat
     * de les hores dels treballadors.
     *
     * @param color color de l'icona de l'usuari
     * @param nom nom de l'usuari
     * @param hores hores que ha introduit
     * @param estat estat en el que es troba
     */
    public ListElementEstatTreballadors(String color, String nom, String hores, String estat) {
        this.color = color;
        this.nom = nom;
        this.hores = hores;
        this.estat = estat;
    }

    /**
     * Getter d'un element
      * @return color
     */
    public String getColor() {
        return color;
    }

    /**
     * Setter d'un element
     * @return color
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * Getter d'un element
     * @return nom
     */
    public String getNom() {
        return nom;
    }

    /**
     * Setter d'un element
     * @return nom
     */
    public void setNom(String nom) {
        this.nom = nom;
    }

    /**
     * Getter d'un element
     * @return hores
     */
    public String getHores() {
        return hores;
    }

    /**
     * setter d'un element
     * @return hores
     */
    public void setHores(String hores) {
        this.hores = hores;
    }

    /**
     * Getter d'un element
     * @return estat
     */
    public String getEstat() {
        return estat;
    }

    /**
     * Setter d'un element
     * @return estat
     */
    public void setEstat(String estat) {
        this.estat = estat;
    }
}
