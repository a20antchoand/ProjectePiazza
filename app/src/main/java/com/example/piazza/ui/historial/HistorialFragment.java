package com.example.piazza.ui.historial;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.piazza.RecyclerView.ListAdapter;
import com.example.piazza.RecyclerView.ListElement;
import com.example.testauth.R;
import com.example.testauth.databinding.FragmentHistorialBinding;

import java.util.ArrayList;
import java.util.List;

public class HistorialFragment extends Fragment {

    private FragmentHistorialBinding binding;
    private View root;
    private List<ListElement> elements;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHistorialBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        setup();

        return root;

    }

    public void setup() {
        elements = new ArrayList<>();

        elements.add(new ListElement("#123456","Toni","5:00","PENDENT"));
        elements.add(new ListElement("#346456","Alia","5:00","PENDENT"));
        elements.add(new ListElement("#978456","Paula","5:00","PENDENT"));
        elements.add(new ListElement("#123532","Arnau","5:00","PENDENT"));
        elements.add(new ListElement("#673452","Fati","5:00","PENDENT"));
        elements.add(new ListElement("#578534","Ricard","5:00","PENDENT"));
        elements.add(new ListElement("#892345","Jordi","5:00","PENDENT"));

        ListAdapter listAdapter = new ListAdapter(elements, root.getContext());
        RecyclerView recyclerView = root.findViewById(R.id.listRecyclerView);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        recyclerView.setAdapter(listAdapter);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}