package com.example.piazza.controladores.admin.fragments.administrar;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.piazza.classes.Usuario;
import com.example.piazza.fireBase.session.AuthUserSession;
import com.example.testauth.R;
import com.example.testauth.databinding.FragmentAdministrarBinding;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AdministrarFragment extends Fragment implements AuthUserSession{

    private FragmentAdministrarBinding binding;
    View root;
    private FirebaseAuth mAuth2;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentAdministrarBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        setup();

        return root;
    }

    private void setup() {



        binding.alta.setOnClickListener(view -> {

            String email = binding.email.getText().toString();
            String nom = binding.nom.getText().toString().substring(0,1).toUpperCase() + binding.nom.getText().toString().substring(1);
            String cognom = binding.cognom.getText().toString().substring(0,1).toUpperCase() + binding.cognom.getText().toString().substring(1);
            String telefon = binding.telefon.getText().toString();
            String rol = binding.spn2.getSelectedItem().toString();
            String horesMensuals = binding.horesMensuals.getText().toString();
            String urlPerfil = "";

            if (email.equals("") || nom.equals("") || nom.equals("") || nom.equals("") || nom.equals("") || nom.equals("") || nom.equals("")) {
                Toast.makeText(getActivity().getApplication(), "Recuerda rellenar todos los campos.",
                        Toast.LENGTH_SHORT).show();
            } else {


                FirebaseOptions firebaseOptions = new FirebaseOptions.Builder()
                        .setDatabaseUrl("(default)")
                        .setApiKey("AIzaSyAh3NcfuNmG-GESIDu98k6_QBIDraUkNn0\n")
                        .setApplicationId("testauth-f5eb4\n").build();

                try {
                    FirebaseApp myApp = FirebaseApp.initializeApp(getContext(), firebaseOptions, "AnyAppName");
                    mAuth2 = FirebaseAuth.getInstance(myApp);
                } catch (IllegalStateException e) {
                    mAuth2 = FirebaseAuth.getInstance(FirebaseApp.getInstance("AnyAppName"));
                }

                mAuth2.createUserWithEmailAndPassword(email, "123456")
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                Toast.makeText(getActivity().getApplication(), "Usuario dado de alta correctamente.",
                                        Toast.LENGTH_SHORT).show();

                                GuardarUsuarioBBDD(new Usuario(task.getResult().getUser().getUid(), email, nom, cognom, telefon, rol, horesMensuals, urlPerfil));

                                mAuth2.signOut();

                                binding.email.setText("");
                                binding.nom.setText("");
                                binding.cognom.setText("");
                                binding.telefon.setText("");
                                binding.horesMensuals.setText("");

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(getActivity().getApplication(), "No se a podido dar de alta al usuario.",
                                        Toast.LENGTH_SHORT).show();

                            }
                        });
            }
        });


    }

}