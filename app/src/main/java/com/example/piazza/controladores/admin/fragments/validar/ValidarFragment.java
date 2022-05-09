package com.example.piazza.controladores.admin.fragments.validar;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.piazza.classes.Horario;
import com.example.piazza.classes.Usuario;
import com.example.piazza.fireBase.data.ReadData;
import com.example.piazza.fireBase.data.WriteData;
import com.example.piazza.fireBase.session.AuthUserSession;
import com.example.piazza.recyclerView.estatTreballadors.ListAdapterEstatTreballadors;
import com.example.piazza.recyclerView.estatTreballadors.ListElementEstatTreballadors;
import com.example.testauth.R;
import com.example.testauth.databinding.FragmentValidarBinding;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ValidarFragment extends Fragment implements AuthUserSession, ReadData, WriteData {

    private static final String TAG = "VALIDAR: " ;
    private FragmentValidarBinding binding;
    private View root;
    private List<ListElementEstatTreballadors> elements = new ArrayList<>();
    public static List<Usuario> treballadors = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentValidarBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        setup();

        return root;


    }

    public void setup() {

        getMultipldeDocuments(DDBB.collection("usuaris"), this::recopilarTreballadors);

        getMultipldeDocuments(DDBB.collection("horari").whereEqualTo("estatJornada", false), this::mostrarEstat);

        getListenerCollections(DDBB.collection("horari"), this::escoltarBBDD);

    }

    private void escoltarBBDD(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {

        if (e != null) {
            Log.w(TAG, "ESCOLTA FAILED.", e);
            return;
        }

        getMultipldeDocuments(DDBB.collection("horari").whereEqualTo("estatJornada", false), this::mostrarEstat);

    }

    private void recopilarTreballadors(Task<QuerySnapshot> querySnapshotTask) {

        treballadors = new ArrayList<>();

        if (querySnapshotTask.isSuccessful()) {

            for (DocumentSnapshot document : querySnapshotTask.getResult().getDocuments()) {

                Usuario temp = document.toObject(Usuario.class);

                treballadors.add(temp);

            }

        }

    }

    private void mostrarEstat(Task<QuerySnapshot> querySnapshotTask) {
        elements = new ArrayList<>();
        if (querySnapshotTask.isSuccessful()) {
            for (Usuario usuari : treballadors) {
                int cont = 0;
                if (usuari.getRol().equals("treballador")) {
                    Horario horarioEmpezado = new Horario();
                    for (DocumentSnapshot documentSnapshot : querySnapshotTask.getResult().getDocuments()) {
                        Horario horario = documentSnapshot.toObject(Horario.class);
                        System.out.println(horario.getUsuario().getUid() + "-->" + usuari.getUid());
                        if (horario.getUsuario().getUid().equals(usuari.getUid())) {
                            cont++;
                            horarioEmpezado = horario;
                        }
                    }
                    if (cont == 0)
                        elements.add(addListElementEstatTreballadorsActius(usuari));
                    else
                        elements.add(addListElementEstatTreballadorsNoActius(usuari, horarioEmpezado));
                }

            }
            ListAdapterEstatTreballadors listAdapter = new ListAdapterEstatTreballadors(elements, root.getContext(), item -> moveToDescription(item));
            RecyclerView recyclerView = root.findViewById(R.id.listRecyclerView);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
            recyclerView.setAdapter(listAdapter);
        }
    }

    private ListElementEstatTreballadors addListElementEstatTreballadorsActius(Usuario usuario) {

        String color = "#FF0000";
        String nom = usuario.getNom().substring(0, 1).toUpperCase() + usuario.getNom().substring(1);
        String hores = usuario.getTelefono();
        String estat = usuario.getRol();
        String uid = usuario.getUid();

        return new ListElementEstatTreballadors(
                color,
                nom ,
                hores,
                estat,
                uid);

    }

    private ListElementEstatTreballadors addListElementEstatTreballadorsNoActius(Usuario usuario, Horario horario) {

        String color = "#00BB2d";
        String nom = usuario.getNom().substring(0, 1).toUpperCase() + usuario.getNom().substring(1);
        String hores = String.format("%01dh %02dm", horario.getHoraEntrada(), horario.getMinutEntrada());
        String estat = usuario.getRol();
        String uid = usuario.getUid();

        return new ListElementEstatTreballadors(
                color,
                nom ,
                hores,
                estat,
                uid);

    }

    void moveToDescription(com.example.piazza.recyclerView.estatTreballadors.ListElementEstatTreballadors item) {
        System.out.println("Estat: "+ item.getEstat());

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}