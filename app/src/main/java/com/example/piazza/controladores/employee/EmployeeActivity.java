package com.example.piazza.controladores.employee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.piazza.classes.Usuario;
import com.example.testauth.R;

import com.example.testauth.databinding.ActivityEmployeeBinding;


import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.widget.ActionMenuView;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class EmployeeActivity extends AppCompatActivity {

    private ActivityEmployeeBinding binding;
    Usuario usuario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme(R.style.Theme_TestAuth);

        binding = ActivityEmployeeBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_introduir_hores, R.id.navigation_historial, R.id.navigation_perfil)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        setup();

    }

    public void setup () {


/*        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.ajustesLogOut) {
                    System.out.println("Logout");
                }

                System.out.println("Logout");

                return false;
            }
        });*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.ajustes_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.ajustesLogOut:
                System.out.println("LogOut");
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}