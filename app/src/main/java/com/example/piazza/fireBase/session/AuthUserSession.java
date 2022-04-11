package com.example.piazza.fireBase.session;

import static com.google.firebase.crashlytics.internal.Logger.TAG;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.piazza.classes.Perfil;
import com.example.piazza.classes.Usuario;
import com.example.piazza.controladores.employee.fragments.introduir_hores.IntroduirHoresFragment;
import com.example.piazza.fireBase.data.ReadData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Map;
import java.util.Objects;

public interface AuthUserSession {

    FirebaseFirestore DDBB = FirebaseFirestore.getInstance();
    Usuario userAuth = new Usuario();
    Perfil perfil = new Perfil();

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

    default void guardarDatosGlobalesJugador(Usuario user) {

        userAuth.setNom(user.getNom());
        userAuth.setUid(user.getUid());
        userAuth.setEmail(user.getEmail());
        userAuth.setCognom(user.getCognom());
        userAuth.setRol(user.getRol());
        userAuth.setTelefono(user.getTelefono());
        userAuth.setHoresMensuals(user.getHoresMensuals());
        userAuth.setUrlPerfil(user.getUrlPerfil());

        if (userAuth.getUrlPerfil() != null) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReferenceFromUrl(userAuth.getUrlPerfil());
            storageRef.getBytes(1024 * 1024)
                    .addOnSuccessListener(bytes -> {
                        perfil.setBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                    });
        }
    }



}
