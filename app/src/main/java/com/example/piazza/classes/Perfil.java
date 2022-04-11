package com.example.piazza.classes;

import android.graphics.Bitmap;

public class Perfil {

    Bitmap bitmap;

    public Perfil(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Perfil () {
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
