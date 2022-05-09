package com.example.piazza.controladores.admin.fragments.alta;

import static android.content.ContentValues.TAG;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.piazza.classes.Usuario;
import com.example.piazza.fireBase.session.AuthUserSession;
import com.example.testauth.R;
import com.example.testauth.databinding.FragmentAdministrarBinding;
import com.example.testauth.databinding.FragmentAltaAdminReportBinding;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AltaAdminReport extends Fragment implements AuthUserSession{

    private FragmentAltaAdminReportBinding binding;
    View root;
    private FirebaseAuth mAuth2;
    private Drawable selected;
    private Drawable not_selected;
    private int diesSetmana = 0;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentAltaAdminReportBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        new Handler(Looper.getMainLooper()).post(() -> setup());

        return root;
    }

    private void setup() {


        binding.alta.setOnClickListener(view -> {

            String email = binding.email.getText().toString();
            String nom = binding.nom.getText().toString().substring(0,1).toUpperCase() + binding.nom.getText().toString().substring(1);
            String cognom = binding.cognom.getText().toString().substring(0,1).toUpperCase() + binding.cognom.getText().toString().substring(1);
            String telefon = binding.telefon.getText().toString();
            String empresa = binding.empresa.getText().toString();

            if (email.equals("") || nom.equals("") || cognom.equals("") || telefon.equals("") || empresa.equals("")) {
                new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Faltan camps per emplenar...")
                        .show();
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

                                new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText("Usuari donat d'alta correctament")
                                        .show();

                                GuardarUsuarioBBDD(new Usuario(task.getResult().getUser().getUid(), email, nom, cognom, telefon, "admin", userAuth.getEmpresa()));

                                mAuth2.signOut();

                                binding.email.setText("");
                                binding.nom.setText("");
                                binding.cognom.setText("");
                                binding.telefon.setText("");
                                binding.empresa.setText("");

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Error al donar d'alta a l'usuari...")
                                        .show();

                            }
                        });
            }
        });


    }

    private void colorea(View view) {

        Button button = (Button) view;


        if (button.getBackground() == selected) {
            button.setBackground(not_selected);
            diesSetmana--;
        } else {
            button.setBackground(selected);
            diesSetmana++;
        }

        System.out.println("Button: " + diesSetmana);

    }



}