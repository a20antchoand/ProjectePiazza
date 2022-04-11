package com.example.piazza.controladores.employee.fragments.introduir_hores;

import static com.google.firebase.crashlytics.internal.Logger.TAG;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;

public class IntroduirHoresFragment extends Fragment implements ReadData, WriteData, AuthUserSession{

    private int mInterval = 5000; // 5 seconds by default, can be changed later
    public static Handler HandlerIntroduirHores = new Handler();


    TextView benvinguda;
    Button iniciarJornadaBtn;
    Button acabarJornadaBtn;
    LinearLayout butons;

    DocumentSnapshot document;
    Horario horarioUsuario;

    DocumentReference docRefHorari;


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

        getMultipldeDocuments(query, this::updateDocumentNumber);

        docRefHorari = DDBB.collection("horari").document(getCurrTimeGMT.zdt.getYear() + "_" + getCurrTimeGMT.zdt.getMonthValue() + "_" + getCurrTimeGMT.zdt.getDayOfMonth() +  "_" + userAuth.getUid() + "_" + numeroDocument);
        System.out.println("INICI DOCUMENTS: " + docRefHorari.getId());

        iniciarJornadaBtn = binding.iniciarJornada;
        acabarJornadaBtn =binding.acabarJornada;
        benvinguda = binding.benvingudaIntoduirHores;
        butons = binding.butonsLayout;
        horarioUsuario = new Horario();

        benvinguda.setText("Hola, " + userAuth.getNom().substring(0, 1).toUpperCase() + userAuth.getNom().substring(1));

        binding.iniciarJornada.setOnClickListener(this::iniciarJornada);

        binding.acabarJornada.setOnClickListener(this::acabarJornada);

        binding.downArrow.setOnClickListener(this::amagarButons);

        RecuperarRegistroUsuariBBDD();

        //escoltarBBDD();

        getMultipldeDocuments(query, this::totalMinutsDiaris);

        FirebaseMessaging.getInstance().subscribeToTopic("40hores");

    }

    public void iniciarJornada (View view) {

        horarioUsuario = new Horario();
        horarioUsuario.setUsuario(userAuth);

        numeroDocument = numeroDocument + 1;
        docRefHorari = DDBB.collection("horari").document(getCurrTimeGMT.zdt.getYear() + "_" + getCurrTimeGMT.zdt.getMonthValue() + "_" + getCurrTimeGMT.zdt.getDayOfMonth() +  "_" + userAuth.getUid() + "_" + numeroDocument);;

        getFechaActual(true);

        GuardarRegistroBBDD(docRefHorari);

        Notificacio.Notificar(getContext(),"Piazza", "Has iniciat la teva Jornada.\nPodras parar-la en qualsevol moment donan-li al boto de parar.", 1);

        changeStateButtons.hideButton(iniciarJornadaBtn);
        changeStateButtons.showButton(acabarJornadaBtn);

        startRepeatingTask();
    }

    public void acabarJornada (View view) {

        horarioUsuario.setEstatJornada(true);

        changeStateButtons.hideButton(acabarJornadaBtn);
        changeStateButtons.showButton(iniciarJornadaBtn);

        getFechaActual(false);

        Notificacio.Notificar(getContext(),"Piazza", "Has acabat la teva Jornada.\nEspero que descansis.\nHas fet un torn de: " + horarioUsuario.getTotalMinutsTreballats()/60 + "h " + horarioUsuario.getTotalMinutsTreballats()%60 + "m" , 1);

        stopRepeatingTask();
    }

