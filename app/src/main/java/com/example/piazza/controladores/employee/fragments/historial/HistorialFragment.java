package com.example.piazza.controladores.employee.fragments.historial;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.piazza.classes.Horario;
import com.example.piazza.controladores.employee.fragments.introduir_hores.IntroduirHoresFragment;
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
import java.util.Objects;

public class HistorialFragment extends Fragment implements ReadData, AuthUserSession{

    private int mInterval = 5000; // 5 seconds by default, can be changed later
    public static Handler HandlerHistorial = new Handler();

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

        binding.tvHoresMensuals.setText(userAuth.getHoresMensuals() + "h");

        getMultipldeDocuments(query, this::calcularHoresTreballades);

        startRepeatingTask();

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

        calcularResiduHores(totalTempsMes);

    }

    private void calcularResiduHores(int totalTempsMes) {

        int totalMinutsTreballar = Integer.parseInt(userAuth.getHoresMensuals()) * 60;
        int residu;

        if (totalMinutsTreballar > totalTempsMes) {
            residu = totalMinutsTreballar - totalTempsMes;
            binding.tvResiduHores.setText(String.format("-%01d:%02d",residu/60,residu%60));
            binding.tvResiduHores.setTextColor(root.getContext().getResources().getColor(R.color.end_btn));
        } else if (totalMinutsTreballar < totalTempsMes) {
            residu = totalTempsMes - totalMinutsTreballar;
            binding.tvResiduHores.setText(String.format("+%01d:%02d",residu/60,residu%60));
            binding.tvResiduHores.setTextColor(root.getContext().getResources().getColor(R.color.start_btn));
        } else
            binding.tvResiduHores.setText("00:00");

    }


    public void setElements(Task<QuerySnapshot> querySnapshotTask) {

        if (querySnapshotTask.isSuccessful()) {

            for (QueryDocumentSnapshot documentSnapshot : querySnapshotTask.getResult()) {
                if (documentSnapshot.getId().contains(userAuth.getUid())) {
                    Horario horario = documentSnapshot.toObject(Horario.class);
                    if (horario.isEstatJornada())
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

        String data = String.format("%4d/%02d/%02d",horario.getAnioEntrada(), horario.getMesEntrada(), horario.getDiaEntrada());
        String entrada = String.format("%d:%02d",horario.getHoraEntrada(),horario.getMinutEntrada()) ;
        String sortida = String.format("%d:%02d",horario.getHoraSalida(), horario.getMinutSalida());
        String totalFinal = String.format("%dh %02dm",horario.getTotalMinutsTreballats()/60, horario.getTotalMinutsTreballats()%60);

        return new ListElementHistorialHores(
                data,
                entrada + "  ",
                sortida,
                totalFinal);

    }

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