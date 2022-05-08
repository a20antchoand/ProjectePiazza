package com.example.piazza.controladores.admin.fragments.treballdors;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.piazza.classes.Horario;
import com.example.piazza.classes.Usuario;
import com.example.piazza.commons.Notificacio;
import com.example.piazza.fireBase.data.ReadData;
import com.example.piazza.fireBase.session.AuthUserSession;
import com.example.piazza.recyclerView.treballadors.ListAdapterTreballadors;
import com.example.piazza.recyclerView.treballadors.ListElementTreballadors;
import com.example.testauth.R;
import com.example.testauth.databinding.FragmentTreballadorsBinding;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreballadorsFragment extends Fragment implements ReadData, AuthUserSession {

    private static final String TAG = "TREBALLADORS_FRAGMENT: ";
    private FragmentTreballadorsBinding binding;
    private List<String> treballadors = new ArrayList<>();
    private List<ListElementTreballadors> listElements = new ArrayList<>();
    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentTreballadorsBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        new Handler(Looper.getMainLooper()).post(() -> setup());

        return root;
    }

    public void setup() {

        new Handler(Looper.getMainLooper()).post(() -> getMultipldeDocuments(DDBB.collection("horari"), this::mostrarCampModificat));

        getListenerDocument(DDBB.collection("horari"), this::mostrarCampModificat);

    }

    private void mostrarCampModificat(Task<QuerySnapshot> querySnapshotTask) {

        if (querySnapshotTask.isSuccessful()) {

            for (DocumentSnapshot documentSnapshot : querySnapshotTask.getResult().getDocuments()) {

                Horario temp = documentSnapshot.toObject(Horario.class);

                if (!temp.isEstatJornada()) {

                    System.out.println("JORNADA INICIADA: " + temp.getUsuario().getNom());

                    treballadors.add(temp.getUsuario().getUid());


                }

            }

            getMultipldeDocuments(DDBB.collection("usuaris").whereEqualTo("empresa", userAuth.getEmpresa()), this::setElements);

        }

    }

    private void mostrarCampModificat(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {

        if (!queryDocumentSnapshots.isEmpty()) {

            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                Horario temp = documentSnapshot.toObject(Horario.class);

                if (!temp.isEstatJornada()) {

                    System.out.println("JORNADA INICIADA: " + temp.getUsuario().getNom());

                    treballadors.add(temp.getUsuario().getUid());

                    new Handler(Looper.getMainLooper()).post(() -> Notificacio.Notificar(getContext(), "Inici de jornada!", "Usuari: " + temp.getUsuario().getNom() + " ha iniciat jornada.", (int) Math.random()));

                }

            }

            getMultipldeDocuments(DDBB.collection("usuaris").whereEqualTo("empresa", userAuth.getEmpresa()), this::setElements);

        }

    }

    public void setElements(Task<QuerySnapshot> querySnapshotTask) {

        listElements.clear();

        if (querySnapshotTask.isSuccessful()) {

            for (QueryDocumentSnapshot documentSnapshot : querySnapshotTask.getResult()) {
                Usuario usuari = documentSnapshot.toObject(Usuario.class);

                if (!usuari.getRol().equals("admin"))

                    if (treballadors.size() != 0 && treballadors.contains(usuari.getUid()))
                        addListElementTreballadors(usuari, true);
                    else
                        addListElementTreballadors(usuari, false);


            }

            System.out.println("Elements actualitzats " + listElements.size());

        } else {
            Log.d(TAG, "Error al recuperar varios documentos.");
        }

        ListAdapterTreballadors listAdapter = new ListAdapterTreballadors(listElements, root.getContext(), this::showName);
        RecyclerView recyclerView = root.findViewById(R.id.recyclerViewTreballadors);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        recyclerView.setAdapter(listAdapter);

    }

    private void addListElementTreballadors(Usuario usuario, Boolean treballant) {

        String nom = usuario.getNom().substring(0, 1).toUpperCase() + usuario.getNom().substring(1);
        String cognom = usuario.getCognom();
        String uid = usuario.getUid();
        listElements.add(new ListElementTreballadors(
                nom ,
                cognom,
                uid, treballant));

    }

    private void showName(ListElementTreballadors item, View itemview) {

        Toast.makeText(root.getContext(), "Empleat:  " + item.getNom(), Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        listElements.clear();
    }
}