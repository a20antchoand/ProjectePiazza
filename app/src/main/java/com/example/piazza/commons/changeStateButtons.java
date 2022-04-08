package com.example.piazza.commons;

import android.content.Context;
import android.view.View;
import android.widget.Button;

public class changeStateButtons {

    public static void hideButton(Button button) {

        button.setEnabled(false);
        button.setVisibility(View.GONE);

    }

    public static void showButton(Button button) {

        button.setEnabled(true);
        button.setVisibility(View.VISIBLE);

    }

}
