package com.example.piazza.controladores.employee.fragments.introduir_hores;

import static com.google.firebase.crashlytics.internal.Logger.TAG;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.piazza.classes.Horario;
import com.example.piazza.commons.*;
import com.example.piazza.controladores.auth.SplashScreen;
import com.example.piazza.controladores.employee.EmployeeActivity;
import com.example.piazza.fireBase.data.ReadData;
import com.example.piazza.fireBase.data.WriteData;
import com.example.piazza.fireBase.session.AuthUserSession;
import com.example.piazza.recyclerView.missatges.ListAdapterMissatges;
import com.example.piazza.recyclerView.missatges.Missatge;
import com.example.testauth.R;
import com.example.testauth.databinding.FragmentIntroduirHoresBinding;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.grpc.android.BuildConfig;

public class IntroduirHoresFragment extends Fragment implements ReadData, WriteData, AuthUserSession {

    private int mInterval = 10000;
    public static Handler handlerIntroduirHores = new Handler();
    float coordX;

    LocationManager mLocationManager;
    private String nomUbicacio = "";

    private List<Missatge> missatges = new ArrayList<>();
    private ListAdapterMissatges listAdapter;
    private Button iniciarJornadaBtn;
    private Button acabarJornadaBtn;
    private LinearLayout butons;
    private DocumentSnapshot document;
    private Horario horarioUsuario;
    private DocumentReference docRefHorari;
    private Context context;

    private Query queryJornada = DDBB.collection("horari")
            .whereEqualTo("estatJornada", false);
    public static int numeroDocument = 0;
    private ListenerRegistration registration;
    private FragmentIntroduirHoresBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentIntroduirHoresBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        try {
            if (userAuth.getUid() != null) {
                context = root.getContext();
                setup();

            } else {
                startActivity(new Intent(getActivity(), SplashScreen.class));

            }
        } catch (Exception e) {
            startActivity(new Intent(getActivity(), SplashScreen.class));

        }

