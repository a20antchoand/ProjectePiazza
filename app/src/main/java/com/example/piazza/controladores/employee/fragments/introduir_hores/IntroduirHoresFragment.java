package com.example.piazza.controladores.employee.fragments.introduir_hores;

import static com.google.firebase.crashlytics.internal.Logger.TAG;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.piazza.classes.Horario;
import com.example.piazza.classes.Usuario;
import com.example.piazza.fireBase.data.ReadData;
import com.example.piazza.fireBase.session.AuthUserSession;
import com.example.testauth.R;
import com.example.testauth.databinding.FragmentIntroduirHoresBinding;
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

public class IntroduirHoresFragment extends Fragment {


    TextView iniciarTextView;
    TextView acabarTextView;
    TextView resultat;
    Button iniciarJornadaBtn;
    Button acabarJornadaBtn;

    DocumentSnapshot document;
    static Usuario usuarioApp;
    Horario horarioUsuario;


    private FragmentIntroduirHoresBinding binding;
    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentIntroduirHoresBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        setup();

        return root;
    }

    public static void setUsuarioApp(Usuario usuari) {
        usuarioApp = usuari;
    }

    public void setup () {

        AuthUserSession.cargarDatosUsuario(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        iniciarTextView = root.findViewById(R.id.iniciarTextView);
        acabarTextView = root.findViewById(R.id.acabarTextView);
        resultat = root.findViewById(R.id.resultat);
        iniciarJornadaBtn = root.findViewById(R.id.iniciarJornada);
        acabarJornadaBtn = root.findViewById(R.id.acabarJornada);
        horarioUsuario = new Horario();

        root.findViewById(R.id.iniciarJornada).setOnClickListener(view -> {

            iniciarJornada(view);
        });

        root.findViewById(R.id.acabarJornada).setOnClickListener(view -> {

            acabarJornada(view);
        });

        RecuperarRegistroUsuariBBDD();

        escoltarBBDD();

    }

    private void escoltarBBDD() {

        final DocumentReference docRef = AuthUserSession.getDDBB().collection("horari").document(getFechaActual().getYear() + "_" + getFechaActual().getMonthValue() + "_" + getFechaActual().getDayOfMonth() +  "_usuari_" + FirebaseAuth.getInstance().getCurrentUser().getEmail());
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


        DocumentReference docRef = AuthUserSession.getDDBB().collection("horari").document(Objects.requireNonNull(getFechaActual().getYear() + "_" + getFechaActual().getMonthValue() + "_" + getFechaActual().getDayOfMonth() +  "_usuari_" + FirebaseAuth.getInstance().getCurrentUser().getEmail()));
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

        AuthUserSession.getDDBB().collection("horari").document(Objects.requireNonNull(getFechaActual().getYear() + "_" + getFechaActual().getMonthValue() + "_" + getFechaActual().getDayOfMonth() +  "_usuari_" + FirebaseAuth.getInstance().getCurrentUser().getEmail()))
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