package com.example.piazza.classes;

import android.graphics.Bitmap;

/**
 * Classe utilitzada per poder emmagatzemar de forma global durant l'us de la applicació la imatge de perfil de l'usauri.
 */
public class Perfil {

    Bitmap bitmap;

    /**
     * Constructor per defecte Perfil
     */
    public Perfil () {
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
