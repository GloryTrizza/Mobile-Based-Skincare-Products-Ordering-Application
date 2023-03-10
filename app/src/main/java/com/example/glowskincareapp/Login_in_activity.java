package com.example.glowskincareapp;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;


public class Login_in_activity extends AppCompatActivity {

    Button login;
    EditText username,password;
    private FirebaseAuth firebaseAuth;
    private LottieAnimationView loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_in_activity);

        login = findViewById(R.id.loginButtonFromLoginPage);
        username= findViewById(R.id.login_id);
        password= findViewById(R.id.login_pass);
        loading= findViewById(R.id.loadingAnimationOnLogin);

        Intent okIntent = new Intent(Login_in_activity.this,MainActivity.class);
        login.setOnClickListener(view -> {
            loading.setVisibility(View.VISIBLE);
            String uname=username.getText().toString().trim();
            String pass=password.getText().toString().trim();

            if(TextUtils.isEmpty(uname)){
                Toast.makeText(getApplicationContext(),"Error: Username cannot be Empty",Toast.LENGTH_SHORT).show();
                loading.setVisibility(View.INVISIBLE);
            }
            else if(TextUtils.isEmpty(pass)){
                Toast.makeText(Login_in_activity.this, "Error: Password cannot be Empty", Toast.LENGTH_SHORT).show();
                loading.setVisibility(View.INVISIBLE);
            }
            else{
                firebaseAuth = FirebaseAuth.getInstance();
                if(firebaseAuth.getCurrentUser()==null){
                    firebaseAuth.signInWithEmailAndPassword(uname,pass).addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            Toast.makeText(Login_in_activity.this, "Sign in Success. Redirecting", Toast.LENGTH_SHORT).show();
                            Log.d("","Login Successful UID: "+firebaseAuth.getCurrentUser().getEmail());
                            finishAffinity();
                            startActivity(okIntent);
                        }
                        else{
                            Log.w("","Login Failure: ", task.getException());
                            loading.setVisibility(View.INVISIBLE);
                            password.setText("");
                            Toast.makeText(Login_in_activity.this, "Login Failed: "+ Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}











