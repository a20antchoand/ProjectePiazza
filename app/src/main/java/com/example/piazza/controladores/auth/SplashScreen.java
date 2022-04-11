package com.example.piazza.controladores.auth;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.example.piazza.classes.Usuario;
import com.example.piazza.commons.getCurrTimeGMT;
import com.example.piazza.controladores.admin.AdminActivity;
import com.example.piazza.controladores.employee.EmployeeActivity;
import com.example.piazza.controladores.employee.fragments.introduir_hores.IntroduirHoresFragment;
import com.example.piazza.fireBase.data.ReadData;
import com.example.piazza.fireBase.session.AuthUserSession;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class SplashScreen extends Activity implements ReadData, AuthUserSession {

    Usuario user = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setup();
    }

    private void setup() {

        try {
            String s = new getCurrTimeGMT().execute().get();

            getCurrTimeGMT.zdt = getCurrTimeGMT.getZoneDateTime(s);



        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

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

            DocumentReference docRef = DDBB.collection("usuaris").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
            cargarDatosUsuario(docRef, this::getUsuari);

            Query query2 = DDBB.collection("horari");
            getMultipldeDocuments(query2, this::setNumeroDocument);

        } else {
            startActivity(new Intent(SplashScreen.this, AuthActivity.class));
        }

    }

    private void getUsuari(Task<DocumentSnapshot> documentSnapshotTask) {

        guardarDatosGlobalesJugador(documentSnapshotTask.getResult().toObject(Usuario.class));

    }

    private void setNumeroDocument(Task<QuerySnapshot> querySnapshotTask) {

        Intent intent;

        for (DocumentSnapshot d : querySnapshotTask.getResult()) {

            if (d.getId().contains(userAuth.getUid()) && Integer.parseInt(Objects.requireNonNull(d.get("diaEntrada")).toString()) == getCurrTimeGMT.zdt.getDayOfMonth()) {
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

}