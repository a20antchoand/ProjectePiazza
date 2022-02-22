package com.example.piazza.ui.historial;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.piazza.RecyclerView.ListAdapterEstatTreballadors;
import com.example.piazza.RecyclerView.ListElementEstatTreballadors;
import com.example.testauth.R;
import com.example.testauth.databinding.FragmentHistorialBinding;

import java.util.ArrayList;
import java.util.List;

public class HistorialFragment extends Fragment {

    private FragmentHistorialBinding binding;
    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHistorialBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        return root;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}