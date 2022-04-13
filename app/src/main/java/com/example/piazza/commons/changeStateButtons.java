package com.example.piazza.commons;

import android.content.Context;
import android.view.View;
import android.widget.Button;

public class changeStateButtons {

    /**
     * Oculta un buto de la interficie
     * @param button
     */
    public static void hideButton(Button button) {

        button.setEnabled(false);
        button.setVisibility(View.GONE);

    }

    /**
     * Mostra un boto d ela interficie
     * @param button
     */
    public static void showButton(Button button) {

        button.setEnabled(true);
        button.setVisibility(View.VISIBLE);

    }

}
