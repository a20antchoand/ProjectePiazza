package com.example.piazza.controladores.employee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.piazza.classes.Usuario;
import com.example.piazza.commons.getCurrTimeGMT;
import com.example.piazza.controladores.admin.AdminActivity;
import com.example.piazza.controladores.auth.AuthActivity;
import com.example.piazza.controladores.auth.SplashScreen;
import com.example.piazza.controladores.employee.fragments.introduir_hores.IntroduirHoresFragment;
import com.example.piazza.fireBase.session.AuthUserSession;
import com.example.testauth.R;

import com.example.testauth.databinding.ActivityEmployeeBinding;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.StorageReference;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Calendar;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.grpc.android.BuildConfig;

public class EmployeeActivity extends AppCompatActivity implements AuthUserSession {

    private ActivityEmployeeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setTheme(R.style.Theme_TestAuth);

            binding = ActivityEmployeeBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            View root = findViewById(R.id.employeeContainer);

            Toolbar toolbar = findViewById(R.id.my_toolbar);
            ImageView logo = findViewById(R.id.yourlogo);

            StorageReference storageRef = STORAGE.getReferenceFromUrl("gs://testauth-f5eb4.appspot.com/" + userAuth.getEmpresa() + ".png");
            storageRef.getBytes(1024 * 1024)
                    .addOnSuccessListener(bytes -> logo.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length)));

            /*toolbar.setBackgroundColor(getResources().getColor(R.color.start_btn));*/
            toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_baseline_menu_24));

            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayUseLogoEnabled(true);

            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.navigation_introduir_hores, R.id.navigation_historial, R.id.navigation_perfil)
                    .build();

            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            NavigationUI.setupWithNavController(binding.navView, navController);

            root.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
                int heightDiff = root.getRootView().getHeight() - root.getHeight();
                if (heightDiff > dpToPx(EmployeeActivity.this, 200)) {
                    findViewById(R.id.nav_view).setVisibility(View.GONE); // Lo haces invisible y que no ocupe espacio.
                } else {
                    findViewById(R.id.nav_view).setVisibility(View.VISIBLE); // Lo haces visible
                }
            });

            binding.yourlogo.setOnClickListener(l -> startActivity(new Intent(EmployeeActivity.this, EmployeeActivity.class)));

            binding.btnContinuar.setOnClickListener(l -> {
                if (binding.checkBox.isChecked()) {
                    binding.constraintWelcome.setVisibility(View.GONE);
                    binding.checkBox.setTextColor(Color.BLACK);
                } else {
                    binding.checkBox.setTextColor(Color.RED);
                }
            });

            switch(getFirstTimeRun()) {
                case 0:
                    Log.d("appPreferences", "Es la primera vez!");
                    break;
                case 1:
                    Log.d("appPreferences", "ya has iniciado la app alguna vez");

                    break;
                case 2:
                    Log.d("appPreferences", "es una versi??n nueva");
                    binding.constraintWelcome.setVisibility(View.VISIBLE);
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
            startActivity(new Intent(this, SplashScreen.class));
        }

    }

    private int getFirstTimeRun() {
        SharedPreferences sp = getSharedPreferences("Piazza", 0);
        int result, currentVersionCode = BuildConfig.VERSION_CODE;
        int lastVersionCode = sp.getInt("FIRSTTIMERUN", -1);
        if (lastVersionCode == -1) result = 0; else
            result = (lastVersionCode == currentVersionCode) ? 1 : 2;
        sp.edit().putInt("FIRSTTIMERUN", currentVersionCode).apply();
        return result;
    }

    public static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ajustes_menu_employee, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.logout:
                logOut();
                break;
            case R.id.info:
                binding.checkBox.setVisibility(View.INVISIBLE);
                binding.checkBox.setChecked(true);
                binding.constraintWelcome.setVisibility(View.VISIBLE);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void logOut() {
        FirebaseAuth.getInstance().signOut();
        new Handler(Looper.getMainLooper()).postDelayed( this::guardarDatosGlobalesJugador, 7000);
        Intent intent = new Intent(this, AuthActivity.class);
        startActivity(intent);

        binding = null;

        finish();
    }

    private void guardarDatosGlobalesJugador() {
        guardarDatosGlobalesJugador(new Usuario());
    }


    @Override
    protected void onRestart() {
        super.onRestart();

        startActivity(new Intent(this, SplashScreen.class));

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
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