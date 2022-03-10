package com.example.piazza.controladores.employee.fragments.historial;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.example.testauth.databinding.FragmentHistorialBinding;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HistorialFragment extends Fragment {

    private static final String TAG = "HistorialFragment: ";
    private FragmentHistorialBinding binding;
    private static View root;
    private List<ListElementHistorialHores> listElements = new ArrayList<>();
    private ReadData readData = new ReadData();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHistorialBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        setup();
        return root;

    }

    private void setup() {

        Query query = AuthUserSession.getDDBB().collection("horari")
                .orderBy("diaEntrada", Query.Direction.DESCENDING);

        readData.getHistorialCurrUser( query, this::setElements);

    }


    public void setElements(Task<QuerySnapshot> querySnapshotTask) {

        if (querySnapshotTask.isSuccessful()) {

            for (QueryDocumentSnapshot documentSnapshot : querySnapshotTask.getResult()) {
                if (documentSnapshot.getId().contains(AuthUserSession.getUser().getEmail())) {
                    Horario horario = documentSnapshot.toObject(Horario.class);
                    if (horario.getHoraSalida() != -1)
                        listElements.add(addListElementHistorial(horario));

                }
            }

            System.out.println("Elements actualitzats");

        } else {
            Log.d(TAG, "Error al recuperar varios documentos.");
        }

        if (listElements.size() == 0){

            binding.titolHistorial.setVisibility(View.VISIBLE);
            binding.imatgeHistorial.setVisibility(View.VISIBLE);
            binding.recyclerViewHistorial.setVisibility(View.GONE);

        } else {

            binding.titolHistorial.setVisibility(View.GONE);
            binding.imatgeHistorial.setVisibility(View.GONE);
            binding.recyclerViewHistorial.setVisibility(View.VISIBLE);

            ListAdapterHistorialHores listAdapter = new ListAdapterHistorialHores(listElements, root.getContext(), new ListAdapterHistorialHores.onItemClickListener() {
                @Override
                public void onItemClickListener(ListElementHistorialHores item) {
                    //moveToDescription(item);
                }
            });
            RecyclerView recyclerView = root.findViewById(R.id.recyclerViewHistorial);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
            recyclerView.setAdapter(listAdapter);
        }
    }

    private ListElementHistorialHores addListElementHistorial(Horario horario) {

        String data = horario.getAnioEntrada() + "/" + horario.getMesEntrada() + "/" + horario.getDiaEntrada();
        String entrada = horario.getHoraEntrada() + ":" + horario.getMinutEntrada() ;
        String sortida = horario.getHoraSalida() + ":" + horario.getMinutSalida();
        String totalFinal = horario.getTotalMinutsTreballats()/60 + ":" + horario.getTotalMinutsTreballats()%60;

        if (horario.getMinutEntrada() < 10)
            entrada = horario.getHoraEntrada() + ":0" + horario.getMinutEntrada();
        if (horario.getHoraEntrada() < 10)
            entrada = "0" + horario.getHoraEntrada() + ":" + horario.getHoraEntrada();
        if (horario.getHoraEntrada() < 10 && horario.getMinutEntrada() < 10)
            entrada = "0" + horario.getHoraEntrada() + ":0" + horario.getHoraEntrada();

        if (horario.getMinutSalida() < 10)
            sortida = horario.getHoraSalida() + ":0" + horario.getMinutSalida();
        if (horario.getHoraSalida() < 10)
            sortida = "0" + horario.getHoraSalida() + ":" + horario.getMinutSalida();
        if (horario.getHoraSalida() < 10 && horario.getMinutSalida() < 10)
            sortida = "0" + horario.getHoraSalida() + ":0" + horario.getMinutSalida();;

        if ((horario.getTotalMinutsTreballats() % 60) < 10)
            totalFinal = horario.getTotalMinutsTreballats()/60 + ":0" + horario.getTotalMinutsTreballats()%60;

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