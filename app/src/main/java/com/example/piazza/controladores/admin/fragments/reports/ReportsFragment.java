package com.example.piazza.controladores.admin.fragments.reports;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toolbar;

import com.example.piazza.classes.Horario;
import com.example.piazza.classes.Usuario;
import com.example.piazza.fireBase.data.ReadData;
import com.example.piazza.fireBase.data.WriteData;
import com.example.piazza.fireBase.session.AuthUserSession;
import com.example.piazza.recyclerView.historialHores.ListAdapterHistorialHores;
import com.example.piazza.recyclerView.historialHores.ListElementHistorialHores;
import com.example.piazza.recyclerView.reportHores.ListAdapterReportHores;
import com.example.piazza.recyclerView.reportHores.ListElementReportHores;
import com.example.testauth.BuildConfig;
import com.example.testauth.R;
import com.example.testauth.databinding.FragmentReportsBinding;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.example.piazza.commons.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ReportsFragment extends Fragment implements ReadData, WriteData, AuthUserSession {

    private FragmentReportsBinding binding;
    private View root;

    private final String CAPCELERA_CSV = "NOM, DATA, ENTRADA, SORTIDA, TOTAL\n";

    Map<String, Usuario> usuarios = new HashMap<>();
    List<Usuario> listaUsuarios = new ArrayList<>();
    List<String> noms = new ArrayList<>();
    int documentsRecuperar = 0;
    Calendar calendariInici, calendariFinal;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentReportsBinding.inflate(inflater, container, false);
        root = binding.getRoot();


        new Handler(Looper.getMainLooper()).post(() -> setup());

        // Inflate the layout for this fragment
        return root;
    }

    private void setup() {

        pedirPermisos();

        // Application of the Array to the Spinner
        getMultipldeDocuments(DDBB.collection("usuaris").whereEqualTo("empresa", userAuth.getEmpresa()), this::obtenerUsuarios);


        binding.button.setOnClickListener(l -> {
            if (binding.spnTreballador.getSelectedItem().equals(getActivity().getString(R.string.tots)))
                getMultipldeDocuments(DDBB.collection("horari"), this::exportarCSVGeneral);
            else
                getMultipldeDocuments(DDBB.collection("horari"), this::exportarCSVUsuari);

        });

        binding.constraintLayout.bringToFront();

        binding.spnTreballador.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


                    if (binding.cardViewHistorial.getVisibility() == View.VISIBLE && !binding.spnTreballador.getSelectedItem().equals(getActivity().getString(R.string.tots))) {
                        TranslateAnimation animate = new TranslateAnimation(
                                0,                 // fromXDelta
                                binding.cardViewHistorial.getWidth() + 50,                 // toXDelta
                                0,  // fromYDelta
                                0);                // toYDelta
                        animate.setDuration(500);
                        animate.setFillAfter(true);
                        binding.cardViewHistorial.setVisibility(View.INVISIBLE);
                        binding.cardViewHistorial.startAnimation(animate);
                        binding.cardViewHistorial.postDelayed(() -> {

                            getMultipldeDocuments(DDBB.collection("horari"), this::recopilarHoresTreballadesMesActual);

                            TranslateAnimation animate2 = new TranslateAnimation(
                                    -(binding.cardViewHistorial.getWidth()),                 // fromXDelta
                                    0,                 // toXDelta
                                    0,  // fromYDelta
                                    0);                // toYDelta
                            animate2.setDuration(500);
                            animate2.setFillAfter(true);
                            binding.cardViewHistorial.setVisibility(View.VISIBLE);
                            binding.cardViewHistorial.startAnimation(animate2);
                        }, 500);


                    } else {
                        if (!binding.spnTreballador.getSelectedItem().equals(getActivity().getString(R.string.tots))) {

                            getMultipldeDocuments(DDBB.collection("horari"), this::recopilarHoresTreballadesMesActual);

                            TranslateAnimation animate2 = new TranslateAnimation(
                                    -(binding.cardViewHistorial.getWidth()),                 // fromXDelta
                                    0,                 // toXDelta
                                    0,  // fromYDelta
                                    0);                // toYDelta
                            animate2.setDuration(500);
                            animate2.setFillAfter(true);
                            binding.cardViewHistorial.setVisibility(View.VISIBLE);
                            binding.cardViewHistorial.startAnimation(animate2);
                        }
                    }

                    if (binding.cardViewHistorial.getVisibility() == View.VISIBLE && binding.spnTreballador.getSelectedItem().equals(getActivity().getString(R.string.tots))) {
                        TranslateAnimation animate = new TranslateAnimation(
                                0,                 // fromXDelta
                                binding.cardViewHistorial.getWidth() + 50,                 // toXDelta
                                0,  // fromYDelta
                                0);                // toYDelta
                        animate.setDuration(500);
                        animate.setFillAfter(true);
                        binding.cardViewHistorial.setVisibility(View.INVISIBLE);
                        binding.cardViewHistorial.startAnimation(animate);
                    }


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

            private void recopilarHoresTreballadesMesActual(Task<QuerySnapshot> querySnapshotTask) {

                Map<String, Horario> registres = new HashMap<>();

                int horesTreballades = 0, horesMensuals;

                Usuario usuari = usuarios.get(binding.spnTreballador.getSelectedItem());
                horesMensuals = Integer.parseInt(usuari.getHoresMensuals()) * 60;

                for (DocumentSnapshot documentSnapshot : querySnapshotTask.getResult()) {

                    Horario temp = documentSnapshot.toObject(Horario.class);

                    if (temp.getUsuario().getUid().equals(usuari.getUid())
                            && temp.getMesEntrada() == getCurrTimeGMT.zdt.getMonthValue()
                            && temp.getAnioEntrada() == getCurrTimeGMT.zdt.getYear()) {

                        horesTreballades += temp.getTotalMinutsTreballats();

                        registres.put(documentSnapshot.getId(), temp);

                    }

                }

                mostrarInformacioMesActual(usuari, horesTreballades, horesMensuals, registres);


            }

            public void mostrarInformacioMesActual(Usuario usuari, int horesTreballades, int horesMensuals, Map<String, Horario> registres) {

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYYY");
                int residu;

                if (horesMensuals > horesTreballades) {
                    residu = horesMensuals - horesTreballades;

                    if (residu > 6000) {
                        binding.tvResiduHores.setText(String.format("-%01dh", residu / 60));
                        binding.tvResiduHores.setTextColor(root.getContext().getResources().getColor(R.color.end_btn));
                    } else {
                        binding.tvResiduHores.setText(String.format("-%01dh %02dm", residu / 60, residu % 60));
                        binding.tvResiduHores.setTextColor(root.getContext().getResources().getColor(R.color.end_btn));
                    }
                    //si els minuts a treballar son menors als minuts treballats
                    //mostrem en positiu el residu d'hores.
                } else if (horesTreballades > horesMensuals) {
                    residu = horesTreballades - horesMensuals;
                    if (residu > 6000) {
                        binding.tvResiduHores.setText(String.format("+%01dh", residu / 60));
                        binding.tvResiduHores.setTextColor(root.getContext().getResources().getColor(R.color.start_btn));
                    } else {
                        binding.tvResiduHores.setText(String.format("+%01dh %02dm", residu / 60, residu % 60));
                        binding.tvResiduHores.setTextColor(root.getContext().getResources().getColor(R.color.start_btn));
                    }
                    //mostrem el residu a 00:00
                } else {
                    binding.tvResiduHores.setText("0h 0m");
                    binding.tvResiduHores.setTextColor(root.getContext().getResources().getColor(R.color.black));
                }



                if (horesMensuals > 6000) {
                    binding.tvHoresMensuals.setText(String.format("%01dh", horesMensuals / 60));
                } else {
                    binding.tvHoresMensuals.setText(String.format("%01dh %02dm", horesMensuals/60, horesMensuals%60));
                }

                if (horesTreballades > 6000) {
                    binding.tvTotalHores.setText(String.format("%01dh", horesTreballades / 60));
                } else {
                    binding.tvTotalHores.setText(String.format("%01dh %02dm", horesTreballades / 60, horesTreballades % 60));
                }

                binding.progressBar2.setMax(horesMensuals);
                binding.progressBar2.setProgress(horesTreballades);
                binding.progressBar2.getIndeterminateDrawable().setColorFilter(getContext().getColor(R.color.start_btn), PorterDuff.Mode.SRC_IN);
                binding.progressBar2.getProgressDrawable().setColorFilter(getContext().getColor(R.color.start_btn), PorterDuff.Mode.SRC_IN);

                binding.percentatgeJornada.setText((horesTreballades * 100) / horesMensuals + "%");

                binding.tvData2.setText(1 + "/" + getCurrTimeGMT.zdt.getMonthValue() + "/" + getCurrTimeGMT.zdt.getYear() + " - " +  getCurrTimeGMT.zdt.getDayOfMonth() + "/" + getCurrTimeGMT.zdt.getMonthValue() + "/" + getCurrTimeGMT.zdt.getYear());

                mostrarRegistres(registres);

            }

        });

        binding.imageButton.setOnClickListener(l -> mostrarSelectorData(binding.constraintLayout));

        binding.textView6.setOnClickListener(l -> ocultarSelectorData(binding.textView6, binding.constraintLayout));
        binding.textView7.setOnClickListener(l -> ocultarSelectorData(binding.textView7, binding.constraintLayout));
        binding.textView8.setOnClickListener(l -> ocultarSelectorData(binding.textView8, binding.constraintLayout));
        binding.textView9.setOnClickListener(l -> ocultarSelectorData(binding.textView9, binding.constraintLayout));
        binding.textView10.setOnClickListener(l -> ocultarSelectorData(binding.textView10, binding.constraintLayout));
    }

    private void recopilarHoresTreballadesOpcio(Task<QuerySnapshot> querySnapshotTask) {

        Map<String, Horario> registres = new HashMap<>();

        int horesTreballades = 0, horesMensuals;

        Usuario usuari = usuarios.get(binding.spnTreballador.getSelectedItem());
        horesMensuals = Integer.parseInt(usuari.getHoresMensuals()) * 60;

        switch (documentsRecuperar) {
            case 1:
                horesMensuals = (horesMensuals / 4) / Integer.parseInt(usuari.getDiesSetmana());
                break;
            case 7:
                horesMensuals = (horesMensuals / 4);
                break;
            case 365:
                horesMensuals = horesMensuals * 12;
                break;
        }

        System.out.println("HORES: " + horesMensuals);
        System.out.println("DOCUMENTS: " + documentsRecuperar);

        for (DocumentSnapshot documentSnapshot : querySnapshotTask.getResult()) {

            Horario temp = documentSnapshot.toObject(Horario.class);

            if (temp.getUsuario().getUid().equals(usuari.getUid())
                    && temp.getDiaAny() > (getCurrTimeGMT.zdt.getDayOfYear() - documentsRecuperar)) {

                horesTreballades += temp.getTotalMinutsTreballats();

                registres.put(documentSnapshot.getId(), temp);

            }

        }

        mostrarInformacioOpcio(usuari, horesTreballades, horesMensuals, registres);

    }

    public void mostrarInformacioOpcio(Usuario usuari, int horesTreballades, int horesMensuals, Map<String, Horario> registres) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYYY");
        int residu;

        if (horesMensuals > horesTreballades) {
            residu = horesMensuals - horesTreballades;

            if (residu > 6000) {
                binding.tvResiduHores.setText(String.format("-%01dh", residu / 60));
                binding.tvResiduHores.setTextColor(root.getContext().getResources().getColor(R.color.end_btn));
            } else {
                binding.tvResiduHores.setText(String.format("-%01dh %02dm", residu / 60, residu % 60));
                binding.tvResiduHores.setTextColor(root.getContext().getResources().getColor(R.color.end_btn));
            }
            //si els minuts a treballar son menors als minuts treballats
            //mostrem en positiu el residu d'hores.
        } else if (horesTreballades > horesMensuals) {
            residu = horesTreballades - horesMensuals;
            if (residu > 6000) {
                binding.tvResiduHores.setText(String.format("+%01dh", residu / 60));
                binding.tvResiduHores.setTextColor(root.getContext().getResources().getColor(R.color.start_btn));
            } else {
                binding.tvResiduHores.setText(String.format("+%01dh %02dm", residu / 60, residu % 60));
                binding.tvResiduHores.setTextColor(root.getContext().getResources().getColor(R.color.start_btn));
            }
            //mostrem el residu a 00:00
        } else {
            binding.tvResiduHores.setText("0h 0m");
            binding.tvResiduHores.setTextColor(root.getContext().getResources().getColor(R.color.black));
        }



        if (horesMensuals > 6000) {
            binding.tvHoresMensuals.setText(String.format("%01dh", horesMensuals / 60));
        } else {
            binding.tvHoresMensuals.setText(String.format("%01dh %02dm", horesMensuals/60, horesMensuals%60));
        }

        if (horesTreballades > 6000) {
            binding.tvTotalHores.setText(String.format("%01dh", horesTreballades / 60));
        } else {
            binding.tvTotalHores.setText(String.format("%01dh %02dm", horesTreballades / 60, horesTreballades % 60));
        }

        binding.progressBar2.setMax(horesMensuals);
        binding.progressBar2.setProgress(horesTreballades);
        binding.progressBar2.getIndeterminateDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
        binding.progressBar2.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);

        binding.percentatgeJornada.setText((horesTreballades * 100) / horesMensuals + "%");

        if (documentsRecuperar != 0) {
            documentsRecuperar--;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, (getCurrTimeGMT.zdt.getDayOfYear() - documentsRecuperar));


        binding.tvData2.setText(sdf.format(calendar.getTime()) + " - " +  getCurrTimeGMT.zdt.getDayOfMonth() + "/" + getCurrTimeGMT.zdt.getMonthValue() + "/" + getCurrTimeGMT.zdt.getYear());

        mostrarRegistres(registres);

    }

    private void recopilarHoresTreballadesPersonalitzat(Task<QuerySnapshot> querySnapshotTask) {

        Map<String, Horario> registres = new HashMap<>();

        int horesTreballades = 0, horesMensuals;

        Date max = calendariFinal.getTime(), min = calendariInici.getTime();

        Usuario usuari = usuarios.get(binding.spnTreballador.getSelectedItem());

        horesMensuals = Integer.parseInt(usuari.getHoresMensuals()) * 60;

        horesMensuals = horesMensuals / 4;

        int differenceWeek = (int) (max.getTime() - min.getTime());

        int dies = (int) ChronoUnit.DAYS.between(min.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), max.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

        horesMensuals = horesMensuals / Integer.parseInt(usuari.getDiesSetmana());

        horesMensuals = horesMensuals * dies;

        System.out.println("TEMPS: " + differenceWeek);
        for (DocumentSnapshot documentSnapshot : querySnapshotTask.getResult()) {

            Horario temp = documentSnapshot.toObject(Horario.class);

            if (temp.getUsuario().getUid().equals(usuari.getUid())) {

                Date data = null;
                try {
                    data = new SimpleDateFormat("yyyy-MM-dd").parse(temp.getAnioEntrada() + "-" + temp.getMesEntrada() +"-" + temp.getDiaEntrada());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (data.after(min) && data.before(max)) {

                    horesTreballades += temp.getTotalMinutsTreballats();

                }

                registres.put(documentSnapshot.getId(), temp);

            }

        }

        mostrarInformacioPersonalitzada(usuari, horesTreballades, horesMensuals, registres);


    }

    private void mostrarInformacioPersonalitzada(Usuario usuari, int horesTreballades, int horesMensuals, Map<String, Horario> registres) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYYY");
        int residu;

        if (horesMensuals > horesTreballades) {
            residu = horesMensuals - horesTreballades;

            if (residu > 6000) {
                binding.tvResiduHores.setText(String.format("-%01dh", residu / 60));
                binding.tvResiduHores.setTextColor(root.getContext().getResources().getColor(R.color.end_btn));
            } else {
                binding.tvResiduHores.setText(String.format("-%01dh %02dm", residu / 60, residu % 60));
                binding.tvResiduHores.setTextColor(root.getContext().getResources().getColor(R.color.end_btn));
            }
            //si els minuts a treballar son menors als minuts treballats
            //mostrem en positiu el residu d'hores.
        } else if (horesTreballades > horesMensuals) {
            residu = horesTreballades - horesMensuals;
            if (residu > 6000) {
                binding.tvResiduHores.setText(String.format("+%01dh", residu / 60));
                binding.tvResiduHores.setTextColor(root.getContext().getResources().getColor(R.color.start_btn));
            } else {
                binding.tvResiduHores.setText(String.format("+%01dh %02dm", residu / 60, residu % 60));
                binding.tvResiduHores.setTextColor(root.getContext().getResources().getColor(R.color.start_btn));
            }
            //mostrem el residu a 00:00
        } else {
            binding.tvResiduHores.setText("0h 0m");
            binding.tvResiduHores.setTextColor(root.getContext().getResources().getColor(R.color.black));
        }



        if (horesMensuals > 6000) {
            binding.tvHoresMensuals.setText(String.format("%01dh", horesMensuals / 60));
        } else {
            binding.tvHoresMensuals.setText(String.format("%01dh %02dm", horesMensuals/60, horesMensuals%60));
        }
        if (horesTreballades > 6000) {
            binding.tvTotalHores.setText(String.format("%01dh", horesTreballades / 60));
        } else {
            binding.tvTotalHores.setText(String.format("%01dh %02dm", horesTreballades / 60, horesTreballades % 60));
        }

        binding.progressBar2.setMax(horesMensuals);
        binding.progressBar2.setProgress(horesTreballades);
        binding.progressBar2.getIndeterminateDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
        binding.progressBar2.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);

        binding.percentatgeJornada.setText((horesTreballades * 100) / horesMensuals + "%");


        binding.tvData2.setText(sdf.format(calendariInici.getTime()) + " - " +  sdf.format(calendariFinal.getTime()));

        mostrarRegistres(registres);

    }

    private void mostrarRegistres(Map<String, Horario> registres) {

        List<ListElementReportHores> listRegistres = new ArrayList<>();

        for (Map.Entry<String, Horario> map : registres.entrySet()) {

            listRegistres.add(new ListElementReportHores(map.getValue(), map.getKey()));

        }

        //Creem l'adaptador de la recyclerview
        ListAdapterReportHores listAdapter = new ListAdapterReportHores(listRegistres, getContext(), null);

        //creem la recyclerview
        RecyclerView recyclerView = binding.rceyclerViewReport;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(listAdapter);

    }




    public void mostrarSelectorData(View view) {

        if (view.getVisibility() != View.VISIBLE) {
            binding.textView6.setVisibility(View.VISIBLE);
            binding.textView7.setVisibility(View.VISIBLE);
            binding.textView8.setVisibility(View.VISIBLE);
            binding.textView9.setVisibility(View.VISIBLE);
            binding.textView10.setVisibility(View.VISIBLE);

            TranslateAnimation animate = new TranslateAnimation(
                    0,                 // fromXDelta
                    0,                 // toXDelta
                    view.getHeight(),  // fromYDelta
                    0);                // toYDelta
            animate.setDuration(500);
            animate.setFillAfter(true);
            view.setVisibility(View.VISIBLE);
            view.startAnimation(animate);


        }
    }

    public void ocultarSelectorData(TextView item, View view) {

        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,  // fromYDelta
                view.getHeight());                // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.setVisibility(View.INVISIBLE);
        view.startAnimation(animate);

        binding.textView6.setVisibility(View.INVISIBLE);
        binding.textView7.setVisibility(View.INVISIBLE);
        binding.textView8.setVisibility(View.INVISIBLE);
        binding.textView9.setVisibility(View.INVISIBLE);
        binding.textView10.setVisibility(View.INVISIBLE);

        String text = item.getText().toString();
        if (getActivity().getString(R.string.dia).equals(text)) {
            documentsRecuperar = 1;
        } else if (getActivity().getString(R.string.setmana).equals(text)) {
            documentsRecuperar = 7;
        } else if (getActivity().getString(R.string.mes).equals(text)) {
            documentsRecuperar = 30;
        } else if (getActivity().getString(R.string.any).equals(text)) {
            documentsRecuperar = 365;
        } else if (getActivity().getString(R.string.personalitzat).equals(text)) {


            DatePickerDialog.OnDateSetListener dateListener1 =
                    (datePicker, year, month, dayOfMonth) -> {

                        Calendar now = Calendar.getInstance();

                        now.set(year, month, dayOfMonth);

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/YYYY");

                        binding.tvData2.setText(binding.tvData2.getText() + " - " + simpleDateFormat.format(now.getTime()));

                        calendariFinal = now;

                        getMultipldeDocuments(DDBB.collection("horari"), this::recopilarHoresTreballadesPersonalitzat);

                    };

            DatePickerDialog.OnDateSetListener dateListener =
                    (datePicker, year, month, dayOfMonth) -> {

                        Calendar now = Calendar.getInstance();

                        now.set(year, month, dayOfMonth-1);

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

                        System.out.println(now.getTime());

                        binding.tvData2.setText(simpleDateFormat.format(now.getTime()));

                        calendariInici = now;

                        DatePickerDialog datePicker1 = new DatePickerDialog(getContext(),
                                dateListener1, getCurrTimeGMT.zdt.getYear(), (getCurrTimeGMT.zdt.getMonthValue()-1), getCurrTimeGMT.zdt.getDayOfMonth());
                        datePicker1.getDatePicker().setMinDate(0);
                        datePicker1.show();
                    };


            DatePickerDialog datePicker1 = new DatePickerDialog(getContext(),
                    dateListener, getCurrTimeGMT.zdt.getYear(), (getCurrTimeGMT.zdt.getMonthValue()-1), getCurrTimeGMT.zdt.getDayOfMonth());
            datePicker1.getDatePicker().setMinDate(0);
            datePicker1.show();

        }

        if (!getActivity().getString(R.string.personalitzat).equals(text))
            getMultipldeDocuments(DDBB.collection("horari"), this::recopilarHoresTreballadesOpcio);


    }

    public void pedirPermisos() {
        // PERMISOS PARA ANDROID 6 O SUPERIOR
        int permissionCheck = ContextCompat.checkSelfPermission(
                getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Log.i("Mensaje", "No se tiene permiso para leer.");
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 225);
        } else {
            Log.i("Mensaje", "Se tiene permiso para leer!");
        }

        int permissionCheck2 = ContextCompat.checkSelfPermission(
                getContext(), Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permissionCheck2 != PackageManager.PERMISSION_GRANTED) {
            Log.i("Mensaje", "No se tiene permiso para leer.");
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 225);
        } else {
            Log.i("Mensaje", "Se tiene permiso para escribir!");
        }
    }

    private void obtenerUsuarios(Task<QuerySnapshot> querySnapshotTask) {



        noms.add("Tots");

        if (querySnapshotTask.isSuccessful()) {

            for (DocumentSnapshot document : querySnapshotTask.getResult().getDocuments()) {

                Usuario temp = document.toObject(Usuario.class);

                if (!temp.getRol().equals("admin") && temp.getNom() != null) {
                    listaUsuarios.add(temp);
                    usuarios.put(temp.getNom(), temp);
                    noms.add(temp.getNom());
                }

            }

            if (getContext() != null) {
                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, noms.toArray(new String[0]));
                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
                binding.spnTreballador.setAdapter(spinnerArrayAdapter);
            }
        }

    }

    private void exportarCSVGeneral(Task<QuerySnapshot> querySnapshotTask) {

        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(path.getPath(), "Report_" + getCurrTimeGMT.zdt.getDayOfMonth() + "_" + getCurrTimeGMT.zdt.getMonthValue() + "_" + getCurrTimeGMT.zdt.getYear() + "-" + Calendar.getInstance().getTime().getMinutes() + "_" + Calendar.getInstance().getTime().getSeconds() + ".csv");

        path.mkdir();

        try {
            FileWriter fileWriter = new FileWriter(file);

            fileWriter.append(CAPCELERA_CSV);

            for (Usuario usuari : listaUsuarios) {

                if (usuari.getRol().equals("treballador")) {

                    for (DocumentSnapshot documentSnapshot : querySnapshotTask.getResult().getDocuments()) {

                        escriuLineaCSV(documentSnapshot, fileWriter, usuari);

                    }

                }

            }

            fileWriter.close();

            new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Registre generat correctament, vols enviar-lo?")
                    .setConfirmText("Compartir")
                    .setCancelText("No")
                    .setConfirmClickListener(l -> compartirFitxer(file))
                    .show();

        } catch (Exception e) {
            new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error al generar l'informe... \n" + e.getLocalizedMessage())
                    .setConfirmClickListener(l -> compartirFitxer(file))
                    .show();

        }
    }

    private void exportarCSVUsuari(Task<QuerySnapshot> querySnapshotTask) {

        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(path.getPath(), binding.spnTreballador.getSelectedItem() + "_report_" + getCurrTimeGMT.zdt.getDayOfMonth() + "_" + getCurrTimeGMT.zdt.getMonthValue() + "_" + getCurrTimeGMT.zdt.getYear() + "-" + Calendar.getInstance().getTime().getMinutes() + "_" + Calendar.getInstance().getTime().getSeconds() + ".csv");

        path.mkdir();

        try {
            FileWriter fileWriter = new FileWriter(file);

            fileWriter.append(CAPCELERA_CSV);

            Usuario usuari = usuarios.get(binding.spnTreballador.getSelectedItem());

            for (DocumentSnapshot documentSnapshot : querySnapshotTask.getResult().getDocuments()) {

                escriuLineaCSV(documentSnapshot, fileWriter, usuari);

            }

            fileWriter.close();

            new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Registre generat correctament, vols enviar-lo?")
                    .setConfirmText("Compartir")
                    .setCancelText("No")
                    .setConfirmClickListener(l -> {
                        compartirFitxer(file);
                    })
                    .show();

        } catch (Exception e) {
            e.printStackTrace();
            new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error al generar l'informe... \n" + e.getLocalizedMessage())
                    .show();

        }
    }

    private void escriuLineaCSV(DocumentSnapshot documentSnapshot, FileWriter fileWriter, Usuario usuari) throws IOException {

        Horario horario = documentSnapshot.toObject(Horario.class);

        if (horario.getUsuario().getUid().equals(usuari.getUid()) && horario.getMesEntrada() == (getCurrTimeGMT.zdt.getMonthValue())) {

            fileWriter.append(usuari.getNom());
            fileWriter.append(",");
            fileWriter.append(horario.getDiaEntrada() + "/" + horario.getMesEntrada() + "/" + horario.getAnioEntrada());
            fileWriter.append(",");
            fileWriter.append(horario.getHoraEntrada() + ":" + String.format("%02d",horario.getMinutEntrada()));
            fileWriter.append(",");
            fileWriter.append(horario.getHoraSalida() + ":" + String.format("%02d",horario.getMinutSalida()));
            fileWriter.append(",");
            fileWriter.append(horario.getTotalMinutsTreballats() + "");
            fileWriter.append("\n");
        }

    }

    private void compartirFitxer(File file) {

        Uri uri = FileProvider.getUriForFile(Objects.requireNonNull(getContext()),
                BuildConfig.APPLICATION_ID + ".provider", file);

        if(file.exists()) {
            Intent intent = ShareCompat.IntentBuilder.from(Objects.requireNonNull(getActivity()))
                    .setStream(uri) // uri from FileProvider
                    .setType("application/*")
                    .getIntent()
                    .setAction(Intent.ACTION_SEND) //Change if needed
                    .setDataAndType(uri, "application/*")
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(intent);
        }

    }

}