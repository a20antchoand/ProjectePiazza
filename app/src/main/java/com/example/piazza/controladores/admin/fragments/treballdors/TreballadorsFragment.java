package com.example.piazza.controladores.admin.fragments.treballdors;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.piazza.classes.Horario;
import com.example.piazza.classes.Usuario;
import com.example.piazza.controladores.auth.AuthActivity;
import com.example.piazza.controladores.employee.fragments.introduir_hores.IntroduirHoresFragment;
import com.example.piazza.fireBase.data.ReadData;
import com.example.piazza.fireBase.session.AuthUserSession;
import com.example.piazza.recyclerView.estatTreballadors.ListAdapterEstatTreballadors;
import com.example.piazza.recyclerView.estatTreballadors.ListElementEstatTreballadors;
import com.example.piazza.recyclerView.historialHores.ListAdapterHistorialHores;
import com.example.piazza.recyclerView.historialHores.ListElementHistorialHores;
import com.example.piazza.recyclerView.treballadors.ListAdapterTreballadors;
import com.example.piazza.recyclerView.treballadors.ListElementTreballadors;
import com.example.testauth.R;
import com.example.testauth.databinding.FragmentTreballadorsBinding;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.collection.LLRBNode;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

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

        if (querySnapshotTask.isSuccessful() && !querySnapshotTask.getResult().isEmpty()) {

            for (QueryDocumentSnapshot documentSnapshot : querySnapshotTask.getResult()) {
                Usuario usuari = documentSnapshot.toObject(Usuario.class);

                System.out.println(usuari.getEmail());

                if (!usuari.getEmail().contains("admin"))
                    listElements.add(addListElementHistorial(usuari));

            }

            System.out.println("Elements actualitzats");

        } else {
            Log.d(TAG, "Error al recuperar varios documentos.");
        }


        ListAdapterTreballadors listAdapter = new ListAdapterTreballadors(listElements, root.getContext(), new ListAdapterTreballadors.onItemClickListener() {
            @Override
            public void onItemClickListener(ListElementTreballadors item) throws FirebaseAuthException {
                Toast.makeText(getActivity().getApplicationContext(), item.getNom(), Toast.LENGTH_SHORT).show();
            }
        });
        RecyclerView recyclerView = root.findViewById(R.id.recyclerViewTreballadors);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        recyclerView.setAdapter(listAdapter);

    }

    private ListElementTreballadors addListElementHistorial(Usuario usuario) {

        String color = "#123456";
        String nom = usuario.getNom();
        String hores = usuario.getTelefono();
        String sou = usuario.getSalario();
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