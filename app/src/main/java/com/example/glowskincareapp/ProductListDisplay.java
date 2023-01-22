package com.example.glowskincareapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.glowskincareapp.Adapter.Product;
import com.example.glowskincareapp.Adapter.ProductListRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ProductListDisplay extends AppCompatActivity {

    TextView headline,subHeadline;
    RecyclerView recyclerView;
    Intent callingIntent;
    String query;
    FirebaseFirestore firestore;
    FirebaseUser currentUser;
    QuerySnapshot allDocuments;
    ArrayList<Product> productListArray;
    ProductListRecyclerAdapter productListAdapter;
    TextView noProductFound;
    Boolean isSeller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list_display);

        callingIntent=getIntent();
        headline= findViewById(R.id.productListHeadline);
        subHeadline= findViewById(R.id.productListSubHeadline);
        noProductFound= findViewById(R.id.EmptyProductListInProductList);

        recyclerView= findViewById(R.id.productListRecyclerView);

        currentUser=FirebaseAuth.getInstance().getCurrentUser();
        firestore=FirebaseFirestore.getInstance();

        if(!CommonUtility.isInternetAvailable()){Toast.makeText(this,"Turn on Internet Connection",Toast.LENGTH_SHORT).show();}

        int num=callingIntent.getIntExtra("category",-1);
        switch(num) {
            case 0:
                headline.setText("Cleansers");
                query = "Cleansers";
                break;
            case 1:
                headline.setText("Moisturisers");
                query = "Moisturisers";
                break;
            case 2:
                headline.setText("Sunscreens");
                query = "Sunscreens";
                break;
            case 3:
                headline.setText("Serums");
                query = "Serums";
                break;
            case 4:
                headline.setText("Toners");
                query = "Toners";
                break;
            case 5:
                headline.setText("Masks");
                query = "Masks";
                break;
            case 6:
                headline.setText("Search Result");
                query = callingIntent.getStringExtra("searchQuery");
                subHeadline.setText("Search Result for " + query);
//                query = "search";
                break;
            case 7:
                headline.setText("Product List");
                String uid=callingIntent.getStringExtra("currentUserUid");
                isSeller=callingIntent.getBooleanExtra("isSeller",false);
                if(uid!=null){
                    query=uid;
                }
                else
                    query= currentUser.getUid();
                break;
            case 8:
                headline.setText("Most Popular");
                break;
            case 9:
                headline.setText("New Arrivals");
                break;
            default:
                finish();
                return;
        }
        if(callingIntent.getStringExtra("subHeading")!=null){
            subHeadline.setText(callingIntent.getStringExtra("subHeading"));
            subHeadline.setVisibility(View.VISIBLE);
        }

        productListArray= new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        if(num==6){
            firestore.collection("Products").get().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot doc:task.getResult()){
                        Product p=doc.toObject(Product.class);
                        p.setProductId(doc.getId());
                        if(p.getProductName().toLowerCase().contains(query.toLowerCase()) || p.getProductDescription().toLowerCase().contains(query.toLowerCase()))
                            productListArray.add(p);
                    }
                    if(productListArray.isEmpty()){
                        noProductFound.setVisibility(View.VISIBLE);
                    }
                    else{
                        subHeadline.setVisibility(View.VISIBLE);
                        productListAdapter=new ProductListRecyclerAdapter(productListArray,getApplicationContext(),false);
                        recyclerView.setAdapter(productListAdapter);
                    }
                }
                else{
                    Toast.makeText(ProductListDisplay.this, "Data Error", Toast.LENGTH_SHORT).show();
                    noProductFound.setVisibility(View.VISIBLE);
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(ProductListDisplay.this, "Data Fetch Error", Toast.LENGTH_SHORT).show();
                noProductFound.setVisibility(View.VISIBLE);
            });
        }
        else if(num==7){
            firestore.collection("Products").whereEqualTo("ProductSellerUid",query).get().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    allDocuments=task.getResult();
                    for(QueryDocumentSnapshot doc:allDocuments){
                        Log.d("Data:",doc.getData()+" ");
                        Product p=doc.toObject(Product.class);
                        p.setProductId(doc.getId());
                        productListArray.add(p);
                    }
                    if(productListArray.isEmpty()){
                        noProductFound.setVisibility(View.VISIBLE);
                    }
                    productListAdapter=new ProductListRecyclerAdapter(productListArray,getApplicationContext(),isSeller);
                    recyclerView.setAdapter(productListAdapter);
                }
                else{
                    Toast.makeText(ProductListDisplay.this, "Data Error", Toast.LENGTH_SHORT).show();
                    noProductFound.setVisibility(View.VISIBLE);
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(ProductListDisplay.this, "Data Fetch Error", Toast.LENGTH_SHORT).show();
                noProductFound.setVisibility(View.VISIBLE);
            });
        }
        else if(num==8){
            firestore.collection("Products").orderBy("ProductSellCount", Query.Direction.DESCENDING).limit(10).get().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    allDocuments=task.getResult();
                    for(QueryDocumentSnapshot doc:allDocuments){
                        Product p=doc.toObject(Product.class);
                        p.setProductId(doc.getId());
                        productListArray.add(p);
                    }
                    if(productListArray.isEmpty()){
                        noProductFound.setVisibility(View.VISIBLE);
                    }
                    productListAdapter=new ProductListRecyclerAdapter(productListArray,getApplicationContext(),false);
                    recyclerView.setAdapter(productListAdapter);
                }
                else{
                    Toast.makeText(getApplicationContext(), "Data Error", Toast.LENGTH_SHORT).show();
                    noProductFound.setVisibility(View.VISIBLE);
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(getApplicationContext(), "Product Fetch Failed", Toast.LENGTH_SHORT).show();
                noProductFound.setVisibility(View.VISIBLE);
            });
        }
        else if(num==9){
            firestore.collection("Products").orderBy("ProductAddedDate", Query.Direction.ASCENDING).limit(10).get().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    allDocuments=task.getResult();
                    for(QueryDocumentSnapshot doc:allDocuments){
                        Product p=doc.toObject(Product.class);
                        p.setProductId(doc.getId());
                        productListArray.add(p);
                    }
                    if(productListArray.isEmpty()){
                        noProductFound.setVisibility(View.VISIBLE);
                    }
                    productListAdapter=new ProductListRecyclerAdapter(productListArray,getApplicationContext(),false);
                    recyclerView.setAdapter(productListAdapter);
                }
                else{
                    Toast.makeText(getApplicationContext(), "Data Error", Toast.LENGTH_SHORT).show();
                    noProductFound.setVisibility(View.VISIBLE);
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(getApplicationContext(), "Product Fetch Failed", Toast.LENGTH_SHORT).show();
                noProductFound.setVisibility(View.VISIBLE);
            });
        }
        else{
            firestore.collection("Products").whereEqualTo("ProductCategory",query).get().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    allDocuments=task.getResult();
                    for(QueryDocumentSnapshot doc:allDocuments){
                        Product p=doc.toObject(Product.class);
                        p.setProductId(doc.getId());
                        productListArray.add(p);
                    }
                    if(productListArray.isEmpty()){
                        noProductFound.setVisibility(View.VISIBLE);
                    }
                    productListAdapter=new ProductListRecyclerAdapter(productListArray,getApplicationContext(),false);
                    recyclerView.setAdapter(productListAdapter);
                }
                else{
                    Toast.makeText(ProductListDisplay.this, "Data Error", Toast.LENGTH_SHORT).show();
                    noProductFound.setVisibility(View.VISIBLE);
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(ProductListDisplay.this, "Data Fetch Error", Toast.LENGTH_SHORT).show();
                noProductFound.setVisibility(View.VISIBLE);
            });
        }

    }

}
