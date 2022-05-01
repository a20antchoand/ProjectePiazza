package com.example.piazza.fireBase.session;

import android.graphics.BitmapFactory;

import com.example.piazza.classes.Perfil;
import com.example.piazza.classes.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public interface AuthUserSession {

    FirebaseFirestore DDBB = FirebaseFirestore.getInstance();
    FirebaseStorage STORAGE = FirebaseStorage.getInstance();
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

        System.out.println("nom: " + usuario.getNom());

        DDBB.collection("usuaris").document(Objects.requireNonNull(usuario.getUid()))
                .set(usuario);

    }


    /**
     * Funcio per actualitzar les dades de l'usuari que s'utulitzaran durant la sessió a la APP
     *
     * @param user usuari que s'ha recuperat de firebase
     */
    default void guardarDatosGlobalesJugador(Usuario user) {

        userAuth.setNom(user.getNom());
        userAuth.setUid(user.getUid());
        userAuth.setEmail(user.getEmail());
        userAuth.setCognom(user.getCognom());
        userAuth.setRol(user.getRol());
        userAuth.setTelefono(user.getTelefono());
        userAuth.setHoresMensuals(user.getHoresMensuals());
        userAuth.setDiesSetmana(user.getDiesSetmana());
        userAuth.setUrlPerfil(user.getUrlPerfil());

        if (userAuth.getUrlPerfil() != null && !userAuth.getUrlPerfil().equals("")) {

            //Recuperem la imatge del usuari del seu apartat de firebase storage amb la seva url

            StorageReference storageRef = STORAGE.getReferenceFromUrl(userAuth.getUrlPerfil());
            storageRef.getBytes(1024 * 1024)
                    .addOnSuccessListener(bytes -> perfil.setBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length)));
        } else {

            //Si l'usuari no te url de foto de perfil encara, posem la imatge de perfil per defecte

            StorageReference storageRef = STORAGE.getReferenceFromUrl("gs://testauth-f5eb4.appspot.com/ORF8060.jpg");
            storageRef.getBytes(1024 * 1024)
                    .addOnSuccessListener(bytes -> perfil.setBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length)));
        }
    }



}
