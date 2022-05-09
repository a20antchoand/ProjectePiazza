package com.example.piazza.controladores.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.example.piazza.classes.Horario;
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

import java.util.concurrent.ExecutionException;

public class SplashScreen extends Activity implements ReadData, AuthUserSession {

    FirebaseUser user;
    DocumentReference docRefUsuari;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setup();
    }

    /**
     * Prepara la app per funcionar
     */
    private void setup() {


        new Handler(Looper.getMainLooper()).post(() -> {
                //demana el temps actual i espera resposta d ela asynk task
                String s = null;
                try {
                    s = new getCurrTimeGMT().execute().get();
                    getCurrTimeGMT.zdt = getCurrTimeGMT.getZoneDateTime(s);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error al coger la fecha", Toast.LENGTH_SHORT).show();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error al coger la fecha", Toast.LENGTH_SHORT).show();

                }
                //emmagatzema el resultat passant la cadena que hem recuperat a ZonedDateTime
        });

        //Cargem la informació de l'usauri actual
        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {

            //busquem la informació de firebase sobre l'usuari si aquest te una sessió activa
            docRefUsuari = DDBB.collection("usuaris")
                    .document(user.getUid());

            //recuperem la informació del document que coincideix amb la referencia i validem la informació
            getOneDocument(docRefUsuari, this::validarLogin);

        } else {
            //Si no te sesio activa l'enviem a AuthActivity
            startActivity(new Intent(SplashScreen.this, AuthActivity.class));
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

            //agafem la referencia als horaris
            Query query = DDBB.collection("horari");
            //Recuperem tots els documents i a ctualitzem el numero de document (numero que utilitzarem per la gestió dels torns)
            getMultipldeDocuments(query, this::setNumeroDocument);

        } else {
            //Si la informació que recuperem és null enviem a l'usauri a la pantalla de AuthActivity
            startActivity(new Intent(SplashScreen.this, AuthActivity.class));
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

    }

    /**
     * Funcio per actualitzar el numero de document actual
     *
     * @param horarisDocuments tots els documents recuperats
     */
    private void setNumeroDocument(Task<QuerySnapshot> horarisDocuments) {

        if (horarisDocuments.isSuccessful() && !horarisDocuments.getResult().isEmpty()) {
            //recorrem tots els documents recuperats
            for (DocumentSnapshot horariDocument : horarisDocuments.getResult()) {

                Horario horarioTemp = horariDocument.toObject(Horario.class);

                //per cada document que pertany a l'usuari i és del dia a ctual augmentem per 1 el document
                if (horariDocument.getId().contains(userAuth.getUid()) && horarioTemp.getDiaEntrada() == getCurrTimeGMT.zdt.getDayOfMonth()) {
                    IntroduirHoresFragment.numeroDocument++;
                }
            }
        }

        if (userAuth.getRol().equals("admin") || userAuth.getRol().equals("superadmin")) {
            showAdmin();
        } else if (userAuth.getRol().equals("treballador")) {
            showEmployee();
        } else {
            showAuth();
        }

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

    private void showAuth () {
        Intent intent = new Intent(this, AuthActivity.class);
        startActivity(intent);
        finish();
    }

}