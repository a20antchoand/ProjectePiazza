package com.example.piazza.controladores.admin.fragments.validar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.piazza.classes.Horari;
import com.example.piazza.classes.Usuari;
import com.example.piazza.controladores.auth.SplashScreen;
import com.example.piazza.fireBase.data.ReadData;
import com.example.piazza.fireBase.data.WriteData;
import com.example.piazza.fireBase.session.AuthUserSession;
import com.example.piazza.recyclerView.estatTreballadors.ListAdapterEstatTreballadors;
import com.example.piazza.recyclerView.estatTreballadors.ListElementEstatTreballadors;
import com.example.testauth.R;
import com.example.testauth.databinding.FragmentValidarBinding;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ValidarFragment extends Fragment implements AuthUserSession, ReadData, WriteData {

    private static final String TAG = "VALIDAR: " ;
    private FragmentValidarBinding binding;
    private View root;
    private List<ListElementEstatTreballadors> elements = new ArrayList<>();
    public static List<Usuari> treballadors = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentValidarBinding.inflate(inflater, container, false);
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

        return root;


    }

    public void setup() {

        getMultipldeDocuments(DDBB.collection("usuaris"), this::recopilarTreballadors);

        getMultipldeDocuments(DDBB.collection("horari").whereEqualTo("estatJornada", false), this::mostrarEstat);

        getListenerCollections(DDBB.collection("horari"), this::escoltarBBDD);

    }

    private void escoltarBBDD(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {

        if (e != null) {
            Log.w(TAG, "ESCOLTA FAILED.", e);
            return;
        }

        getMultipldeDocuments(DDBB.collection("horari").whereEqualTo("estatJornada", false), this::mostrarEstat);

    }

    private void recopilarTreballadors(Task<QuerySnapshot> querySnapshotTask) {

        treballadors = new ArrayList<>();

        if (querySnapshotTask.isSuccessful()) {

            for (DocumentSnapshot document : querySnapshotTask.getResult().getDocuments()) {

                Usuari temp = document.toObject(Usuari.class);

                treballadors.add(temp);

            }

        }

    }

    private void mostrarEstat(Task<QuerySnapshot> querySnapshotTask) {
        elements = new ArrayList<>();
        if (querySnapshotTask.isSuccessful()) {
            for (Usuari usuari : treballadors) {
                int cont = 0;
                if (usuari.getRol().equals("treballador")) {
                    Horari HorariEmpezado = new Horari();
                    for (DocumentSnapshot documentSnapshot : querySnapshotTask.getResult().getDocuments()) {
                        Horari Horari = documentSnapshot.toObject(Horari.class);
                        if (Horari.getUsuari().getUid().equals(usuari.getUid())) {
                            cont++;
                            HorariEmpezado = Horari;
                        }
                    }
                    if (cont == 0)
                        elements.add(addListElementEstatTreballadorsActius(usuari));
                    else
                        elements.add(addListElementEstatTreballadorsNoActius(usuari, HorariEmpezado));
                }

            }
            ListAdapterEstatTreballadors listAdapter = new ListAdapterEstatTreballadors(elements, root.getContext(), item -> moveToDescription(item));
            RecyclerView recyclerView = root.findViewById(R.id.listRecyclerView);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
            recyclerView.setAdapter(listAdapter);
        }
    }

    private ListElementEstatTreballadors addListElementEstatTreballadorsActius(Usuari Usuari) {

        String color = "#FF0000";
        String nom = Usuari.getNom().substring(0, 1).toUpperCase() + Usuari.getNom().substring(1);
        String hores = Usuari.getTelefono();
        String estat = Usuari.getRol();
        String uid = Usuari.getUid();

        return new ListElementEstatTreballadors(
                color,
                nom ,
                hores,
                estat,
                uid);

    }

    private ListElementEstatTreballadors addListElementEstatTreballadorsNoActius(Usuari Usuari, Horari Horari) {

        String color = "#00BB2d";
        String nom = Usuari.getNom().substring(0, 1).toUpperCase() + Usuari.getNom().substring(1);
        String hores = String.format("%01dh %02dm", Horari.getHoraEntrada(), Horari.getMinutEntrada());
        String estat = Usuari.getRol();
        String uid = Usuari.getUid();

        return new ListElementEstatTreballadors(
                color,
                nom ,
                hores,
                estat,
                uid);

    }

    void moveToDescription(com.example.piazza.recyclerView.estatTreballadors.ListElementEstatTreballadors item) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}