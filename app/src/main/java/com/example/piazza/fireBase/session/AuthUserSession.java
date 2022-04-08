package com.example.piazza.fireBase.session;

import static com.google.firebase.crashlytics.internal.Logger.TAG;

import android.util.Log;

import com.example.piazza.classes.Usuario;
import com.example.piazza.controladores.employee.fragments.introduir_hores.IntroduirHoresFragment;
import com.example.piazza.fireBase.data.ReadData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;
import java.util.Objects;

public interface AuthUserSession {

    FirebaseFirestore DDBB = FirebaseFirestore.getInstance();
    Usuario userAuth = new Usuario();

    /**
     *Metode per cargar a la APP tota la informació del usuari.
     *
     * @param docRef referencia al document de firebase amb la informació de l'usuari.
     * @param action funció qeu es cridara un cop es recuperi la informació.
     */
    default void cargarDatosUsuario(DocumentReference docRef, OnCompleteListener<DocumentSnapshot> action) {

        docRef.get().addOnCompleteListener(action);

    }

    /**
     *
     * Metode per guardar la informació a FireBase de l'usuari de la APP.
     * @param usuario
     */
    default void GuardarUsuarioBBDD(Usuario usuario) {


        DDBB.collection("usuaris").document(Objects.requireNonNull(usuario.getUid()))
                .set(usuario);

    }

}
