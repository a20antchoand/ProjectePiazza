package com.example.piazza.controladores.employee.fragments.historial;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.piazza.classes.Horario;
import com.example.piazza.fireBase.data.ReadData;
import com.example.piazza.fireBase.session.AuthUserSession;
import com.example.piazza.recyclerView.historialHores.ListAdapterHistorialHores;
import com.example.piazza.recyclerView.historialHores.ListElementHistorialHores;
import com.example.testauth.R;
import com.example.piazza.commons.*;
import com.example.testauth.databinding.FragmentHistorialBinding;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HistorialFragment extends Fragment implements ReadData, AuthUserSession{

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

        getMultipldeDocuments(query, this::setElements);

        getMultipldeDocuments(query, this::calcularHoresTreballades);

    }

    private void calcularHoresTreballades(Task<QuerySnapshot> querySnapshotTask) {

        int totalTempsMes = 0;

        if (querySnapshotTask.isSuccessful()) {
            for (QueryDocumentSnapshot documentSnapshot : querySnapshotTask.getResult()) {
                if (documentSnapshot.getId().contains(userAuth.getUid())) {
                    Horario horario = documentSnapshot.toObject(Horario.class);
                    if (horario.getMesEntrada() == getCurrTimeGMT.zdt.getMonthValue())
                        totalTempsMes += horario.getTotalMinutsTreballats();

                }
            }
        }

        binding.tvTotalHores.setText(totalTempsMes/60 + "h " + totalTempsMes%60 + "m");

    }


    public void setElements(Task<QuerySnapshot> querySnapshotTask) {

        if (querySnapshotTask.isSuccessful()) {

            for (QueryDocumentSnapshot documentSnapshot : querySnapshotTask.getResult()) {
                if (documentSnapshot.getId().contains(userAuth.getUid())) {
                    Horario horario = documentSnapshot.toObject(Horario.class);
                    if (horario.getHoraSalida() != -1)
                        listElements.add(bindDataElementHistorial(horario));

                }
            }

            System.out.println("Elements actualitzats");

        } else {
            Log.d(TAG, "Error al recuperar varios documentos.");
        }

        if (listElements.size() == 0){

            showHistorialEmpty();

        } else {

            showHistorial();

        }
    }

    private void showHistorial() {

        binding.titolHistorial.setVisibility(View.GONE);
        binding.imatgeHistorial.setVisibility(View.GONE);
        binding.recyclerViewHistorial.setVisibility(View.VISIBLE);
        binding.cardView.setVisibility(View.VISIBLE);
        binding.cardView2.setVisibility(View.VISIBLE);
        binding.horesTreballadesTotal.setVisibility(View.VISIBLE);

        ListAdapterHistorialHores listAdapter = new ListAdapterHistorialHores(listElements, root.getContext(), item -> {
            //moveToDescription(item);
        });

        RecyclerView recyclerView = root.findViewById(R.id.recyclerViewHistorial);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        recyclerView.setAdapter(listAdapter);

    }

    private void showHistorialEmpty() {

        binding.titolHistorial.setVisibility(View.VISIBLE);
        binding.imatgeHistorial.setVisibility(View.VISIBLE);
        binding.recyclerViewHistorial.setVisibility(View.GONE);
        binding.cardView.setVisibility(View.GONE);
        binding.cardView2.setVisibility(View.GONE);
        binding.horesTreballadesTotal.setVisibility(View.GONE);

    }

    private ListElementHistorialHores bindDataElementHistorial(Horario horario) {

        String data2 = String.format("%02d",horario.getHoraEntrada());

        System.out.println("DATA2: " + data2);

        String data = String.format("%04d/%02d/%02d",horario.getAnioEntrada(), horario.getMesEntrada(), horario.getDiaEntrada());
        String entrada = String.format("%02d:%02d",horario.getHoraEntrada(),horario.getMinutEntrada()) ;
        String sortida = String.format("%02d:%02d",horario.getHoraSalida(), horario.getMinutSalida());
        String totalFinal = String.format("%02dh %02dm",horario.getTotalMinutsTreballats()/60, horario.getTotalMinutsTreballats()%60);

        return new ListElementHistorialHores(
                data,
                entrada + "  ",
                sortida,
                totalFinal);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}