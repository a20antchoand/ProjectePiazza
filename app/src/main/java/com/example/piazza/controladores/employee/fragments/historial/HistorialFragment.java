package com.example.piazza.controladores.employee.fragments.historial;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.piazza.classes.Horario;
import com.example.piazza.controladores.employee.fragments.introduir_hores.IntroduirHoresFragment;
import com.example.piazza.fireBase.data.ReadData;
import com.example.piazza.fireBase.session.AuthUserSession;
import com.example.piazza.recyclerView.historialHores.ListAdapterHistorialHores;
import com.example.piazza.recyclerView.historialHores.ListElementHistorialHores;
import com.example.testauth.R;
import com.example.piazza.commons.*;
import com.example.testauth.databinding.FragmentHistorialBinding;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HistorialFragment extends Fragment implements ReadData, AuthUserSession{

    private int mInterval = 5000; // 5 seconds by default, can be changed later
    public static Handler HandlerHistorial = new Handler();

    private static final String TAG = "HistorialFragment: ";
    private FragmentHistorialBinding binding;
    private static View root;
    private List<ListElementHistorialHores> listElements = new ArrayList<>();

    Query query = DDBB.collection("horari")
            .orderBy("diaEntrada", Query.Direction.DESCENDING);

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHistorialBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        setup();

        return root;
    }

    private void setup() {

        //Recorrem tots els registres horaris
        getMultipldeDocuments(query, this::setElements);

        //mostrem les hores mensuals
        binding.tvHoresMensuals.setText(userAuth.getHoresMensuals() + "h");

        //Recorrem tots els registres horaris
        getMultipldeDocuments(query, this::calcularHoresTreballades);

        //Iniciem un handler
        startRepeatingTask();

    }

    public void setElements(Task<QuerySnapshot> histrorialsDocuments) {

        //si el resultat es successful
        if (histrorialsDocuments.isSuccessful()) {

            //recorrem els registres
            for (QueryDocumentSnapshot historialDocument : histrorialsDocuments.getResult()) {
                //Si el registre pertany al usuari acual
                if (historialDocument.getId().contains(userAuth.getUid())) {
                    //Creem l'objecte Historial que hem rcuperat del document
                    Horario horario = historialDocument.toObject(Horario.class);
                    //si la jornada esta acabada
                    if (horario.isEstatJornada())
                        //creem l'item de la recycler view i l'afegim a un array list d'elements
                        listElements.add(bindDataElementHistorial(horario));
                }
            }

            System.out.println("Elements actualitzats");

        } else {
            Log.d(TAG, "Error al recuperar varios documentos.");
        }

        //al finalitzar, si no hi han elements a l'array
        if (listElements.size() == 0){

            //mostrem l'estat de registrres buit
            showHistorialEmpty();

        } else {

            //mostrem l'historial de registres
            showHistorial();

        }
    }

    private void showHistorial() {

        //Mostrem la recyclerView i tots els elements necessaris
        binding.titolHistorial.setVisibility(View.GONE);
        binding.imatgeHistorial.setVisibility(View.GONE);
        binding.recyclerViewHistorial.setVisibility(View.VISIBLE);
        binding.cardView.setVisibility(View.VISIBLE);
        binding.cardView2.setVisibility(View.VISIBLE);
        binding.horesTreballadesTotal.setVisibility(View.VISIBLE);

        //Creem l'adaptador de la recyclerview
        ListAdapterHistorialHores listAdapter = new ListAdapterHistorialHores(listElements, root.getContext(), item -> {
            //moveToDescription(item);
        });

        //creem la recyclerview
        RecyclerView recyclerView = root.findViewById(R.id.recyclerViewHistorial);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        recyclerView.setAdapter(listAdapter);

    }

    private void showHistorialEmpty() {

        //mostrem un titol dient que no tens registres junt amb una imatge
        binding.titolHistorial.setVisibility(View.VISIBLE);
        binding.imatgeHistorial.setVisibility(View.VISIBLE);
        binding.recyclerViewHistorial.setVisibility(View.GONE);
        binding.cardView.setVisibility(View.GONE);
        binding.cardView2.setVisibility(View.GONE);
        binding.horesTreballadesTotal.setVisibility(View.GONE);

    }

    private ListElementHistorialHores bindDataElementHistorial(Horario horario) {

        String data2 = String.format("%02d",horario.getHoraEntrada());

        System.out.println("DATA2: " + data2);

        String data = String.format("%4d/%02d/%02d",horario.getAnioEntrada(), horario.getMesEntrada(), horario.getDiaEntrada());
        String entrada = String.format("%d:%02d",horario.getHoraEntrada(),horario.getMinutEntrada()) ;
        String sortida = String.format("%d:%02d",horario.getHoraSalida(), horario.getMinutSalida());
        String totalFinal = String.format("%dh %02dm",horario.getTotalMinutsTreballats()/60, horario.getTotalMinutsTreballats()%60);

        return new ListElementHistorialHores(
                data,
                entrada + "  ",
                sortida,
                totalFinal);

    }

    private void calcularHoresTreballades(Task<QuerySnapshot> historialsDocuments) {

        int totalTempsMes = 0;

        //si el resultat es successful
        if (historialsDocuments.isSuccessful()) {
            //recorrem els documents
            for (QueryDocumentSnapshot historialDocument : historialsDocuments.getResult()) {
                //si el document pertany a l'usuari
                if (historialDocument.getId().contains(userAuth.getUid())) {
                    //creem l'objecte Horario recuperat del document
                    Horario horario = historialDocument.toObject(Horario.class);
                    //comprovem si el document és del mes actual
                    if (horario.getMesEntrada() == getCurrTimeGMT.zdt.getMonthValue())
                        //sumem els minuts totals treballats
                        totalTempsMes += horario.getTotalMinutsTreballats();

                }
            }
        }

        //es mostra el total de temps treballat durant el mes
        binding.tvTotalHores.setText(String.format("%01dh %02dm",totalTempsMes/60,totalTempsMes%60));

        //calculem el residu d'hores
        calcularResiduHores(totalTempsMes);

    }

    private void calcularResiduHores(int totalTempsMes) {

        //minuts a treballar al mes
        int totalMinutsTreballar = Integer.parseInt(userAuth.getHoresMensuals()) * 60;
        int residu;

        //si els minuts a treballar son majors als minuts treballats
        //mostrem en negatiu el residu d'hores.
        if (totalMinutsTreballar > totalTempsMes) {
            residu = totalMinutsTreballar - totalTempsMes;
            binding.tvResiduHores.setText(String.format("-%01dh %02dm",residu/60,residu%60));
            binding.tvResiduHores.setTextColor(root.getContext().getResources().getColor(R.color.end_btn));

        //si els minuts a treballar son menors als minuts treballats
        //mostrem en positiu el residu d'hores.
        } else if (totalMinutsTreballar < totalTempsMes) {
            residu = totalTempsMes - totalMinutsTreballar;
            binding.tvResiduHores.setText(String.format("+%01dh %02dm",residu/60,residu%60));
            binding.tvResiduHores.setTextColor(root.getContext().getResources().getColor(R.color.start_btn));
        //mostrem el residu a 00:00
        } else {
            binding.tvResiduHores.setText("0h 0m");
            binding.tvResiduHores.setTextColor(root.getContext().getResources().getColor(R.color.black));
        }
    }


    /*
    * Cada 5 segons actualitzem les hores treballades
    * */

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                updateStatus(); //this function can change value of mInterval.
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                HandlerHistorial.postDelayed(mStatusChecker, mInterval);
            }
        }
    };

    private void updateStatus() {

        getMultipldeDocuments(query, this::calcularHoresTreballades);

    }

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        HandlerHistorial.removeCallbacks(mStatusChecker);
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopRepeatingTask();
    }
}