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

            String email = ((TextView) root.findViewById(R.id.editTextEmail)).getText().toString();
            String nom = ((TextView) root.findViewById(R.id.editTextNom)).getText().toString();
            String cognom = ((TextView) root.findViewById(R.id.editTextCognom)).getText().toString();
            String telefon = ((TextView) root.findViewById(R.id.editTextTelefon)).getText().toString();
            String rol = ((Spinner) root.findViewById(R.id.spn)).getSelectedItem().toString();
            String horesMensuals = ((TextView) root.findViewById(R.id.editTextHoresMensuals)).getText().toString();
            String urlPerfil = "";


            FirebaseOptions firebaseOptions = new FirebaseOptions.Builder()
                    .setDatabaseUrl("(default)")
                    .setApiKey("AIzaSyAh3NcfuNmG-GESIDu98k6_QBIDraUkNn0\n")
                    .setApplicationId("testauth-f5eb4\n").build();

            try {
                FirebaseApp myApp = FirebaseApp.initializeApp(getContext(), firebaseOptions, "AnyAppName");
                mAuth2 = FirebaseAuth.getInstance(myApp);
            } catch (IllegalStateException e){
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
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getActivity().getApplication(), "No se a podido dar de alta al usuario.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    });

        });


    }

}