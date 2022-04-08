package com.example.piazza.fireBase.data;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

public interface WriteData {

    default void writeOneDocument(DocumentReference docRef, Object object, OnCompleteListener<Void> action) {

        docRef.set(object).
                addOnCompleteListener(action);

    }

    default void writeOneDocument(DocumentReference docRef, Object object) {

        docRef.set(object);

    }

}
