package com.example.testauth.Controladores;

import static com.google.firebase.crashlytics.internal.Logger.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testauth.Classes.Registro;
import com.example.testauth.Classes.Usuario;
import com.example.testauth.R;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class EmployeeActivity extends AppCompatActivity {

    TextView iniciarTextView;
    TextView acabarTextView;
    TextView resultat;
    Button iniciarJornadaBtn;
    Button acabarJornadaBtn;

    DocumentSnapshot document;
    FirebaseFirestore db;
    FirebaseUser user;
    Usuario usuarioApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee);

        setup();

    }

    public void setup () {
        iniciarTextView = findViewById(R.id.iniciarTextView);
        acabarTextView = findViewById(R.id.acabarTextView);
        resultat = findViewById(R.id.resultat);
        iniciarJornadaBtn = findViewById(R.id.iniciarJornada);
        acabarJornadaBtn = findViewById(R.id.acabarJornada);
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        ((TextView) findViewById(R.id.usuariTextView)).setText(user.getEmail());

        findViewById(R.id.logOutEmployee).setOnClickListener(view -> {

            FirebaseAuth.getInstance().signOut();
            showAuth();
        });

        RecuperarUsuariBBDD();

    }


    private void RecuperarUsuariBBDD() {


        DocumentReference docRef = db.collection("users").document(Objects.requireNonNull(user.getEmail()));
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                document = task.getResult();

                if (document.exists()) {
                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    usuarioApp = new Usuario(document.getData().get("email").toString());
                    comprovarEntradaSortida();
                } else {
                    Log.d(TAG, "No such document");
                    Toast toast=Toast. makeText(getApplicationContext(),"Se crea el usuario en la BBDD",Toast. LENGTH_SHORT);
                    toast. show();
                    usuarioApp = new Usuario(user.getEmail());
                    GuardarUsuarioBBDD();
                    iniciarJornadaBtn.setEnabled(true);
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });

    }

    private void GuardarUsuarioBBDD() {


        db.collection("users").document(Objects.requireNonNull(user.getEmail()))
                .set(usuarioApp)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "DocumentSnapshot successfully written!");
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error writing document", e);
                    Toast toast2=Toast. makeText(getApplicationContext(),"No sha guardat a la BBDD",Toast. LENGTH_SHORT);
                    toast2. setMargin(50,50);
                    toast2. show();
                });

    }


    private void comprovarEntradaSortida() {

        HashMap e = (HashMap) document.getData().get("registroEntrada");
        HashMap s = (HashMap) document.getData().get("registroSalida");

        long horaEntrada = Long.parseLong(String.valueOf(e.get("hora")));
        long minutEntrada = Long.parseLong(String.valueOf(e.get("minut")));
        long horaSortida = Long.parseLong(String.valueOf(s.get("hora")));
        long minutSortida = Long.parseLong(String.valueOf(s.get("minut")));

        if (horaEntrada != 0) {

            iniciarTextView.setText(horaEntrada + ":" + minutEntrada);
            iniciarJornadaBtn.setEnabled(false);

            if (horaSortida != 0) {

                acabarTextView.setText(horaSortida + ":" + minutSortida);
                acabarJornadaBtn.setEnabled(false);

            } else {

                acabarJornadaBtn.setEnabled(true);
            }

        } else {
            iniciarJornadaBtn.setEnabled(true);
        }


    }

    public Registro getFechaActual() {

        int dia = Integer.parseInt(new SimpleDateFormat("dd", Locale.getDefault()).format(new Date()));
        int mes = Integer.parseInt(new SimpleDateFormat("MM", Locale.getDefault()).format(new Date()));
        int anio = Integer.parseInt(new SimpleDateFormat("yyyy", Locale.getDefault()).format(new Date()));
        int hora = Integer.parseInt(new SimpleDateFormat("HH", Locale.getDefault()).format(new Date()));
        int minut = Integer.parseInt(new SimpleDateFormat("mm", Locale.getDefault()).format(new Date()));

        return new Registro(anio, mes, dia, hora, minut);

    }

    public Registro resetFecha() {

        return new Registro(0,0,0,0,0);

    }

    public void iniciarJornada (View view) {

        usuarioApp.setRegistroEntrada(getFechaActual());

        GuardarUsuarioBBDD();

        changeTextTime(iniciarTextView,usuarioApp.getRegistroEntrada().getHora(), usuarioApp.getRegistroEntrada().getMinut() );

        iniciarJornadaBtn.setEnabled(false);

        acabarJornadaBtn.setEnabled(true);
    }

    public void acabarJornada (View view) {

        usuarioApp.setRegistroSalida(getFechaActual());

        GuardarUsuarioBBDD();

        changeTextTime(acabarTextView, usuarioApp.getRegistroSalida().getHora(), usuarioApp.getRegistroSalida().getMinut());

        acabarJornadaBtn.setEnabled(false);

        calcularHores();

    }


    public void resetTime (View view) {
        iniciarTextView.setText("--:--");
        acabarTextView.setText("--:--");
        resultat.setText("--:--");

        iniciarJornadaBtn.setEnabled(true);
        acabarJornadaBtn.setEnabled(false);

        usuarioApp.setRegistroEntrada(resetFecha());
        usuarioApp.setRegistroSalida(resetFecha());

        GuardarUsuarioBBDD();
        
    }

    private void calcularHores() {

        long diaEntrada = usuarioApp.getRegistroEntrada().getDia();
        long diaSalida = usuarioApp.getRegistroSalida().getDia();

        long horasTotals = usuarioApp.getRegistroSalida().getHora() - usuarioApp.getRegistroEntrada().getHora();
        long minutsTotals = usuarioApp.getRegistroSalida().getMinut() - usuarioApp.getRegistroEntrada().getMinut();

        changeTextTime((TextView) findViewById(R.id.resultat), horasTotals, minutsTotals);

    }


    public void changeTextTime (TextView textView, long hora, long minut) {
        if (minut < 10)
            textView.setText(hora + ":0" + minut);
        else
            textView.setText(hora + ":" + minut);
    }


    public void showAuth() {

        Intent intent = new Intent(this, AuthActivity.class);
        startActivity(intent);
        finish();
    }
}