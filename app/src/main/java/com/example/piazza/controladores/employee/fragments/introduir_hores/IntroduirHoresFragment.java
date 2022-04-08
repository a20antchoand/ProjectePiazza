package com.example.piazza.controladores.employee.fragments.introduir_hores;

import static com.google.firebase.crashlytics.internal.Logger.TAG;

import android.graphics.Color;
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
import androidx.fragment.app.Fragment;

import com.example.piazza.classes.Horario;
import com.example.piazza.commons.*;
import com.example.piazza.fireBase.data.ReadData;
import com.example.piazza.fireBase.data.WriteData;
import com.example.piazza.fireBase.session.AuthUserSession;
import com.example.testauth.R;
import com.example.testauth.databinding.FragmentIntroduirHoresBinding;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class IntroduirHoresFragment extends Fragment implements ReadData, WriteData, AuthUserSession{

    TextView benvinguda;
    Button iniciarJornadaBtn;
    Button acabarJornadaBtn;
    LinearLayout butons;

    DocumentSnapshot document;
    Horario horarioUsuario;

    Query query = DDBB.collection("horari")
            .orderBy("diaEntrada", Query.Direction.DESCENDING);

    public static int numeroDocument = 0;

    private FragmentIntroduirHoresBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentIntroduirHoresBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setup();

        return root;
    }

    public void setup () {

        System.out.println("INICI DOCUMENTS: " + numeroDocument);

        iniciarJornadaBtn = binding.iniciarJornada;
        acabarJornadaBtn =binding.acabarJornada;
        benvinguda = binding.benvingudaIntoduirHores;
        butons = binding.butonsLayout;
        horarioUsuario = new Horario();

        benvinguda.setText("Hola, " + userAuth.getNom().substring(0, 1).toUpperCase() + userAuth.getNom().substring(1));

        binding.iniciarJornada.setOnClickListener(this::iniciarJornada);

        binding.acabarJornada.setOnClickListener(this::acabarJornada);

        binding.downArrow.setOnClickListener(this::amagarButons);

        RecuperarRegistroUsuariBBDD(DDBB.collection("horari").document(getCurrTimeGMT.zdt.getYear() + "_" + getCurrTimeGMT.zdt.getMonthValue() + "_" + getCurrTimeGMT.zdt.getDayOfMonth() +  "_" + userAuth.getUid() + "_" + numeroDocument));

        escoltarBBDD();

        getMultipldeDocuments(query, this::totalMinutsDiaris);

    }

    private void escoltarBBDD() {

        Query docRefHorari = DDBB.collection("horari").whereEqualTo("horaSalida", -1);

        getListenerDocument(docRefHorari, this::resultatEscoltarBBDD);

    }

    private void resultatEscoltarBBDD(Object o, FirebaseFirestoreException e) {

        QuerySnapshot snapshot = (QuerySnapshot) o;

        if (e != null) {
            Log.w(TAG, "ESCOLTA FAILED.", e);
            return;
        }


        getMultipldeDocuments(query, this::totalMinutsDiaris);

        if (snapshot.size() == 0) {
            System.out.println("NO HI HA CAP REGISTRE MODIFICAT");
            //changeStateButtons.hideButton(acabarJornadaBtn);
            //changeStateButtons.showButton(iniciarJornadaBtn);

        } else {
            System.out.println("DOCUMENTS AMB SORIDA -2: " + snapshot.getDocuments().get(0).getId());
            System.out.println("SI HI HA CAP REGISTRE MODIFICAT");
            RecuperarRegistroUsuariBBDD((DDBB.collection("horari").document(snapshot.getDocuments().get(0).getId())));
        }

    }


    private void RecuperarRegistroUsuariBBDD(DocumentReference docRefHorari) {

        System.out.println("RECUPERAR: " + docRefHorari.getId());

        getOneDocument(docRefHorari, this::validarRegistre);

    }

    private void validarRegistre(Task<DocumentSnapshot> documentSnapshotTask) {

        if (documentSnapshotTask.isSuccessful()) {
            document = documentSnapshotTask.getResult();

            if (document.exists()) {
                Log.d(TAG, "DocumentSnapshot data: " + document.getId());
                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                comprovarEntradaSortida(true);
            } else {
                Log.d(TAG, "No such document");
                changeTextTimeResultat(binding.totalTempsTreballat, 0, 0);
            }
        } else {
            Log.d(TAG, "get failed with ", documentSnapshotTask.getException());
        }

    }

    private void GuardarRegistroBBDD(boolean iniciar) {

            DocumentReference docRefHorari = DDBB.collection("horari").document(getCurrTimeGMT.zdt.getYear() + "_" + getCurrTimeGMT.zdt.getMonthValue() + "_" + getCurrTimeGMT.zdt.getDayOfMonth() +  "_" + userAuth.getUid() + "_" + numeroDocument);;

            System.out.println("GUARDAR: " + docRefHorari.getId());

            writeOneDocument(docRefHorari, horarioUsuario);

            getMultipldeDocuments(query, this::totalMinutsDiaris);
    }

    private void updateDocumentNumber(Task<QuerySnapshot> querySnapshotTask) {

        numeroDocument = 1;

        if (querySnapshotTask.isSuccessful()) {
            for (DocumentSnapshot d : querySnapshotTask.getResult().getDocuments()) {

                Horario horarioTemp = d.toObject(Horario.class);

                if (d.getId().contains(userAuth.getUid()))
                    if (horarioTemp.getDiaEntrada() == getCurrTimeGMT.zdt.getDayOfMonth()) {
                        System.out.println(horarioTemp.getDiaEntrada() + "-->" + getCurrTimeGMT.zdt.getDayOfMonth());
                        numeroDocument = numeroDocument + 1;
                        System.out.println("DOUCMENT NUMBER UPDATE " + numeroDocument);
                    }


            }

        }

        GuardarRegistroBBDD(true);

    }

    private void comprovarEntradaSortida(boolean exist) {

        if (exist) {

           Horario horariTemp = document.toObject(Horario.class);

            if ((long) horariTemp.getHoraEntrada() != -1) {

                horarioUsuario.setAnioEntrada(horariTemp.getAnioEntrada());
                horarioUsuario.setMesEntrada(horariTemp.getMesEntrada());
                horarioUsuario.setDiaEntrada(horariTemp.getDiaEntrada());
                horarioUsuario.setHoraEntrada(horariTemp.getHoraEntrada());
                horarioUsuario.setMinutEntrada(horariTemp.getMinutEntrada());

                changeStateButtons.hideButton(iniciarJornadaBtn);

                if ((long) horariTemp.getHoraSalida() != -1) {

                    horarioUsuario.setAnioSalida(horariTemp.getAnioSalida());
                    horarioUsuario.setMesSalida(horariTemp.getMesSalida());
                    horarioUsuario.setDiaSalida(horariTemp.getDiaSalida());
                    horarioUsuario.setHoraSalida(horariTemp.getHoraSalida());
                    horarioUsuario.setMinutSalida(horariTemp.getMinutSalida());


                    changeStateButtons.hideButton(acabarJornadaBtn);
                    changeStateButtons.showButton(iniciarJornadaBtn);


                    calcularHores();

                } else {

                    changeStateButtons.showButton(acabarJornadaBtn);
                    changeStateButtons.hideButton(iniciarJornadaBtn);

                }


            }
        } else {
            changeStateButtons.showButton(iniciarJornadaBtn);

            changeTextTimeResultat(binding.totalTempsTreballat, 0, 0);

            horarioUsuario = new Horario();
        }

    }

    public void getFechaActual(boolean entrada) {

        try {
            String stringTemps = new getCurrTimeGMT().execute().get();
            getCurrTimeGMT.zdt = getCurrTimeGMT.getZoneDateTime(stringTemps);

            Toast.makeText(getContext(), "HORA: "+getCurrTimeGMT.zdt, Toast.LENGTH_SHORT).show();

        } catch (ExecutionException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "HORA: FUCK", Toast.LENGTH_SHORT).show();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        if (entrada) {
            horarioUsuario.setAnioEntrada(getCurrTimeGMT.zdt.getYear());
            horarioUsuario.setMesEntrada(getCurrTimeGMT.zdt.getMonthValue());
            horarioUsuario.setDiaEntrada(getCurrTimeGMT.zdt.getDayOfMonth());
            horarioUsuario.setHoraEntrada(getCurrTimeGMT.zdt.getHour());
            horarioUsuario.setMinutEntrada(getCurrTimeGMT.zdt.getMinute());

        } else {
            horarioUsuario.setAnioSalida(getCurrTimeGMT.zdt.getYear());
            horarioUsuario.setMesSalida(getCurrTimeGMT.zdt.getMonthValue());
            horarioUsuario.setDiaSalida(getCurrTimeGMT.zdt.getDayOfMonth());
            horarioUsuario.setHoraSalida(getCurrTimeGMT.zdt.getHour());
            horarioUsuario.setMinutSalida(getCurrTimeGMT.zdt.getMinute());
        }

    }

    public void iniciarJornada (View view) {

        horarioUsuario = new Horario();

        getFechaActual(true);

        getMultipldeDocuments(query, this::updateDocumentNumber);

        DocumentReference docRefHorari = DDBB.collection("horari").document(getCurrTimeGMT.zdt.getYear() + "_" + getCurrTimeGMT.zdt.getMonthValue() + "_" + getCurrTimeGMT.zdt.getDayOfMonth() +  "_" + userAuth.getUid() + "_" + numeroDocument);;

        System.out.println("DOCREF: " + docRefHorari.getId());
        //mostrarEstado(true);

        changeStateButtons.hideButton(iniciarJornadaBtn);
        changeStateButtons.showButton(acabarJornadaBtn);

    }

    public void acabarJornada (View view) {

        getFechaActual(false);

        GuardarRegistroBBDD(false);

        DocumentReference docRefHorari = DDBB.collection("horari").document(getCurrTimeGMT.zdt.getYear() + "_" + getCurrTimeGMT.zdt.getMonthValue() + "_" + getCurrTimeGMT.zdt.getDayOfMonth() +  "_" + userAuth.getUid() + "_" + numeroDocument);;

        System.out.println("DOCREF: " + docRefHorari.getId());
        //mostrarEstado(false);

        changeStateButtons.hideButton(acabarJornadaBtn);
        changeStateButtons.showButton(iniciarJornadaBtn);

        calcularHores();

    }

    private void amagarButons(View view) {

        binding.downArrow.setImageDrawable(getResources().getDrawable(R.drawable.icons8_up_arrow_32));

        binding.butonsLayout.setVisibility(View.GONE);

    }

