package com.example.glowskincareapp;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.glowskincareapp.Adapter.Order;
import com.example.glowskincareapp.Adapter.OrderListRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class OrderList extends AppCompatActivity {

    FirebaseUser currentUser;
    RecyclerView recycler;
    TextView empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        currentUser= FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser==null){
            finish();
            return;
        }

        recycler= findViewById(R.id.orderListRecycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        empty=findViewById(R.id.orderListEmpty);

        ArrayList<Order> orderList= new ArrayList<>();
        FirebaseFirestore.getInstance().collection("Orders").whereEqualTo("CustomerId",currentUser.getUid()).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                for(QueryDocumentSnapshot doc:task.getResult()){
                    orderList.add(doc.toObject(Order.class));
                }
                if(orderList.size()==0){
                    empty.setVisibility(View.VISIBLE);
                }
                else{
                    OrderListRecyclerAdapter adapter=new OrderListRecyclerAdapter(orderList,OrderList.this);
                    recycler.setAdapter(adapter);
                }
            }
            else{
                empty.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(e -> empty.setVisibility(View.VISIBLE));
    }
}