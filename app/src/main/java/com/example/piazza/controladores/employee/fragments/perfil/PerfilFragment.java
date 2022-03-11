package com.example.piazza.controladores.employee.fragments.perfil;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.piazza.classes.Usuario;
import com.example.piazza.controladores.auth.AuthActivity;
import com.example.piazza.controladores.employee.fragments.introduir_hores.IntroduirHoresFragment;
import com.example.piazza.fireBase.session.AuthUserSession;
import com.example.testauth.R;
import com.example.testauth.databinding.FragmentPerfilBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class PerfilFragment extends Fragment implements AuthUserSession{

    private FragmentPerfilBinding binding;
    private View root;
    ImageView imatge;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        setup();

        return root;

    }

    private void setup() {

        mostrarDatosPerfil();


    }

    public void mostrarDatosPerfil() {

        binding.email.setText(userAuth.getEmail());
        binding.nom.setText(userAuth.getNom());
        binding.cognom.setText(userAuth.getCognom());
        binding.telefon.setText(userAuth.getTelefono());
        binding.salari.setText(userAuth.getSalario());

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}