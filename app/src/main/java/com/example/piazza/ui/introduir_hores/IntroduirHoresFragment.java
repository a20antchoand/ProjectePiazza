package com.example.piazza.ui.introduir_hores;

import static com.google.firebase.crashlytics.internal.Logger.TAG;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.piazza.Classes.Contador;
import com.example.piazza.Classes.Registro;
import com.example.piazza.Classes.Usuario;
import com.example.piazza.Controladores.AuthActivity;
import com.example.testauth.R;
import com.example.testauth.databinding.FragmentIntroduirHoresBinding;
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

public class IntroduirHoresFragment extends Fragment {


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



    private FragmentIntroduirHoresBinding binding;
    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentIntroduirHoresBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        setup();

        return root;
    }



    public void setup () {
        iniciarTextView = root.findViewById(R.id.iniciarTextView);
        acabarTextView = root.findViewById(R.id.acabarTextView);
        resultat = root.findViewById(R.id.resultat);
        iniciarJornadaBtn = root.findViewById(R.id.iniciarJornada);
        iniciarJornadaBtn.setBackgroundColor(Color.GRAY);
        acabarJornadaBtn = root.findViewById(R.id.acabarJornada);
        acabarJornadaBtn.setBackgroundColor(Color.GRAY);
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        contador = new Contador("Contador", root.findViewById(R.id.contador));

        //((TextView) findViewById(R.id.contador)).setText(user.getEmail());

        root.findViewById(R.id.logOutEmployee).setOnClickListener(view -> {

            logOut();
        });


        root.findViewById(R.id.resetTime).setOnClickListener(view -> {

            resetTime(view);
        });

        root.findViewById(R.id.iniciarJornada).setOnClickListener(view -> {

            iniciarJornada(view);
        });

        root.findViewById(R.id.acabarJornada).setOnClickListener(view -> {

            acabarJornada(view);
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


    public void logOut() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getActivity(), AuthActivity.class);
        startActivity(intent);

    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}