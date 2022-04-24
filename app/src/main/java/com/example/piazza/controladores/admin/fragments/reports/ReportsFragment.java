package com.example.piazza.controladores.admin.fragments.reports;

import android.Manifest;
import android.content.Context;
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
import android.widget.Toast;

import com.example.piazza.classes.Horario;
import com.example.piazza.classes.Usuario;
import com.example.piazza.controladores.admin.AdminActivity;
import com.example.piazza.controladores.admin.fragments.validar.ValidarFragment;
import com.example.piazza.controladores.auth.SplashScreen;
import com.example.piazza.fireBase.data.ReadData;
import com.example.piazza.fireBase.data.WriteData;
import com.example.piazza.fireBase.session.AuthUserSession;
import com.example.testauth.BuildConfig;
import com.example.testauth.databinding.FragmentReportsBinding;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.example.piazza.commons.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class ReportsFragment extends Fragment implements ReadData, WriteData, AuthUserSession {

    private FragmentReportsBinding binding;
    private View root;

    List<Usuario> listaUsuarios = new ArrayList<>();

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

        getMultipldeDocuments(DDBB.collection("usuaris"), this::obtenerUsuarios);


        binding.button.setOnClickListener(l -> getMultipldeDocuments(DDBB.collection("horari"), this::exportarCSV));

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


        if (querySnapshotTask.isSuccessful()) {

            for (DocumentSnapshot document : querySnapshotTask.getResult().getDocuments()) {

                Usuario temp = document.toObject(Usuario.class);

                listaUsuarios.add(temp);

            }

        }

    }


    private void exportarCSV(Task<QuerySnapshot> querySnapshotTask) {

        pedirPermisos();

        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        System.out.println(path.toString());

        File file = new File(path, "Report_" + getCurrTimeGMT.zdt.getDayOfMonth() + "_" + getCurrTimeGMT.zdt.getMonthValue() + "_" + getCurrTimeGMT.zdt.getYear() + ".csv");

        path.mkdir();

        try {
            FileWriter fileWriter = new FileWriter(file);

            fileWriter.append("NOM, ENTRADA, SORTIDA, TOTAL\n\n");

            for (Usuario usuari : listaUsuarios) {

                if (usuari.getRol().equals("treballador")) {

                    for (DocumentSnapshot documentSnapshot : querySnapshotTask.getResult().getDocuments()) {

                        Horario horario = documentSnapshot.toObject(Horario.class);

                        if (horario.getUsuario().getUid().equals(usuari.getUid())) {

                            System.out.println(usuari.getNom());

                            fileWriter.append(usuari.getNom());
                            fileWriter.append(",");
                            fileWriter.append(horario.getHoraEntrada() + ":" + horario.getMinutEntrada());
                            fileWriter.append(",");
                            fileWriter.append(horario.getHoraSalida() + ":" + horario.getMinutSalida());
                            fileWriter.append(",");
                            fileWriter.append(horario.getTotalMinutsTreballats() + "");
                            fileWriter.append("\n");
                        }

                    }

                    fileWriter.append("\n");

                }

            }

            System.out.println("UEP");
            Toast.makeText(this.getContext(), "SE CREO EL ARCHIVO CSV EXITOSAMENTE", Toast.LENGTH_SHORT).show();

            fileWriter.close();

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

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this.getContext(), "ERROR \"PERMISOS\"", Toast.LENGTH_SHORT).show();

        }
    }



}