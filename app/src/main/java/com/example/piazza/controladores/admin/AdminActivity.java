package com.example.piazza.controladores.admin;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.piazza.controladores.auth.AuthActivity;
import com.example.piazza.fireBase.session.AuthUserSession;
import com.example.testauth.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

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

        setTheme(R.style.Theme_TestAuth);

        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = findViewById(R.id.my_toolbar_admin);
        ImageView logo = findViewById(R.id.yourlogo);

        StorageReference storageRef = STORAGE.getReferenceFromUrl("gs://testauth-f5eb4.appspot.com/" + userAuth.getEmpresa() + ".png");
        storageRef.getBytes(1024 * 1024)
                .addOnSuccessListener(bytes -> logo.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length)));

/*
        logo.setImageDrawable(getResources().getDrawable(R.drawable.mipmap_piazza));
*/
        /*toolbar.setBackgroundColor(getResources().getColor(R.color.start_btn));*/


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
            System.out.println("ADMIN");
        } else if (userAuth.getRol().equals("superadmin")) {
            System.out.println("SUPER");
            appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.navigation_treballadors, R.id.navigation_alta_administradors, R.id.navigation_reports)
                    .build();
            binding.navView.inflateMenu(R.menu.bottom_nav_menu_super);
        }

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_admin);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ajustes_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.ajustesLogOut:
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