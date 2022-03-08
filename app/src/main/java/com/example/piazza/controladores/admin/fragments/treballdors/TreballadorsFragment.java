package com.example.piazza.controladores.admin.fragments.treballdors;

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

import com.example.piazza.recyclerView.estatTreballadors.ListAdapterEstatTreballadors;
import com.example.piazza.recyclerView.estatTreballadors.ListElementEstatTreballadors;
import com.example.piazza.recyclerView.treballadors.ListAdapterTreballadors;
import com.example.piazza.recyclerView.treballadors.ListElementTreballadors;
import com.example.testauth.R;
import com.example.testauth.databinding.FragmentTreballadorsBinding;

import java.util.ArrayList;
import java.util.List;

public class TreballadorsFragment extends Fragment {

    private FragmentTreballadorsBinding binding;
    private List<ListElementTreballadors> elements;
    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentTreballadorsBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        setup();
        return root;
    }

    public void setup() {

        elements = new ArrayList<>();

        elements.add(new ListElementTreballadors("#123456","Toni","60h","350"));
        elements.add(new ListElementTreballadors("#346456","Alia","80h","650"));
        elements.add(new ListElementTreballadors("#978456","Paula","60h","650"));
        elements.add(new ListElementTreballadors("#123532","Arnau","80h","350"));
        elements.add(new ListElementTreballadors("#673452","Fati","40h","B"));

        ListAdapterTreballadors listAdapter = new ListAdapterTreballadors(elements, root.getContext(), new ListAdapterTreballadors.onItemClickListener() {
            @Override
            public void onItemClickListener(ListElementTreballadors item) {

                showName(item);
            }
        });
        RecyclerView recyclerView = root.findViewById(R.id.recyclerViewTreballadors);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        recyclerView.setAdapter(listAdapter);

    }

    private void showName(ListElementTreballadors item) {

        Toast.makeText(root.getContext(), "Empleat:  " + item.getNom(), Toast.LENGTH_LONG).show();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}