package com.example.piazza.controladores.admin.fragments.administrar;

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

import com.example.piazza.classes.Usuario;
import com.example.piazza.controladores.auth.AuthActivity;
import com.example.piazza.fireBase.session.AuthUserSession;
import com.example.testauth.R;
import com.example.testauth.databinding.FragmentAdministrarBinding;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

public class AdministrarFragment extends Fragment implements AuthUserSession{

    private FragmentAdministrarBinding binding;
    View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentAdministrarBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        setup();

        return root;
    }

    private void setup() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        binding.alta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = ((TextView) root.findViewById(R.id.editTextEmail)).getText().toString();
                String nom = ((TextView) root.findViewById(R.id.editTextNom)).getText().toString();
                String cognom = ((TextView) root.findViewById(R.id.editTextCognom)).getText().toString();
                String telefon = ((TextView) root.findViewById(R.id.editTextTelefon)).getText().toString();
                String salari = ((TextView) root.findViewById(R.id.editTextSalari)).getText().toString();



                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, "123456")
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                Toast.makeText(getActivity().getApplication(), "Usuario dado de alta correctamente.",
                                        Toast.LENGTH_SHORT).show();

                                GuardarUsuarioBBDD(new Usuario(task.getResult().getUser().getUid(), email, nom, cognom, telefon, salari));

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(getActivity().getApplication(), "No se a podido dar de alta al usuario.",
                                        Toast.LENGTH_SHORT).show();

                            }
                        });

                FirebaseAuth.getInstance().signOut();

            }
        });


    }

}