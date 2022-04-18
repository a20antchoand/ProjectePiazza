package com.example.piazza.controladores.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.piazza.classes.Horario;
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

public class AuthActivity extends AppCompatActivity implements ReadData, AuthUserSession{

    Button logIn;
    TextView errorLogin;
    ProgressBar pbLogin;

    DocumentReference docRefUsuari;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_auth);
        setup();

    }

    public void setup() {

        logIn = findViewById(R.id.logIn);
        errorLogin = findViewById(R.id.errorLogin);
        pbLogin = findViewById(R.id.pbLogin);

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //agafem els valors de email i password
                String email = ((EditText) findViewById(R.id.editTextEmail)).getText().toString();
                String password = ((EditText) findViewById(R.id.editTextPassword)).getText().toString();

                if (!email.equals("") && !password.equals("")) {

                    //retirem missatge d'error per si de cas i mostrem progress bar
                    errorLogin.setVisibility(View.GONE);
                    pbLogin.setVisibility(View.VISIBLE);

                    //Fem un login amb l'usuari i password
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {

                        //si funciona cargem les dades de l'usauri
                        if (task.isSuccessful()) {

                            /* ======================================
                             * Cargamos datos del usuario actual
                             * ======================================
                             * */

                            docRefUsuari = DDBB.collection("usuaris")
                                    .document(task.getResult().getUser().getUid());

                            getOneDocument(docRefUsuari, this::validarLogin);


                        } else {

                            //sino mostrem missatge d'error
                            showAlert("El usuario o la contrasenya no són correctes.");
                        }
                    });
                } else {
                    //mostrem error eliminem progressbar
                    errorLogin.setVisibility(View.VISIBLE);
                    pbLogin.setVisibility(View.GONE);

                }
            }

            /**
             * Funcio per validar el login de l'usuari recuperat de la BBDD
             *
             * @param usuariDocument document recuperat de firebse
             */
            private void validarLogin(Task<DocumentSnapshot> usuariDocument) {

                //validem que la informació recuperada sigui diferent a null
                if (usuariDocument.getResult().getData() != null) {

                    //agafem la refere
                    cargarDatosUsuario(docRefUsuari, this::setUserAuth);


                }

            }

            /**
             * Funcio per guardar les dades de l'usuari recuperat
             *
             * @param userDocument resultat recuperat de firebase
             */
            private void setUserAuth(Task<DocumentSnapshot> userDocument) {

                //Cargem la informació que recuperem de firebase i guardem la informació a la variable estatica que utilitzarem durant
                //tot el transcurs de la app. Li enviem a la funcio un objecte usuari creantlo directament desde firebase.
                guardarDatosGlobalesJugador(userDocument.getResult().toObject(Usuario.class));

                //agafem la referencia als horaris
                Query query = DDBB.collection("horari");
                //Recuperem tots els documents i a ctualitzem el numero de document (numero que utilitzarem per la gestió dels torns)
                getMultipldeDocuments(query, this::setNumeroDocument);

            }

            /**
             * Funcio per actualitzar el numero de document actual
             *
             * @param horarisDocuments tots els documents recuperats
             */
            private void setNumeroDocument(Task<QuerySnapshot> horarisDocuments) {

                //recorrem tots els documents recuperats
                for (DocumentSnapshot horariDocument : horarisDocuments.getResult()) {

                    Horario horarioTemp = horariDocument.toObject(Horario.class);

                    //per cada document que pertany a l'usuari i és del dia a ctual augmentem per 1 el document
                    if (horariDocument.getId().contains(userAuth.getUid()) && horarioTemp.getDiaEntrada() == getCurrTimeGMT.zdt.getDayOfMonth()) {
                        IntroduirHoresFragment.numeroDocument++;
                    }
                }

                if (userAuth.getRol().equals("admin")) {
                    showAdmin();
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

    private void showAdmin() {
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