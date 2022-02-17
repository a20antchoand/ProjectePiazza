package com.example.piazza.Controladores;

import static com.google.firebase.crashlytics.internal.Logger.TAG;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.piazza.Classes.Contador;
import com.example.piazza.Classes.Registro;
import com.example.piazza.Classes.Usuario;
import com.example.testauth.R;

import com.example.testauth.databinding.ActivityEmployeeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

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

    Contador contador;

    private ActivityEmployeeBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEmployeeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_introduir_hores, R.id.navigation_home)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        setup();

    }

    public void setup () {
        iniciarTextView = findViewById(R.id.iniciarTextView);
        acabarTextView = findViewById(R.id.acabarTextView);
        resultat = findViewById(R.id.resultat);
        iniciarJornadaBtn = findViewById(R.id.iniciarJornada);
        iniciarJornadaBtn.setBackgroundColor(Color.GRAY);
        acabarJornadaBtn = findViewById(R.id.acabarJornada);
        acabarJornadaBtn.setBackgroundColor(Color.GRAY);
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

         contador = new Contador("Contador", findViewById(R.id.contador));

        //((TextView) findViewById(R.id.contador)).setText(user.getEmail());

        findViewById(R.id.logOutEmployee).setOnClickListener(view -> {

            FirebaseAuth.getInstance().signOut();
            showAuth();
        });



        RecuperarUsuariBBDD();


        final DocumentReference docRef = db.collection("users").document(user.getEmail());
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, "Current data: " + snapshot.getData());
                    Toast toast=Toast. makeText(getApplicationContext(),"Se ha modificado la BBDD",Toast. LENGTH_SHORT);
                    toast. show();

                    RecuperarUsuariBBDD();

                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });


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
                    usuarioApp = new Usuario(user.getEmail());
                    GuardarUsuarioBBDD();
                    iniciarJornadaBtn.setEnabled(true);
                    iniciarJornadaBtn.setBackgroundColor(Color.GREEN);
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

        long anioEntrada = Long.parseLong(String.valueOf(e.get("anio")));
        long mesEntrada = Long.parseLong(String.valueOf(e.get("mes")));
        long diaEntrada = Long.parseLong(String.valueOf(e.get("dia")));
        long horaEntrada = Long.parseLong(String.valueOf(e.get("hora")));
        long minutEntrada = Long.parseLong(String.valueOf(e.get("minut")));

        long anioSortida = Long.parseLong(String.valueOf(s.get("anio")));
        long mesSortida = Long.parseLong(String.valueOf(s.get("mes")));
        long diaSortida = Long.parseLong(String.valueOf(s.get("dia")));
        long horaSortida = Long.parseLong(String.valueOf(s.get("hora")));
        long minutSortida = Long.parseLong(String.valueOf(s.get("minut")));

        Registro entrada;
        Registro salida;

        if (horaEntrada != 0) {

            changeTextTime(iniciarTextView, horaEntrada, minutEntrada);
            iniciarJornadaBtn.setEnabled(false);
            iniciarJornadaBtn.setBackgroundColor(Color.GRAY);

            entrada = new Registro(anioEntrada, mesEntrada, diaEntrada, horaEntrada, minutEntrada);

            if (horaSortida != 0) {

                changeTextTime(acabarTextView, horaSortida, minutSortida);

                acabarJornadaBtn.setEnabled(false);
                acabarJornadaBtn.setBackgroundColor(Color.GRAY);

                salida = new Registro(anioSortida, mesSortida, diaSortida, horaSortida, minutSortida);

                usuarioApp = new Usuario(user.getEmail(), entrada, salida);

                calcularHores();

            } else {

                salida = new Registro(0,0,0,0,0);

                usuarioApp = new Usuario(user.getEmail(), entrada, salida);

                acabarJornadaBtn.setEnabled(true);
                acabarJornadaBtn.setBackgroundColor(Color.RED);

            }



        } else {
            iniciarJornadaBtn.setEnabled(true);
            iniciarJornadaBtn.setBackgroundColor(Color.GREEN);

            usuarioApp = new Usuario(user.getEmail());

            changeTextTime(iniciarTextView, horaEntrada, minutEntrada);
            changeTextTime(acabarTextView, horaSortida, minutSortida);
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
        iniciarJornadaBtn.setBackgroundColor(Color.GRAY);

        acabarJornadaBtn.setEnabled(true);
        acabarJornadaBtn.setBackgroundColor(Color.RED);

        new Thread(contador).start();

    }

    public void acabarJornada (View view) {

        usuarioApp.setRegistroSalida(getFechaActual());

        GuardarUsuarioBBDD();

        changeTextTime(acabarTextView, usuarioApp.getRegistroSalida().getHora(), usuarioApp.getRegistroSalida().getMinut());

        acabarJornadaBtn.setEnabled(false);
        acabarJornadaBtn.setBackgroundColor(Color.GRAY);

        contador.pause();

        calcularHores();

    }


    public void resetTime (View view) {
        iniciarTextView.setText("--:--");
        acabarTextView.setText("--:--");
        resultat.setText("--:--");

        contador.pause();
        contador.reiniciar();

        iniciarJornadaBtn.setEnabled(true);
        iniciarJornadaBtn.setBackgroundColor(Color.GREEN);
        acabarJornadaBtn.setEnabled(false);
        acabarJornadaBtn.setBackgroundColor(Color.GRAY);

        usuarioApp.setRegistroEntrada(resetFecha());
        usuarioApp.setRegistroSalida(resetFecha());

        GuardarUsuarioBBDD();
        
    }

    private void calcularHores() {

        long diaEntrada = usuarioApp.getRegistroEntrada().getDia();
        long diaSalida = usuarioApp.getRegistroSalida().getDia();

        long horasTotals = usuarioApp.getRegistroSalida().getHora() - usuarioApp.getRegistroEntrada().getHora();
        long minutsTotals = usuarioApp.getRegistroSalida().getMinut() - usuarioApp.getRegistroEntrada().getMinut();

        changeTextTime(resultat, horasTotals, minutsTotals);

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