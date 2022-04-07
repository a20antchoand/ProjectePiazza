package com.example.piazza.controladores.employee.fragments.introduir_hores;

import static com.google.firebase.crashlytics.internal.Logger.TAG;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.piazza.classes.Horario;
import com.example.piazza.classes.Usuario;
import com.example.piazza.fireBase.data.ReadData;
import com.example.piazza.fireBase.session.AuthUserSession;
import com.example.piazza.recyclerView.historialHores.ListAdapterHistorialHores;
import com.example.piazza.recyclerView.historialHores.ListElementHistorialHores;
import com.example.testauth.R;
import com.example.testauth.databinding.FragmentIntroduirHoresBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import com.example.piazza.commons.getCurrTimeGMT;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class IntroduirHoresFragment extends Fragment implements ReadData, AuthUserSession{



    TextView benvinguda;
    Button iniciarJornadaBtn;
    Button acabarJornadaBtn;
    LinearLayout butons;

    DocumentSnapshot document;
    public static Usuario usuarioApp;
    Horario horarioUsuario;

    public static int numeroDocument = 0;
    public static ZonedDateTime zdt;
    private FragmentIntroduirHoresBinding binding;
    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentIntroduirHoresBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        setup();

        return root;
    }

    public void setup () {

        DocumentReference docRef = DDBB.collection("usuaris").document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getUid()));
        cargarDatosUsuario(docRef, this::getUsuari);

        System.out.println("INICI DOCUMENTS: " + numeroDocument);

        iniciarJornadaBtn = binding.iniciarJornada;
        acabarJornadaBtn =binding.acabarJornada;
        benvinguda = binding.benvingudaIntoduirHores;
        butons = binding.butonsLayout;
        horarioUsuario = new Horario();

        benvinguda.setText("Hola, " + userAuth.getNom().substring(0, 1).toUpperCase() + userAuth.getNom().substring(1));

        binding.iniciarJornada.setOnClickListener(view -> {

            iniciarJornada(view);
        });

       binding.acabarJornada.setOnClickListener(view -> {

            acabarJornada(view);
        });

        RecuperarRegistroUsuariBBDD();

        escoltarBBDD();

        Query query = DDBB.collection("horari")
                .orderBy("diaEntrada", Query.Direction.DESCENDING);

        getMultipldeDocuments(query, this::setElements);


    }


    private void getUsuari(Task<DocumentSnapshot> documentSnapshotTask) {

        usuarioApp = documentSnapshotTask.getResult().toObject(Usuario.class);

    }


    private void escoltarBBDD() {

        final DocumentReference docRef = DDBB.collection("horari").document(getFechaActual().getYear() + "_" + getFechaActual().getMonthValue() + "_" + getFechaActual().getDayOfMonth() +  "_" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "_" + numeroDocument);
        docRef.addSnapshotListener((snapshot, e) -> {
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
        });

    }


    private void RecuperarRegistroUsuariBBDD() {

        System.out.println("COM ESTA AQUI: " + numeroDocument);

        DocumentReference docRef = DDBB.collection("horari").document(Objects.requireNonNull(getFechaActual().getYear() + "_" + getFechaActual().getMonthValue() + "_" + getFechaActual().getDayOfMonth() +  "_" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "_" + numeroDocument));
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                document = task.getResult();

                if (document.exists()) {
                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    comprovarEntradaSortida(true);
                } else {
                    Log.d(TAG, "No such document");
                    changeTextTimeResultat(binding.totalTempsTreballat, 0, 0);
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });

    }

    private void GuardarRegistroBBDD(boolean iniciar) {

        if (iniciar) {
            DDBB.collection("horari").get().addOnSuccessListener(
                    queryDocumentSnapshots -> {

                        numeroDocument = 1;

                        for (DocumentSnapshot d : queryDocumentSnapshots) {

                            if (d.getId().contains(userAuth.getUid()))
                                if (Integer.parseInt(d.get("diaEntrada").toString()) == getFechaActual().getDayOfMonth()) {
                                    System.out.println(d.get("diaEntrada") +"-->"+getFechaActual().getDayOfMonth());
                                    numeroDocument = numeroDocument + 1;
                                }


                        }

                        DDBB.collection("horari").document(Objects.requireNonNull(getFechaActual().getYear() + "_" + getFechaActual().getMonthValue() + "_" + getFechaActual().getDayOfMonth() +  "_" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "_" + numeroDocument))
                                .set(horarioUsuario)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d(TAG, "DocumentSnapshot successfully written!");
                                })
                                .addOnFailureListener(e -> {
                                            Log.w(TAG, "Error writing document", e);
                                        }
                                );

                    }).addOnFailureListener(e -> {
                        numeroDocument = 1;
                    });
        } else {

            DDBB.collection("horari").document(Objects.requireNonNull(getFechaActual().getYear() + "_" + getFechaActual().getMonthValue() + "_" + getFechaActual().getDayOfMonth() +  "_" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "_" + numeroDocument))
                    .set(horarioUsuario)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    })
                    .addOnFailureListener(e -> {
                                Log.w(TAG, "Error writing document", e);
                            }
                    );

        }



    }

    private void comprovarEntradaSortida(boolean exist) {

        if (exist) {

            HashMap<String, Object> data = (HashMap<String, Object>) document.getData();

            if ((long) data.get("horaEntrada") != -1) {

                horarioUsuario.setAnioEntrada(Math.toIntExact((Long) data.get("anioEntrada")));
                horarioUsuario.setMesEntrada(Math.toIntExact((Long) data.get("mesEntrada")));
                horarioUsuario.setDiaEntrada(Math.toIntExact((Long) data.get("diaEntrada")));
                horarioUsuario.setHoraEntrada(Math.toIntExact((Long) data.get("horaEntrada")));
                horarioUsuario.setMinutEntrada(Math.toIntExact((Long) data.get("minutEntrada")));

                iniciarJornadaBtn.setEnabled(false);
                iniciarJornadaBtn.setVisibility(View.GONE);

                if ((long) data.get("horaSalida") != -1) {

                    horarioUsuario.setAnioSalida(Math.toIntExact((Long) data.get("anioSalida")));
                    horarioUsuario.setMesSalida(Math.toIntExact((Long) data.get("mesSalida")));
                    horarioUsuario.setDiaSalida(Math.toIntExact((Long) data.get("diaSalida")));
                    horarioUsuario.setHoraSalida(Math.toIntExact((Long) data.get("horaSalida")));
                    horarioUsuario.setMinutSalida(Math.toIntExact((Long) data.get("minutSalida")));


                    acabarJornadaBtn.setEnabled(false);
                    acabarJornadaBtn.setVisibility(View.GONE);
                    iniciarJornadaBtn.setEnabled(true);
                    iniciarJornadaBtn.setVisibility(View.VISIBLE);


                    calcularHores();

                } else {

                    acabarJornadaBtn.setEnabled(true);
                    acabarJornadaBtn.setVisibility(View.VISIBLE);
                    iniciarJornadaBtn.setEnabled(false);
                    iniciarJornadaBtn.setVisibility(View.GONE);

                }


            }
        } else {
            iniciarJornadaBtn.setEnabled(true);
            iniciarJornadaBtn.setVisibility(View.VISIBLE);

            changeTextTimeResultat(binding.totalTempsTreballat, 0, 0);

            horarioUsuario = new Horario();
        }

    }

    public void getFechaActual(boolean entrada) {

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Toast.makeText(getContext(), "HORA: "+zdt, Toast.LENGTH_SHORT).show();

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
        }

    }

    public static ZonedDateTime getFechaActual() {

        new getCurrTimeGMT().execute();

        return zdt;

    }

    public void iniciarJornada (View view) {

        new getCurrTimeGMT().execute();

        getFechaActual(true);

        GuardarRegistroBBDD(true);

        iniciarJornadaBtn.setEnabled(false);
        iniciarJornadaBtn.setVisibility(View.GONE);
        acabarJornadaBtn.setEnabled(true);
        acabarJornadaBtn.setVisibility(View.VISIBLE);

    }

    public void acabarJornada (View view) {

        new getCurrTimeGMT().execute();

        getFechaActual(false);

        GuardarRegistroBBDD(false);

        acabarJornadaBtn.setEnabled(false);
        acabarJornadaBtn.setVisibility(View.GONE);
        iniciarJornadaBtn.setEnabled(true);
        iniciarJornadaBtn.setVisibility(View.VISIBLE);

        calcularHores();

    }

    private void calcularHores() {

        LocalDateTime entrada = formatarDateTime(horarioUsuario.getAnioEntrada(), horarioUsuario.getMesEntrada(), horarioUsuario.getDiaEntrada(), horarioUsuario.getHoraEntrada(), horarioUsuario.getMinutEntrada());
        LocalDateTime sortida = formatarDateTime(horarioUsuario.getAnioSalida(), horarioUsuario.getMesSalida(), horarioUsuario.getDiaSalida(), horarioUsuario.getHoraSalida(), horarioUsuario.getMinutSalida());

        Duration diff = Duration.between(entrada, sortida);

        long diffMinuts = diff.toMinutes();

        Query query = DDBB.collection("horari")
                .orderBy("diaEntrada", Query.Direction.DESCENDING);

        getMultipldeDocuments(query, this::setElements);

        horarioUsuario.setUsuario(usuarioApp);
        horarioUsuario.setTotalMinutsTreballats(diffMinuts);

        GuardarRegistroBBDD(false);

    }

    public void setElements(Task<QuerySnapshot> querySnapshotTask) {

        if (querySnapshotTask.isSuccessful()) {

            int totalTempsTreballat = 0;

            for (QueryDocumentSnapshot documentSnapshot : querySnapshotTask.getResult()) {
                if (documentSnapshot.getId().contains(userAuth.getUid())) {
                    Horario horario = documentSnapshot.toObject(Horario.class);
                    if (horario.getDiaEntrada() == getFechaActual().getDayOfMonth() && horario.getHoraSalida() != -1) {
                        totalTempsTreballat += horario.getTotalMinutsTreballats();
                    }

                }
            }

            changeTextTimeResultat(binding.totalTempsTreballat, totalTempsTreballat/60, totalTempsTreballat%60);

        } else {
            Log.d(TAG, "Error al recuperar varios documentos.");
        }

    }

    private LocalDateTime formatarDateTime(int anioEntrada, int mesEntrada, int diaEntrada, int horaEntrada, int minutEntrada) {

        LocalDateTime dataEntrada;

        dataEntrada = LocalDateTime.of(anioEntrada, mesEntrada, diaEntrada, horaEntrada, minutEntrada);

        return dataEntrada;

    }

    public void changeTextTimeResultat (TextView textView, long hora, long minut) {
        if (minut < 10)
            textView.setText(hora + "h 0" + minut + "m");
        else
            textView.setText(hora + "h " + minut + "m");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}