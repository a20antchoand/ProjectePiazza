package com.example.testauth;

import static com.google.firebase.crashlytics.internal.Logger.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class EmployeeActivity extends AppCompatActivity {

    TextView iniciarTextView;
    TextView acabarTextView;
    TextView resultat;
    Button iniciarJornadaBtn;
    Button acabarJornadaBtn;

    Map<String, Integer> fechaEntrada;
    Map<String, Integer> fechaSortida;

    HashMap<String, Object> dadesUsuari;

    DocumentSnapshot document;
    FirebaseFirestore db;
    FirebaseUser user;

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
                    comprovarEntradaSortida();
                } else {
                    Log.d(TAG, "No such document");
                    Toast toast=Toast. makeText(getApplicationContext(),"Se crea el usuario en la BBDD",Toast. LENGTH_SHORT);
                    toast. show();
                    GuardarUsuarioBBDD();
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });

    }

    private void GuardarUsuarioBBDD() {

        dadesUsuari = new HashMap<>();
        dadesUsuari.put("email", user.getEmail());
        dadesUsuari.put("hora_entrada", 0);
        dadesUsuari.put("minuts_entrada", 0);
        dadesUsuari.put("hora_sortida", 0);
        dadesUsuari.put("minuts_sortida", 0);

        db.collection("users").document(Objects.requireNonNull(user.getEmail()))
                .set(dadesUsuari)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written!"))
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error writing document", e);
                    Toast toast2=Toast. makeText(getApplicationContext(),"No sha guardat a la BBDD",Toast. LENGTH_SHORT);
                    toast2. setMargin(50,50);
                    toast2. show();
                });

    }

    private void GuardarUsuarioEntradaBBDD(long hora, long minuts) {

        dadesUsuari.put("hora_entrada", hora);
        dadesUsuari.put("minuts_entrada", minuts);

        db.collection("users").document(Objects.requireNonNull(user.getEmail()))
                .set(dadesUsuari)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written!"))
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error writing document", e);
                    Toast toast2=Toast. makeText(getApplicationContext(),"No sha guardat a la BBDD",Toast. LENGTH_SHORT);
                    toast2. setMargin(50,50);
                    toast2. show();
                });

    }

    private void GuardarUsuarioSalidaBBDD(long hora, long minuts) {

        dadesUsuari.put("hora_sortida", hora);
        dadesUsuari.put("minuts_sortida", minuts);

        db.collection("users").document(Objects.requireNonNull(user.getEmail()))
                .set(dadesUsuari)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written!"))
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error writing document", e);
                    Toast toast2=Toast. makeText(getApplicationContext(),"No sha guardat a la BBDD",Toast. LENGTH_SHORT);
                    toast2. setMargin(50,50);
                    toast2. show();
                });

    }



    private void comprovarEntradaSortida() {

        Toast toast=Toast. makeText(getApplicationContext(),""+ Objects.requireNonNull(document.getData()).get("hora_entrada"),Toast. LENGTH_SHORT);
        toast. show();

        Map<String, Object> informacioUsuari = document.getData();

        long horaEntrada = 0;
        long horaSortida = 0;

        try {
            horaEntrada = (long) informacioUsuari.get("hora_entrada");
        } catch (NullPointerException e) {
            Log.d(TAG, "Error agafant hora d'entrada: " + e);
        }

        try {
            horaSortida = (long) informacioUsuari.get("hora_sortida");
        } catch (NullPointerException e) {
            Log.d(TAG, "Error agafant hora de sortida: " + e);
        }

        if (horaEntrada != 0) {

            iniciarTextView.setText(document.getData().get("hora_entrada") + ":" + document.getData().get("minuts_entrada"));

            if (horaSortida != 0) {

                acabarTextView.setText(document.getData().get("hora_sortida") + ":" + document.getData().get("minuts_sortida"));
                acabarJornadaBtn.setEnabled(false);

            } else {

                acabarJornadaBtn.setEnabled(true);
            }

            iniciarJornadaBtn.setEnabled(false);

        } else {
            iniciarJornadaBtn.setEnabled(true);
        }
    }

    public Map<String, Integer> getFechaActual() {

        Map<String, Integer> fecha = new HashMap<>();

        int dia = Integer.parseInt(new SimpleDateFormat("dd", Locale.getDefault()).format(new Date()));
        int mes = Integer.parseInt(new SimpleDateFormat("MM", Locale.getDefault()).format(new Date()));
        int anio = Integer.parseInt(new SimpleDateFormat("yyyy", Locale.getDefault()).format(new Date()));
        int hores = Integer.parseInt(new SimpleDateFormat("HH", Locale.getDefault()).format(new Date()));
        int minuts = Integer.parseInt(new SimpleDateFormat("mm", Locale.getDefault()).format(new Date()));


        fecha.put("dia", dia);
        fecha.put("mes", mes);
        fecha.put("anio", anio);
        fecha.put("hores", hores);
        fecha.put("minuts", minuts);

        return fecha;

    }

    public void iniciarJornada (View view) {

        fechaEntrada = getFechaActual();

        GuardarUsuarioEntradaBBDD(fechaEntrada.get("hores"), fechaEntrada.get("minuts"));

        iniciarJornadaBtn.setEnabled(false);
    }

    public void acabarJornada (View view) {
        fechaSortida = getFechaActual();

        GuardarUsuarioSalidaBBDD(fechaSortida.get("hores"), fechaSortida.get("minuts"));

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

        int horasTotals = fechaSortida.get("hores") - fechaEntrada.get("hores");
        int minutsTotals = fechaSortida.get("minuts") - fechaEntrada.get("minuts");

        if (minutsTotals < 10)
            ((TextView) findViewById(R.id.resultat)).setText(new StringBuilder().append("Total: ").append(horasTotals + ":0" + minutsTotals).toString());
        else
            ((TextView) findViewById(R.id.resultat)).setText(new StringBuilder().append("Total: ").append(horasTotals + ":" + minutsTotals).toString());

    }

    public void showAuth() {

        Intent intent = new Intent(this, AuthActivity.class);
        startActivity(intent);
        finish();
    }
}