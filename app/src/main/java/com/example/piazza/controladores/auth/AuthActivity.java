package com.example.piazza.controladores.auth;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.piazza.controladores.admin.AdminActivity;
import com.example.piazza.controladores.employee.EmployeeActivity;
import com.example.piazza.fireBase.session.AuthUserSession;
import com.example.testauth.R;
import com.google.firebase.auth.FirebaseAuth;

public class AuthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme(R.style.AppTheme);

        setContentView(R.layout.activity_auth);
        setup();

    }

    public void setup() {

        Button logIn = (Button) findViewById(R.id.logIn);

        /* ======================================
         * Comprovamos si tiene el usuario sesión activa
         * ======================================
         * */

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            AuthUserSession.cargarDatosUsuario(FirebaseAuth.getInstance().getCurrentUser().getEmail());
            if (FirebaseAuth.getInstance().getCurrentUser().getEmail().contains("admin")) {
                showHome();
            } else {
                showEmployee();
            }

        /*
        * ======================================
        * Comprovamos si el usuario inicia sesión.
        * ======================================
        * */

        } else {

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

                                AuthUserSession.cargarDatosUsuario(email);

                                if (email.contains("admin"))
                                    showHome();
                                else
                                    showEmployee();
                            } else {
                                showAlert();
                            }
                        });
                    }
                }
            });

        }

    }


    private void showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setMessage("El usuario o la contraseña no son correctos.");
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