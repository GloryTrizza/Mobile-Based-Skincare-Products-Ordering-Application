package com.example.glowskincareapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.glowskincareapp.Adapter.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PaymentPage extends AppCompatActivity {

    Product product;
    DocumentSnapshot sellerInfo;
    FirebaseUser currentUser;
    FirebaseFirestore firestore;
    ImageView productImage;
    TextView pname,pprice,sname, smobile;
    Button viewSS,placeOrder;
    EditText transaction,addressInput;
    Bitmap paymentSS;
    String transactionNo,address;
    private static final int CAMERA_PERMISSION_CODE=105;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_page);

        currentUser= FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser==null){
            finish();
            return;
        }

        product=(Product) getIntent().getSerializableExtra("product");
        productImage= findViewById(R.id.checkoutPageProductImage);
        pname= findViewById(R.id.checkoutPageProductName);
        pprice= findViewById(R.id.checkoutPageProductPrice);
        sname= findViewById(R.id.checkoutPageSellerName);
        smobile = findViewById(R.id.checkoutPageSellerMobileNo);
        placeOrder= findViewById(R.id.checkoutPagePlaceOrderButton);
        addressInput= findViewById(R.id.checkoutPageAddressInput);
        transaction= findViewById(R.id.checkoutPageMobileNumber);
        firestore=FirebaseFirestore.getInstance();

        firestore.collection("Users").document(product.getProductSellerUid()).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                sellerInfo=task.getResult();
                if(sellerInfo.exists()){
                    smobile.setText(sellerInfo.getString("BrandBankAcc"));
                }
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(PaymentPage.this, "Please try again.", Toast.LENGTH_SHORT).show();
            finish();
        });

        productImage.setImageBitmap(ImageStringOperation.getImage(product.getProductImage()));
        pname.setText(product.getProductName());
        pprice.setText("Total Price: Kshs. "+product.getProductPrice());
        sname.setText(product.getProductSeller());
        placeOrder.setOnClickListener(view -> {
            transactionNo=transaction.getText().toString().trim();
            address=addressInput.getText().toString().trim();
            if(TextUtils.isEmpty(transactionNo)){
                Toast.makeText(PaymentPage.this, "Enter Mobile Number!", Toast.LENGTH_SHORT).show();
            }
            else if(TextUtils.isEmpty(address)){
                Toast.makeText(PaymentPage.this, "Enter the Address", Toast.LENGTH_SHORT).show();
            }

            else{
                Map<String,Object> map=new HashMap<>();
                map.put("ProductId",product.getProductId());
                map.put("ProductName",product.getProductName());
                map.put("ProductPrice",product.getProductPrice());
                map.put("ProductImage",product.getProductImage());
                map.put("SellerId",product.getProductSellerUid());
                map.put("Status","Pending");
                map.put("OrderDate",new Date());
                map.put("TransactionNo",transactionNo);
                map.put("DeliveryAddress",address);
                map.put("CustomerId",currentUser.getUid());
                firestore.collection("Orders").document(transactionNo).set(map).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(PaymentPage.this, "Order Placed Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                        firestore.collection("Users").document(currentUser.getUid()).collection("Cart").document(product.getProductId()).delete();
                    }
                    else{
                        Toast.makeText(PaymentPage.this, "Please try Again", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> Toast.makeText(PaymentPage.this, "Please try Again", Toast.LENGTH_SHORT).show());
            }
        });
    }


    ActivityResultLauncher<Intent> launchSomeActivity = registerForActivityResult( new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode()
                        == Activity.RESULT_OK) {
                    Intent data = result.getData();

                    if (data != null
                            && data.getData() != null) {
                        Uri selectedImageUri = data.getData();
                        try {
                            paymentSS = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                            viewSS.setEnabled(true);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);

    }
}