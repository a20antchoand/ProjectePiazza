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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.errorprone.annotations.Var;
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

public class EmployeeActivity extends AppCompatActivity {

    static TextView iniciarTextView;
    static TextView acabarTextView;
    static TextView resultat;
    static Button iniciarJornadaBtn;
    static Button acabarJornadaBtn;
    static Map<String, Integer> dataEntrada;
    static Map<String, Integer> dataSortida;
    static DocumentSnapshot document;
    static FirebaseFirestore db;
    static FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee);

        setup();

    }

    public void setup () {
        iniciarTextView = ((TextView) findViewById(R.id.iniciarTextView));
        acabarTextView = ((TextView) findViewById(R.id.acabarTextView));
        resultat = ((TextView) findViewById(R.id.resultat));
        iniciarJornadaBtn = ((Button) findViewById(R.id.iniciarJornada));
        acabarJornadaBtn = ((Button) findViewById(R.id.acabarJornada));
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        ((TextView) findViewById(R.id.usuariTextView)).setText(user.getEmail());

        ((Button) findViewById(R.id.logOutEmployee)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseAuth.getInstance().signOut();
                showAuth();
            }
        });

        validarUsuarioDB(user);

    }

    private void validarUsuarioDB(FirebaseUser user) {

        RecuperarUsuariBBDD();

    }


    private void CrearUsuarioBBDD(FirebaseFirestore db, FirebaseUser user) {

        HashMap<String, Object> map = new HashMap<>();
        map.put("email", user.getEmail());
        map.put("hora_entrada", 0);
        map.put("minuts_entrada", 0);
        map.put("hora_sortida", 0);
        map.put("minuts_sortida", 0);

        db.collection("users").document(user.getEmail())
                .set(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                        Toast toast2=Toast. makeText(getApplicationContext(),"No sha guardat a la BBDD",Toast. LENGTH_SHORT);
                        toast2. setMargin(50,50);
                        toast2. show();
                    }
                });

    }

    private void RecuperarUsuariBBDD() {


        DocumentReference docRef = db.collection("users").document(user.getEmail());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        comprovarEntradaSortida();
                    } else {
                        Log.d(TAG, "No such document");
                        Toast toast=Toast. makeText(getApplicationContext(),"Se crea el usuario en la BBDD",Toast. LENGTH_SHORT);
                        toast. show();
                        CrearUsuarioBBDD(db, user);
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }


        });

    }

    private void comprovarEntradaSortida() {

        Toast toast=Toast. makeText(getApplicationContext(),""+document.getData().get("hora_entrada"),Toast. LENGTH_SHORT);
        toast. show();

        long horaEntrada = (long) document.getData().get("hora_entrada");
        long horaSortida = (long) document.getData().get("hora_sortida");

        if (horaEntrada != 0) {
            iniciarTextView.setText(document.getData().get("hora_entrada") + ":" + document.getData().get("minuts_entrada"));

            if (horaSortida != 0) {

                acabarTextView.setText(document.getData().get("hora_sortida") + ":" + document.getData().get("minuts_sortida"));
            }

        }
    }

    public Map<String, Integer> getDataActual() {

        Map<String, Integer> data = new HashMap<>();

        int dia = Integer.parseInt(new SimpleDateFormat("dd", Locale.getDefault()).format(new Date()));
        int mes = Integer.parseInt(new SimpleDateFormat("MM", Locale.getDefault()).format(new Date()));
        int hores = Integer.parseInt(new SimpleDateFormat("HH", Locale.getDefault()).format(new Date()));
        int minuts = Integer.parseInt(new SimpleDateFormat("mm", Locale.getDefault()).format(new Date()));


        data.put("dia", dia);
        data.put("mes", mes);
        data.put("hores", hores);
        data.put("minuts", minuts);

        return data;

    }

    public void iniciarJornada (View view) {

        dataEntrada = getDataActual();

        Map<String, Integer> horaEntrada = new HashMap<>();

        horaEntrada.put("hora_entrada", dataEntrada.get("hores"));
        horaEntrada.put("minuts_entrada", dataEntrada.get("minuts"));

        db.collection("users").document(user.getEmail())
                .set(horaEntrada)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        Toast toast=Toast. makeText(getApplicationContext(),"Actualitzada hora d'entrada",Toast. LENGTH_SHORT);
                        toast. show();
                        iniciarTextView.setText(dataEntrada.get("hores") + ":" + dataEntrada.get("minuts"));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                        Toast toast=Toast. makeText(getApplicationContext(),"No sha actualitzat a la BBDD",Toast. LENGTH_SHORT);
                        toast. show();
                    }
                });

        iniciarJornadaBtn.setEnabled(false);
    }

    public void acabarJornada (View view) {
        dataSortida = getDataActual();

        Toast toast=Toast. makeText(getApplicationContext(),dataSortida.get("dia") + "/" + dataSortida.get("mes"),Toast. LENGTH_SHORT);
        toast. show();

        acabarTextView.setText(dataSortida.get("hores") + ":" + dataSortida.get("minuts"));
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

        int horasTotals = dataSortida.get("hores") - dataEntrada.get("hores");
        int minutsTotals = dataSortida.get("minuts") - dataEntrada.get("minuts");

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