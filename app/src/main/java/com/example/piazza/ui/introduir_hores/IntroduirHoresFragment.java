package com.example.piazza.ui.introduir_hores;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.testauth.R;
import com.example.testauth.databinding.FragmentIntroduirHoresBinding;

public class IntroduirHoresFragment extends Fragment {

    private com.example.piazza.ui.introduir_hores.IntroduirHoresViewModel introduir_horesViewModel;
    private FragmentIntroduirHoresBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        introduir_horesViewModel =
                new ViewModelProvider(this).get(com.example.piazza.ui.introduir_hores.IntroduirHoresViewModel.class);

        binding = FragmentIntroduirHoresBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}