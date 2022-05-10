package com.example.piazza.controladores.employee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.example.piazza.controladores.auth.AuthActivity;
import com.example.piazza.fireBase.session.AuthUserSession;
import com.example.testauth.R;

import com.example.testauth.databinding.ActivityEmployeeBinding;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.StorageReference;

import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class EmployeeActivity extends AppCompatActivity implements AuthUserSession {

    private ActivityEmployeeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme(R.style.Theme_TestAuth);

        binding = ActivityEmployeeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        ImageView logo = findViewById(R.id.yourlogo);

        StorageReference storageRef = STORAGE.getReferenceFromUrl("gs://testauth-f5eb4.appspot.com/" + userAuth.getEmpresa() + ".png");
        storageRef.getBytes(1024 * 1024)
                .addOnSuccessListener(bytes -> logo.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length)));

        /*toolbar.setBackgroundColor(getResources().getColor(R.color.start_btn));*/

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_introduir_hores, R.id.navigation_historial, R.id.navigation_perfil)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
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
            case R.id.introduirHores:
                logOut();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void logOut() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, AuthActivity.class);
        startActivity(intent);
        finish();
    }

}