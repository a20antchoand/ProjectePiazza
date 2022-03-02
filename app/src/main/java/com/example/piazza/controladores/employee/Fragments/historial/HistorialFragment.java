package com.example.piazza.controladores.employee.fragments.historial;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.piazza.recyclerView.estatTreballadors.ListAdapterEstatTreballadors;
import com.example.piazza.recyclerView.estatTreballadors.ListElementEstatTreballadors;
import com.example.piazza.recyclerView.historialHores.ListAdapterHistorialHores;
import com.example.piazza.recyclerView.historialHores.ListElementHistorialHores;
import com.example.testauth.R;
import com.example.testauth.databinding.FragmentHistorialBinding;

import java.util.ArrayList;
import java.util.List;

public class HistorialFragment extends Fragment {

    private FragmentHistorialBinding binding;
    private List<ListElementHistorialHores> elements;
    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHistorialBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        setup();

        return root;

    }

    public void setup() {

        elements = new ArrayList<>();

        elements.add(new ListElementHistorialHores("01/03/2022","20:00","23:00", "3h"));
        elements.add(new ListElementHistorialHores("02/03/2022","20:00","23:00", "3h"));
        elements.add(new ListElementHistorialHores("03/03/2022","20:00","23:00", "3h"));
        elements.add(new ListElementHistorialHores("04/03/2022","20:00","23:00", "3h"));
        elements.add(new ListElementHistorialHores("05/03/2022","20:00","23:00", "3h"));


        ListAdapterHistorialHores listAdapter = new ListAdapterHistorialHores(elements, root.getContext(), new ListAdapterHistorialHores.onItemClickListener() {
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}