package com.example.glowskincareapp;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.glowskincareapp.Adapter.CartProductListRecyclerAdapter;
import com.example.glowskincareapp.Adapter.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class CustomerCart extends AppCompatActivity {

    RecyclerView cartList;
    TextView empty;
    FirebaseUser currentUser;
    FirebaseFirestore firestore;
    QuerySnapshot allProductsInCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_cart);

        currentUser= FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser==null){
            finish();
            return;
        }
        cartList= findViewById(R.id.cartProductRecycler);
        empty= findViewById(R.id.emptyProductListInCart);
        firestore=FirebaseFirestore.getInstance();

        ArrayList<Product> cartlistArray=new ArrayList<>();
        firestore.collection("Users").document(currentUser.getUid()).collection("Cart").get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                allProductsInCart=task.getResult();
                for(QueryDocumentSnapshot doc:allProductsInCart){
                    cartlistArray.add(doc.toObject(Product.class));
                }
                cartList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                if(cartlistArray.isEmpty()){
                    empty.setVisibility(View.VISIBLE);
                }
                else{
                    CartProductListRecyclerAdapter adapter=new CartProductListRecyclerAdapter(cartlistArray,getApplicationContext());
                    cartList.setAdapter(adapter);
                }
            }
            else{
                empty.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(e -> empty.setVisibility(View.VISIBLE));
    }
}