/*    private void mostrarEstado(boolean inici) {

        TextView textView = new TextView(getContext());
        textView.setPadding(15,15,15,15);
        textView.setBackground(getResources().getDrawable(R.drawable.edit_text_bg));

        if (inici) {
            textView.setText(getCurrTimeGMT.zdt.getHour() + ":" + getCurrTimeGMT.zdt.getMinute() + ":" + getCurrTimeGMT.zdt.getSecond() + ": Has entrat a treballar.");
        } else {
            textView.setText(getCurrTimeGMT.zdt.getHour() + ":" + getCurrTimeGMT.zdt.getMinute() + ":" + getCurrTimeGMT.zdt.getSecond() + ": Has sortit de treballar.");
        }

        binding.historicDiari.addView(textView);

    }*/

    private void calcularHores() {

        LocalDateTime entrada = formatarDateTime(horarioUsuario.getAnioEntrada(), horarioUsuario.getMesEntrada(), horarioUsuario.getDiaEntrada(), horarioUsuario.getHoraEntrada(), horarioUsuario.getMinutEntrada());
        LocalDateTime sortida = formatarDateTime(horarioUsuario.getAnioSalida(), horarioUsuario.getMesSalida(), horarioUsuario.getDiaSalida(), horarioUsuario.getHoraSalida(), horarioUsuario.getMinutSalida());

        Duration diff = Duration.between(entrada, sortida);

        long diffMinuts = diff.toMinutes();

        horarioUsuario.setUsuario(userAuth);
        horarioUsuario.setTotalMinutsTreballats(diffMinuts);

        GuardarRegistroBBDD(false);



    }

    public void totalMinutsDiaris(Task<QuerySnapshot> querySnapshotTask) {

        if (querySnapshotTask.isSuccessful()) {

            int totalTempsTreballat = 0;

            for (QueryDocumentSnapshot documentSnapshot : querySnapshotTask.getResult()) {
                if (documentSnapshot.getId().contains(userAuth.getUid())) {
                    Horario horario = documentSnapshot.toObject(Horario.class);
                    if (horario.getDiaEntrada() == getCurrTimeGMT.zdt.getDayOfMonth() && horario.getHoraSalida() != -1) {
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



}