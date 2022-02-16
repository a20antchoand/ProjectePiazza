package com.example.testauth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;
import java.util.Date;

public class EmployeeActivity extends AppCompatActivity {

    static TextView iniciarTextView;
    static TextView acabarTextView;
    static TextView resultat;
    static Button iniciarJornadaBtn;
    static Button acabarJornadaBtn;
    static String dataEntrada;
    static String dataSortida;
    static Date entrada;
    static Date sortida;
    static int horaEntrada;
    static int minutEntrada;
    static int segonsEntrada;
    static int horaSortida;
    static int minutSortida;
    static int segonsSortida;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee);

        iniciarTextView = ((TextView) findViewById(R.id.iniciarTextView));
        acabarTextView = ((TextView) findViewById(R.id.acabarTextView));
        resultat = ((TextView) findViewById(R.id.resultat));
        iniciarJornadaBtn = ((Button) findViewById(R.id.iniciarJornada));
        acabarJornadaBtn = ((Button) findViewById(R.id.acabarJornada));

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        ((TextView) findViewById(R.id.usuariTextView)).setText(user.getEmail());

        ((Button) findViewById(R.id.logOutEmployee)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseAuth.getInstance().signOut();
                showAuth();
            }
        });

    }

    public String getData (boolean isEntrada) {

        if (isEntrada) {

            entrada = new Date();   // given date
            Calendar calendarEntrada = Calendar.getInstance(); // creates a new calendar instance
            calendarEntrada.setTime(entrada);

            horaEntrada = calendarEntrada.get(Calendar.HOUR_OF_DAY); // gets hour in 24h format
            minutEntrada = calendarEntrada.get(Calendar.MINUTE);        // gets hour in 12h format

            if (minutEntrada < 10) {
                return horaEntrada + ":0" + minutEntrada;
            } else {
                return horaEntrada + ":" + minutEntrada;

            }
        } else {

            sortida = new Date();   // given date
            Calendar calendarSortida = Calendar.getInstance(); // creates a new calendar instance
            calendarSortida.setTime(sortida);

            horaSortida = calendarSortida.get(Calendar.HOUR_OF_DAY); // gets hour in 24h format
            minutSortida = calendarSortida.get(Calendar.MINUTE);        // gets hour in 12h format

            if (minutSortida < 10) {
                return horaSortida + ":0" + minutEntrada;
            } else {
                return horaSortida + ":" + minutEntrada;

            }

        }

    }

    public void iniciarJornada (View view) {
        dataEntrada = getData(true);
        iniciarTextView.setText(dataEntrada);
        iniciarTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, 20);
        iniciarJornadaBtn.setEnabled(false);
    }

    public void acabarJornada (View view) {
        dataSortida = getData(false);
        acabarTextView.setText(dataSortida);
        acabarJornadaBtn.setEnabled(false);

        calcularHores();
    }

    public void resetTime (View view) {
        iniciarTextView.setText("--:--");
        acabarTextView.setText("--:--");
        resultat.setText("--:--");

        iniciarJornadaBtn.setEnabled(true);
        acabarJornadaBtn.setEnabled(true);

    }

    private void calcularHores() {

        long l = sortida.getTime()-entrada.getTime();
        long dia = l/(24*60*60*1000);
        long horasTotals = (1/(60*60*1000)-dia*24);
        long minutsTotals = ((l/(60*1000))-dia*24*60-horasTotals*60);

        ((TextView) findViewById(R.id.resultat)).setText(new StringBuilder().append("Total: ").append(horasTotals + ":" + minutsTotals).toString());
    }

    public void showAuth() {

        Intent intent = new Intent(this, AuthActivity.class);
        startActivity(intent);
        finish();
    }
}