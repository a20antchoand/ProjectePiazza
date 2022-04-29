package com.example.piazza.controladores.admin.fragments.reports;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.piazza.classes.Horario;
import com.example.piazza.classes.Usuario;
import com.example.piazza.fireBase.data.ReadData;
import com.example.piazza.fireBase.data.WriteData;
import com.example.piazza.fireBase.session.AuthUserSession;
import com.example.testauth.BuildConfig;
import com.example.testauth.R;
import com.example.testauth.databinding.FragmentReportsBinding;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.example.piazza.commons.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ReportsFragment extends Fragment implements ReadData, WriteData, AuthUserSession {

    private FragmentReportsBinding binding;
    private View root;

    private final String CAPCELERA_CSV = "NOM, DATA, ENTRADA, SORTIDA, TOTAL\n";

    Map<String, Usuario> usuarios = new HashMap<>();
    List<Usuario> listaUsuarios = new ArrayList<>();
    List<String> noms = new ArrayList<>();
    int documentsRecuperar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentReportsBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        setup();

        // Inflate the layout for this fragment
        return root;
    }

    private void setup() {

        pedirPermisos();

        /*binding.btnSeleccionaFranja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePicker1 = new DatePickerDialog(getContext(),
                        dateListener, getCurrTimeGMT.zdt.getYear(), (getCurrTimeGMT.zdt.getMonthValue()-1), getCurrTimeGMT.zdt.getDayOfMonth());
                datePicker1.getDatePicker().setMinDate(0);
                datePicker1.show();

            }


            DatePickerDialog.OnDateSetListener dateListener =
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {

                            Calendar now = Calendar.getInstance();

                            now.set(year, month, dayOfMonth);

                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

                            System.out.println(now.getTime());

                            binding.tvData2.setText(simpleDateFormat.format(now.getTime()));

                            DatePickerDialog datePicker1 = new DatePickerDialog(getContext(),
                                    dateListener1, getCurrTimeGMT.zdt.getYear(), (getCurrTimeGMT.zdt.getMonthValue()-1), getCurrTimeGMT.zdt.getDayOfMonth());
                            datePicker1.getDatePicker().setMinDate(0);
                            datePicker1.show();
                        }
                    };

            DatePickerDialog.OnDateSetListener dateListener1 =
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {

                            Calendar now = Calendar.getInstance();

                            now.set(year, month, dayOfMonth);

                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

                            binding.tvData3.setText(simpleDateFormat.format(now.getTime()));

                        }
                    };

        });*/

        // Application of the Array to the Spinner
        getMultipldeDocuments(DDBB.collection("usuaris"), this::obtenerUsuarios);

        binding.button.setOnClickListener(l -> getMultipldeDocuments(DDBB.collection("horari"), this::exportarCSVGeneral));

        binding.constraintLayout.bringToFront();

        binding.spnTreballador.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!binding.spnTreballador.getSelectedItem().equals(getString(R.string.tots))) {
                    binding.cardViewHistorial.setVisibility(View.VISIBLE);
                    getMultipldeDocuments(DDBB.collection("horari"), this::mostrarInfoReport);
                } else {
                    binding.cardViewHistorial.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

            private void mostrarInfoReport(Task<QuerySnapshot> querySnapshotTask) {

                Usuario usuari = usuarios.get(binding.spnTreballador.getSelectedItem());

                int horesTreballades = 0, horesMensuals, residu;
                int cont = 0;

                for (DocumentSnapshot documentSnapshot : querySnapshotTask.getResult()) {

                    Horario temp = documentSnapshot.toObject(Horario.class);

                    if (temp.getUsuario().getUid().equals(usuari.getUid())
                            && temp.getDiaAny() > (getCurrTimeGMT.zdt.getDayOfYear() - documentsRecuperar)) {
                        System.out.println("COINCIDE");
                        horesTreballades += temp.getTotalMinutsTreballats();
                        cont++;
                    } else {
                        System.out.println("NO COINCIDE: " + temp.getUsuario().getUid() + " --> " + usuari.getUid());
                    }

                }

                horesMensuals = Integer.parseInt(usuari.getHoresMensuals()) * 60;
                binding.tvHoresMensuals.setText(usuari.getHoresMensuals());

                binding.documents.setText("DOCUMENTS: " + cont);

                if (horesMensuals > horesTreballades) {
                    residu = horesMensuals - horesTreballades;
                    binding.tvResiduHores.setText(String.format("-%01dh",residu/60));
                    binding.tvResiduHores.setTextColor(root.getContext().getResources().getColor(R.color.end_btn));

                    //si els minuts a treballar son menors als minuts treballats
                    //mostrem en positiu el residu d'hores.
                } else if (horesTreballades > horesMensuals) {
                    residu = horesTreballades - horesMensuals;
                    binding.tvResiduHores.setText(String.format("+%01dh",residu/60));
                    binding.tvResiduHores.setTextColor(root.getContext().getResources().getColor(R.color.start_btn));
                    //mostrem el residu a 00:00
                } else {
                    binding.tvResiduHores.setText("0h");
                    binding.tvResiduHores.setTextColor(root.getContext().getResources().getColor(R.color.black));
                }

                binding.tvTotalHores.setText(String.format("%01dh", horesTreballades/60));;

            }

        });
        //binding.button.setOnClickListener(l -> getMultipldeDocuments(DDBB.collection("horari"), this::mostrarInfoReport));

        binding.btnSeleccionaFranja.setOnClickListener(l -> mostrarSelectorData(binding.constraintLayout));

        binding.textView6.setOnClickListener(l -> ocultarSelectorData(binding.textView6, binding.constraintLayout));
        binding.textView7.setOnClickListener(l -> ocultarSelectorData(binding.textView7, binding.constraintLayout));
        binding.textView8.setOnClickListener(l -> ocultarSelectorData(binding.textView8, binding.constraintLayout));
        binding.textView9.setOnClickListener(l -> ocultarSelectorData(binding.textView9, binding.constraintLayout));
        binding.textView10.setOnClickListener(l -> ocultarSelectorData(binding.textView10, binding.constraintLayout));
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

        binding.tvData2.setText(item.getText());

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
        if (getString(R.string.dia).equals(text)) {
            documentsRecuperar = 1;
        } else if (getString(R.string.setmana).equals(text)) {
            documentsRecuperar = 7;
        } else if (getString(R.string.mes).equals(text)) {
            documentsRecuperar = 31;
        } else if (getString(R.string.any).equals(text)) {
            documentsRecuperar = 365;
        } else if (getString(R.string.personalitzat).equals(text)) {
            documentsRecuperar = 10000;
        }
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

        listaUsuarios.clear();
        usuarios.clear();

        noms.add(getString(R.string.tots));

        if (querySnapshotTask.isSuccessful()) {

            for (DocumentSnapshot document : querySnapshotTask.getResult().getDocuments()) {

                Usuario temp = document.toObject(Usuario.class);

                if (!temp.getRol().equals("admin") && temp.getNom() != null) {
                    listaUsuarios.add(temp);
                    usuarios.put(temp.getNom(), temp);
                    noms.add(temp.getNom());
                }

            }

            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getActivity(),   android.R.layout.simple_spinner_item, noms.stream().toArray(String[]::new));
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
            binding.spnTreballador.setAdapter(spinnerArrayAdapter);

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

            Toast.makeText(this.getContext(), "SE CREO EL ARCHIVO CSV EXITOSAMENTE", Toast.LENGTH_SHORT).show();

            fileWriter.close();

            compartirFitxer(file);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this.getContext(), "ERROR \"PERMISOS\"", Toast.LENGTH_SHORT).show();

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

            compartirFitxer(file);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this.getContext(), "ERROR \"PERMISOS\"", Toast.LENGTH_SHORT).show();

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