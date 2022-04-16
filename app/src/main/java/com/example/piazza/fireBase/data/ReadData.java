package com.example.piazza.fireBase.data;

import static com.google.firebase.crashlytics.internal.Logger.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.piazza.classes.Horario;
import com.example.piazza.classes.Usuario;
import com.example.piazza.controladores.employee.fragments.historial.HistorialFragment;
import com.example.piazza.controladores.employee.fragments.introduir_hores.IntroduirHoresFragment;
import com.example.piazza.fireBase.session.AuthUserSession;
import com.example.piazza.recyclerView.historialHores.ListElementHistorialHores;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface ReadData {

    /**
     * Funcio per recuperar un unic document
     *
     * @param docRef referencia del document
     * @param action acció a fer un cop es reben les dades
     */
    default void getOneDocument(DocumentReference docRef, OnCompleteListener<DocumentSnapshot> action) {

        docRef.get().
                addOnCompleteListener(action);

    }

    default void getListenerDocument(Query docRef, EventListener<QuerySnapshot> action) {

        docRef.
                addSnapshotListener(action);

    }


    /**
     * Funcio per agafar multiples documnets de firebase
     *
     * @param query query amb les caracteristiques dels documents
     * @param action acció a fer un cop es reben les dades
     */
    default void getMultipldeDocuments(Query query, OnCompleteListener<QuerySnapshot> action) {

                query.get()
                .addOnCompleteListener(action);


    }



}