        return root;
    }


    @SuppressLint("ClickableViewAccessibility")
    public void setup() {

        new Handler().postDelayed(checkInternet, 5000);

        //Actualitzem el numero de document
        getMultipldeDocuments(queryJornada, this::updateDocumentNumber);

        //agafem la referencia del document actual
        docRefHorari = DDBB.collection("horari").document(getCurrTimeGMT.zdt.getYear() + "_" + getCurrTimeGMT.zdt.getMonthValue() + "_" + getCurrTimeGMT.zdt.getDayOfMonth() +  "_" + userAuth.getUid() + "_" + numeroDocument);

        //iniciem variables
        iniciarJornadaBtn = binding.iniciarJornada;
        acabarJornadaBtn =binding.acabarJornada;
        butons = binding.butonsLayout;
        horarioUsuario = new Horario();

        //indiquem la funció del boto iniciarJornada
        binding.iniciarJornada.setOnClickListener(l -> iniciarJornada());

        //indiquem la funció del boto acabarJornada
        binding.acabarJornada.setOnClickListener(l -> acabarJornada());

        //recuperem el registre de l'usuari actual
        RecuperarRegistroUsuariBBDD();

        //iniciem el menu
        binding.imageButton2.setOnClickListener(l -> mostrarMenu(binding.imageButton2));

        //calculem el temps total treballats durant el dia
        getMultipldeDocuments(queryJornada, this::totalMinutsDiaris);

        //FirebaseMessaging.getInstance().subscribeToTopic("40hores");
        binding.imageView6.bringToFront();
        binding.imageView6.setX(4);
        binding.imageView6.setOnTouchListener((view, motionEvent) -> {
            switch (motionEvent.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    coordX = motionEvent.getX();
                    break; case MotionEvent.ACTION_MOVE: float dx;
                    float parentDiff = binding.textLL.getX();
                    dx = motionEvent.getX() - coordX;
                    binding.imageView6.setX(binding.imageView6.getX() + dx);
                    if (binding.imageView6.getX() < 4)
                        binding.imageView6.setX(4);
                    if (binding.imageView6.getX() > binding.textLL.getWidth() - (binding.imageView6.getWidth() + 4))
                        binding.imageView6.setX(binding.textLL.getWidth() - (binding.imageView6.getWidth() + 4));
                    break; case MotionEvent.ACTION_UP:
                    if (binding.imageView6.getX() + (binding.imageView6.getWidth() / 2) < binding.textLL.getWidth() / 2) {
                        iniciarJornadaSwipe();
                        if (horarioUsuario.getDiaEntrada() != -1) {
                            acabarJornada();
                        }
                    } else {
                        if (horarioUsuario.getDiaEntrada() == -1) {

                            if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()),
                                    Manifest.permission.ACCESS_FINE_LOCATION)
                                    == PackageManager.PERMISSION_DENIED) {

                                SweetAlertDialog sDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE);

                                sDialog.setTitleText("Es recomanen els permisos d'ubicació!")
                                        .setConfirmText("Donar permís")
                                        .setConfirmClickListener(sweetAlertDialog -> {
                                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                                    Uri.fromParts("package", context.getPackageName(), null));
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);

                                            sweetAlertDialog.dismissWithAnimation();
                                        })
                                .setOnDismissListener(l -> iniciarJornada());

                                sDialog.show();

                            } else {
                                iniciarJornada();
                            }


                        }

                        acabarJornadaSwipe();

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


        mLocationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);

        checkLocationPermission();

    }

    public boolean checkLocationPermission () {

        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            System.out.println("LOCATION");
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1500, 1, mLocationListener);
            return true;
        } else {

            mPermissionResult.launch(Manifest.permission.ACCESS_FINE_LOCATION);

            System.out.println("NO LOCATION");
        }

        return false;
    }

    private void mostrarMissatges(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {

        missatges.clear();


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
                        modificacio.setAnioSalida(year);
                        modificacio.setMesSalida(month+1);
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
                        int month = getCurrTimeGMT.zdt.getMonthValue()-1;
                        int day = getCurrTimeGMT.zdt.getDayOfMonth();
                        DatePickerDialog mTimePicker;
                        mTimePicker = new DatePickerDialog(context, mDateListenerSortida, year, month, day);//Yes 24 hour time
                                mTimePicker.setTitle("Select Time");
                                mTimePicker.setIcon(getResources().getDrawable(R.drawable.lum_soft_02));
                                mTimePicker.show();
                    };
                    DatePickerDialog.OnDateSetListener mDateListenerEntrada = (view, year, month, day) -> {
                        modificacio.setAnioEntrada(year);
                        modificacio.setMesEntrada(month+1);
                        modificacio.setDiaEntrada(day);
                        LocalDateTime entrada = formatarDateTime(year, month, day, 0, 0);

                        modificacio.setDiaAny(entrada.getDayOfYear());
                        int hour = getCurrTimeGMT.zdt.getHour();
                        int minute = getCurrTimeGMT.zdt.getMinute();
                        TimePickerDialog mTimePicker;
                        mTimePicker = new TimePickerDialog(context, mTimeListenerEntrada, hour, minute, true);//Yes 24 hour time
                        mTimePicker.setTitle("Select Time");
                        mTimePicker.setIcon(getResources().getDrawable(R.drawable.lum_soft_02));
                        mTimePicker.show();
                    };
                    int year = getCurrTimeGMT.zdt.getYear();
                    int month = getCurrTimeGMT.zdt.getMonthValue()-1;
                    int day = getCurrTimeGMT.zdt.getDayOfMonth();
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

    public void iniciarJornadaSwipe() {
        binding.imageView6.setX(4);
        binding.imageView6.setImageDrawable(context.getDrawable(R.drawable.ic_round_arrow_forward_24));
    }
    public void acabarJornadaSwipe() {
        binding.imageView6.setX(binding.textLL.getWidth() - (binding.imageView6.getWidth() + 4));
        binding.imageView6.setImageDrawable(context.getDrawable(R.drawable.ic_round_arrow_back_24));
    }

    public void iniciarJornada () {

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

            registration = docRefHorari.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    actualitzarHorari(value, error);
                }
            });
            getListenerDocument(getActivity(), docRefHorari, this::actualitzarHorari);

            new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Jornada iniciada!")
                    .setContentText("S'ha iniciat la jornada correctament!")
                    .show();
        } catch (Exception e) {
        }
    }

    private void actualitzarHorari(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

        if (documentSnapshot.exists()) {
            Horario horario = documentSnapshot.toObject(Horario.class);

            if (horario.isEstatJornada()) {
                horarioUsuario.setEstatJornada(true);
                iniciarJornadaSwipe();
                stopRepeatingTask();
                setup();
            }
        }

    }

    public void acabarJornada () {

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


    private void acabarJornadaAutomatic(ImageView sortida) {

        userAuth.setTreballant(false);
        writeOneDocument(DDBB.collection("usuaris").document(userAuth.getUid()), userAuth);

        //indiquem que la jornada esta acabada
        horarioUsuario.setEstatJornada(true);

        //cambiem els botons ocultant el d'acabar i mostrant el d'iniciar
        changeStateButtons.hideButton(acabarJornadaBtn);
        changeStateButtons.showButton(iniciarJornadaBtn);
        iniciarJornadaSwipe();

        //agafem la dada actual i guardem la informacio
        calcularHores(horarioUsuario);

        //parem el handler
        stopRepeatingTask();

        new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("S'ha parat la jornada automaticament!")
                .setContentText("Hem detectat la possibilitat de que no hagis marcat al sortir, si es un error pots modificar el registre: " + horarioUsuario.getTotalMinutsTreballats() / 60 + "h " + horarioUsuario.getTotalMinutsTreballats() % 60 + "m.")
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

                    Log.d("REGISTRE: ", "" + docRefHorari.getId());

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
        getMultipldeDocuments(queryJornada, this::totalMinutsDiaris);
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
        changeTextTimeResultat(binding.totalTempsTreballat, horario.getTotalMinutsTreballats()/60, horario.getTotalMinutsTreballats()%60);
        //guardem registre a la BBDD
        GuardarRegistroBBDD();
        return horario;
    }
    public void totalMinutsDiaris(Task<QuerySnapshot> horarisDocuments) {
        boolean treballant = false;
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
                    //calculem el total de temps treballat
                    changeTextTimeResultat(binding.totalTempsTreballat, horario.getTotalMinutsTreballats()/60, horario.getTotalMinutsTreballats()%60);

                    treballant = true;

                }
            }
            //mostrem el temps total
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
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            //emmagatzema el resultat passant la cadena que hem recuperat a ZonedDateTime
        }

        if (!treballant) {
            changeTextTimeResultat(binding.totalTempsTreballat, 0, 0);
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
        if (textView != null)
            textView.setText(String.format("%01dh %02dm",hora,minut));
    }





    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            System.out.println("LOCATION");

            if (nomUbicacio.equals("")) {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                try {
                    List<Address> listAddresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    if (null != listAddresses && listAddresses.size() > 0) {
                        String _Location = listAddresses.get(0).getAddressLine(0);
                        if (nomUbicacio.equals(""))
                            nomUbicacio = _Location;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @SuppressLint("MissingPermission")
    private ActivityResultLauncher<String> mPermissionResult = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            result -> {
                if(result) {
                    Log.e("PERMISSION", "onActivityResult: PERMISSION GRANTED");
                    mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1500, 1, mLocationListener);
                } else {
                    Log.e("PERMISSION", "onActivityResult: PERMISSION DENIED");
                }
            });

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                updateStatus(); //this function can change value of mInterval.
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };



    Runnable checkInternet = new Runnable() {
        @Override
        public void run() {

            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(getActivity().CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected())
                System.out.println("INTERNET OK");
            else {
                SweetAlertDialog sDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE);
                sDialog.setTitleText("Error de conexió!")
                        .setContentText("Es possible que no tinguis bona senyal d'internet... Torna a intentar-ho mes tard.")
                        .setConfirmText("Reintentar")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                startActivity(new Intent(getActivity(), SplashScreen.class));
                            }
                        })
                        .setCancelable(false);
                sDialog.show();
            }

        }
    };

    private void updateStatus() {
        try {

            System.out.println("HANDLER");
            horarioUsuario.setNomUbicacio(nomUbicacio);

            getFechaActual(false);

            if (horarioUsuario.getTotalMinutsTreballats() == ((Integer.parseInt(userAuth.getHoresMensuals()) / 4) / Integer.parseInt(userAuth.getDiesSetmana())) * 60L) {
                Notificacio.Notificar(context, "Portes " + horarioUsuario.getTotalMinutsTreballats() / 60 + ":" + horarioUsuario.getTotalMinutsTreballats() % 60 + " hores treballant", "Recorda marcar la sortida", 2);
            }

            if (horarioUsuario != null && !horarioUsuario.isEstatJornada() && horarioUsuario.getTotalMinutsTreballats() > (((Integer.parseInt(userAuth.getHoresMensuals()) / 4) / Integer.parseInt(userAuth.getDiesSetmana())) * 60L) * 1.5){

                LocalDate date = LocalDate.of(horarioUsuario.getAnioEntrada(), horarioUsuario.getMesEntrada(), horarioUsuario.getDiaEntrada());

                LocalTime time = LocalTime.of(horarioUsuario.getHoraEntrada(), horarioUsuario.getMinutEntrada());
                ZoneId zoneId = ZoneId.of("Europe/Madrid");

                ZonedDateTime zonedDateTime = ZonedDateTime.of(date, time, zoneId);

                System.out.println("MINUTS: " + ((Integer.parseInt(userAuth.getHoresMensuals()) / 4) / Integer.parseInt(userAuth.getDiesSetmana())) * 60L);

                zonedDateTime = zonedDateTime.plusMinutes(((Integer.parseInt(userAuth.getHoresMensuals()) / 4) / Integer.parseInt(userAuth.getDiesSetmana())) * 60L);

                horarioUsuario.setDiaSalida(zonedDateTime.getDayOfMonth());
                horarioUsuario.setMesSalida(zonedDateTime.getMonthValue());
                horarioUsuario.setAnioSalida(zonedDateTime.getYear());
                horarioUsuario.setHoraSalida(zonedDateTime.getHour());
                horarioUsuario.setMinutSalida(zonedDateTime.getMinute());


                acabarJornadaAutomatic(binding.imageView6);
                Notificacio.Notificar(context, "T'has oblidat de fitxar?", userAuth.getNom() + " hem detectat que has treballat mes del que et toca. Hem adaptat la jornada a les hores que has de treballar. Si ha estat un error pots modificar el registre.", 2);
            } else {
                handlerIntroduirHores.postDelayed(mStatusChecker, mInterval);
            }

        } catch (Exception e) {
        }
    }

    void startRepeatingTask() {
        try {
            handlerIntroduirHores.post(mStatusChecker);
        } catch (Exception e) {
        }
    }
    void stopRepeatingTask() {
        handlerIntroduirHores.removeCallbacks(mStatusChecker);
    }

    @Override
    public void onDestroy() {
        stopRepeatingTask();
        System.out.println("STOPED");
        super.onDestroy();
    }
}