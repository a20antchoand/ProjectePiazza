package com.example.piazza.controladores.admin.fragments.reports;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
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
import com.example.testauth.databinding.FragmentReportsBinding;
import com.google.android.gms.tasks.Task;
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
import java.util.List;

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
        if(ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    0
            );

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

        File path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS);
        File file = new File(path, "Report.csv");

        path.mkdir();

        try {
            FileWriter fileWriter = new FileWriter(file);

            fileWriter.append("NOM, HORA_ENTRADA, MINUT_ENTRADA, HORA_SORTIDA, MINUT_SORTIDA, TOTAL\n\n");

            for (Usuario usuari : listaUsuarios) {

                if (usuari.getRol().equals("treballador")) {

                    for (DocumentSnapshot documentSnapshot : querySnapshotTask.getResult().getDocuments()) {

                        Horario horario = documentSnapshot.toObject(Horario.class);

                        if (horario.getUsuario().getUid().equals(usuari.getUid())) {

                            System.out.println(usuari.getNom());

                            fileWriter.append(usuari.getNom());
                            fileWriter.append(",");
                            fileWriter.append(horario.getHoraEntrada() + "");
                            fileWriter.append(",");
                            fileWriter.append(horario.getMinutEntrada() + "");
                            fileWriter.append(",");
                            fileWriter.append(horario.getHoraSalida() + "");
                            fileWriter.append(",");
                            fileWriter.append(horario.getMinutSalida() + "");
                            fileWriter.append(",");
                            fileWriter.append(horario.getTotalMinutsTreballats() + "");
                            fileWriter.append("\n");
                        }

                    }

                    fileWriter.append("\n");

                }

            }

            System.out.println("UEP");

            fileWriter.close();
            Toast.makeText(this.getContext(), "SE CREO EL ARCHIVO CSV EXITOSAMENTE", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();

        }
    }



}