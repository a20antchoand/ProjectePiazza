package com.example.piazza.ui.perfil;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.piazza.Classes.Usuario;
import com.example.piazza.Controladores.AuthActivity;
import com.example.piazza.Modelo.UsuarioModelo;
import com.example.piazza.RecyclerView.ListElementEstatTreballadors;
import com.example.testauth.R;
import com.example.testauth.databinding.FragmentPerfilBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class PerfilFragment extends Fragment {

    private FragmentPerfilBinding binding;
    private View root;
    UsuarioModelo usuarioModelo = new UsuarioModelo();
    static Usuario usuarioApp;
    TextView nom;
    TextView email;
    TextView salari;
    TextView telefon;
    TextView cognom;

    public Usuario getUsuarioApp() {
        return usuarioApp;
    }

    public static void setUsuarioApp(Usuario usuari) {
        usuarioApp = usuari;
    }

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
        UsuarioModelo usuarioModelo = new UsuarioModelo();


        root.findViewById(R.id.logOutEmployee).setOnClickListener(view -> {

            logOut();
        });

        usuarioModelo.cargarDatosUsuario();

        mostrarDatosPerfil();
    }

    private void mostrarDatosPerfil() {

        email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

    }

    public void logOut() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getActivity(), AuthActivity.class);
        startActivity(intent);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}