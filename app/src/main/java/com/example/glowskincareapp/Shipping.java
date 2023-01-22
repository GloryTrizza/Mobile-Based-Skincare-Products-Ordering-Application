package com.example.glowskincareapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.glowskincareapp.Adapter.Order;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;

public class Shipping extends AppCompatActivity {

    TextView name,status,desc,price,transaction,address,sellerName,sellerEmail,sellerInstagram,date;
    ImageView image,seller;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipping);

        name=findViewById(R.id.shippingOrderName);
        status= findViewById(R.id.shippingOrderStatus);
        desc= findViewById(R.id.shippingOrderDesc);
        image= findViewById(R.id.shippingOrderImage);
        price= findViewById(R.id.shippinOrderPrice);
        transaction= findViewById(R.id.shippingPageTransactionNo);
        address= findViewById(R.id.shippingPageAddress);
        sellerName= findViewById(R.id.shippingPageSellerName);
        sellerEmail= findViewById(R.id.shippingPageSellerEmail);
        sellerInstagram= findViewById(R.id.shippingPageSellerInstagram);
        seller= findViewById(R.id.shippingPageSellerPhoto);
        date= findViewById(R.id.shippingPageOrderDate);

        Intent callingIntent=getIntent();
        Order order=(Order) callingIntent.getSerializableExtra("order");
        String s=order.getStatus();
        name.setText(order.getProductName());
        status.setText("Status: "+ s);
        image.setImageBitmap(ImageStringOperation.getImage(order.getProductImage()));
        price.setText("Price: Kshs. " + order.getProductPrice());
        transaction.setText("Phone No: " + order.getTransactionNo());
        address.setText("Address: " + order.getDeliveryAddress());
        date.setText("Order Date: " + new SimpleDateFormat("dd/MM/yyyy").format(order.getOrderDate()));

        FirebaseFirestore.getInstance().collection("Users").document(order.getSellerId()).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                DocumentSnapshot doc=task.getResult();
                if(doc.exists()){
                    seller.setImageBitmap(ImageStringOperation.getImage(doc.getString("BrandLogo")));
                    sellerName.setText(doc.getString("BrandName"));
                    sellerEmail.setText("Email: "+doc.getString("Email"));
                    sellerInstagram.setText("Instagram: "+doc.getString("BrandInstagram"));
                }
                else{
                    Toast.makeText(Shipping.this, "Seller Info Fetch Failed", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(e -> Toast.makeText(Shipping.this, "Seller Info Fetch Failed", Toast.LENGTH_SHORT).show());

        if(s.equals("Pending")){
            desc.setText("The order is yet to be checked by the seller. Kindly be patient.");
        }
        else if(s.equals("Approved")){
            desc.setText("The order is approved by the seller, the order will be delivered to your address in 2-3 working days.");
        }
        else{
            desc.setText("The order is rejected by seller. Possible reasons are insufficient supply or unreachable address. ");
        }

    }
}