/*    private void escoltarBBDD() {

        Query docRefHorari = DDBB.collection("horari").whereEqualTo("estatJornada", false);

        getListenerDocument(docRefHorari, this::resultatEscoltarBBDD);

    }

    private void resultatEscoltarBBDD(Object o, FirebaseFirestoreException e) {

        QuerySnapshot snapshot = (QuerySnapshot) o;

        if (e != null) {
            Log.w(TAG, "ESCOLTA FAILED.", e);
            return;
        }

        for (DocumentSnapshot d : snapshot) {

            System.out.println(userAuth.getUid() + " --> " + d.getId());

            if (d.getId().contains(userAuth.getUid())) {
                getMultipldeDocuments(query, this::totalMinutsDiaris);
                getMultipldeDocuments(query, this::updateDocumentNumber);

                System.out.println("iepa: " + snapshot.size());

                if (d.getBoolean("estatJornada")) {

                    System.out.println("num document: " + numeroDocument);

                    changeStateButtons.hideButton(acabarJornadaBtn);
                    changeStateButtons.showButton(iniciarJornadaBtn);

                } else if (snapshot.size() != 0) {

                    System.out.println("DOCUMENTS AMB SORIDA -1: " + snapshot.size());

                    changeStateButtons.hideButton(iniciarJornadaBtn);
                    changeStateButtons.showButton(acabarJornadaBtn);

                }
            }
        }

    }*/

    private void RecuperarRegistroUsuariBBDD() {

        getOneDocument(docRefHorari, this::validarRegistre);

    }

    private void validarRegistre(Task<DocumentSnapshot> documentSnapshotTask) {

        if (documentSnapshotTask.isSuccessful()) {
            document = documentSnapshotTask.getResult();

            if (document.exists()) {

                Log.d(TAG, "DocumentSnapshot data: " + document.getData() + " ID: " + document.getId());
                comprovarEntradaSortida(true);
            } else {

                Log.d(TAG, "No such document");

                changeTextTimeResultat(binding.totalTempsTreballat, 0, 0);
            }
        } else {
            Log.d(TAG, "get failed with ", documentSnapshotTask.getException());
        }

    }

    private void GuardarRegistroBBDD(DocumentReference docRef) {

        horarioUsuario.setUsuario(userAuth);

        writeOneDocument(docRefHorari, horarioUsuario);

        getMultipldeDocuments(query, this::totalMinutsDiaris);

    }

    private void updateDocumentNumber(Task<QuerySnapshot> querySnapshotTask) {

        numeroDocument = 0;

        if (querySnapshotTask.isSuccessful()) {
            for (DocumentSnapshot d : querySnapshotTask.getResult().getDocuments()) {

                Horario horarioTemp = d.toObject(Horario.class);

                if (d.getId().contains(userAuth.getUid()))
                    if (horarioTemp.getDiaEntrada() == getCurrTimeGMT.zdt.getDayOfMonth()) {
                        numeroDocument++;
                        System.out.println("DOUCMENT NUMBER UPDATE " + numeroDocument);
                    }


            }

        }

    }

    private void comprovarEntradaSortida(boolean exist) {

        if (exist) {

           Horario horariTemp = document.toObject(Horario.class);

            if (horariTemp.getHoraEntrada() != 1) {

                horarioUsuario.setAnioEntrada(horariTemp.getAnioEntrada());
                horarioUsuario.setMesEntrada(horariTemp.getMesEntrada());
                horarioUsuario.setDiaEntrada(horariTemp.getDiaEntrada());
                horarioUsuario.setHoraEntrada(horariTemp.getHoraEntrada());
                horarioUsuario.setMinutEntrada(horariTemp.getMinutEntrada());

                changeStateButtons.hideButton(iniciarJornadaBtn);
                changeStateButtons.showButton(acabarJornadaBtn);

                if (horariTemp.getHoraEntrada() != 1 && horariTemp.isEstatJornada()) {

                    horarioUsuario.setAnioSalida(horariTemp.getAnioSalida());
                    horarioUsuario.setMesSalida(horariTemp.getMesSalida());
                    horarioUsuario.setDiaSalida(horariTemp.getDiaSalida());
                    horarioUsuario.setHoraSalida(horariTemp.getHoraSalida());
                    horarioUsuario.setMinutSalida(horariTemp.getMinutSalida());
                    horarioUsuario.setEstatJornada(true);

                    stopRepeatingTask();

                    changeStateButtons.hideButton(acabarJornadaBtn);
                    changeStateButtons.showButton(iniciarJornadaBtn);

                    calcularHores();

                } else {

                    changeStateButtons.showButton(acabarJornadaBtn);
                    changeStateButtons.hideButton(iniciarJornadaBtn);

                    startRepeatingTask();

                }


            }
        } else {
            changeStateButtons.showButton(iniciarJornadaBtn);
            changeStateButtons.hideButton(acabarJornadaBtn);

            changeTextTimeResultat(binding.totalTempsTreballat, 0, 0);

            horarioUsuario = new Horario();
        }

    }

    public void getFechaActual(boolean entrada) {

        try {
            String stringTemps = new getCurrTimeGMT().execute().get();
            getCurrTimeGMT.zdt = getCurrTimeGMT.getZoneDateTime(stringTemps);
        } catch (ExecutionException e) {
            e.printStackTrace();
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

            calcularHores();
        }

    }

    private void amagarButons(View view) {

        binding.downArrow.setImageDrawable(getResources().getDrawable(R.drawable.icons8_up_arrow_32));

        binding.butonsLayout.setVisibility(View.GONE);

    }


    private void calcularHores() {

        LocalDateTime entrada = formatarDateTime(horarioUsuario.getAnioEntrada(), horarioUsuario.getMesEntrada(), horarioUsuario.getDiaEntrada(), horarioUsuario.getHoraEntrada(), horarioUsuario.getMinutEntrada());
        LocalDateTime sortida = formatarDateTime(horarioUsuario.getAnioSalida(), horarioUsuario.getMesSalida(), horarioUsuario.getDiaSalida(), horarioUsuario.getHoraSalida(), horarioUsuario.getMinutSalida());

        Duration diff = Duration.between(entrada, sortida);

        long diffMinuts = diff.toMinutes();

        horarioUsuario.setTotalMinutsTreballats(diffMinuts);

        GuardarRegistroBBDD(docRefHorari);



    }

    public void totalMinutsDiaris(Task<QuerySnapshot> querySnapshotTask) {

        if (querySnapshotTask.isSuccessful()) {

            int totalTempsTreballat = 0;

            for (QueryDocumentSnapshot documentSnapshot : querySnapshotTask.getResult()) {
                if (documentSnapshot.getId().contains(userAuth.getUid())) {
                    Horario horario = documentSnapshot.toObject(Horario.class);
                    if (horario.getDiaEntrada() == getCurrTimeGMT.zdt.getDayOfMonth()) {
                        totalTempsTreballat += horario.getTotalMinutsTreballats();
                        System.out.println(totalTempsTreballat);
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

        System.out.println("MES: " + mesEntrada);

        dataEntrada = LocalDateTime.of(anioEntrada, mesEntrada, diaEntrada, horaEntrada, minutEntrada);

        return dataEntrada;

    }

    public void changeTextTimeResultat (TextView textView, long hora, long minut) {
        if (minut < 10)
            textView.setText(hora + "h 0" + minut + "m");
        else
            textView.setText(hora + "h " + minut + "m");
    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                updateStatus(); //this function can change value of mInterval.
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("ERROOOOOOR");
            }finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                HandlerIntroduirHores.postDelayed(mStatusChecker, mInterval);
            }
        }
    };

    private void updateStatus() {

        System.out.println("docRef updateStatus: " + docRefHorari.getId());

        getFechaActual(false);

    }

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        HandlerIntroduirHores.removeCallbacks(mStatusChecker);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRepeatingTask();
    }
    
    
}