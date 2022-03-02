package com.example.piazza.controladores.employee.Fragments.introduir_hores;

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
    Horario horarioUsuaroi;


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
        iniciarJornadaBtn.setBackgroundColor(Color.GREEN);
        acabarJornadaBtn = root.findViewById(R.id.acabarJornada);
        acabarJornadaBtn.setBackgroundColor(Color.GRAY);
        horarioUsuaroi = new Horario();

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
                    //comprovarEntradaSortida();
                } else {
                    Log.d(TAG, "No such document");
                    GuardarRegistroBBDD();
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });

    }

    private void GuardarRegistroBBDD() {

        AuthUserSession.getDDBB().collection("horari").document(Objects.requireNonNull(getFechaActual().getYear() + "_" + getFechaActual().getMonthValue() + "_" + getFechaActual().getDayOfMonth() +  "_usuari_" + FirebaseAuth.getInstance().getCurrentUser().getEmail()))
                .set(horarioUsuaroi)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "DocumentSnapshot successfully written!");
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error writing document", e);
                });

    }


    private void comprovarEntradaSortida() {

        HashMap<String, Object> entrada = (HashMap<String, Object>) document.getData().get("entrada");
        HashMap<String, Object> sortida = (HashMap<String, Object>) document.getData().get("salida");
        System.out.println("USER: " + AuthUserSession.getUser());

        LocalDateTime dataEntrada;
        LocalDateTime dataSortida;

        if (entrada != null) {

            horarioUsuaroi.setMinutEntrada(Math.toIntExact((Long) entrada.get("year")));
            horarioUsuaroi.setMinutEntrada(Math.toIntExact((Long) entrada.get("monthValue")));
            horarioUsuaroi.setMinutEntrada(Math.toIntExact((Long) entrada.get("dayOfMonth")));
            horarioUsuaroi.setMinutEntrada(Math.toIntExact((Long) entrada.get("hour")));
            horarioUsuaroi.setMinutEntrada(Math.toIntExact((Long) entrada.get("minute")));

            changeTextTime(iniciarTextView, horarioUsuaroi.getHoraEntrada(), horarioUsuaroi.getMinutEntrada());
            iniciarJornadaBtn.setEnabled(false);
            iniciarJornadaBtn.setBackgroundColor(Color.GRAY);

            if (sortida != null) {

                horarioUsuaroi.setMinutSalida(Math.toIntExact((Long) sortida.get("year")));
                horarioUsuaroi.setMinutSalida(Math.toIntExact((Long) sortida.get("monthValue")));
                horarioUsuaroi.setMinutSalida(Math.toIntExact((Long) sortida.get("dayOfMonth")));
                horarioUsuaroi.setMinutSalida(Math.toIntExact((Long) sortida.get("hour")));
                horarioUsuaroi.setMinutSalida(Math.toIntExact((Long) sortida.get("minute")));

                changeTextTime(acabarTextView, horarioUsuaroi.getHoraSalida(), horarioUsuaroi.getMinutSalida());

                calcularHores();

                acabarJornadaBtn.setEnabled(false);
                acabarJornadaBtn.setBackgroundColor(Color.GRAY);

            } else {

                acabarJornadaBtn.setEnabled(true);
                acabarJornadaBtn.setBackgroundColor(Color.RED);

            }



        } else {
            iniciarJornadaBtn.setEnabled(true);
            iniciarJornadaBtn.setBackgroundColor(Color.GREEN);

            changeTextTime(iniciarTextView, 0, 0);
            changeTextTime(acabarTextView, 0, 0);
        }


    }

    public void getFechaActual(boolean entrada) {

        ZoneId zoneId = ZoneId.of("Europe/Madrid");
        ZonedDateTime zdt = ZonedDateTime.now(zoneId);
        System.out.println(zdt);

        if (entrada) {
            horarioUsuaroi.setAnioEntrada(zdt.getYear());
            horarioUsuaroi.setMesEntrada(zdt.getMonthValue());
            horarioUsuaroi.setDiaEntrada(zdt.getDayOfMonth());
            horarioUsuaroi.setHoraEntrada(zdt.getHour());
            horarioUsuaroi.setMinutEntrada(zdt.getMinute());
        } else {
            horarioUsuaroi.setAnioSalida(zdt.getYear());
            horarioUsuaroi.setMesSalida(zdt.getMonthValue());
            horarioUsuaroi.setDiaSalida(zdt.getDayOfMonth());
            horarioUsuaroi.setHoraSalida(zdt.getHour());
            horarioUsuaroi.setMinutSalida(zdt.getMinute());
        }

    }

    public ZonedDateTime getFechaActual() {

        ZoneId zoneId = ZoneId.of("Europe/Madrid");
        ZonedDateTime zdt = ZonedDateTime.now(zoneId);
        System.out.println(zdt);

        return zdt;

    }


    public void iniciarJornada (View view) {

        getFechaActual(true);

        GuardarRegistroBBDD();

        changeTextTime(iniciarTextView,horarioUsuaroi.getHoraEntrada(), horarioUsuaroi.getMinutEntrada() );

        iniciarJornadaBtn.setEnabled(false);
        iniciarJornadaBtn.setBackgroundColor(Color.GRAY);

        acabarJornadaBtn.setEnabled(true);
        acabarJornadaBtn.setBackgroundColor(Color.RED);

    }

    public void acabarJornada (View view) {

        getFechaActual(false);

        System.out.println("Mes " + horarioUsuaroi.getMesSalida());

        GuardarRegistroBBDD();

        changeTextTime(acabarTextView, horarioUsuaroi.getHoraSalida(), horarioUsuaroi.getMinutSalida());

        acabarJornadaBtn.setEnabled(false);
        acabarJornadaBtn.setBackgroundColor(Color.GRAY);

        calcularHores();

    }


    private void calcularHores() {

        LocalDateTime entrada = formatarDateTime(horarioUsuaroi.getAnioEntrada(), horarioUsuaroi.getMesEntrada(), horarioUsuaroi.getDiaEntrada(), horarioUsuaroi.getHoraEntrada(), horarioUsuaroi.getMinutEntrada());
        LocalDateTime sortida = formatarDateTime(horarioUsuaroi.getAnioSalida(), horarioUsuaroi.getMesSalida(), horarioUsuaroi.getDiaSalida(), horarioUsuaroi.getHoraSalida(), horarioUsuaroi.getMinutSalida());

        Duration diff = Duration.between(entrada, sortida);

        long diffMinuts = diff.toMinutes();

        changeTextTime(resultat, diffMinuts/60, diffMinuts%60);

        horarioUsuaroi.setUsuario(usuarioApp);
        horarioUsuaroi.setTotalMinutsTreballats(diffMinuts);

        System.out.println("Horario/Usuario:  " + horarioUsuaroi.getUsuario());
        System.out.println("UsuarioAPP:  " + usuarioApp.getNom());

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