package com.example.piazza.controladores.employee.fragments.perfil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class PerfilFragment extends Fragment implements AuthUserSession{

    private FragmentPerfilBinding binding;
    private View root;

    Bitmap bitmap;

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
        binding.rol.setText(userAuth.getRol());
        binding.rol.setEnabled(false);
        binding.horesMensuals.setText(userAuth.getHoresMensuals());

        binding.imatgePerfil.setImageBitmap(perfil.getBitmap());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}