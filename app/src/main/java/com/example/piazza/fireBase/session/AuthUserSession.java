package com.example.piazza.fireBase.session;

import static com.google.firebase.crashlytics.internal.Logger.TAG;

import android.util.Log;

import com.example.piazza.classes.Usuario;
import com.example.piazza.controladores.employee.Fragments.introduir_hores.IntroduirHoresFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;
import java.util.Objects;

public class AuthUserSession {

    static DocumentSnapshot userDocument;
    static FirebaseFirestore DDBB = FirebaseFirestore.getInstance();
    static FirebaseUser userFirebase = FirebaseAuth.getInstance().getCurrentUser();

    static Usuario user;

    public static DocumentSnapshot getUserDocument() {
        return userDocument;
    }

    public static void setUserDocument(DocumentSnapshot userDocument) {
        AuthUserSession.userDocument = userDocument;
    }

    public static FirebaseUser getUserFirebase() {
        return userFirebase;
    }

    public static void setUserFirebase(FirebaseUser userFirebase) {
        AuthUserSession.userFirebase = userFirebase;
    }

    public static FirebaseFirestore getDDBB() {
        return DDBB;
    }

    public static void setDDBB(FirebaseFirestore DDBB) {
        AuthUserSession.DDBB = DDBB;
    }

    public static Usuario getUser() {
        return user;
    }

    public static void setUser(Usuario user) {
        AuthUserSession.user = user;
    }

    public static void cargarDatosUsuario(String email) {

        DocumentReference docRef = DDBB.collection("usuaris").document(Objects.requireNonNull(email));
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                userDocument = task.getResult();

                if (userDocument.exists()) {
                    Log.d(TAG, "DocumentSnapshot data: " + userDocument.getData());
                    Map<String, Object> data = userDocument.getData();
                    setUser(new Usuario((String) data.get("email"), (String) data.get("nom"), (String) data.get("cognom"), (String) data.get("telefono"), (String) data.get("salario")));
                    IntroduirHoresFragment.setUsuarioApp(getUser());
                } else {
                    Log.d(TAG, "No such document - AUTHFIREBASE");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });

    }

    public static void GuardarUsuarioBBDD(Usuario usuario) {


        DDBB.collection("usuaris").document(Objects.requireNonNull(usuario.getEmail()))
                .set(usuario)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));

    }

}
