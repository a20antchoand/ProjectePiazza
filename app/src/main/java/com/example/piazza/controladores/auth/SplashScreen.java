package com.example.piazza.controladores.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.piazza.classes.Usuario;
import com.example.piazza.controladores.admin.AdminActivity;
import com.example.piazza.controladores.employee.EmployeeActivity;
import com.example.piazza.controladores.employee.fragments.introduir_hores.IntroduirHoresFragment;
import com.example.piazza.fireBase.data.ReadData;
import com.example.piazza.fireBase.session.AuthUserSession;
import com.example.testauth.R;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import com.example.piazza.commons.getCurrTimeGMT;

import java.util.Objects;

public class SplashScreen extends Activity implements ReadData, AuthUserSession {

    private ProgressBar mProgress;
    Usuario user = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Show the splash screen
        // Start lengthy operation in a background thread
        setup();
    }

    private void setup() {

        /* ======================================
         * Comprovamos si tiene el usuario sesión activa
         * ======================================
         * */
        new getCurrTimeGMT().execute();

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

        if (DocumentSnapshotTask.getResult().getData() != null) {

            System.out.println(DocumentSnapshotTask.getResult().getData());

            user = DocumentSnapshotTask.getResult().toObject(Usuario.class);

            guardarDatosGlobalesJugador();

            Query query2 = DDBB.collection("horari");

            getMultipldeDocuments(query2, this::setNumeroDocument);

        } else {
            startActivity(new Intent(SplashScreen.this, AuthActivity.class));
        }

    }

    private void setNumeroDocument(Task<QuerySnapshot> querySnapshotTask) {

        Intent intent = null;

        for (DocumentSnapshot d : querySnapshotTask.getResult()) {

            if (d.getId().contains(userAuth.getUid()) && Integer.parseInt(d.get("diaEntrada").toString()) == IntroduirHoresFragment.zdt.getDayOfMonth()) {
                IntroduirHoresFragment.numeroDocument++;
                System.out.println(IntroduirHoresFragment.numeroDocument);
            }
        }

        if (userAuth.getRol().equals("admin")) {
            intent = new Intent(SplashScreen.this, AdminActivity.class);
        } else if (userAuth.getRol().equals("treballador")) {
            intent = new Intent(SplashScreen.this, EmployeeActivity.class);
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
        userAuth.setRol(user.getRol());
        userAuth.setTelefono(user.getTelefono());

    }

}