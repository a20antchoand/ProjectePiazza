package com.example.piazza.ui.administrar;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.piazza.Classes.Usuario;
import com.example.piazza.Controladores.AuthActivity;
import com.example.piazza.Modelo.UsuarioModelo;
import com.example.testauth.R;
import com.example.testauth.databinding.FragmentAdministrarBinding;
import com.google.firebase.auth.FirebaseAuth;

public class AdministrarFragment extends Fragment {

    private FragmentAdministrarBinding binding;
    private UsuarioModelo jugadorModelo = new UsuarioModelo();
    View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentAdministrarBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        setup();

        return root;
    }

    private void setup() {

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), AuthActivity.class);
                startActivity(intent);
            }
        });

        binding.alta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = String.valueOf(((TextView) root.findViewById(R.id.editTextEmail)).getText());
                String nom = String.valueOf(((TextView) root.findViewById(R.id.editTextNom)).getText());
                String cognom = String.valueOf(((TextView) root.findViewById(R.id.editTextCognom)).getText());
                String telefon = String.valueOf(((TextView) root.findViewById(R.id.editTextTelefon)).getText());
                String salari = String.valueOf(((TextView) root.findViewById(R.id.editTextSalari)).getText());

                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, "123456")
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                Toast.makeText(getActivity().getApplication(), "Authentication Successful.",
                                        Toast.LENGTH_SHORT).show();

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(getActivity().getApplication(), "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                jugadorModelo.GuardarUsuarioBBDD(new Usuario(email, nom, cognom, telefon, salari));

            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}