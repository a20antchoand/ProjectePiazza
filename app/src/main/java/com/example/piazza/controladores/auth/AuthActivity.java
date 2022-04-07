package com.example.piazza.controladores.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.piazza.classes.Usuario;
import com.example.piazza.commons.getCurrTimeGMT;
import com.example.piazza.controladores.admin.AdminActivity;
import com.example.piazza.controladores.employee.EmployeeActivity;
import com.example.piazza.controladores.employee.fragments.introduir_hores.IntroduirHoresFragment;
import com.example.piazza.fireBase.data.ReadData;
import com.example.piazza.fireBase.session.AuthUserSession;
import com.example.testauth.R;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class AuthActivity extends AppCompatActivity implements ReadData, AuthUserSession{

    Button logIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_auth);
        setup();

    }

    public void setup() {

        logIn = findViewById(R.id.logIn);

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = ((EditText) findViewById(R.id.editTextEmail)).getText().toString();
                String password = ((EditText) findViewById(R.id.editTextPassword)).getText().toString();

                if (!email.equals("") && !password.equals("")) {

                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                            /* ======================================
                             * Cargamos datos del usuario actual
                             * ======================================
                             * */

                            DocumentReference query = DDBB.collection("usuaris")
                                    .document(Objects.requireNonNull(Objects.requireNonNull(task.getResult().getUser()).getUid()));

                            System.out.println(task.getResult().getUser().getUid());

                            getOneDocument(query, this::validarLogin);

                        } else {
                            showAlert("El usuario o la contrasenya no són correctes.");
                        }
                    });
                }
            }

            private void validarLogin(Task<DocumentSnapshot> DocumentSnapshotTask) {

                if (DocumentSnapshotTask.getResult().getData() != null) {

                    DocumentReference docRef = DDBB.collection("usuaris").document(Objects.requireNonNull(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()));
                    cargarDatosUsuario(docRef, this::guardarDatosusuarioAuth);

                    Query query2 = DDBB.collection("horari");
                    getMultipldeDocuments(query2, this::setNumeroDocument);


                }

            }

            private void guardarDatosusuarioAuth (Task<DocumentSnapshot> documentSnapshotTask) {

                Usuario user = getUsuari(documentSnapshotTask);

                userAuth.setNom(user.getNom());
                userAuth.setUid(user.getUid());
                userAuth.setEmail(user.getEmail());
                userAuth.setCognom(user.getCognom());
                userAuth.setRol(user.getRol());
                userAuth.setTelefono(user.getTelefono());
            }

            private Usuario getUsuari(Task<DocumentSnapshot> documentSnapshotTask) {

                return documentSnapshotTask.getResult().toObject(Usuario.class);

            }

            private void setNumeroDocument(Task<QuerySnapshot> querySnapshotTask) {

                for (DocumentSnapshot d : querySnapshotTask.getResult()) {
                    if (d.getId().contains(userAuth.getUid()) && Integer.parseInt(Objects.requireNonNull(d.get("diaEntrada")).toString()) == getCurrTimeGMT.zdt.getDayOfMonth()) {
                        IntroduirHoresFragment.numeroDocument++;
                        System.out.println(IntroduirHoresFragment.numeroDocument);
                    }
                }

                System.out.println("EMAIL: " + userAuth.getEmail());

                if (userAuth.getRol().equals("admin")) {
                    showHome();
                } else if (userAuth.getRol().equals("treballador")) {
                    showEmployee();
                }

            }

        });
    }



    private void showAlert(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setMessage(msg);
        builder.setPositiveButton("Aceptar", null);
        AlertDialog alerta = builder.create();
        alerta.show();
    }

    private void showHome () {
        Intent intent = new Intent(this, AdminActivity.class);
        startActivity(intent);
        finish();
    }

    private void showEmployee () {
        Intent intent = new Intent(this, EmployeeActivity.class);
        startActivity(intent);
        finish();
    }

}