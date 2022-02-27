package com.example.piazza.Controladores.Employee.Fragments.introduir_hores;

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

import com.example.piazza.Classes.Horario;
import com.example.piazza.Classes.Registro;
import com.example.piazza.Classes.Usuario;
import com.example.piazza.FireBase.Session.AuthUserSession;
import com.example.testauth.R;
import com.example.testauth.databinding.FragmentIntroduirHoresBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.time.Duration;
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

        root.findViewById(R.id.resetTime).setOnClickListener(view -> {

            resetTime(view);
        });

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
                    comprovarEntradaSortida();
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

        System.out.println("======================================================================================");
        System.out.println(entrada.get("minute"));
        System.out.println("======================================================================================");

        long horaEntrada = (Long) entrada.get("hour");
        long minutEntrada = (Long) entrada.get("minute");

        long horaSortida = (Long) entrada.get("hour");
        long minutSortida = (Long) entrada.get("minute");

        if (horaEntrada != -1) {

            changeTextTime(iniciarTextView, horaEntrada, minutEntrada);
            iniciarJornadaBtn.setEnabled(false);
            iniciarJornadaBtn.setBackgroundColor(Color.GRAY);

            if (horaSortida != -1) {

                changeTextTime(acabarTextView, horaSortida, minutSortida);

                acabarJornadaBtn.setEnabled(false);
                acabarJornadaBtn.setBackgroundColor(Color.GRAY);

            } else {

                acabarJornadaBtn.setEnabled(true);
                acabarJornadaBtn.setBackgroundColor(Color.RED);

            }



        } else {
            iniciarJornadaBtn.setEnabled(true);
            iniciarJornadaBtn.setBackgroundColor(Color.GREEN);

            changeTextTime(iniciarTextView, horaEntrada, minutEntrada);
            changeTextTime(acabarTextView, horaSortida, minutSortida);
        }


    }

    public ZonedDateTime getFechaActual() {

        ZoneId zoneId = ZoneId.of("Europe/Madrid");
        ZonedDateTime zdt = ZonedDateTime.now(zoneId);
        System.out.println(zdt);

        return zdt;

    }

    public ZonedDateTime resetFecha() {

        return ZonedDateTime.now();

    }

    public void iniciarJornada (View view) {

        horarioUsuaroi.setEntrada(getFechaActual());

        GuardarRegistroBBDD();

        changeTextTime(iniciarTextView,horarioUsuaroi.getEntrada().getHour(), horarioUsuaroi.getEntrada().getMinute() );

        iniciarJornadaBtn.setEnabled(false);
        iniciarJornadaBtn.setBackgroundColor(Color.GRAY);

        acabarJornadaBtn.setEnabled(true);
        acabarJornadaBtn.setBackgroundColor(Color.RED);

    }

    public void acabarJornada (View view) {

        horarioUsuaroi.setSalida(getFechaActual());

        GuardarRegistroBBDD();

        changeTextTime(acabarTextView, horarioUsuaroi.getSalida().getHour(), horarioUsuaroi.getSalida().getMinute());

        acabarJornadaBtn.setEnabled(false);
        acabarJornadaBtn.setBackgroundColor(Color.GRAY);

        calcularHores();

    }


    public void resetTime (View view) {
        iniciarTextView.setText("--:--");
        acabarTextView.setText("--:--");
        resultat.setText("--:--");

        iniciarJornadaBtn.setEnabled(true);
        iniciarJornadaBtn.setBackgroundColor(Color.GREEN);
        acabarJornadaBtn.setEnabled(false);
        acabarJornadaBtn.setBackgroundColor(Color.GRAY);

        horarioUsuaroi.setEntrada(resetFecha());
        horarioUsuaroi.setSalida(resetFecha());

        GuardarRegistroBBDD();

    }

    private void calcularHores() {

        Duration diff = Duration.between(horarioUsuaroi.getSalida(), horarioUsuaroi.getEntrada());

        long diffMinuts = diff.toMinutes();

        changeTextTime(resultat, diffMinuts/60, diffMinuts%60);

        horarioUsuaroi.setUsuario(usuarioApp);
        horarioUsuaroi.setTotalMinutsTreballats(diffMinuts);

        System.out.println("Horario/Usuario:  " + horarioUsuaroi.getUsuario());
        System.out.println("UsuarioAPP:  " + usuarioApp.getNom());

        GuardarRegistroBBDD();

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