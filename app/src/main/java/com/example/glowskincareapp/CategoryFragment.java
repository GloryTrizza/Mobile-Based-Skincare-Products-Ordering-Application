package com.example.glowskincareapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

public class CategoryFragment extends Fragment {


    ConstraintLayout expand1,expand2,expand3,expand4,expand5,expand6;
    ImageView search;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category,container,false);

        expand1= view.findViewById(R.id.cleansersCategory);
        expand2= view.findViewById(R.id.moisturisersCategory);
        expand3= view.findViewById(R.id.sunscreensCategory);
        expand4= view.findViewById(R.id.serumsCategory);
        expand5= view.findViewById(R.id.tonersCategory);
        expand6= view.findViewById(R.id.masksCategory);
        search= view.findViewById(R.id.searchIconInCategories);

        search.setOnClickListener(view1 -> {
            View dialogView = LayoutInflater.from(view1.getContext()).inflate(R.layout.searchdialog, null);
            Button ok= dialogView.findViewById(R.id.searchProductButton);
            Button exit= dialogView.findViewById(R.id.searchProductCancel);
            EditText textQuery= dialogView.findViewById(R.id.searchProductInput);
            AlertDialog.Builder builder = new AlertDialog.Builder(view1.getContext());
            builder.setView(dialogView);
            AlertDialog dialog=builder.create();
            dialog.setOnShowListener(dialogInterface -> {
                exit.setOnClickListener(view11 -> dialog.dismiss());
                ok.setOnClickListener(view11 -> {
                    String text=textQuery.getText().toString().trim();
                    if(TextUtils.isEmpty(text)){
                        Toast.makeText(view11.getContext(), "Search Query cannot be Empty", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Intent okIntent=new Intent(view11.getContext(),ProductListDisplay.class);
                        okIntent.putExtra("category",6);
                        okIntent.putExtra("searchQuery",text);
                        dialog.dismiss();
                        startActivity(okIntent);
                    }
                });
            });
            dialog.show();
        });

        expand1.setOnClickListener(view17 -> {
            Intent okIntent=new Intent(view17.getContext(),ProductListDisplay.class);
            okIntent.putExtra("category",0);
            startActivity(okIntent);
        });

        expand2.setOnClickListener(view16 -> {
            Intent okIntent=new Intent(view16.getContext(),ProductListDisplay.class);
            okIntent.putExtra("category",1);
            startActivity(okIntent);
        });
        expand3.setOnClickListener(view15 -> {
            Intent okIntent=new Intent(view15.getContext(),ProductListDisplay.class);
            okIntent.putExtra("category",2);
            startActivity(okIntent);
        });
        expand4.setOnClickListener(view14 -> {
            Intent okIntent=new Intent(view14.getContext(),ProductListDisplay.class);
            okIntent.putExtra("category",3);
            startActivity(okIntent);
        });
        expand5.setOnClickListener(view13 -> {
            Intent okIntent=new Intent(view13.getContext(),ProductListDisplay.class);
            okIntent.putExtra("category",4);
            startActivity(okIntent);
        });
        expand6.setOnClickListener(view12 -> {
            Intent okIntent=new Intent(view12.getContext(),ProductListDisplay.class);
            okIntent.putExtra("category",5);
            startActivity(okIntent);
        });
        return view;
    }
}