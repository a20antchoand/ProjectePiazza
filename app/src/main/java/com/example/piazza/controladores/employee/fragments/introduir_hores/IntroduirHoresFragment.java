package com.example.piazza.controladores.employee.fragments.introduir_hores;

import static com.google.firebase.crashlytics.internal.Logger.TAG;


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
import androidx.navigation.Navigation;

import com.example.piazza.classes.Horario;
import com.example.piazza.classes.Usuario;
import com.example.piazza.fireBase.session.AuthUserSession;
import com.example.testauth.R;
import com.example.testauth.databinding.FragmentIntroduirHoresBinding;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Objects;

import com.example.piazza.commons.OnSwipeTouchListener;

public class IntroduirHoresFragment extends Fragment implements AuthUserSession{


    TextView iniciarTextView;
    TextView acabarTextView;
    TextView benvinguda;
    TextView resultat;
    Button iniciarJornadaBtn;
    Button acabarJornadaBtn;

    DocumentSnapshot document;
    public static Usuario usuarioApp;
    Horario horarioUsuario;


    private FragmentIntroduirHoresBinding binding;
    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentIntroduirHoresBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        setup();

        return root;
    }

    public void setup () {

        root.setOnTouchListener(new OnSwipeTouchListener(getContext()) {
            @Override
            public void onSwipeLeft() {
                super.onSwipeRight();
                Navigation.findNavController(root).navigate(R.id.action_navigation_introduir_hores_to_navigation_historial);
            }

            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
                Navigation.findNavController(root).navigate(R.id.action_navigation_introduir_hores_to_navigation_perfil);
            }
        });

        DocumentReference docRef = DDBB.collection("usuaris").document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getUid()));
        cargarDatosUsuario(docRef, this::getUsuari);

        iniciarTextView = binding.iniciarTextView;
        acabarTextView = binding.acabarTextView;
        resultat =binding.resultat;
        iniciarJornadaBtn = binding.iniciarJornada;
        acabarJornadaBtn =binding.acabarJornada;
        benvinguda = binding.benvingudaIntoduirHores;
        horarioUsuario = new Horario();

        benvinguda.setText("Hola, " + userAuth.getNom().substring(0, 1).toUpperCase() + userAuth.getNom().substring(1));

        root.findViewById(R.id.iniciarJornada).setOnClickListener(view -> {

            iniciarJornada(view);
        });

        root.findViewById(R.id.acabarJornada).setOnClickListener(view -> {

            acabarJornada(view);
        });

        RecuperarRegistroUsuariBBDD();

        escoltarBBDD();

        System.out.println("INTRODUIR_HORES: " + userAuth.getEmail());

    }

    private void getUsuari(Task<DocumentSnapshot> documentSnapshotTask) {

        usuarioApp = documentSnapshotTask.getResult().toObject(Usuario.class);

    }


    private void escoltarBBDD() {

        final DocumentReference docRef = DDBB.collection("horari").document(getFechaActual().getYear() + "_" + getFechaActual().getMonthValue() + "_" + getFechaActual().getDayOfMonth() +  "_" + FirebaseAuth.getInstance().getCurrentUser().getUid());
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

                    RecuperarRegistroUsuariBBDD();

                } else {
                    Log.d(TAG, "Current data: null");

                    comprovarEntradaSortida(false);

                }
            }
        });

    }


    private void RecuperarRegistroUsuariBBDD() {


        DocumentReference docRef = DDBB.collection("horari").document(Objects.requireNonNull(getFechaActual().getYear() + "_" + getFechaActual().getMonthValue() + "_" + getFechaActual().getDayOfMonth() +  "_" + FirebaseAuth.getInstance().getCurrentUser().getUid()));
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                document = task.getResult();

                if (document.exists()) {
                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    comprovarEntradaSortida(true);
                } else {
                    Log.d(TAG, "No such document");
                    changeTextTime(iniciarTextView, 0,0);
                    changeTextTime(acabarTextView, 0,0);
                    changeTextTime(resultat, 0, 0);
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });

    }

    private void GuardarRegistroBBDD() {

        DDBB.collection("horari").document(Objects.requireNonNull(getFechaActual().getYear() + "_" + getFechaActual().getMonthValue() + "_" + getFechaActual().getDayOfMonth() +  "_" + FirebaseAuth.getInstance().getCurrentUser().getUid()))
                .set(horarioUsuario)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "DocumentSnapshot successfully written!");
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error writing document", e);
                });

    }


    private void comprovarEntradaSortida(boolean exist) {

        HashMap<String, Object> data = (HashMap<String, Object>) document.getData();

        if (exist) {
            if ((long) data.get("horaEntrada") != -1) {

                horarioUsuario.setAnioEntrada(Math.toIntExact((Long) data.get("anioEntrada")));
                horarioUsuario.setMesEntrada(Math.toIntExact((Long) data.get("mesEntrada")));
                horarioUsuario.setDiaEntrada(Math.toIntExact((Long) data.get("diaEntrada")));
                horarioUsuario.setHoraEntrada(Math.toIntExact((Long) data.get("horaEntrada")));
                horarioUsuario.setMinutEntrada(Math.toIntExact((Long) data.get("minutEntrada")));

                changeTextTime(iniciarTextView, horarioUsuario.getHoraEntrada(), horarioUsuario.getMinutEntrada());
                iniciarJornadaBtn.setEnabled(false);
                iniciarJornadaBtn.setVisibility(View.GONE);

                if ((long) data.get("horaSalida") != -1) {

                    horarioUsuario.setAnioSalida(Math.toIntExact((Long) data.get("anioSalida")));
                    horarioUsuario.setMesSalida(Math.toIntExact((Long) data.get("mesSalida")));
                    horarioUsuario.setDiaSalida(Math.toIntExact((Long) data.get("diaSalida")));
                    horarioUsuario.setHoraSalida(Math.toIntExact((Long) data.get("horaSalida")));
                    horarioUsuario.setMinutSalida(Math.toIntExact((Long) data.get("minutSalida")));

                    changeTextTime(acabarTextView, horarioUsuario.getHoraSalida(), horarioUsuario.getMinutSalida());

                    acabarJornadaBtn.setEnabled(false);
                    acabarJornadaBtn.setVisibility(View.GONE);

                    calcularHores();

                } else {

                    acabarJornadaBtn.setEnabled(true);
                    acabarJornadaBtn.setVisibility(View.VISIBLE);

                }


            }
        } else {
            iniciarJornadaBtn.setEnabled(true);
            iniciarJornadaBtn.setVisibility(View.VISIBLE);

            changeTextTime(iniciarTextView, 0, 0);
            changeTextTime(acabarTextView, 0, 0);
            changeTextTime(resultat, 0, 0);

            horarioUsuario = new Horario();
        }

    }



    public void getFechaActual(boolean entrada) {

        ZoneId zoneId = ZoneId.of("Europe/Madrid");
        ZonedDateTime zdt = ZonedDateTime.now(zoneId);

        if (entrada) {
            horarioUsuario.setAnioEntrada(zdt.getYear());
            horarioUsuario.setMesEntrada(zdt.getMonthValue());
            horarioUsuario.setDiaEntrada(zdt.getDayOfMonth());
            horarioUsuario.setHoraEntrada(zdt.getHour());
            horarioUsuario.setMinutEntrada(zdt.getMinute());
        } else {
            horarioUsuario.setAnioSalida(zdt.getYear());
            horarioUsuario.setMesSalida(zdt.getMonthValue());
            horarioUsuario.setDiaSalida(zdt.getDayOfMonth());
            horarioUsuario.setHoraSalida(zdt.getHour());
            horarioUsuario.setMinutSalida(zdt.getMinute());

            System.out.println("Antes de guardar: " + horarioUsuario.getMinutSalida());
        }

    }

    public ZonedDateTime getFechaActual() {

        ZoneId zoneId = ZoneId.of("Europe/Madrid");
        ZonedDateTime zdt = ZonedDateTime.now(zoneId);

        return zdt;

    }


    public void iniciarJornada (View view) {

        getFechaActual(true);

        GuardarRegistroBBDD();

        changeTextTime(iniciarTextView, horarioUsuario.getHoraEntrada(), horarioUsuario.getMinutEntrada() );

        iniciarJornadaBtn.setEnabled(false);
        iniciarJornadaBtn.setVisibility(View.GONE);
        acabarJornadaBtn.setEnabled(true);


    }

    public void acabarJornada (View view) {

        getFechaActual(false);

        GuardarRegistroBBDD();

        System.out.println("Despues de guardar: " + horarioUsuario.getMinutSalida());

        changeTextTime(acabarTextView, horarioUsuario.getHoraSalida(), horarioUsuario.getMinutSalida());

        acabarJornadaBtn.setEnabled(false);
        acabarJornadaBtn.setVisibility(View.GONE);

        calcularHores();

    }


    private void calcularHores() {

        LocalDateTime entrada = formatarDateTime(horarioUsuario.getAnioEntrada(), horarioUsuario.getMesEntrada(), horarioUsuario.getDiaEntrada(), horarioUsuario.getHoraEntrada(), horarioUsuario.getMinutEntrada());
        LocalDateTime sortida = formatarDateTime(horarioUsuario.getAnioSalida(), horarioUsuario.getMesSalida(), horarioUsuario.getDiaSalida(), horarioUsuario.getHoraSalida(), horarioUsuario.getMinutSalida());

        Duration diff = Duration.between(entrada, sortida);

        long diffMinuts = diff.toMinutes();

        changeTextTime(resultat, diffMinuts/60, diffMinuts%60);

        horarioUsuario.setUsuario(usuarioApp);
        horarioUsuario.setTotalMinutsTreballats(diffMinuts);

        GuardarRegistroBBDD();

    }

    private LocalDateTime formatarDateTime(int anioEntrada, int mesEntrada, int diaEntrada, int horaEntrada, int minutEntrada) {

        LocalDateTime dataEntrada;

        dataEntrada = LocalDateTime.of(anioEntrada, mesEntrada, diaEntrada, horaEntrada, minutEntrada);

        return dataEntrada;

    }


    public void changeTextTime (TextView textView, long hora, long minut) {
        if (minut < 10)
            textView.setText(hora + ":0" + minut);
        else
            textView.setText(hora + ":" + minut);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}