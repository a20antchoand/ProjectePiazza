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
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadData extends AuthUserSession {

    DocumentSnapshot userDocument;
    List<ListElementHistorialHores> listElementHistorialHores = new ArrayList<>();
    List<ListElementHistorialHores> listElementHistorialHoresOrdenat = new ArrayList<>();

    public void getOneDocument (String email) {

        DocumentReference docRef = getDDBB().collection("usuaris").document(Objects.requireNonNull(email));
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                userDocument = task.getResult();

                if (userDocument.exists()) {
                    Log.d(TAG, "DocumentSnapshot data: " + userDocument.getData());
                    Map<String, Object> data = userDocument.getData();
                    setUser(new Usuario((String) data.get("email"), (String) data.get("nom"), (String) data.get("cognom"), (String) data.get("telefono"), (String) data.get("salario")));
                    IntroduirHoresFragment.setUsuarioApp(getUser());
                } else {
                    Log.d(TAG, "No se encuentra el usuario: " + email);
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });

    }


    public void getHistorialCurrUser(String collection, Query q, OnCompleteListener<QuerySnapshot> action) {

        getDDBB().collection(collection)
                .orderBy("diaEntrada", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(action);


    }

    private void addListElementHistorial(Horario horario) {

        String data = horario.getAnioEntrada() + "/" + horario.getMesEntrada() + "/" + horario.getDiaEntrada();
        String entrada = ho + ":" + minutEntrada;
        String sortida = horaSortida + ":" + minutSortida;
        String totalFinal = total/60 + ":" + total%60;

        if (minutEntrada < 10)
            entrada = horaEntrada + ":0" + minutEntrada;
        if (horaEntrada < 10)
            entrada = "0" + horaEntrada + ":" + horaEntrada;
        if (horaEntrada < 10 && minutEntrada < 10)
            entrada = "0" + horaEntrada + ":0" + horaEntrada;

        if (minutSortida < 10)
            sortida = horaSortida + ":0" + minutSortida;
        if (horaSortida < 10)
            sortida = "0" + horaSortida + ":" + minutSortida;
        if (horaSortida < 10 && minutSortida < 10)
            sortida = "0" + horaSortida + ":0" + minutSortida;;

        if ((total % 60) < 10)
            totalFinal = total/60 + ":0" + total%60;

        listElementHistorialHores.add(new ListElementHistorialHores(
                data,
                entrada + "  ",
                sortida,
                totalFinal));

    }

}
