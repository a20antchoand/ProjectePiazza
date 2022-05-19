package com.example.piazza.controladores.admin.fragments.reports;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
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

import androidx.core.app.ActivityCompat;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.piazza.classes.Horario;
import com.example.piazza.classes.Usuario;
import com.example.piazza.commons.getCurrTimeGMT;
import com.example.piazza.controladores.auth.SplashScreen;
import com.example.piazza.fireBase.data.ReadData;
import com.example.piazza.fireBase.data.WriteData;
import com.example.piazza.fireBase.session.AuthUserSession;
import com.example.piazza.recyclerView.reportHores.ListAdapterReportHores;
import com.example.piazza.recyclerView.reportHores.ListElementReportHores;
import com.example.testauth.BuildConfig;
import com.example.testauth.R;
import com.example.testauth.databinding.FragmentReportsBinding;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ReportsFragment extends Fragment implements ReadData, WriteData, AuthUserSession {

    private FragmentReportsBinding binding;
    private View root;

    private final String FORMAT_DATA = "dd/MM/YYYY";
    private final String HORES_NEGATIU = "-%01dh";
    private final String HORES_POSITIU = "+%01dh";

    private final String HORES_MINUTS_NEGATIU = "-%01dh %02dm";
    private final String HORES_MINUTS_POSITIU = "+%01dh %02dm";

    private final String HORES_NEUTRE = "%01dh";
    private final String HORES_MINUTS_NEUTRE = "%01dh %02dm";

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


        try {
            if (userAuth.getUid() != null) {

                setup();

            }else {
                startActivity(new Intent(getActivity(), SplashScreen.class));

            }
        } catch (Exception e) {
            startActivity(new Intent(getActivity(), SplashScreen.class));

        }
        // Inflate the layout for this fragment
        return root;
    }

    private void setup() {

        pedirPermisos();

        // Application of the Array to the Spinner
        getMultipldeDocuments(DDBB.collection("usuaris").whereEqualTo("empresa", userAuth.getEmpresa()), this::obtenerUsuarios);


        binding.button.setOnClickListener(l -> {
            if (binding.spnTreballador.getSelectedItem().equals(getActivity().getString(R.string.tots)))
                getMultipldeDocuments(DDBB.collection("horari").orderBy("diaEntrada"), this::exportarCSVGeneral);
            else
                getMultipldeDocuments(DDBB.collection("horari").orderBy("diaEntrada"), this::exportarCSVUsuari);

        });

        binding.constraintLayout.bringToFront();

        binding.spnTreballador.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (binding.constraintLayout.getVisibility() == View.VISIBLE)
                    ocultarSelectorData(binding.constraintLayout);

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

                        getMultipldeDocuments(DDBB.collection("horari").orderBy("diaEntrada"), this::recopilarHoresTreballadesMesActual);

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

                        getMultipldeDocuments(DDBB.collection("horari").orderBy("diaEntrada"), this::recopilarHoresTreballadesMesActual);

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

                List<Horario> registres = new ArrayList<>();

                int horesTreballades = 0, horesMensuals;

                Usuario usuari = usuarios.get(binding.spnTreballador.getSelectedItem());

                horesMensuals = Integer.parseInt(usuari.getHoresMensuals()) * 60;

                for (DocumentSnapshot documentSnapshot : querySnapshotTask.getResult()) {

                    Horario temp = documentSnapshot.toObject(Horario.class);

                    if (temp.getUsuario() != null) {
                        if (temp.getUsuario().getUid().equals(usuari.getUid())
                                && temp.getMesEntrada() == getCurrTimeGMT.zdt.getMonthValue()
                                && temp.getAnioEntrada() == getCurrTimeGMT.zdt.getYear()) {

                            horesTreballades += temp.getTotalMinutsTreballats();

                            registres.add(temp);

                        }
                    }
                }

                mostrarInformacioMesActual(usuari, horesTreballades, horesMensuals, registres);


            }

            public void mostrarInformacioMesActual(Usuario usuari, int horesTreballades, int horesMensuals, List<Horario> registres) {

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYYY");
                int residu;

                if (horesMensuals > horesTreballades) {
                    residu = horesMensuals - horesTreballades;

                    if (residu > 6000) {
                        binding.tvResiduHores.setText(String.format(HORES_NEGATIU, residu / 60));
                        binding.tvResiduHores.setTextColor(root.getContext().getResources().getColor(R.color.end_btn));
                    } else {
                        binding.tvResiduHores.setText(String.format(HORES_MINUTS_NEGATIU, residu / 60, residu % 60));
                        binding.tvResiduHores.setTextColor(root.getContext().getResources().getColor(R.color.end_btn));
                    }
                    //si els minuts a treballar son menors als minuts treballats
                    //mostrem en positiu el residu d'hores.
                } else if (horesTreballades > horesMensuals) {
                    residu = horesTreballades - horesMensuals;
                    if (residu > 6000) {
                        binding.tvResiduHores.setText(String.format(HORES_POSITIU, residu / 60));
                        binding.tvResiduHores.setTextColor(root.getContext().getResources().getColor(R.color.start_btn));
                    } else {
                        binding.tvResiduHores.setText(String.format(HORES_MINUTS_POSITIU, residu / 60, residu % 60));
                        binding.tvResiduHores.setTextColor(root.getContext().getResources().getColor(R.color.start_btn));
                    }
                    //mostrem el residu a 00:00
                } else {
                    binding.tvResiduHores.setText("0h 0m");
                    binding.tvResiduHores.setTextColor(root.getContext().getResources().getColor(R.color.black));
                }

                if (horesMensuals > 6000) {
                    binding.tvHoresMensuals.setText(String.format(HORES_NEUTRE, horesMensuals / 60));
                } else {
                    binding.tvHoresMensuals.setText(String.format(HORES_MINUTS_NEUTRE, horesMensuals / 60, horesMensuals % 60));
                }

                if (horesTreballades > 6000) {
                    binding.tvTotalHores.setText(String.format(HORES_NEUTRE, horesTreballades / 60));
                } else {
                    binding.tvTotalHores.setText(String.format(HORES_MINUTS_NEUTRE, horesTreballades / 60, horesTreballades % 60));
                }

                binding.progressBar2.setMax(horesMensuals);
                binding.progressBar2.setProgress(horesTreballades);
                binding.progressBar2.getIndeterminateDrawable().setColorFilter(getContext().getColor(R.color.start_btn), PorterDuff.Mode.SRC_IN);
                binding.progressBar2.getProgressDrawable().setColorFilter(getContext().getColor(R.color.start_btn), PorterDuff.Mode.SRC_IN);

                binding.percentatgeJornada.setText((horesTreballades * 100) / horesMensuals + "%");

                binding.tvData2.setText(1 + "/" + getCurrTimeGMT.zdt.getMonthValue() + "/" + getCurrTimeGMT.zdt.getYear() + " - " + getCurrTimeGMT.zdt.getDayOfMonth() + "/" + getCurrTimeGMT.zdt.getMonthValue() + "/" + getCurrTimeGMT.zdt.getYear());

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

        List<Horario> registres = new ArrayList<>();

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


        for (DocumentSnapshot documentSnapshot : querySnapshotTask.getResult()) {

            Horario temp = documentSnapshot.toObject(Horario.class);

            System.out.println("DIA: " + temp.getDiaEntrada() + " DIA ANY: " + temp.getDiaAny() + " >= " + (getCurrTimeGMT.zdt.getDayOfYear() - documentsRecuperar));

            if (temp.getUsuario().getUid().equals(usuari.getUid())
                    && temp.getDiaAny() >= (getCurrTimeGMT.zdt.getDayOfYear() - documentsRecuperar)) {

                horesTreballades += temp.getTotalMinutsTreballats();

                registres.add(temp);


            }

        }

        mostrarInformacioOpcio(usuari, horesTreballades, horesMensuals, registres);

    }

    public void mostrarInformacioOpcio(Usuario usuari, int horesTreballades, int horesMensuals, List<Horario> registres) {

        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DATA);
        int residu;

        if (horesMensuals > horesTreballades) {
            residu = horesMensuals - horesTreballades;

            if (residu > 6000) {
                binding.tvResiduHores.setText(String.format(HORES_NEGATIU, residu / 60));
                binding.tvResiduHores.setTextColor(root.getContext().getResources().getColor(R.color.end_btn));
            } else {
                binding.tvResiduHores.setText(String.format(HORES_MINUTS_NEGATIU, residu / 60, residu % 60));
                binding.tvResiduHores.setTextColor(root.getContext().getResources().getColor(R.color.end_btn));
            }
            //si els minuts a treballar son menors als minuts treballats
            //mostrem en positiu el residu d'hores.
        } else if (horesTreballades > horesMensuals) {
            residu = horesTreballades - horesMensuals;
            if (residu > 6000) {
                binding.tvResiduHores.setText(String.format(HORES_POSITIU, residu / 60));
                binding.tvResiduHores.setTextColor(root.getContext().getResources().getColor(R.color.start_btn));
            } else {
                binding.tvResiduHores.setText(String.format(HORES_MINUTS_POSITIU, residu / 60, residu % 60));
                binding.tvResiduHores.setTextColor(root.getContext().getResources().getColor(R.color.start_btn));
            }
            //mostrem el residu a 00:00
        } else {
            binding.tvResiduHores.setText("0h 0m");
            binding.tvResiduHores.setTextColor(root.getContext().getResources().getColor(R.color.black));
        }


        if (horesMensuals > 6000) {
            binding.tvHoresMensuals.setText(String.format(HORES_NEUTRE, horesMensuals / 60));
        } else {
            binding.tvHoresMensuals.setText(String.format(HORES_MINUTS_NEUTRE, horesMensuals / 60, horesMensuals % 60));
        }

        if (horesTreballades > 6000) {
            binding.tvTotalHores.setText(String.format(HORES_NEUTRE, horesTreballades / 60));
        } else {
            binding.tvTotalHores.setText(String.format(HORES_MINUTS_NEUTRE, horesTreballades / 60, horesTreballades % 60));
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


        binding.tvData2.setText(sdf.format(calendar.getTime()) + " - " + getCurrTimeGMT.zdt.getDayOfMonth() + "/" + getCurrTimeGMT.zdt.getMonthValue() + "/" + getCurrTimeGMT.zdt.getYear());

        mostrarRegistres(registres);

    }

    private void recopilarHoresTreballadesPersonalitzat(Task<QuerySnapshot> querySnapshotTask) {

        List<Horario> registres = new ArrayList<>();

        int horesTreballades = 0, horesMensuals;

        calendariInici.add(Calendar.DATE, -1);

        Date max = calendariFinal.getTime(), min = calendariInici.getTime();

        Usuario usuari = usuarios.get(binding.spnTreballador.getSelectedItem());

        horesMensuals = Integer.parseInt(usuari.getHoresMensuals()) * 60;

        horesMensuals = horesMensuals / 4;

        int differenceWeek = (int) (max.getTime() - min.getTime());

        int dies = (int) ChronoUnit.DAYS.between(min.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), max.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

        horesMensuals = horesMensuals / Integer.parseInt(usuari.getDiesSetmana());

        horesMensuals = horesMensuals * dies;

        for (DocumentSnapshot documentSnapshot : querySnapshotTask.getResult()) {

            Horario temp = documentSnapshot.toObject(Horario.class);

            if (temp.getUsuario().getUid().equals(usuari.getUid())) {

                Date data = null;
                try {
                    data = new SimpleDateFormat("yyyy-MM-dd").parse(temp.getAnioEntrada() + "-" + temp.getMesEntrada() + "-" + temp.getDiaEntrada());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (data.after(min) && data.before(max) || data.compareTo(min) == 0 || data.compareTo(max) == 0) {

                    horesTreballades += temp.getTotalMinutsTreballats();
                    registres.add(temp);

                }


            }

        }

        mostrarInformacioPersonalitzada(usuari, horesTreballades, horesMensuals, registres);


    }

    private void mostrarInformacioPersonalitzada(Usuario usuari, int horesTreballades, int horesMensuals, List<Horario> registres) {

        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DATA);
        int residu;

        if (horesMensuals > horesTreballades) {
            residu = horesMensuals - horesTreballades;

            if (residu > 6000) {
                binding.tvResiduHores.setText(String.format(HORES_NEGATIU, residu / 60));
                binding.tvResiduHores.setTextColor(root.getContext().getResources().getColor(R.color.end_btn));
            } else {
                binding.tvResiduHores.setText(String.format(HORES_MINUTS_NEGATIU, residu / 60, residu % 60));
                binding.tvResiduHores.setTextColor(root.getContext().getResources().getColor(R.color.end_btn));
            }
            //si els minuts a treballar son menors als minuts treballats
            //mostrem en positiu el residu d'hores.
        } else if (horesTreballades > horesMensuals) {
            residu = horesTreballades - horesMensuals;
            if (residu > 6000) {
                binding.tvResiduHores.setText(String.format(HORES_POSITIU, residu / 60));
                binding.tvResiduHores.setTextColor(root.getContext().getResources().getColor(R.color.start_btn));
            } else {
                binding.tvResiduHores.setText(String.format(HORES_MINUTS_POSITIU, residu / 60, residu % 60));
                binding.tvResiduHores.setTextColor(root.getContext().getResources().getColor(R.color.start_btn));
            }
            //mostrem el residu a 00:00
        } else {
            binding.tvResiduHores.setText("0h 0m");
            binding.tvResiduHores.setTextColor(root.getContext().getResources().getColor(R.color.black));
        }


        if (horesMensuals > 6000) {
            binding.tvHoresMensuals.setText(String.format(HORES_NEUTRE, horesMensuals / 60));
        } else {
            binding.tvHoresMensuals.setText(String.format(HORES_MINUTS_NEUTRE, horesMensuals / 60, horesMensuals % 60));
        }
        if (horesTreballades > 6000) {
            binding.tvTotalHores.setText(String.format(HORES_NEUTRE, horesTreballades / 60));
        } else {
            binding.tvTotalHores.setText(String.format(HORES_MINUTS_NEUTRE, horesTreballades / 60, horesTreballades % 60));
        }

        binding.progressBar2.setMax(horesMensuals);
        binding.progressBar2.setProgress(horesTreballades);
        binding.progressBar2.getIndeterminateDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
        binding.progressBar2.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);

        binding.percentatgeJornada.setText((horesTreballades * 100) / horesMensuals + "%");


        binding.tvData2.setText(sdf.format(calendariInici.getTime()) + " - " + sdf.format(calendariFinal.getTime()));

        mostrarRegistres(registres);

    }

    private void mostrarRegistres(List<Horario> registres) {

        List<ListElementReportHores> listRegistres = new ArrayList<>();

        for (Horario horario : registres) {

            listRegistres.add(new ListElementReportHores(horario));

        }

        //Creem l'adaptador de la recyclerview
        ListAdapterReportHores listAdapter = new ListAdapterReportHores(listRegistres, getContext());

        //creem la recyclerview
        RecyclerView recyclerView = binding.rceyclerViewReport;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(listAdapter);

    }


    public void mostrarSelectorData(View view) {

        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                view.getHeight(),  // fromYDelta
                0);                // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.setVisibility(View.VISIBLE);
        view.startAnimation(animate);

        binding.textView6.setVisibility(View.VISIBLE);
        binding.textView7.setVisibility(View.VISIBLE);
        binding.textView8.setVisibility(View.VISIBLE);
        binding.textView9.setVisibility(View.VISIBLE);
        binding.textView10.setVisibility(View.VISIBLE);

    }

    public void ocultarSelectorData(View view) {

        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,  // fromYDelta
                view.getHeight());                // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.setVisibility(View.GONE);
        view.startAnimation(animate);

        binding.textView6.setVisibility(View.GONE);
        binding.textView7.setVisibility(View.GONE);
        binding.textView8.setVisibility(View.GONE);
        binding.textView9.setVisibility(View.GONE);
        binding.textView10.setVisibility(View.GONE);


    }

    public void ocultarSelectorData(TextView item, View view) {

        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,  // fromYDelta
                view.getHeight());                // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.setVisibility(View.GONE);
        view.startAnimation(animate);

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


            DatePickerDialog.OnDateSetListener dateListenerSortida =
                    (datePicker, year, month, dayOfMonth) -> {

                        Calendar now = Calendar.getInstance();

                        now.set(year, month, dayOfMonth);

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FORMAT_DATA);

                        binding.tvData2.setText(binding.tvData2.getText() + " - " + simpleDateFormat.format(now.getTime()));

                        calendariFinal = now;

                        getMultipldeDocuments(DDBB.collection("horari").orderBy("diaEntrada"), this::recopilarHoresTreballadesPersonalitzat);

                    };

            DatePickerDialog.OnDateSetListener dateListenerEntrada =
                    (datePicker, year, month, dayOfMonth) -> {

                        Calendar now = Calendar.getInstance();

                        now.set(year, month, dayOfMonth);

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FORMAT_DATA);


                        binding.tvData2.setText(simpleDateFormat.format(now.getTime()));

                        calendariInici = now;

                        DatePickerDialog datePicker1 = new DatePickerDialog(getContext(),
                                dateListenerSortida, getCurrTimeGMT.zdt.getYear(), (getCurrTimeGMT.zdt.getMonthValue() - 1), getCurrTimeGMT.zdt.getDayOfMonth());
                        datePicker1.getDatePicker().setMinDate(0);
                        datePicker1.show();
                    };


            DatePickerDialog datePicker1 = new DatePickerDialog(getContext(),
                    dateListenerEntrada, getCurrTimeGMT.zdt.getYear(), (getCurrTimeGMT.zdt.getMonthValue() - 1), getCurrTimeGMT.zdt.getDayOfMonth());
            datePicker1.getDatePicker().setMinDate(0);
            datePicker1.show();

        }

        if (!getActivity().getString(R.string.personalitzat).equals(text))
            getMultipldeDocuments(DDBB.collection("horari").orderBy("diaEntrada"), this::recopilarHoresTreballadesOpcio);

        binding.textView6.setVisibility(View.GONE);
        binding.textView7.setVisibility(View.GONE);
        binding.textView8.setVisibility(View.GONE);
        binding.textView9.setVisibility(View.GONE);
        binding.textView10.setVisibility(View.GONE);

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

                if (temp.getRol().equals("treballador")) {
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
            fileWriter.append(horario.getHoraEntrada() + ":" + String.format("%02d", horario.getMinutEntrada()));
            fileWriter.append(",");
            fileWriter.append(horario.getHoraSalida() + ":" + String.format("%02d", horario.getMinutSalida()));
            fileWriter.append(",");
            fileWriter.append(horario.getTotalMinutsTreballats()/60 + ":" + horario.getTotalMinutsTreballats()%60);
            fileWriter.append("\n");
        }

    }

    private void compartirFitxer(File file) {

        Uri uri = FileProvider.getUriForFile(Objects.requireNonNull(getContext()),
                BuildConfig.APPLICATION_ID + ".provider", file);

        if (file.exists()) {
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