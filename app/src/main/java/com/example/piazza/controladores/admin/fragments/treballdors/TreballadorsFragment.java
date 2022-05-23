package com.example.piazza.controladores.admin.fragments.treballdors;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
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

import com.example.piazza.classes.Horari;
import com.example.piazza.classes.Usuari;
import com.example.piazza.commons.Notificacio;
import com.example.piazza.commons.getCurrTimeGMT;
import com.example.piazza.controladores.auth.SplashScreen;
import com.example.piazza.fireBase.data.ReadData;
import com.example.piazza.fireBase.data.WriteData;
import com.example.piazza.fireBase.session.AuthUserSession;
import com.example.piazza.recyclerView.treballadors.ListAdapterTreballadors;
import com.example.piazza.recyclerView.treballadors.ListElementTreballadors;
import com.example.testauth.R;
import com.example.testauth.databinding.FragmentTreballadorsBinding;
import com.google.android.gms.tasks.Task;
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
import java.util.Random;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class TreballadorsFragment extends Fragment implements ReadData, WriteData, AuthUserSession {

    private static final String TAG = "TREBALLADORS_FRAGMENT: ";
    private FragmentTreballadorsBinding binding;
    private List<ListElementTreballadors> listElements = new ArrayList<>();
    private View root;
    private Boolean firstLoad;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentTreballadorsBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        firstLoad = true;

        try {
            if (userAuth.getUid() != null) {

                setup();

            }else {
                startActivity(new Intent(getActivity(), SplashScreen.class));

            }
        } catch (Exception e) {
            startActivity(new Intent(getActivity(), SplashScreen.class));

        }
        return root;
    }

    public void setup() {

        getMultipldeDocuments(DDBB.collection("usuaris").whereEqualTo("empresa", userAuth.getEmpresa()), this::mostrarCampModificat);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            firstLoad = false;
        }, 5000);

        binding.floatingActionButton.bringToFront();
        binding.floatingActionButton.setOnClickListener(l -> {
            getMultipldeDocuments(DDBB.collection("modificacions"), this::mostrarValidacions);
        });

        binding.pararJornada.setOnClickListener(l -> {
            new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Parar jornada general")
                    .setContentText("Estas segur que vols parar la jornda de tots els treballadors?")
                    .setConfirmText("Si")
                    .setCancelText("No")
                    .setConfirmClickListener(sweetAlertDialog -> {

                        getMultipldeDocuments(DDBB.collection("horari"), this::pararJornadaGeneral);

                        sweetAlertDialog.dismissWithAnimation();
                    })
                    .setCancelClickListener(sweetAlertDialog -> {

                        sweetAlertDialog.dismissWithAnimation();
                    }).show();
        });

    }

    private void pararJornadaGeneral(Task<QuerySnapshot> querySnapshotTask) {

        System.out.println("ENTRA");

        if (querySnapshotTask.isSuccessful()) {

            System.out.println("ENTRA: " + querySnapshotTask.getResult().size());

            List<Horari> horaris = new ArrayList<>();
            List<Horari> horarisModificats = new ArrayList<>();

            for (DocumentSnapshot documentSnapshot : querySnapshotTask.getResult()) {

                Horari Horari = documentSnapshot.toObject(Horari.class);

                if (Horari.getUsuari().getEmpresa().equals(userAuth.getEmpresa()) && !Horari.isEstatJornada()) {
                    System.out.println(documentSnapshot.getId());
                    if (!Horari.isEstatJornada()) {
                        Horari.setEstatJornada(true);
                        Horari.getUsuari().setTreballant(false);
                        writeOneDocument(DDBB.collection("usuaris").document(Horari.getUsuari().getUid()), Horari.getUsuari());
                        writeOneDocument(documentSnapshot.getReference(), Horari);
                    }
                }

            }

        }

    }

    private void mostrarValidacions(Task<QuerySnapshot> querySnapshotTask) {

        for (DocumentSnapshot documentSnapshot : querySnapshotTask.getResult().getDocuments()) {
            Horari Horari = documentSnapshot.toObject(Horari.class);
            if (Horari.getUsuari().getEmpresa().equals(userAuth.getEmpresa()) && Horari.getModificacio() != null) {
                String HORES_MINUTS_NEUTRE = "%01dh:%02dm";

                String titol;
                String contingut = "Data: " + Horari.getModificacio().getDiaEntrada() + "/" + Horari.getModificacio().getMesEntrada() + " a " + Horari.getModificacio().getDiaSalida() + "/" + Horari.getModificacio().getMesSalida()
                        + "\n\nHora entrada: " + String.format(HORES_MINUTS_NEUTRE, Horari.getModificacio().getHoraEntrada(), Horari.getModificacio().getMinutEntrada())
                        + "\n\nHora sortida: " + String.format(HORES_MINUTS_NEUTRE, Horari.getModificacio().getHoraSalida(), Horari.getModificacio().getMinutSalida())
                        + "\n\nTotal treballat: " + String.format(HORES_MINUTS_NEUTRE, Horari.getModificacio().getTotalMinutsTreballats() / 60, Horari.getModificacio().getTotalMinutsTreballats() % 60);

                if (documentSnapshot.getId().contains("afegit")) {
                    titol = "L'empleat: " + Horari.getUsuari().getNom() + " vol afegir el següent registre...";
                } else {
                    titol = "L'empleat: " + Horari.getUsuari().getNom() + " vol modificar el següent registre...";

                }

                new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText(titol)
                        .setContentText(contingut)
                        .setConfirmText("Validar")
                        .setCancelText("Denegar")
                        .setConfirmClickListener(sweetAlertDialog -> {

                            Horari temp = Horari.getModificacio();

                            writeOneDocument(DDBB.collection("horari").document(documentSnapshot.getId()), temp);

                            DDBB.collection("modificacions").document(documentSnapshot.getId()).delete();

                            sweetAlertDialog.dismissWithAnimation();
                        })
                        .setCancelClickListener(sweetAlertDialog -> {

                            DDBB.collection("modificacions").document(documentSnapshot.getId()).delete();

                            sweetAlertDialog.dismissWithAnimation();
                        }).show();
            }
        }

    }


    private void mostrarCampModificat(Task<QuerySnapshot> querySnapshotTask) {

        if (querySnapshotTask.isSuccessful() && !querySnapshotTask.getResult().isEmpty()) {


            for (DocumentSnapshot documentSnapshot : querySnapshotTask.getResult().getDocuments()) {

                Usuari temp = documentSnapshot.toObject(Usuari.class);

                if (temp.getRol().equals("treballador"))
                    getListenerDocument(getActivity(), documentSnapshot.getReference(), this::notificarCanvi);

            }

            getMultipldeDocuments(DDBB.collection("usuaris").whereEqualTo("empresa", userAuth.getEmpresa()), this::setElements);

        }

    }

    private void notificarCanvi(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

        if (documentSnapshot.exists()) {
            Usuari treballador = documentSnapshot.toObject(Usuari.class);
            Random rand = new Random();
            if (!firstLoad) {
                if (treballador.getTreballant() && getContext() != null)
                    Notificacio.Notificar(getContext(), "Jornada iniciada!", treballador.getNom() + " ha entrat a treballar", rand.nextInt(100 - 20) + 20);
                else {
                    Notificacio.Notificar(getContext(), "Jornada acabada!", treballador.getNom() + " ha sortit de treballar", rand.nextInt(100 - 20) + 20);
                }
            }
            getMultipldeDocuments(DDBB.collection("usuaris").whereEqualTo("empresa", userAuth.getEmpresa()), this::setElements);

        }

    }

    public void setElements(Task<QuerySnapshot> querySnapshotTask) {

        listElements.clear();

        if (querySnapshotTask.isSuccessful()) {

            for (QueryDocumentSnapshot documentSnapshot : querySnapshotTask.getResult()) {
                Usuari usuari = documentSnapshot.toObject(Usuari.class);

                if (!usuari.getRol().equals("admin") && !usuari.getRol().equals("superadmin"))

                    if (usuari.getTreballant())
                        addListElementTreballadors(usuari, true);
                    else
                        addListElementTreballadors(usuari, false);


            }


        } else {
            Log.d(TAG, "Error al recuperar varios documentos.");
        }

        ListAdapterTreballadors listAdapter = new ListAdapterTreballadors(listElements, root.getContext(), this::showName);
        binding.recyclerViewTreballadors.setLayoutManager(new LinearLayoutManager(root.getContext()));
        binding.recyclerViewTreballadors.setAdapter(listAdapter);

        binding.shimmerTreballador.setVisibility(View.GONE);
        binding.recyclerViewTreballadors.setVisibility(View.VISIBLE);

    }

    private void addListElementTreballadors(Usuari Usuari, Boolean treballant) {

        String nom = Usuari.getNom().substring(0, 1).toUpperCase() + Usuari.getNom().substring(1);
        String cognom = Usuari.getCognom();
        String uid = Usuari.getUid();
        listElements.add(new ListElementTreballadors(
                nom ,
                cognom,
                uid, treballant));

    }

    private void showName(ListElementTreballadors item, View itemview) {

        Toast.makeText(root.getContext(), "Empleat:  " + item.getNom(), Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        listElements.clear();
    }
}