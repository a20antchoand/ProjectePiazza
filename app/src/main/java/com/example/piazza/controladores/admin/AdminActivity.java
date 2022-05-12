package com.example.piazza.controladores.admin;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.piazza.controladores.auth.AuthActivity;
import com.example.piazza.controladores.auth.SplashScreen;
import com.example.piazza.controladores.employee.EmployeeActivity;
import com.example.piazza.fireBase.session.AuthUserSession;
import com.example.testauth.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.testauth.databinding.ActivityAdminBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.StorageReference;

public class AdminActivity extends AppCompatActivity implements AuthUserSession {

    private ActivityAdminBinding binding;

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
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Hola " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ajustes_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.introduirHores:
                logOut();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void logOut() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, AuthActivity.class));
        finish();
    }

}