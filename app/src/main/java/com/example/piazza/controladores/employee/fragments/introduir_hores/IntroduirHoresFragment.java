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
    public static boolean finalJornadaNoti = false;

    private FragmentIntroduirHoresBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentIntroduirHoresBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setup();

        return root;
    }

    public void setup () {

        //Actualitzem el numero de document
        getMultipldeDocuments(query, this::updateDocumentNumber);

        //agafem la referencia del document actual
        docRefHorari = DDBB.collection("horari").document(getCurrTimeGMT.zdt.getYear() + "_" + getCurrTimeGMT.zdt.getMonthValue() + "_" + getCurrTimeGMT.zdt.getDayOfMonth() +  "_" + userAuth.getUid() + "_" + numeroDocument);

        //iniciem variables
        iniciarJornadaBtn = binding.iniciarJornada;
        acabarJornadaBtn =binding.acabarJornada;
        benvinguda = binding.benvingudaIntoduirHores;
        butons = binding.butonsLayout;
        horarioUsuario = new Horario();

        //donem la benvingua a l'usuari
        benvinguda.setText("Hola, " + userAuth.getNom().substring(0, 1).toUpperCase() + userAuth.getNom().substring(1));

        //indiquem la funció del boto iniciarJornada
        binding.iniciarJornada.setOnClickListener(this::iniciarJornada);

        //indiquem la funció del boto acabarJornada
        binding.acabarJornada.setOnClickListener(this::acabarJornada);

        //binding.downArrow.setOnClickListener(this::amagarButons);

        //recuperem el registre de l'usuari actual
        RecuperarRegistroUsuariBBDD();

        //escoltarBBDD();

        //calculem el temps total treballats durant el dia
        getMultipldeDocuments(query, this::totalMinutsDiaris);

        //FirebaseMessaging.getInstance().subscribeToTopic("40hores");

    }

    public void iniciarJornada (View view) {

        //iniciem l'horari de l'usuari
        horarioUsuario = new Horario();
        //indiquem l'usuari a l'horari
        horarioUsuario.setUsuario(userAuth);

        //augmentem el numero de document en +1
        numeroDocument = numeroDocument + 1;
        //agafem la referencia del document a treballar
        docRefHorari = DDBB.collection("horari").document(getCurrTimeGMT.zdt.getYear() + "_" + getCurrTimeGMT.zdt.getMonthValue() + "_" + getCurrTimeGMT.zdt.getDayOfMonth() +  "_" + userAuth.getUid() + "_" + numeroDocument);;

        //agafem la data actual
        getFechaActual(true);

        //guardem el registre a la BBDD
        GuardarRegistroBBDD();

        //noifiquem a l'usuari de l'entrada a treballar
        Notificacio.Notificar(getContext(),"Piazza", "Has iniciat la teva Jornada.\nPodras parar-la en qualsevol moment donan-li al boto de parar.", 1);

        //amagem e lboto d'inici i mostrem el d'acabar
        changeStateButtons.hideButton(iniciarJornadaBtn);
        changeStateButtons.showButton(acabarJornadaBtn);

        //iniciem el handler que actualitzara el contador
        startRepeatingTask();
    }

    public void acabarJornada (View view) {

        //indiquem que la jornada esta acabada
        horarioUsuario.setEstatJornada(true);

        //cambiem els botons ocultant el d'acabar i mostrant el d'iniciar
        changeStateButtons.hideButton(acabarJornadaBtn);
        changeStateButtons.showButton(iniciarJornadaBtn);

        //agafem la dada actual i guardem la informacio
        getFechaActual(false);

        //notifiquem del final de la jornada
        Notificacio.Notificar(getContext(),"Piazza", "Has acabat la teva Jornada.\nEspero que descansis.\nHas fet un torn de: " + horarioUsuario.getTotalMinutsTreballats()/60 + "h " + horarioUsuario.getTotalMinutsTreballats()%60 + "m" , 1);

        //parem el handler
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

        //query de documents que tenen la jornada iniciada
        Query queryRegistre = DDBB.collection("horari").whereEqualTo("estatJornada", false);

        //validme el registre
        getMultipldeDocuments(queryRegistre, this::validarRegistres);

    }

    private void validarRegistres(Task<QuerySnapshot> horarisDocuments) {

        //si l'estat es successful
        if (horarisDocuments.isSuccessful()) {

            //recorrem els documents
            for (DocumentSnapshot horariDocument : horarisDocuments.getResult()) {

                //si el registre pertany a l'usuari
                if (horariDocument.getId().contains(userAuth.getUid())) {

                    //modifiquem la referencia del document
                    docRefHorari = horariDocument.getReference();
                    //modifiquem el document
                    document = horariDocument;
                    //comprovem la entrada i sortida
                    comprovarEntradaSortida();
                }

            }

        }

    }


    private void GuardarRegistroBBDD() {

        //tornem a guardar l'usuari
        horarioUsuario.setUsuario(userAuth);

        //escribim el document a firestore
        writeOneDocument(docRefHorari, horarioUsuario);

        //tornem a calcular el total de minuts treballats per rectificar les  dades.
        getMultipldeDocuments(query, this::totalMinutsDiaris);

    }

    private void updateDocumentNumber(Task<QuerySnapshot> horarisDocuments) {

        numeroDocument = 0;

        //si el resultat es successful
        if (horarisDocuments.isSuccessful()) {

            //recorrrem els horaris
            for (DocumentSnapshot horariDocument : horarisDocuments.getResult().getDocuments()) {

                //creem l'objecte Horari del regsistre recuperat
                Horario horarioTemp = horariDocument.toObject(Horario.class);

                //Si el document pertany a l'usauri
                if (horariDocument.getId().contains(userAuth.getUid()))
                    //si dai d'entrada es igual al dia actual
                    if (horarioTemp.getDiaEntrada() == getCurrTimeGMT.zdt.getDayOfMonth()) {
                        //augmentem el numero de document
                        numeroDocument++;
                    }


            }

        }

    }

    private void comprovarEntradaSortida() {

        //Creem l'objecte Horari del document recuperat
        horarioUsuario = document.toObject(Horario.class);

        //Si l'horari te hora d'entrada
        if (horarioUsuario.getHoraEntrada() != -1) {

            //modifiquem els butons
            changeStateButtons.hideButton(iniciarJornadaBtn);
            changeStateButtons.showButton(acabarJornadaBtn);

            //si l'horari te hora d'entrada i la jornada esta acabada
            if (horarioUsuario.getHoraEntrada() != -1 && horarioUsuario.isEstatJornada()) {

                //parem el Handler
                stopRepeatingTask();

                //cambiem els butons
                changeStateButtons.hideButton(acabarJornadaBtn);
                changeStateButtons.showButton(iniciarJornadaBtn);

                //calculem les hores totals
                calcularHores();

            //si te entrada i no sortida
            } else {

                //cambiem els butons
                changeStateButtons.showButton(acabarJornadaBtn);
                changeStateButtons.hideButton(iniciarJornadaBtn);

                //iniciem el Handler
                startRepeatingTask();

            }
        }


    }

    public void getFechaActual(boolean entrada) {

        //agafem l adata actual desde la api de worldtimeapi
        try {
            String stringTemps = new getCurrTimeGMT().execute().get();
            getCurrTimeGMT.zdt = getCurrTimeGMT.getZoneDateTime(stringTemps);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //si estem registrant l'entrada guardem les dades
        if (entrada) {
            horarioUsuario.setAnioEntrada(getCurrTimeGMT.zdt.getYear());
            horarioUsuario.setMesEntrada(getCurrTimeGMT.zdt.getMonthValue());
            horarioUsuario.setDiaEntrada(getCurrTimeGMT.zdt.getDayOfMonth());
            horarioUsuario.setHoraEntrada(getCurrTimeGMT.zdt.getHour());
            horarioUsuario.setMinutEntrada(getCurrTimeGMT.zdt.getMinute());
            horarioUsuario.setDiaAny(getCurrTimeGMT.zdt.getDayOfYear());
        //si no, guadrem les dades i calculem les hores totals
        } else {
            horarioUsuario.setAnioSalida(getCurrTimeGMT.zdt.getYear());
            horarioUsuario.setMesSalida(getCurrTimeGMT.zdt.getMonthValue());
            horarioUsuario.setDiaSalida(getCurrTimeGMT.zdt.getDayOfMonth());
            horarioUsuario.setHoraSalida(getCurrTimeGMT.zdt.getHour());
            horarioUsuario.setMinutSalida(getCurrTimeGMT.zdt.getMinute());

            //calculem les hores
            calcularHores();
        }

    }

    private void amagarButons(View view) {

        binding.downArrow.setImageDrawable(getResources().getDrawable(R.drawable.lum_soft_02));

        binding.butonsLayout.setVisibility(View.GONE);

    }


    private void calcularHores() {

        //agafem la data d'entrada i la de sortida
        LocalDateTime entrada = formatarDateTime(horarioUsuario.getAnioEntrada(), horarioUsuario.getMesEntrada(), horarioUsuario.getDiaEntrada(), horarioUsuario.getHoraEntrada(), horarioUsuario.getMinutEntrada());
        LocalDateTime sortida = formatarDateTime(horarioUsuario.getAnioSalida(), horarioUsuario.getMesSalida(), horarioUsuario.getDiaSalida(), horarioUsuario.getHoraSalida(), horarioUsuario.getMinutSalida());

        //calculem la diferencia entre entrada i sortida
        Duration diff = Duration.between(entrada, sortida);

        //ho passem a minuts
        long diffMinuts = diff.toMinutes();

        //afegim al horari el total de minuts treballats
        horarioUsuario.setTotalMinutsTreballats(diffMinuts);

        //guardem registre a la BBDD
        GuardarRegistroBBDD();



    }

    public void totalMinutsDiaris(Task<QuerySnapshot> horarisDocuments) {

        //si el resultat es successful
        if (horarisDocuments.isSuccessful()) {

            int totalTempsTreballat = 0;

            //recuperem els documents individualment
            for (QueryDocumentSnapshot historialDocument : horarisDocuments.getResult()) {
                //si el document pertany a l'usuri
                if (historialDocument.getId().contains(userAuth.getUid())) {
                    //Creem l'objecte Horari del registre que hem recuperat
                    Horario horario = historialDocument.toObject(Horario.class);
                    //si el dia de entrada recuperat es el mateix que el dia actual
                    if (horario.getDiaEntrada() == getCurrTimeGMT.zdt.getDayOfMonth()) {
                        //calculem el total de temps treballat
                        totalTempsTreballat += horario.getTotalMinutsTreballats();
                    }

                }
            }

            //mostrem el temps total
            changeTextTimeResultat(binding.totalTempsTreballat, totalTempsTreballat/60, totalTempsTreballat%60);

        } else {
            Log.d(TAG, "Error al recuperar varios documentos.");
        }

    }

    private LocalDateTime formatarDateTime(int anioEntrada, int mesEntrada, int diaEntrada, int horaEntrada, int minutEntrada) {

        LocalDateTime dataEntrada;

        //creem un LocalDateTime desde les dades recuperades
        dataEntrada = LocalDateTime.of(anioEntrada, mesEntrada, diaEntrada, horaEntrada, minutEntrada);

        return dataEntrada;

    }

    public void changeTextTimeResultat (TextView textView, long hora, long minut) {

        //formatem el text
        textView.setText(String.format("%01dh %02dm",hora,minut));
    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                updateStatus(); //this function can change value of mInterval.
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                HandlerIntroduirHores.postDelayed(mStatusChecker, mInterval);
            }
        }
    };

    private void updateStatus() {

        getFechaActual(false);

        System.out.println(horarioUsuario.getTotalMinutsTreballats());

        if (horarioUsuario.getTotalMinutsTreballats() == 2 ) {

            System.out.println(horarioUsuario.getTotalMinutsTreballats());

            Notificacio.Notificar(getContext(), "Portes 3 hores treballant", "Recorda marcar la sortida", 2);
        }

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