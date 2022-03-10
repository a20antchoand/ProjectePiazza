package com.example.piazza.controladores.auth;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class AuthActivity extends AppCompatActivity implements ReadData, AuthUserSession{

    Button logIn;
    Usuario usuarioApp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_auth);
        setup();

    }

    public void setup() {

        logIn = (Button) findViewById(R.id.logIn);

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
                                    .document(Objects.requireNonNull(task.getResult().getUser().getUid()));

                            System.out.println(task.getResult().getUser().getUid());

                            getOneDocument(query, this::validarLogin);

                            DDBB.collection("usuaris").document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getUid()));

                        } else {
                            showAlert("El usuario o la contrasenya no són correctes.");
                        }
                    });
                }
            }

            private void validarLogin(Task<DocumentSnapshot> DocumentSnapshotTask) {
                if (DocumentSnapshotTask.getResult().getData() != null && DocumentSnapshotTask.getResult().getString("email").contains("admin")) {
                    showHome();
                } else if (DocumentSnapshotTask.getResult().getData() != null) {
                    showEmployee();
                } else {
                   showAlert("No estas registrat a la piazza.");
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