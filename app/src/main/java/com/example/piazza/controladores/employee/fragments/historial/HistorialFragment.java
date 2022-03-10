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
import com.example.testauth.databinding.FragmentHistorialBinding;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HistorialFragment extends Fragment {

    private static final String TAG = "HistorialFragment: ";
    private FragmentHistorialBinding binding;
    private static View root;
    ReadData readData = new ReadData();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHistorialBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        readData.getHistorialCurrUser("horari", this::setElements);

        return root;

    }


    /*
    *
    * new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                if (documentSnapshot.getId().contains(getUser().getEmail())) {
                                    Horario horario = documentSnapshot.toObject(Horario.class);

                                    addListElementHistorial(horario);

                                }
                            }

                            HistorialFragment.setElements(listElementHistorialHores);

                            System.out.println("Elements actualitzats");

                        } else {
                            Log.d(TAG, "Error al recuperar varios documentos.");
                        }
                    }
    *
    *
    * */



    public void setElements(Task<QuerySnapshot> querySnapshotTask) {

        List<ListElementHistorialHores> listElements = new ArrayList<>();


        if (querySnapshotTask.isSuccessful()) {

            for (QueryDocumentSnapshot documentSnapshot : querySnapshotTask.getResult()) {
                if (documentSnapshot.getId().contains(AuthUserSession.getUser().getEmail())) {
                    Horario horario = documentSnapshot.toObject(Horario.class);

                    listElements.add(addListElementHistorial(horario));

                }
            }

            System.out.println("Elements actualitzats");

        } else {
            Log.d(TAG, "Error al recuperar varios documentos.");
        }

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

    private ListElementHistorialHores addListElementHistorial(Horario horario) {

        String data = horario.getAnioEntrada() + "/" + horario.getMesEntrada() + "/" + horario.getDiaEntrada();
        String entrada = horario.getHoraEntrada() + ":" + horario.getMinutEntrada();
        String sortida = horaSortida + ":" + minutSortida;
        String totalFinal = total/60 + ":" + total%60;

        if (minutEntrada < 10)
            entrada = horaEntrada + ":0" + minutEntrada;
        if (horaEntrada < 10)
            entrada = "0" + horaEntrada + ":" + horaEntrada;
        if (horaEntrada < 10 && minutEntrada < 10)
            entrada = "0" + horaEntrada + ":0" + horaEntrada;

        if (minutSortida < 10)
            sortida = horaSortida + ":0" + minutSortida;
        if (horaSortida < 10)
            sortida = "0" + horaSortida + ":" + minutSortida;
        if (horaSortida < 10 && minutSortida < 10)
            sortida = "0" + horaSortida + ":0" + minutSortida;;

        if ((total % 60) < 10)
            totalFinal = total/60 + ":0" + total%60;

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