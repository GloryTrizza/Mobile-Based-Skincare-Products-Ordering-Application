package com.example.glowskincareapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.glowskincareapp.Adapter.Order;
import com.example.glowskincareapp.Adapter.OrderRequestRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class OrderRequests extends AppCompatActivity {

    FirebaseUser currentUser;
    RecyclerView recycler;
    TextView empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_requests);

        Intent callingIntent=getIntent();
        currentUser= FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser==null || !callingIntent.getBooleanExtra("isSeller",false)){
            finish();
            return;
        }

        recycler= findViewById(R.id.orderRequestRecycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        empty= findViewById(R.id.orderRequestEmpty);

        ArrayList<Order> orderList= new ArrayList<>();
        FirebaseFirestore.getInstance().collection("Orders").whereEqualTo("SellerId",currentUser.getUid()).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                for(QueryDocumentSnapshot doc:task.getResult()){
                    orderList.add(doc.toObject(Order.class));
                }
                if(orderList.size()==0){
                    empty.setVisibility(View.VISIBLE);
                }
                else{
                    OrderRequestRecyclerAdapter adapter=new OrderRequestRecyclerAdapter(orderList,OrderRequests.this);
                    recycler.setAdapter(adapter);
                }
            }
            else{
                empty.setVisibility(View.VISIBLE);

            }
        }).addOnFailureListener(e -> empty.setVisibility(View.VISIBLE));

    }
}