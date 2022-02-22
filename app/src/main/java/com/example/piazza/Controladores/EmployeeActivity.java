package com.example.piazza.Controladores;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.piazza.Classes.Usuario;
import com.example.piazza.Modelo.UsuarioModelo;
import com.example.testauth.R;

import com.example.testauth.databinding.ActivityEmployeeBinding;


import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class EmployeeActivity extends AppCompatActivity {

    private ActivityEmployeeBinding binding;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme(R.style.AppTheme);

        binding = ActivityEmployeeBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_introduir_hores, R.id.navigation_historial)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);


    }


}