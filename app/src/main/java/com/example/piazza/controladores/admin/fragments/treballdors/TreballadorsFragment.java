package com.example.piazza.controladores.admin.fragments.treballdors;

import android.os.Bundle;
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
import com.example.piazza.fireBase.data.ReadData;
import com.example.piazza.fireBase.session.AuthUserSession;
import com.example.piazza.recyclerView.treballadors.ListAdapterTreballadors;
import com.example.piazza.recyclerView.treballadors.ListElementTreballadors;
import com.example.testauth.R;
import com.example.testauth.databinding.FragmentTreballadorsBinding;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreballadorsFragment extends Fragment implements ReadData, AuthUserSession {

    private static final String TAG = "TREBALLADORS_FRAGMENT: ";
    private FragmentTreballadorsBinding binding;
    private List<ListElementTreballadors> listElements = new ArrayList<>();
    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentTreballadorsBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        setup();
        return root;
    }

    public void setup() {

        Query query = DDBB.collection("usuaris");

        getMultipldeDocuments( query, this::setElements);

    }

    public void setElements(Task<QuerySnapshot> querySnapshotTask) {

        if (querySnapshotTask.isSuccessful()) {

            for (QueryDocumentSnapshot documentSnapshot : querySnapshotTask.getResult()) {
                Usuario usuari = documentSnapshot.toObject(Usuario.class);

                if (!usuari.getRol().equals("admin"))
                    listElements.add(addListElementTreballadors(usuari));

            }

            System.out.println("Elements actualitzats");

        } else {
            Log.d(TAG, "Error al recuperar varios documentos.");
        }


        ListAdapterTreballadors listAdapter = new ListAdapterTreballadors(listElements, root.getContext(), item -> {

            Map<String, Object> data = new HashMap<>();

            DDBB.collection("usuaris").document(item.getUid()).get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.getString("rol").equals("despedit")) {
                    data.put("rol", "treballador");
                } else {
                    data.put("rol", "despedit");
                }

                DDBB.collection("usuaris").document(item.getUid())
                        .set(data, SetOptions.merge());

                listElements.clear();
                setup();
            });

        });
        RecyclerView recyclerView = root.findViewById(R.id.recyclerViewTreballadors);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        recyclerView.setAdapter(listAdapter);

    }

    private ListElementTreballadors addListElementTreballadors(Usuario usuario) {

        String color = "#123456";
        String nom = usuario.getNom().substring(0, 1).toUpperCase() + usuario.getNom().substring(1);
        String hores = usuario.getTelefono();
        String sou = usuario.getRol();
        String uid = usuario.getUid();

        return new ListElementTreballadors(
                color,
                nom ,
                hores,
                sou,
                uid);

    }

    private void showName(ListElementTreballadors item) {

        Toast.makeText(root.getContext(), "Empleat:  " + item.getNom(), Toast.LENGTH_LONG).show();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}