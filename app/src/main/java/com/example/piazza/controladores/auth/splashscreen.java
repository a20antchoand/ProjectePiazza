package com.example.piazza.controladores.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;

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

public class splashscreen extends Activity implements ReadData, AuthUserSession {

    private ProgressBar mProgress;
    Usuario usuario = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Show the splash screen
        setContentView(R.layout.splashscreen);
        mProgress = (ProgressBar) findViewById(R.id.progressBar);

        // Start lengthy operation in a background thread
        new Thread(new Runnable() {
            public void run() {
                doWork();
                startApp();
            }
        }).start();
    }

    private void doWork() {
        for (int progress=0; progress<=100; progress+=1) {
            try {
                Thread.sleep(20);
                mProgress.setProgress(progress);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void startApp() {


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
            startActivity(new Intent(splashscreen.this, AuthActivity.class));
        }



    }

    private void validarLogin(Task<DocumentSnapshot> DocumentSnapshotTask) {

        Intent intent = null;

        if (DocumentSnapshotTask.getResult().getData() != null) {

            System.out.println(DocumentSnapshotTask.getResult().getData());

            usuario = DocumentSnapshotTask.getResult().toObject(Usuario.class);

            if (usuario.getEmail().contains("admin")) {
                intent = new Intent(splashscreen.this, AdminActivity.class);
            } else {
                intent = new Intent(splashscreen.this, EmployeeActivity.class);
            }
        } else {
            intent = new Intent(splashscreen.this, AuthActivity.class);
        }
        startActivity(intent);
        finish();

    }

}