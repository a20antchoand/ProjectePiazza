package com.example.piazza.controladores.admin.fragments.administrar;

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
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AdministrarFragment extends Fragment implements AuthUserSession{

    private FragmentAdministrarBinding binding;
    View root;
    private FirebaseAuth mAuth2;
    private Drawable selected;
    private Drawable not_selected;
    private int diesSetmana = 0;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentAdministrarBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        new Handler(Looper.getMainLooper()).post(() -> setup());

        return root;
    }

    private void setup() {

        selected = getContext().getDrawable(R.drawable.color_selected);
        not_selected = getContext().getDrawable(R.drawable.color_not_selected);

        binding.button18.setOnClickListener(this::colorea);
        binding.button19.setOnClickListener(this::colorea);
        binding.button20.setOnClickListener(this::colorea);
        binding.button21.setOnClickListener(this::colorea);
        binding.button22.setOnClickListener(this::colorea);
        binding.button23.setOnClickListener(this::colorea);
        binding.button24.setOnClickListener(this::colorea);


        binding.alta.setOnClickListener(view -> {

            String email = binding.email.getText().toString();
            String nom = binding.nom.getText().toString().substring(0,1).toUpperCase() + binding.nom.getText().toString().substring(1);
            String cognom = binding.cognom.getText().toString().substring(0,1).toUpperCase() + binding.cognom.getText().toString().substring(1);
            String telefon = binding.telefon.getText().toString();
            String rol = binding.spn2.getSelectedItem().toString();
            String horesMensuals = binding.horesMensuals.getText().toString();
            String diesSetmanaStr = diesSetmana + "";
            String urlPerfil = "";

            if (email.equals("") || nom.equals("") || cognom.equals("") || telefon.equals("") || rol.equals("") || horesMensuals.equals("") || diesSetmana == 0) {
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

                                new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText("Usuari donat d'alta correctament")
                                        .show();

                                GuardarUsuarioBBDD(new Usuario(task.getResult().getUser().getUid(), email, nom, cognom, telefon, rol, horesMensuals, diesSetmanaStr, urlPerfil, userAuth.getEmpresa()));

                                mAuth2.signOut();

                                binding.email.setText("");
                                binding.nom.setText("");
                                binding.cognom.setText("");
                                binding.telefon.setText("");
                                binding.horesMensuals.setText("");

                                for (int i = 0; i < binding.constraintLayoutSetmana.getChildCount(); i++) {

                                    View button = binding.constraintLayoutSetmana.getChildAt(i);

                                    if (button instanceof Button) {

                                        Button btn = (Button) button;

                                        btn.setBackground(getContext().getDrawable(R.drawable.color_not_selected));

                                    }

                                }

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