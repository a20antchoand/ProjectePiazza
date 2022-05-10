package com.example.piazza.controladores.employee.fragments.historial;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.piazza.classes.Horario;
import com.example.piazza.fireBase.data.ReadData;
import com.example.piazza.fireBase.data.WriteData;
import com.example.piazza.fireBase.session.AuthUserSession;
import com.example.piazza.recyclerView.historialHores.ListAdapterHistorialHores;
import com.example.piazza.recyclerView.historialHores.ListElementHistorialHores;
import com.example.testauth.R;
import com.example.piazza.commons.*;
import com.example.testauth.databinding.FragmentHistorialBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class HistorialFragment extends Fragment implements ReadData, WriteData, AuthUserSession{

    private int mInterval = 5000; // 5 seconds by default, can be changed later
    public static Handler HandlerHistorial = new Handler();

    private int horaEntrada, minutEntrada, horaSortida, minutSortida;

    private static final String TAG = "HistorialFragment: ";
    private FragmentHistorialBinding binding;
    private static View root;
    private List<ListElementHistorialHores> listElements = new ArrayList<>();

    Query query = DDBB.collection("horari")
            .orderBy("diaEntrada", Query.Direction.DESCENDING);

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHistorialBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        setup();

        return root;
    }

    private void setup() {

        //Recorrem tots els registres horaris
        getMultipldeDocuments(query, this::setElements);

        //mostrem les hores mensuals
        binding.tvHoresMensuals.setText(userAuth.getHoresMensuals() + "h");

        //Recorrem tots els registres horaris
        getMultipldeDocuments(query, this::calcularHoresTreballades);

        //Iniciem un handler
        startRepeatingTask();

    }

    public void setElements(Task<QuerySnapshot> histrorialsDocuments) {

        //si el resultat es successful
        if (histrorialsDocuments.isSuccessful()) {

            //recorrem els registres
            for (QueryDocumentSnapshot historialDocument : histrorialsDocuments.getResult()) {
                //Si el registre pertany al usuari acual
                if (historialDocument.getId().contains(userAuth.getUid())) {
                    //Creem l'objecte Historial que hem rcuperat del document
                    Horario horario = historialDocument.toObject(Horario.class);
                    //si la jornada esta acabada
                    if (horario.isEstatJornada() && horario.getDiaEntrada() != -1)
                        //creem l'item de la recycler view i l'afegim a un array list d'elements
                        listElements.add(bindDataElementHistorial(horario, historialDocument.getId()));
                }
            }

            System.out.println("Elements actualitzats");

        } else {
            Log.d(TAG, "Error al recuperar varios documentos.");
        }

        //al finalitzar, si no hi han elements a l'array
        if (listElements.size() == 0){

            //mostrem l'estat de registrres buit
            showHistorialEmpty();

        } else {

            //mostrem l'historial de registres
            showHistorial();

        }
    }

    private void showHistorial() {

        //Mostrem la recyclerView i tots els elements necessaris
        binding.titolHistorial.setVisibility(View.GONE);
        binding.imatgeHistorial.setVisibility(View.GONE);
        binding.recyclerViewHistorial.setVisibility(View.VISIBLE);
        binding.tvHoresMensuals.setVisibility(View.VISIBLE);
        binding.tvResiduHores.setVisibility(View.VISIBLE);
        binding.tvTotalHores.setVisibility(View.VISIBLE);
        binding.tvMensuals.setVisibility(View.VISIBLE);
        binding.tvExtres.setVisibility(View.VISIBLE);
        binding.tvTreballades.setVisibility(View.VISIBLE);
        binding.cvResidu.setVisibility(View.VISIBLE);
        binding.cvHoresMensuals.setVisibility(View.VISIBLE);
        binding.horesTreballadesTotal.setVisibility(View.VISIBLE);

        //Creem l'adaptador de la recyclerview
        ListAdapterHistorialHores listAdapter = new ListAdapterHistorialHores(listElements, root.getContext(), this::modificarRegistre);

        //creem la recyclerview
        RecyclerView recyclerView = root.findViewById(R.id.recyclerViewHistorial);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        recyclerView.setAdapter(listAdapter);

        binding.shimmerLayout.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);

    }

    private void modificarRegistre(ListElementHistorialHores listElementHistorialHores) {

        Horario horario = listElementHistorialHores.getHorario();
        Horario modificacio = new Horario();

        new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Estas segur que vols modificar el registre?")
                .setContentText("L'administrador decidira si la mante o no!")
                .setConfirmText("Editar")
                .setCancelText("Eliminar")
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

                            modificacio.setUsuario(horario.getUsuario());

                        horario.setModificacio(modificacio);

                        writeOneDocument(DDBB.collection("horari").document(listElementHistorialHores.getId()),horario);
                        writeOneDocument(DDBB.collection("modificacions").document(listElementHistorialHores.getId()),horario);
                        new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("S'ha enviat la modificació a validar!")
                                .show();

                        binding.recyclerViewHistorial.getAdapter().notifyDataSetChanged();
                    };

                    DatePickerDialog.OnDateSetListener mDateListenerSortida = (view, year, month, day) -> {

                        System.out.println(year + "/" + month + "/" + day);

                        modificacio.setAnioSalida(year);
                        modificacio.setMesSalida(month);
                        modificacio.setDiaSalida(day);

                        int hourSortida = listElementHistorialHores.getHorario().getHoraSalida();
                        int minuteSortida = listElementHistorialHores.getHorario().getMinutSalida();
                        TimePickerDialog mTimePicker;
                        mTimePicker = new TimePickerDialog(getContext(), mTimeListenerSortida, hourSortida, minuteSortida, true);//Yes 24 hour time
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

                             int year = listElementHistorialHores.getHorario().getAnioSalida();
                             int month = listElementHistorialHores.getHorario().getMesSalida();
                             int day = listElementHistorialHores.getHorario().getDiaSalida();

                             DatePickerDialog mTimePicker;
                             mTimePicker = new DatePickerDialog(getContext(), mDateListenerSortida, year, month, day);//Yes 24 hour time
                             mTimePicker.setTitle("Select Time");
                             mTimePicker.setIcon(getResources().getDrawable(R.drawable.lum_soft_02));
                             mTimePicker.show();
                    };

                    DatePickerDialog.OnDateSetListener mDateListenerEntrada = (view, year, month, day) -> {

                        System.out.println(year + "/" + month + "/" + day);

                        modificacio.setAnioEntrada(year);
                        modificacio.setMesEntrada(month);
                        modificacio.setDiaEntrada(day);


                        int hour = listElementHistorialHores.getHorario().getHoraEntrada();
                        int minute = listElementHistorialHores.getHorario().getMinutEntrada();
                        TimePickerDialog mTimePicker;
                        mTimePicker = new TimePickerDialog(getContext(), mTimeListenerEntrada, hour, minute, true);//Yes 24 hour time
                        mTimePicker.setTitle("Select Time");
                        mTimePicker.setIcon(getResources().getDrawable(R.drawable.lum_soft_02));
                        mTimePicker.show();

                    };

                    int year = listElementHistorialHores.getHorario().getAnioEntrada();
                    int month = listElementHistorialHores.getHorario().getMesEntrada();
                    int day = listElementHistorialHores.getHorario().getDiaEntrada();

                    DatePickerDialog mTimePicker;
                    mTimePicker = new DatePickerDialog(getContext(), mDateListenerEntrada, year, month, day);//Yes 24 hour time
                    mTimePicker.setTitle("Select Time");
                    mTimePicker.setIcon(getResources().getDrawable(R.drawable.lum_soft_02));
                    mTimePicker.show();

                    sDialog.dismissWithAnimation();
                })
                .setCancelClickListener(sweetAlertDialog -> {

                    sweetAlertDialog
                            .setTitleText("Estas segur? No podras recuperar-lo")
                            .setConfirmText("Segur")
                            .setCancelText("No")
                            .setConfirmClickListener(l -> {



                                DDBB.collection("horari").document(listElementHistorialHores.getId())
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                                                        .setTitleText("S'ha eliminat el registre correctament!")
                                                        .show();

                                                listElements.remove(listElementHistorialHores);

                                                binding.recyclerViewHistorial.getAdapter().notifyDataSetChanged();


                                            }

                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                                        .setTitleText("No s'ha pogut editar el registre!")
                                                        .show();

                                            }
                                        });

                                sweetAlertDialog.dismissWithAnimation();

                            })
                            .show();



                })
                .show();

    }

    private void updateDisplay(int hora, int minut) {

        System.out.println("UPDATE TIME: " + hora + ":" + minut);

    }

    private void showHistorialEmpty() {

        //mostrem un titol dient que no tens registres junt amb una imatge
        binding.titolHistorial.setVisibility(View.VISIBLE);
        binding.imatgeHistorial.setVisibility(View.VISIBLE);
        binding.recyclerViewHistorial.setVisibility(View.GONE);
        binding.tvHoresMensuals.setVisibility(View.GONE);
        binding.tvResiduHores.setVisibility(View.GONE);
        binding.tvTotalHores.setVisibility(View.GONE);
        binding.tvMensuals.setVisibility(View.GONE);
        binding.tvExtres.setVisibility(View.GONE);
        binding.tvTreballades.setVisibility(View.GONE);
        binding.cvHoresMensuals.setVisibility(View.GONE);
        binding.cvResidu.setVisibility(View.GONE);
        binding.horesTreballadesTotal.setVisibility(View.GONE);

    }

    private ListElementHistorialHores bindDataElementHistorial(Horario horario, String id) {

        return new ListElementHistorialHores(horario, id);

    }

    private void calcularHoresTreballades(Task<QuerySnapshot> historialsDocuments) {

        int totalTempsMes = 0;

        //si el resultat es successful
        if (historialsDocuments.isSuccessful() && getCurrTimeGMT.zdt != null) {
            //recorrem els documents
            for (QueryDocumentSnapshot historialDocument : historialsDocuments.getResult()) {
                //si el document pertany a l'usuari
                if (historialDocument.getId().contains(userAuth.getUid())) {
                    //creem l'objecte Horario recuperat del document
                    Horario horario = historialDocument.toObject(Horario.class);
                    //comprovem si el document és del mes actual
                    if (horario.getMesEntrada() == getCurrTimeGMT.zdt.getMonthValue())
                        //sumem els minuts totals treballats
                        totalTempsMes += horario.getTotalMinutsTreballats();

                }
            }

            //es mostra el total de temps treballat durant el mes
            binding.tvTotalHores.setText(String.format("%01dh %02dm",totalTempsMes/60,totalTempsMes%60));

            //calculem el residu d'hores
            calcularResiduHores(totalTempsMes);

        } else {

            new Handler(Looper.getMainLooper()).post(() -> {
                //demana el temps actual i espera resposta d ela asynk task
                String s = null;
                try {
                    s = new getCurrTimeGMT().execute().get();
                    //emmagatzema el resultat passant la cadena que hem recuperat a ZonedDateTime
                    getCurrTimeGMT.zdt = getCurrTimeGMT.getZoneDateTime(s);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error al coger la fecha", Toast.LENGTH_SHORT).show();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error al coger la fecha", Toast.LENGTH_SHORT).show();

                }

                calcularHoresTreballades(historialsDocuments);

            });

        }


    }

    private void calcularResiduHores(int totalTempsMes) {

        //minuts a treballar al mes
        int totalMinutsTreballar = Integer.parseInt(userAuth.getHoresMensuals()) * 60;
        int residu;

        //si els minuts a treballar son majors als minuts treballats
        //mostrem en negatiu el residu d'hores.
        if (totalMinutsTreballar > totalTempsMes) {
            residu = totalMinutsTreballar - totalTempsMes;
            binding.tvResiduHores.setText(String.format("-%01dh %02dm",residu/60,residu%60));
            binding.tvResiduHores.setTextColor(root.getContext().getResources().getColor(R.color.end_btn));

        //si els minuts a treballar son menors als minuts treballats
        //mostrem en positiu el residu d'hores.
        } else if (totalMinutsTreballar < totalTempsMes) {
            residu = totalTempsMes - totalMinutsTreballar;
            binding.tvResiduHores.setText(String.format("+%01dh %02dm",residu/60,residu%60));
            binding.tvResiduHores.setTextColor(root.getContext().getResources().getColor(R.color.start_btn));
        //mostrem el residu a 00:00
        } else {
            binding.tvResiduHores.setText("0h 0m");
            binding.tvResiduHores.setTextColor(root.getContext().getResources().getColor(R.color.black));
        }
    }


    /*
    * Cada 5 segons actualitzem les hores treballades
    * */

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                updateStatus(); //this function can change value of mInterval.
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                HandlerHistorial.postDelayed(mStatusChecker, mInterval);
            }
        }
    };

    private void updateStatus() {

        getMultipldeDocuments(query, this::calcularHoresTreballades);

    }

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        HandlerHistorial.removeCallbacks(mStatusChecker);
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopRepeatingTask();
    }
}