package com.example.piazza.controladores.employee.fragments.historial;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.piazza.fireBase.data.ReadData;
import com.example.piazza.recyclerView.historialHores.ListAdapterHistorialHores;
import com.example.piazza.recyclerView.historialHores.ListElementHistorialHores;
import com.example.testauth.R;
import com.example.testauth.databinding.FragmentHistorialBinding;

import java.util.List;

public class HistorialFragment extends Fragment {

    private FragmentHistorialBinding binding;
    private static List<ListElementHistorialHores> listElements;
    private static View root;
    ReadData readData = new ReadData();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHistorialBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        readData.getHistorialCurrUser("horari");

        return root;

    }


    public static void setElements(List<ListElementHistorialHores> elements) {
        listElements = elements;

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}