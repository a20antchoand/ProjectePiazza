package com.example.piazza.controladores.admin.fragments.validar;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.piazza.controladores.employee.EmployeeActivity;
import com.example.piazza.recyclerView.estatTreballadors.ListAdapterEstatTreballadors;
import com.example.piazza.recyclerView.estatTreballadors.ListElementEstatTreballadors;
import com.example.testauth.R;
import com.example.testauth.databinding.FragmentValidarBinding;

import java.util.ArrayList;
import java.util.List;

public class ValidarFragment extends Fragment {

    private FragmentValidarBinding binding;
    private View root;
    private List<ListElementEstatTreballadors> elements;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentValidarBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        setup();

        return root;


    }

    public void setup() {

        elements = new ArrayList<>();

        elements.add(new ListElementEstatTreballadors("#123456","Toni","5:00","PENDENT"));
        elements.add(new ListElementEstatTreballadors("#346456","Alia","5:00","VALIDAT"));
        elements.add(new ListElementEstatTreballadors("#978456","Paula","5:00","PENDENT"));
        elements.add(new ListElementEstatTreballadors("#123532","Arnau","5:00","VALIDAT"));
        elements.add(new ListElementEstatTreballadors("#673452","Fati","5:00","PENDENT"));


        ListAdapterEstatTreballadors listAdapter = new ListAdapterEstatTreballadors(elements, root.getContext(), new ListAdapterEstatTreballadors.onItemClickListener() {
            @Override
            public void onItemClickListener(ListElementEstatTreballadors item) {
                moveToDescription(item);
            }
        });
        RecyclerView recyclerView = root.findViewById(R.id.listRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        recyclerView.setAdapter(listAdapter);

    }

    void moveToDescription(ListElementEstatTreballadors item) {
        System.out.println("Estat: "+ item.getEstat());

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}