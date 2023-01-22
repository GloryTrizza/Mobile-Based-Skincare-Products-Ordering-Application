package com.example.glowskincareapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class signupcustomer extends AppCompatActivity {

    private EditText customerconfpass, customerpass, firstName, lastName, mobile, email;
    private FirebaseFirestore firestore ;
    private FirebaseAuth firebaseAuth;
    private String gender;
    private RadioButton male, female;
    private LottieAnimationView loading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signupcustomer);

        firstName = findViewById(R.id.cfirstname);
        lastName = findViewById(R.id.clastname);
        mobile = findViewById(R.id.cmobile);
        email = findViewById(R.id.cemailid);
        customerpass = findViewById(R.id.createPassword);
        customerconfpass = findViewById(R.id.confirmPassword);
        Button btnsignup = findViewById(R.id.createAccountForCustomer);
        female = findViewById(R.id.femaleGender);
        male= findViewById(R.id.maleGender);
        loading= findViewById(R.id.loadingAnimationOnCustomer);

        Intent okIntent=new Intent(this,MainActivity.class);
        btnsignup.setOnClickListener(view -> {
            loading.setVisibility(View.VISIBLE);
            String firstname = firstName.getText().toString().trim();
            String lastname = lastName.getText().toString().trim();
            String mobile = this.mobile.getText().toString().trim();
            String customeremail = email.getText().toString().trim();
            String pass = customerpass.getText().toString().trim();
            String confirmpass = customerconfpass.getText().toString().trim();
            if(!male.isChecked() && !female.isChecked()){
                Toast.makeText(signupcustomer.this, "Error: Select your Gender", Toast.LENGTH_SHORT).show();
                loading.setVisibility(View.INVISIBLE);
            }
            else if(male.isChecked()){
                gender="Male";
            }
            else{
                gender="Female";
            }

            if(TextUtils.isEmpty(firstname)){
                Toast.makeText(signupcustomer.this, "Error: Please enter your first name!", Toast.LENGTH_SHORT).show();
                loading.setVisibility(View.INVISIBLE);
            }
            else if(TextUtils.isEmpty(lastname)) {
                Toast.makeText(signupcustomer.this, "Error: Please enter your last name!", Toast.LENGTH_SHORT).show();
                loading.setVisibility(View.INVISIBLE);
            }
            else if(TextUtils.isEmpty(mobile)) {
                Toast.makeText(signupcustomer.this, "Error: Please enter your phone number!", Toast.LENGTH_SHORT).show();
                loading.setVisibility(View.INVISIBLE);
            }
            else if(TextUtils.isEmpty(customeremail)) {
                Toast.makeText(signupcustomer.this, "Error: Please enter your email address!", Toast.LENGTH_SHORT).show();
                loading.setVisibility(View.INVISIBLE);
            }
            else if(TextUtils.isEmpty(pass)) {
                Toast.makeText(signupcustomer.this, "Error: Please enter a password!", Toast.LENGTH_SHORT).show();
                loading.setVisibility(View.INVISIBLE);
            }
            else if(TextUtils.isEmpty(confirmpass)) {
                Toast.makeText(signupcustomer.this, "Error: Please confirm your password!", Toast.LENGTH_SHORT).show();
                loading.setVisibility(View.INVISIBLE);
            }
            else if(!pass.equals(confirmpass)){
                Toast.makeText(signupcustomer.this, "Error: The passwords do not match!", Toast.LENGTH_SHORT).show();
                loading.setVisibility(View.INVISIBLE);
            }
            else{
                firebaseAuth = FirebaseAuth.getInstance();
                if(firebaseAuth.getCurrentUser()==null){
                    firebaseAuth.createUserWithEmailAndPassword(customeremail,pass).addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            FirebaseUser currentUser=firebaseAuth.getCurrentUser();
                            Log.d("","Account Created Successfully: "+currentUser.getEmail());
                            Map<String,Object> userInfo=new HashMap<>();
                            userInfo.put("Email",currentUser.getEmail());
                            userInfo.put("isSeller",false);
                            userInfo.put("FirstName",firstname);
                            userInfo.put("LastName",lastname);
                            userInfo.put("MobileNumber",mobile);
                            userInfo.put("Gender",gender);
                            userInfo.put("ProfilePic",null);
                            firestore=FirebaseFirestore.getInstance();
                            firestore.collection("Users").document(currentUser.getUid()).set(userInfo).addOnSuccessListener(unused -> {
                                Log.d("Done","Data Entered Successfully Uid: "+currentUser.getUid());
                                Toast.makeText(signupcustomer.this, "Account Created and Logged In", Toast.LENGTH_SHORT).show();
                            })
                                    .addOnFailureListener(e -> {
                                        loading.setVisibility(View.INVISIBLE);
                                        Log.w("Failed","Data Creation Failed",e);
                                        Toast.makeText(signupcustomer.this, "To Update Information Visit Edit Profile Page", Toast.LENGTH_SHORT).show();
                                    });
                            finishAffinity();
                            startActivity(okIntent);
                            finish();
                        }else{
                            Log.w("","Account cannot be created: ", task.getException());
                            Toast.makeText(signupcustomer.this, "Sign Up Failed: "+ Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                            loading.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
        });
    }
}