package com.example.glowskincareapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class signupseller2 extends AppCompatActivity {

    private EditText brandOwnerName, brandInstagram, brandAddress, brandBankAcc;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private String brandName,brandMobile,brandEmail,brandPass,brandLogo,brandOwnerNameS,brandInstagramS,brandBankAccS,brandAddressS;
    private static final int INTERNET_PERMISSION_CODE=999;
    private LottieAnimationView loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signupseller2);

        loading= findViewById(R.id.loadingAnimationOnSeller);
        brandOwnerName = findViewById(R.id.brandOwnerName);
        brandInstagram = findViewById(R.id.brandInstagram);
        brandAddress = findViewById(R.id.brandRegAddress);
        brandBankAcc = findViewById(R.id.brandBankAccountNo);
        Button createButton = findViewById(R.id.createSellerAccountFinalButton);
        Button previousButton = findViewById(R.id.previousButtonSeller);

        Intent dataIntent=getIntent();
        brandName=dataIntent.getStringExtra("name");
        brandMobile=dataIntent.getStringExtra("mobile");
        brandEmail=dataIntent.getStringExtra("email");
        brandPass=dataIntent.getStringExtra("pass");
        brandLogo=dataIntent.getStringExtra("logo");



        previousButton.setOnClickListener(view -> finish());

        createButton.setOnClickListener(view -> {
            if(checkPermission(Manifest.permission.INTERNET,100)){
                loading.setVisibility(View.VISIBLE);
                createNewSellerAccountWithInfo();
            }
        });
    }

    private void createNewSellerAccountWithInfo(){

        brandOwnerNameS=brandOwnerName.getText().toString().trim();
        brandInstagramS=brandInstagram.getText().toString().trim();
        brandBankAccS=brandBankAcc.getText().toString().trim();
        brandAddressS=brandAddress.getText().toString().trim();
        Intent okIntent=new Intent(this,MainActivity.class);

        if(TextUtils.isEmpty(brandInstagramS)){
            Toast.makeText(signupseller2.this, "Error: Instagram Id must not be Empty", Toast.LENGTH_SHORT).show();
            loading.setVisibility(View.INVISIBLE);
        }
        else if(TextUtils.isEmpty(brandOwnerNameS)){
            Toast.makeText(signupseller2.this, "Error: Owner Name must not be Empty", Toast.LENGTH_SHORT).show();
            loading.setVisibility(View.INVISIBLE);
        }
        else if(TextUtils.isEmpty(brandBankAccS)){
            Toast.makeText(signupseller2.this, "Error: Enter Bank account number", Toast.LENGTH_SHORT).show();
            loading.setVisibility(View.INVISIBLE);
        }
        else if(TextUtils.isEmpty(brandAddressS)){
            Toast.makeText(signupseller2.this, "Error: Enter registered Address for brand", Toast.LENGTH_SHORT).show();
            loading.setVisibility(View.INVISIBLE);
        }
        else{
            firebaseAuth = FirebaseAuth.getInstance();
            if(firebaseAuth.getCurrentUser()==null){
                firebaseAuth.createUserWithEmailAndPassword(brandEmail,brandPass).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        FirebaseUser currentUser=firebaseAuth.getCurrentUser();
                        Log.d("","Brand Account Created Successfully: "+currentUser.getEmail());
                        Map<String,Object> userInfo=new HashMap<>();
                        userInfo.put("Email",currentUser.getEmail());
                        userInfo.put("isSeller",true);
                        userInfo.put("BrandName",brandName);
                        userInfo.put("BrandLogo",brandLogo);
                        userInfo.put("BrandMobileNumber",brandMobile);
                        userInfo.put("BrandOwnerName",brandOwnerNameS);
                        userInfo.put("BrandInstagram",brandInstagramS);
                        userInfo.put("BrandAddress",brandAddressS);
                        userInfo.put("BrandBankAcc",brandBankAccS);
                        firestore= FirebaseFirestore.getInstance();
                        firestore.collection("Users").document(currentUser.getUid()).set(userInfo)
                                .addOnSuccessListener(unused -> {
                                    Log.d("Done","Data Entered Successfully Uid: "+currentUser.getUid());
                                    Toast.makeText(signupseller2.this, "Account Created and Logged In", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Log.w("Failed","Data Creation Failed",e);
                                    Toast.makeText(signupseller2.this, "To Update Information Visit Edit Profile Page", Toast.LENGTH_SHORT).show();
                                });
                        finishAffinity();
                        startActivity(okIntent);
                        finish();
                    }
                    else{
                        if(CommonUtility.isInternetAvailable()){
                            Log.w("","Account Cannot be Created: ", task.getException());
                            Toast.makeText(signupseller2.this, "Sign Up Failed: "+ Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else{
                            Toast.makeText(signupseller2.this,"Please check you internet connection",Toast.LENGTH_SHORT).show();
                            loading.setVisibility(View.INVISIBLE);
                        }

                    }
                });
            }
        }
    }
    public boolean checkPermission(String permission, int requestCode)
    {
        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(getApplicationContext(), permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[] { permission }, requestCode);
        }
        else {
            return true;
        }
        return false;
    }
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);

        if (requestCode == INTERNET_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                createNewSellerAccountWithInfo();
            } else {
                Toast.makeText(this, "Error: You need to grant INTERNET permission to Create Account", Toast.LENGTH_SHORT).show();
            }
        }
    }
}