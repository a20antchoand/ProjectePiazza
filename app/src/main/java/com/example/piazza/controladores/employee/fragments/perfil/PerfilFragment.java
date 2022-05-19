package com.example.piazza.controladores.employee.fragments.perfil;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.piazza.controladores.auth.SplashScreen;
import com.example.piazza.fireBase.data.WriteData;
import com.example.piazza.fireBase.session.AuthUserSession;
import com.example.testauth.databinding.FragmentPerfilBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class PerfilFragment extends Fragment implements AuthUserSession, WriteData {

    private FragmentPerfilBinding binding;
    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        try {
            if (userAuth.getUid() != null) {
                setup();

            }else {
                startActivity(new Intent(getActivity(), SplashScreen.class));

            }
        } catch (Exception e) {
            startActivity(new Intent(getActivity(), SplashScreen.class));

        }

        return root;

    }

    private void setup() {

        mostrarDatosPerfil();


        binding.btnGuardar.setOnClickListener(l -> {

            userAuth.setNom(binding.nom.getText().toString());
            userAuth.setCognom(binding.cognom.getText().toString());
            userAuth.setTelefono(binding.telefon.getText().toString());

            writeOneDocument(DDBB.collection("usuaris").document(userAuth.getUid()), userAuth);

            new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("S'ha actualitzat la informació correctament!")
                    .show();
        });
    }

    /**
     * Funcio per mostrar dades del perfil mostrant la informacio que tenim
     * emmagatzemada a cada camp de la interficie
     */
    public void mostrarDatosPerfil() {

        binding.email.setText(userAuth.getEmail());
        binding.nom.setText(userAuth.getNom());
        binding.cognom.setText(userAuth.getCognom());
        binding.telefon.setText(userAuth.getTelefono());
        binding.rol.setText(userAuth.getRol());
        binding.rol.setEnabled(false);
        binding.horesMensuals.setText(userAuth.getHoresMensuals());

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}