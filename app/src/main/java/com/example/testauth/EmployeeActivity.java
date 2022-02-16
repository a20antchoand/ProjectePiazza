package com.example.testauth;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmployeeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        ((TextView) findViewById(R.id.employeeUser)).setText(user.getEmail());



    }
}