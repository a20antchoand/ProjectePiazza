package com.example.piazza.controladores.admin;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.piazza.classes.Horario;
import com.example.piazza.classes.Usuario;
import com.example.piazza.controladores.auth.AuthActivity;
import com.example.piazza.controladores.auth.SplashScreen;
import com.example.piazza.fireBase.data.ReadData;
import com.example.piazza.fireBase.data.WriteData;
import com.example.piazza.fireBase.session.AuthUserSession;
import com.example.testauth.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.testauth.databinding.ActivityAdminBinding;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.concurrent.atomic.AtomicInteger;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AdminActivity extends AppCompatActivity implements AuthUserSession, ReadData, WriteData {

    private ActivityAdminBinding binding;
    TextView textCartItemCount;
    int mCartItemCount = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            if (userAuth.getRol() == null) {
                startActivity(new Intent(this, SplashScreen.class));
                finish();
            }

            setTheme(R.style.Theme_TestAuth);

            binding = ActivityAdminBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            Toolbar toolbar = findViewById(R.id.my_toolbar_admin);
            ImageView logo = findViewById(R.id.yourlogo);

            StorageReference storageRef = STORAGE.getReferenceFromUrl("gs://testauth-f5eb4.appspot.com/" + userAuth.getEmpresa() + ".png");
            storageRef.getBytes(1024 * 1024)
                    .addOnSuccessListener(bytes -> logo.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length)));


            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayUseLogoEnabled(true);

            AppBarConfiguration appBarConfiguration = null;

            binding.navView.getMenu().clear();

            if (userAuth.getRol().equals("admin")) {
                appBarConfiguration = new AppBarConfiguration.Builder(
                        R.id.navigation_treballadors, R.id.navigation_administrar, R.id.navigation_reports)
                        .build();
                binding.navView.inflateMenu(R.menu.bottom_nav_menu);
            } else if (userAuth.getRol().equals("superadmin")) {
                appBarConfiguration = new AppBarConfiguration.Builder(
                        R.id.navigation_treballadors, R.id.navigation_alta_administradors, R.id.navigation_reports)
                        .build();
                binding.navView.inflateMenu(R.menu.bottom_nav_menu_super);
            }

            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_admin);
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            NavigationUI.setupWithNavController(binding.navView, navController);

            binding.yourlogo.setOnClickListener(l -> startActivity(new Intent(AdminActivity.this, AdminActivity.class)));

        } catch (Exception e) {
            e.printStackTrace();
            startActivity(new Intent(this, SplashScreen.class));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ajustes_menu, menu);

        final MenuItem menuItem = menu.findItem(R.id.validar);

        View actionView = menuItem.getActionView();
        textCartItemCount = actionView.findViewById(R.id.cart_badge);

        getMultipldeDocuments(DDBB.collection("modificacions"), this::setupBadge);

        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });

        return true;
    }

    private void setupBadge(Task<QuerySnapshot> querySnapshotTask) {

        int size = 0;
        System.out.println("AQUI 1");
        for (DocumentSnapshot documentSnapshot : querySnapshotTask.getResult()) {

            Horario temp = documentSnapshot.toObject(Horario.class);

            if (temp.getUsuario().getEmpresa().equals(userAuth.getEmpresa()))
                size++;

        }

        if (!querySnapshotTask.getResult().isEmpty()) {
            textCartItemCount.setText(""+size);
            textCartItemCount.setVisibility(View.VISIBLE);
            System.out.println("VISIBLE");
        } else {
            textCartItemCount.setVisibility(View.INVISIBLE);
            System.out.println("INVISIBLE");
        }

    }

    private void setupBadge(int size) {

        if (size > 0) {
            textCartItemCount.setVisibility(View.VISIBLE);
            textCartItemCount.bringToFront();
            textCartItemCount.setText(size + "");
            System.out.println("VISIBLE 2");
        } else {
            textCartItemCount.setVisibility(View.INVISIBLE);
            System.out.println("INVISIBLE 2");
        }
    }

    private void mostrarValidacions(Task<QuerySnapshot> querySnapshotTask) {

        AtomicInteger validacions = new AtomicInteger(querySnapshotTask.getResult().size());

        if (!querySnapshotTask.getResult().isEmpty()) {

            setupBadge(querySnapshotTask);

            for (DocumentSnapshot documentSnapshot : querySnapshotTask.getResult().getDocuments()) {
                Horario horario = documentSnapshot.toObject(Horario.class);
                if (horario.getUsuario().getEmpresa().equals(userAuth.getEmpresa()) && horario.getModificacio() != null) {
                    String HORES_MINUTS_NEUTRE = "%01dh:%02dm";

                    String titol;
                    String contingut = "Data: " + horario.getModificacio().getDiaEntrada() + "/" + horario.getModificacio().getMesEntrada() + " a " + horario.getModificacio().getDiaSalida() + "/" + horario.getModificacio().getMesSalida()
                            + "\n\nHora entrada: " + String.format(HORES_MINUTS_NEUTRE, horario.getModificacio().getHoraEntrada(), horario.getModificacio().getMinutEntrada())
                            + "\n\nHora sortida: " + String.format(HORES_MINUTS_NEUTRE, horario.getModificacio().getHoraSalida(), horario.getModificacio().getMinutSalida())
                            + "\n\nTotal treballat: " + String.format(HORES_MINUTS_NEUTRE, horario.getModificacio().getTotalMinutsTreballats() / 60, horario.getModificacio().getTotalMinutsTreballats() % 60);

                    if (documentSnapshot.getId().contains("afegit")) {
                        titol = "L'empleat: " + horario.getUsuario().getNom() + " vol afegir el següent registre...";
                    } else {
                        titol = "L'empleat: " + horario.getUsuario().getNom() + " vol modificar el següent registre...";

                    }

                    new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText(titol)
                            .setContentText(contingut)
                            .setConfirmText("Validar")
                            .setCancelText("Denegar")
                            .setConfirmClickListener(sweetAlertDialog -> {

                                Horario temp = horario.getModificacio();

                                temp.setModificacio(null);

                                writeOneDocument(DDBB.collection("horari").document(documentSnapshot.getId()), temp);

                                DDBB.collection("modificacions").document(documentSnapshot.getId()).delete();

                                validacions.getAndDecrement();
                                System.out.println("VALIDACIONS: " + validacions);
                                setupBadge(validacions.get());

                                sweetAlertDialog.dismissWithAnimation();
                            })
                            .setCancelClickListener(sweetAlertDialog -> {

                                DDBB.collection("modificacions").document(documentSnapshot.getId()).delete();

                                sweetAlertDialog.dismissWithAnimation();
                            }).show();
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.logout:
                logOut();
                break;
            case R.id.validar:
                getMultipldeDocuments(DDBB.collection("modificacions"), this::mostrarValidacions);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void logOut() {

        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, AuthActivity.class));

        binding = null;

        finish();

        guardarDatosGlobalesJugador(new Usuario());

    }

    @Override
    protected void onRestart() {
        super.onRestart();

        startActivity(new Intent(this, SplashScreen.class));

    }

    @Override
    public void onBackPressed() {

        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Vols tancar l'app")
                .setConfirmText("Si")
                .setCancelText("No")
                .setConfirmClickListener(sDialog -> {
                    sDialog.dismissWithAnimation();
                    finish();
                })
                .setCancelClickListener(sweetAlertDialog -> {
                    sweetAlertDialog.dismissWithAnimation();
                }).show();

    }

}