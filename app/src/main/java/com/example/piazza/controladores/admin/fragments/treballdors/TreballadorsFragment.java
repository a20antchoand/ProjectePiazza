package com.example.piazza.controladores.admin.fragments.treballdors;

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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TreballadorsFragment extends Fragment implements ReadData, AuthUserSession {

    private static final String TAG = "TREBALLADORS_FRAGMENT: ";
    private FragmentTreballadorsBinding binding;
    private List<ListElementTreballadors> listElements = new ArrayList<>();
    private View root;
    private Boolean firstLoad;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentTreballadorsBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        firstLoad = true;

        new Handler(Looper.getMainLooper()).post(() -> setup());

        return root;
    }

    public void setup() {

        getMultipldeDocuments(DDBB.collection("usuaris").whereEqualTo("empresa", userAuth.getEmpresa()), this::mostrarCampModificat);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            firstLoad = false;
        }, 5000);

    }


    private void mostrarCampModificat(Task<QuerySnapshot> querySnapshotTask) {

        if (querySnapshotTask.isSuccessful() && !querySnapshotTask.getResult().isEmpty()) {

            System.out.println("CAMPS: " + querySnapshotTask.getResult().size());

            for (DocumentSnapshot documentSnapshot : querySnapshotTask.getResult().getDocuments()) {

                Usuario temp = documentSnapshot.toObject(Usuario.class);

                if (temp.getRol().equals("treballador"))
                    getListenerDocument(documentSnapshot.getReference(), this::notificarCanvi);

            }

            getMultipldeDocuments(DDBB.collection("usuaris").whereEqualTo("empresa", userAuth.getEmpresa()), this::setElements);

        }

    }

    private void notificarCanvi(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

        if (documentSnapshot.exists()) {
            Usuario treballador = documentSnapshot.toObject(Usuario.class);
            Random rand = new Random();
            if (treballador.getTreballant())
                Notificacio.Notificar(getContext(),"Jornada iniciada!", treballador.getNom() + " ha entrat a treballar", rand.nextInt(100-20)+20);
            else if (!firstLoad){
                Notificacio.Notificar(getContext(), "Jornada acabada!", treballador.getNom() + " ha sortit de treballar", rand.nextInt(100 - 20) + 20);
            }
            getMultipldeDocuments(DDBB.collection("usuaris").whereEqualTo("empresa", userAuth.getEmpresa()), this::setElements);

        }

    }

    public void setElements(Task<QuerySnapshot> querySnapshotTask) {

        listElements.clear();

        if (querySnapshotTask.isSuccessful()) {

            for (QueryDocumentSnapshot documentSnapshot : querySnapshotTask.getResult()) {
                Usuario usuari = documentSnapshot.toObject(Usuario.class);

                if (!usuari.getRol().equals("admin") && !usuari.getRol().equals("superadmin"))

                    if (usuari.getTreballant())
                        addListElementTreballadors(usuari, true);
                    else
                        addListElementTreballadors(usuari, false);


            }

            System.out.println("Elements actualitzats " + listElements.size());

        } else {
            Log.d(TAG, "Error al recuperar varios documentos.");
        }

        ListAdapterTreballadors listAdapter = new ListAdapterTreballadors(listElements, root.getContext(), this::showName);
        binding.recyclerViewTreballadors.setHasFixedSize(true);
        binding.recyclerViewTreballadors.setLayoutManager(new LinearLayoutManager(root.getContext()));
        binding.recyclerViewTreballadors.setAdapter(listAdapter);

        binding.shimmerTreballador.setVisibility(View.GONE);
        binding.recyclerViewTreballadors.setVisibility(View.VISIBLE);

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