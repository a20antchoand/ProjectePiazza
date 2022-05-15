package com.example.piazza.controladores.employee.fragments.introduir_hores;

import static com.google.firebase.crashlytics.internal.Logger.TAG;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.piazza.classes.Horario;
import com.example.piazza.commons.*;
import com.example.piazza.controladores.auth.SplashScreen;
import com.example.piazza.fireBase.data.ReadData;
import com.example.piazza.fireBase.data.WriteData;
import com.example.piazza.fireBase.session.AuthUserSession;
import com.example.piazza.recyclerView.missatges.ListAdapterMissatges;
import com.example.piazza.recyclerView.missatges.Missatge;
import com.example.piazza.recyclerView.reportHores.ListAdapterReportHores;
import com.example.testauth.BuildConfig;
import com.example.testauth.R;
import com.example.testauth.databinding.FragmentIntroduirHoresBinding;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class IntroduirHoresFragment extends Fragment implements ReadData, WriteData, AuthUserSession{

    private int mInterval = 5000; // 5 seconds by default, can be changed later
    public static Handler HandlerIntroduirHores = new Handler();
    float coordX;

    private List<Missatge> missatges = new ArrayList<>();
    private ListAdapterMissatges listAdapter;
    private Button iniciarJornadaBtn;
    private Button acabarJornadaBtn;
    private LinearLayout butons;
    private DocumentSnapshot document;
    private Horario horarioUsuario;
    private DocumentReference docRefHorari;
    private Context context;
    private Query query = DDBB.collection("horari")
            .orderBy("diaEntrada", Query.Direction.DESCENDING);

    public static int numeroDocument = 0;

    private FragmentIntroduirHoresBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentIntroduirHoresBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        context = getContext();

        setup();
        return root;
    }



    @SuppressLint("ClickableViewAccessibility")
    public void setup () {
        switch(getFirstTimeRun()) {
            case 0:
                Log.d("appPreferences", "Es la primera vez!");
                break; case 1: Log.d("appPreferences", "ya has iniciado la app alguna vez");
                break; case 2: Log.d("appPreferences", "es una versión nueva");
        }
        //Actualitzem el numero de document
        getMultipldeDocuments(query, this::updateDocumentNumber);
        //agafem la referencia del document actual
        docRefHorari = DDBB.collection("horari").document(getCurrTimeGMT.zdt.getYear() + "_" + getCurrTimeGMT.zdt.getMonthValue() + "_" + getCurrTimeGMT.zdt.getDayOfMonth() +  "_" + userAuth.getUid() + "_" + numeroDocument);
        //iniciem variables
        iniciarJornadaBtn = binding.iniciarJornada;
        acabarJornadaBtn =binding.acabarJornada;
        butons = binding.butonsLayout;
        horarioUsuario = new Horario();
        //donem la benvingua a l'usuari
        //indiquem la funció del boto iniciarJornada
        binding.iniciarJornada.setOnClickListener(this::iniciarJornada);
        //indiquem la funció del boto acabarJornada
        binding.acabarJornada.setOnClickListener(this::acabarJornada);
        //binding.downArrow.setOnClickListener(this::amagarButons);
        //recuperem el registre de l'usuari actual
        RecuperarRegistroUsuariBBDD();
        //iniciem el menu
        binding.imageButton2.setOnClickListener(l -> mostrarMenu(binding.imageButton2));
        //calculem el temps total treballats durant el dia
        getMultipldeDocuments(query, this::totalMinutsDiaris);
        //FirebaseMessaging.getInstance().subscribeToTopic("40hores");
        binding.imageView6.bringToFront();
        binding.imageView6.setX(4);
        binding.imageView6.setOnTouchListener((view, motionEvent) -> {
            switch (motionEvent.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    coordX = motionEvent.getX();
                    System.out.println("DOWN: " + coordX);
                    break; case MotionEvent.ACTION_MOVE: float dx;
                    float parentDiff = binding.textLL.getX();
                    dx = motionEvent.getX() - coordX;
                    binding.imageView6.setX(binding.imageView6.getX() + dx);
                    if (binding.imageView6.getX() < 4)
                        binding.imageView6.setX(4);
                    if (binding.imageView6.getX() > binding.textLL.getWidth() - (binding.imageView6.getWidth() + 4))
                        binding.imageView6.setX(binding.textLL.getWidth() - (binding.imageView6.getWidth() + 4));
                    System.out.println("IMAGE: " + binding.imageView6.getX());
                    System.out.println("PAREN: " + (binding.textLL.getX() - parentDiff) + " / " + ((binding.textLL.getWidth() - parentDiff) - binding.imageView6.getWidth()));
                    break; case MotionEvent.ACTION_UP: System.out.println(binding.imageView6.getX() + " -------- " + binding.textLL.getWidth() / 2);
                    if (binding.imageView6.getX() + (binding.imageView6.getWidth() / 2) < binding.textLL.getWidth() / 2) {
                        binding.imageView6.setX(4);
                        binding.imageView6.setImageDrawable(context.getDrawable(R.drawable.ic_round_arrow_forward_24));
                        if (horarioUsuario.getDiaEntrada() != -1) {
                            System.out.println("ACABAR");
                            acabarJornada(binding.imageView6);
                        }
                    } else {
                        binding.imageView6.setX(binding.textLL.getWidth() - (binding.imageView6.getWidth() + 4));
                        binding.imageView6.setImageDrawable(context.getDrawable(R.drawable.ic_round_arrow_back_24));
                        if (horarioUsuario.getDiaEntrada() == -1) {
                            System.out.println("INICIAR");
                            iniciarJornada(binding.imageView6);
                        }
                    }
            }
            return true;
        });

        getListenerCollections(DDBB.collection("missatges").orderBy("hora"), this::mostrarMissatges);

        binding.BtnEnviarMissatge.setOnClickListener(l -> {
            Missatge missatge = new Missatge(userAuth, binding.editTextMissatge.getText().toString());
            writeOneDocument(DDBB.collection("missatges").document(), missatge);
            binding.editTextMissatge.setText("");
        });
    }

    private void mostrarMissatges(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {

        missatges.clear();

        System.out.println("HEY: " + queryDocumentSnapshots.size());

        if (!queryDocumentSnapshots.isEmpty()) {

            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                Missatge missatge = documentSnapshot.toObject(Missatge.class);
                if (missatge.getUsuari().getEmpresa().equals(userAuth.getEmpresa()))
                    missatges.add(missatge);
            }

        }

        //Creem l'adaptador de la recyclerview
        listAdapter = new ListAdapterMissatges(missatges, context, null);

        //creem la recyclerview
        RecyclerView recyclerView = binding.recyclerChat;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(listAdapter);

        recyclerView.smoothScrollToPosition(missatges.size());
    }

    private void mostrarMenu(View view) {
        PopupMenu popup = new PopupMenu(context, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.introduir_hores_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(this::menuItemClick);
        popup.show();
    }

    private boolean menuItemClick(MenuItem menuItem) {

        switch (menuItem.getItemId()) {

            case R.id.introduirJornada:
                introduirRegistre();
                break;
            case R.id.gestionarChat:
                gestionarChat();
                break;

        }

        return true;
    }

    private void gestionarChat() {

        if (binding.chatLayout.getVisibility() == View.VISIBLE)
            binding.chatLayout.setVisibility(View.INVISIBLE);
        else
            binding.chatLayout.setVisibility(View.VISIBLE);

    }

    private void introduirRegistre() {
        Horario horari = new Horario();
        horari.setEstatJornada(true);
        horari.setUsuario(userAuth);
        Horario modificacio = new Horario();
        new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Estas segur que vols afegir un registre?")
                .setContentText("L'administrador decidira si el mante o no!")
                .setConfirmText("Afegir")
                .setCancelText("No")
                .setConfirmClickListener(sDialog -> {
                    TimePickerDialog.OnTimeSetListener mTimeListenerSortida =
                            (view, hour, minute) -> {
                        /**
                         * MODIFICAR PER ATRIBUT TEMPORAL (A CREAR ENCARA)
                         */
                        modificacio.setHoraSalida(hour);
                        modificacio.setMinutSalida(minute);
                        LocalDateTime dataEntrada = LocalDateTime.of(modificacio.getAnioEntrada(), modificacio.getMesEntrada(), modificacio.getDiaEntrada(), modificacio.getHoraEntrada(), modificacio.getMinutEntrada());
                        LocalDateTime dataSalida = LocalDateTime.of(modificacio.getAnioSalida(), modificacio.getMesSalida(), modificacio.getDiaSalida(), modificacio.getHoraSalida(), modificacio.getMinutSalida());
                        //calculem la diferencia entre entrada i sortida
                                Duration diff = Duration.between(dataEntrada, dataSalida);
                                //ho passem a minuts
                                long diffMinuts = diff.toMinutes();
                                //afegim al horari el total de minuts treballats
                                modificacio.setTotalMinutsTreballats(diffMinuts);
                                modificacio.setUsuario(userAuth);
                                modificacio.setEstatJornada(true);
                                horari.setModificacio(modificacio);
                                String docRef = getCurrTimeGMT.zdt.getYear() + "_" + getCurrTimeGMT.zdt.getMonthValue() + "_" + getCurrTimeGMT.zdt.getDayOfMonth() +  "_" + userAuth.getUid() + "_afegit_" + Calendar.getInstance().getTimeInMillis();
                                writeOneDocument(DDBB.collection("horari").document(docRef),horari);
                                writeOneDocument(DDBB.collection("modificacions").document(docRef),horari);
                                new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText("S'ha enviat la modificació a validar!")
                                        .show();
                    };
                    DatePickerDialog.OnDateSetListener mDateListenerSortida = (view, year, month, day) -> {
                        System.out.println(year + "/" + month + "/" + day);
                        modificacio.setAnioSalida(year);
                        modificacio.setMesSalida(month);
                        modificacio.setDiaSalida(day);
                        int hourSortida = getCurrTimeGMT.zdt.getHour();
                        int minuteSortida = getCurrTimeGMT.zdt.getMinute();
                        TimePickerDialog mTimePicker;
                        mTimePicker = new TimePickerDialog(context, mTimeListenerSortida, hourSortida, minuteSortida, true);//Yes 24 hour time
                        mTimePicker.setTitle("Select Time");
                        mTimePicker.show();
                    };
                    TimePickerDialog.OnTimeSetListener mTimeListenerEntrada =
                            (view, hour, minute) -> {
                        /**
                         * MODIFICAR PER ATRIBUT TEMPORAL (A CREAR ENCARA)
                         */
                        modificacio.setHoraEntrada(hour);
                        modificacio.setMinutEntrada(minute);
                        int year = getCurrTimeGMT.zdt.getYear();
                        int month = getCurrTimeGMT.zdt.getMonthValue();
                        int day = getCurrTimeGMT.zdt.getDayOfMonth();
                        DatePickerDialog mTimePicker;
                        mTimePicker = new DatePickerDialog(context, mDateListenerSortida, year, month, day);//Yes 24 hour time
                                mTimePicker.setTitle("Select Time");
                                mTimePicker.setIcon(getResources().getDrawable(R.drawable.lum_soft_02));
                                mTimePicker.show();
                    };
                    DatePickerDialog.OnDateSetListener mDateListenerEntrada = (view, year, month, day) -> {
                        System.out.println(year + "/" + month + "/" + day);
                        modificacio.setAnioEntrada(year);
                        modificacio.setMesEntrada(month);
                        modificacio.setDiaEntrada(day);
                        int hour = getCurrTimeGMT.zdt.getHour();
                        int minute = getCurrTimeGMT.zdt.getMinute();
                        TimePickerDialog mTimePicker;
                        mTimePicker = new TimePickerDialog(context, mTimeListenerEntrada, hour, minute, true);//Yes 24 hour time
                        mTimePicker.setTitle("Select Time");
                        mTimePicker.setIcon(getResources().getDrawable(R.drawable.lum_soft_02));
                        mTimePicker.show();
                    };
                    int year = getCurrTimeGMT.zdt.getYear();
                    int month = getCurrTimeGMT.zdt.getMonthValue();
                    int day = getCurrTimeGMT.zdt.getDayOfMonth();;
                    DatePickerDialog mTimePicker;
                    mTimePicker = new DatePickerDialog(context, mDateListenerEntrada, year, month, day);//Yes 24 hour time
                    mTimePicker.setTitle("Select Time");
                    mTimePicker.setIcon(getResources().getDrawable(R.drawable.lum_soft_02));
                    mTimePicker.show();
                    sDialog.dismissWithAnimation();
                })
                .setCancelClickListener(sweetAlertDialog -> {

                    sweetAlertDialog.dismissWithAnimation();
                }).show();

    }
    private void cargarEfecteTextEntrar() {
        binding.textTextLL.setText(getResources().getString(R.string.iniciarJornadaStr));
    }
    private void cargarEfecteTextSalir() {
        binding.textTextLL.setText(getResources().getString(R.string.acabarJornadaStr));
    }
    public void iniciarJornadaSwipe() {
        binding.imageView6.setX(4);
        binding.imageView6.setImageDrawable(context.getDrawable(R.drawable.ic_round_arrow_forward_24));
        cargarEfecteTextEntrar();
    }
    public void acabarJornadaSwipe() {
        binding.imageView6.setX(binding.textLL.getWidth() - (binding.imageView6.getWidth() + 4));
        binding.imageView6.setImageDrawable(context.getDrawable(R.drawable.ic_round_arrow_back_24));
        cargarEfecteTextSalir();
    }
    private int getFirstTimeRun() {
        SharedPreferences sp = getActivity().getSharedPreferences("Piazza", 0);
        int result, currentVersionCode = BuildConfig.VERSION_CODE;
        int lastVersionCode = sp.getInt("FIRSTTIMERUN", -1);
        if (lastVersionCode == -1) result = 0; else
            result = (lastVersionCode == currentVersionCode) ? 1 : 2;
        sp.edit().putInt("FIRSTTIMERUN", currentVersionCode).apply();
        return result;
    }
    public void iniciarJornada (View view) {

        try {
            userAuth.setTreballant(true);
            writeOneDocument(DDBB.collection("usuaris").document(userAuth.getUid()), userAuth);

            //iniciem l'horari de l'usuari
            horarioUsuario = new Horario();

            //indiquem l'usuari a l'horari
            horarioUsuario.setUsuario(userAuth);

            //augmentem el numero de document en +1
            numeroDocument = numeroDocument + 1;

            //agafem la referencia del document a treballar
            docRefHorari = DDBB.collection("horari").document(getCurrTimeGMT.zdt.getYear() + "_" + getCurrTimeGMT.zdt.getMonthValue() + "_" + getCurrTimeGMT.zdt.getDayOfMonth() + "_" + userAuth.getUid() + "_" + numeroDocument);

            //agafem la data actual
            getFechaActual(true);

            //guardem el registre a la BBDD
            GuardarRegistroBBDD();

            //amagem e lboto d'inici i mostrem el d'acabar
            changeStateButtons.hideButton(iniciarJornadaBtn);
            changeStateButtons.showButton(acabarJornadaBtn);
            acabarJornadaSwipe();

            //iniciem el handler que actualitzara el contador
            startRepeatingTask();

            new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Jornada iniciada!")
                    .setContentText("S'ha iniciat la jornada correctament!")
                    .show();
        } catch (Exception e) {
            Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
    }
    public void acabarJornada (View view) {

        userAuth.setTreballant(false);
        writeOneDocument(DDBB.collection("usuaris").document(userAuth.getUid()), userAuth);

        //indiquem que la jornada esta acabada
        horarioUsuario.setEstatJornada(true);

        //cambiem els botons ocultant el d'acabar i mostrant el d'iniciar
        changeStateButtons.hideButton(acabarJornadaBtn);
        changeStateButtons.showButton(iniciarJornadaBtn);
        iniciarJornadaSwipe();

        //agafem la dada actual i guardem la informacio
        getFechaActual(false);

        //parem el handler
        stopRepeatingTask();

        new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Jornada guardada!")
                .setContentText("La jornada ha durat: " + horarioUsuario.getTotalMinutsTreballats() / 60 + "h " + horarioUsuario.getTotalMinutsTreballats() % 60 + "m.")
                .show();

        horarioUsuario = new Horario();
    }

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
                if (horariDocument.getId().contains(userAuth.getUid()) && !horariDocument.getId().contains("afegit"))
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
        //si l'horari te hora d'entrada i la jornada esta acabada
        if (!horarioUsuario.isEstatJornada()) {
            //cambiem els butons
            changeStateButtons.hideButton(iniciarJornadaBtn);
            changeStateButtons.showButton(acabarJornadaBtn);
            acabarJornadaSwipe();
            writeOneDocument(DDBB.collection("REGISTRE").document("VEGADES LINEA 442"), userAuth);
            //iniciem el Handler
            startRepeatingTask();
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
            horarioUsuario.setDiaAny(getCurrTimeGMT.zdt.getDayOfYear());//si no, guadrem les dades i calculem les hores totals
        } else {
            horarioUsuario.setAnioSalida(getCurrTimeGMT.zdt.getYear());
            horarioUsuario.setMesSalida(getCurrTimeGMT.zdt.getMonthValue());
            horarioUsuario.setDiaSalida(getCurrTimeGMT.zdt.getDayOfMonth());
            horarioUsuario.setHoraSalida(getCurrTimeGMT.zdt.getHour());
            horarioUsuario.setMinutSalida(getCurrTimeGMT.zdt.getMinute());
            //calculem les hores
            calcularHores(horarioUsuario);
        }
    }
    private void amagarButons(View view) {
        binding.butonsLayout.setVisibility(View.GONE);
    }
    private Horario calcularHores(Horario horario) {
        //agafem la data d'entrada i la de sortida
        LocalDateTime entrada = formatarDateTime(horario.getAnioEntrada(), horario.getMesEntrada(), horario.getDiaEntrada(), horario.getHoraEntrada(), horario.getMinutEntrada());
        LocalDateTime sortida = formatarDateTime(horario.getAnioSalida(), horario.getMesSalida(), horario.getDiaSalida(), horario.getHoraSalida(), horario.getMinutSalida());
        //calculem la diferencia entre entrada i sortida
        Duration diff = Duration.between(entrada, sortida);
        //ho passem a minuts
        long diffMinuts = diff.toMinutes();
        //afegim al horari el total de minuts treballats
        horario.setTotalMinutsTreballats(diffMinuts);
        //guardem registre a la BBDD
        GuardarRegistroBBDD();
        return horario;
    }
    public void totalMinutsDiaris(Task<QuerySnapshot> horarisDocuments) {
        //si el resultat es successful
        if (horarisDocuments.isSuccessful() && getCurrTimeGMT.zdt != null) {
            int totalTempsTreballat = 0;
            //recuperem els documents individualment
            for (QueryDocumentSnapshot historialDocument : horarisDocuments.getResult()) {
                //si el document pertany a l'usuri
                if (historialDocument.getId().contains(userAuth.getUid())) {
                    //Creem l'objecte Horari del registre que hem recuperat
                    Horario horario = historialDocument.toObject(Horario.class);
                    //si el dia de entrada recuperat es el mateix que el dia actual
                    if (horario.getDiaEntrada() >= getCurrTimeGMT.zdt.getDayOfMonth()) {
                        //calculem el total de temps treballat
                        totalTempsTreballat += horario.getTotalMinutsTreballats();
                    }
                }
            }
            //mostrem el temps total
            changeTextTimeResultat(binding.totalTempsTreballat, totalTempsTreballat/60, totalTempsTreballat%60);
        } else {
            Log.d(TAG, "Error al recuperar varios documentos.");
            //demana el temps actual i espera resposta d ela asynk task
            new Handler(Looper.getMainLooper()).post(() -> {
                //demana el temps actual i espera resposta d ela asynk task
                String s = null;
                try {
                    s = new getCurrTimeGMT().execute().get();
                    //emmagatzema el resultat passant la cadena que hem recuperat a ZonedDateTime
                    getCurrTimeGMT.zdt = getCurrTimeGMT.getZoneDateTime(s);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Error al coger la fecha", Toast.LENGTH_SHORT).show();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Error al coger la fecha", Toast.LENGTH_SHORT).show();
                }
            });
            //emmagatzema el resultat passant la cadena que hem recuperat a ZonedDateTime
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
        try {
            getFechaActual(false);
            System.out.println(((Integer.parseInt(userAuth.getHoresMensuals()) / 4) / Integer.parseInt(userAuth.getDiesSetmana())) * 60);
            if (horarioUsuario.getTotalMinutsTreballats() == ((Integer.parseInt(userAuth.getHoresMensuals()) / 4) / Integer.parseInt(userAuth.getDiesSetmana())) * 60) {
                Notificacio.Notificar(context, "Portes " + horarioUsuario.getTotalMinutsTreballats() / 60 + ":" + horarioUsuario.getTotalMinutsTreballats() % 60 + " hores treballant", "Recorda marcar la sortida", 2);
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
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
    }

    @Override
    public void onResume() {
        super.onResume();

        if (userAuth.getUid() == null) {
            startActivity(new Intent(context, SplashScreen.class));
        }

    }
}