package com.example.piazza.fireBase.data;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

public interface WriteData {

    /**
     * Funcio per escriure un fitxer de firebase i actualitzar la informació
     *
     * @param docRef referencia del document
     * @param object objecte a modificar
     * @param action accio a fer quan es reben les dades
     */
    default void writeOneDocument(DocumentReference docRef, Object object, OnCompleteListener<Void> action) {

        docRef.set(object).
                addOnCompleteListener(action);

    }

    /**
     * Funcio per escriure un fitxer de firebase i actualitzar la informació
     *
     * @param docRef referencia del document
     * @param object objecte a modificar
     */
    default void writeOneDocument(DocumentReference docRef, Object object) {

        docRef.set(object);

    }

}
