package com.example.piazza.controladores.employee.fragments.perfil;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.piazza.classes.Usuario;
import com.example.piazza.controladores.employee.fragments.introduir_hores.IntroduirHoresFragment;
import com.example.piazza.fireBase.session.AuthUserSession;
import com.example.testauth.R;
import com.example.testauth.databinding.FragmentPerfilBinding;

public class PerfilFragment extends Fragment implements AuthUserSession{

    private FragmentPerfilBinding binding;
    private View root;
    Usuario currUser;
    EditText nom, email, salari, telefon, cognom;
    ImageView imatge;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        setup();

        return root;

    }

    private void setup() {

        nom = root.findViewById(R.id.nom);
        email = root.findViewById(R.id.email);
        salari = root.findViewById(R.id.salari);
        telefon = root.findViewById(R.id.telefon);
        cognom = root.findViewById(R.id.cognom);
        imatge = root.findViewById(R.id.imatgePerfil);


        mostrarDatosPerfil();


    }

    public void mostrarDatosPerfil() {

        currUser = IntroduirHoresFragment.usuarioApp;

        email.setText(currUser.getEmail());
        nom.setText(currUser.getNom());
        cognom.setText(currUser.getCognom());
        telefon.setText(currUser.getTelefono());
        salari.setText(currUser.getRol());

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}