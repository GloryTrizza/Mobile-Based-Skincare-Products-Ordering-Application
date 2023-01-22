package com.example.glowskincareapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;


public class log_sign_options extends AppCompatActivity {

    Button btncustomer, btnseller,loginBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_sign_options);

        loginBtn= findViewById(R.id.loginButton);
        btncustomer= findViewById(R.id.csignup);
        btnseller= findViewById(R.id.ssignup);

        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            Log.d("Info",FirebaseAuth.getInstance().getCurrentUser().getEmail());
            startActivity(new Intent(this,MainActivity.class));
            finish();
        }

//        Toast.makeText(log_sign_options.this, "Welcome", Toast.LENGTH_LONG).show();
        btncustomer.setOnClickListener(v -> {
            Intent i1 = new Intent(getApplicationContext(), signupcustomer.class);
            startActivity(i1);
            finish();
        });
        btnseller.setOnClickListener(v -> {
            Intent i2 = new Intent(getApplicationContext(), signupseller1.class);
            startActivity(i2);
            finish();
        });
        loginBtn.setOnClickListener(view -> {
            Intent i=new Intent(getApplicationContext(),Login_in_activity.class);
            startActivity(i);
            finish();
        });
    }

}