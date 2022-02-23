package com.example.piazza.Modelo;

import static com.google.firebase.crashlytics.internal.Logger.TAG;

import android.app.usage.NetworkStatsManager;
import android.util.Log;

import com.example.piazza.Classes.Usuario;
import com.example.piazza.ui.introduir_hores.IntroduirHoresFragment;
import com.example.piazza.ui.perfil.PerfilFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.Objects;

public class UsuarioModelo {

    static DocumentSnapshot userDocument;
    static FirebaseFirestore DDBB;
    static FirebaseUser userFirebase;
    static Usuario usuario;

    public UsuarioModelo() {
        this.DDBB = FirebaseFirestore.getInstance();
        this.userFirebase = FirebaseAuth.getInstance().getCurrentUser();
    }

    public FirebaseUser getUserFirebase() {
        return userFirebase;
    }

    public static FirebaseFirestore getDDBB() {
        return DDBB;
    }

    public static Usuario cargarDatosUsuario() {

        DocumentReference docRef = DDBB.collection("usuaris").document(Objects.requireNonNull(userFirebase.getEmail()));
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                userDocument = task.getResult();

                if (userDocument.exists()) {
                    Log.d(TAG, "DocumentSnapshot data: " + userDocument.getData());
                    if (!userDocument.getData().isEmpty()) {
                        validarUsuario();
                    }
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });

        return usuario;
    }

    private static void validarUsuario() {
        String email = (String) userDocument.getData().get("email");
        String nom = (String) userDocument.getData().get("nom");
        String cognom = (String) userDocument.getData().get("cognom");
        String telefon = (String) userDocument.getData().get("telefon");
        String salari = (String) userDocument.getData().get("salari");

        if (!((String) userDocument.getData().get("email")).isEmpty()) {
            usuario = new Usuario(email, nom, cognom, telefon, salari);
        } else {
            usuario = new Usuario("a","a","a","a","a");
        }
    }

    public void GuardarUsuarioBBDD(Usuario usuario) {


        DDBB.collection("usuaris").document(Objects.requireNonNull(usuario.getEmail()))
                .set(usuario)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));

    }

}
