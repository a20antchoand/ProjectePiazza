package com.example.piazza.controladores.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;

import com.example.piazza.classes.Usuario;
import com.example.piazza.controladores.admin.AdminActivity;
import com.example.piazza.controladores.employee.EmployeeActivity;
import com.example.piazza.fireBase.data.ReadData;
import com.example.piazza.fireBase.session.AuthUserSession;
import com.example.testauth.R;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Objects;

public class SplashScreen extends Activity implements ReadData, AuthUserSession {

    private ProgressBar mProgress;
    Usuario user = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Show the splash screen
        setContentView(R.layout.splashscreen);

        // Start lengthy operation in a background thread
        setup();
    }

    private void setup() {

        /* ======================================
         * Comprovamos si tiene el usuario sesión activa
         * ======================================
         * */

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {

            DocumentReference query = DDBB.collection("usuaris")
                    .document(Objects.requireNonNull(user.getUid()));

            getOneDocument(query, this::validarLogin);

        } else {
            startActivity(new Intent(SplashScreen.this, AuthActivity.class));
        }



    }

    private void validarLogin(Task<DocumentSnapshot> DocumentSnapshotTask) {

        Intent intent = null;

        if (DocumentSnapshotTask.getResult().getData() != null) {

            System.out.println(DocumentSnapshotTask.getResult().getData());

            user = DocumentSnapshotTask.getResult().toObject(Usuario.class);

            guardarDatosGlobalesJugador();

            if (userAuth.getEmail().contains("admin")) {
                intent = new Intent(SplashScreen.this, AdminActivity.class);
            } else {
                intent = new Intent(SplashScreen.this, EmployeeActivity.class);
            }
        } else {
            intent = new Intent(SplashScreen.this, AuthActivity.class);
        }
        startActivity(intent);
        finish();

    }

    private void guardarDatosGlobalesJugador() {

        userAuth.setNom(user.getNom());
        userAuth.setUid(user.getUid());
        userAuth.setEmail(user.getEmail());
        userAuth.setCognom(user.getCognom());
        userAuth.setSalario(user.getSalario());
        userAuth.setTelefono(user.getTelefono());

    }

}