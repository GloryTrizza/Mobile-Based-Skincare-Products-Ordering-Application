package com.example.glowskincareapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class ProfileFragment extends Fragment {

    Button log_profile,logout;
    LinearLayout editproflay,cartlay,wishlitlay,orderlistlay,addproductlay,orderrequestlay,allproductlay;
    TextView contactus, headline, faq;
    FirebaseFirestore firestore;
    Boolean isSeller;
    DocumentSnapshot currentUserDataDoc;
    ShapeableImageView profileIcon;
    Context mContext;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        log_profile = view.findViewById(R.id.login_btn);
        contactus= view.findViewById(R.id.contactus);
        editproflay = view.findViewById(R.id.editnamelay);
        cartlay= view.findViewById(R.id.cartlay);
        wishlitlay= view.findViewById(R.id.wishlistlay);
        orderlistlay= view.findViewById(R.id.orderlistlay);
        logout= view.findViewById(R.id.logoutbtn);
        headline= view.findViewById(R.id.headlineOfProfileFragment);
        profileIcon= view.findViewById(R.id.ProfileFragmentProfileIcon);
        addproductlay= view.findViewById(R.id.addProductLay);
        orderrequestlay= view.findViewById(R.id.viewOrderRequestLay);
        allproductlay= view.findViewById(R.id.allProductsLay);
        mContext=view.getContext();
        faq =view.findViewById(R.id.userguide);

        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
        {
            if(currentUserDataDoc==null){
                retrieveData();
            }
            else{
                setData();
            }
            log_profile.setVisibility(View.GONE);
            editproflay.setVisibility(View.VISIBLE);
            logout.setVisibility(View.VISIBLE);
        }
        else{
            headline.setText("Welcome to GlowSkinCare");
        }

        log_profile.setOnClickListener(view1 -> {
            Intent intent10 = new Intent(mContext,log_sign_options.class);
            startActivity(intent10);
            ( requireActivity()).overridePendingTransition(0,0);

        });
        editproflay.setOnClickListener(view16 -> {
            Intent intent10 = new Intent(mContext,EditProfilePage.class);
            intent10.putExtra("isSeller",isSeller);
            if(isSeller)
                intent10.putExtra("Picture",currentUserDataDoc.getString("BrandLogo"));
            else
                intent10.putExtra("Picture",currentUserDataDoc.getString("ProfilePic"));
            startActivity(intent10);
            (requireActivity()).overridePendingTransition(0,0);
        });
        wishlitlay.setOnClickListener(view17 -> {
            Intent intent10 = new Intent(mContext,CustomerWishlist.class);
            startActivity(intent10);
            (requireActivity()).overridePendingTransition(0,0);

        });
        cartlay.setOnClickListener(view18 -> {
            Intent okIntent=new Intent(mContext,CustomerCart.class);
            startActivity(okIntent);
            (requireActivity()).overridePendingTransition(0,0);

        });

        orderlistlay.setOnClickListener(view19 -> {
            Intent okIntent=new Intent(mContext,OrderList.class);
            startActivity(okIntent);
            (requireActivity()).overridePendingTransition(0,0);
        });

        contactus.setOnClickListener(view15 -> {
            Intent intent10 = new Intent(mContext,contactus.class);
            startActivity(intent10);
            (requireActivity()).overridePendingTransition(0,0);
        });
        faq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent10 = new Intent(mContext, UserGuide.class);
                startActivity(intent10);
                ((Activity)getActivity()).overridePendingTransition(0,0);

            }
        });
        logout.setOnClickListener(view12 -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(getActivity(), "Logout Success", Toast.LENGTH_SHORT).show();
            new Handler().post(() -> {
                Intent intent = requireActivity().getIntent();
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_NO_ANIMATION);

                requireActivity().overridePendingTransition(0, 0);
                requireActivity().finish();
                requireActivity().overridePendingTransition(0, 0);
                startActivity(intent);
            });
        });
        orderrequestlay.setOnClickListener(view13 -> {
            Intent okIntent = new Intent(mContext,OrderRequests.class);
            okIntent.putExtra("isSeller",isSeller);
            startActivity(okIntent);
            ( requireActivity()).overridePendingTransition(0,0);
        });
        return view;
    }
    public void retrieveData(){
        FirebaseUser currentUser=FirebaseAuth.getInstance().getCurrentUser();
        firestore=FirebaseFirestore.getInstance();
        assert currentUser != null;
        firestore.collection("Users").document(currentUser.getUid()).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                currentUserDataDoc=task.getResult();
                setData();
            }
        }).addOnFailureListener(e -> {
            headline.setText("Welcome to GlowSkinCare");
            Log.w("N/W","Data Fetch Failed",e);
        });
    }
    void setData(){
        if(currentUserDataDoc.exists()){
            isSeller = currentUserDataDoc.getBoolean("isSeller");
            if(Boolean.TRUE.equals(isSeller)){
                headline.setText("Welcome, "+currentUserDataDoc.getString("BrandName"));
                Bitmap logo=ImageStringOperation.getImage(currentUserDataDoc.getString("BrandLogo"));
                if(logo!=null){
                    profileIcon.setImageBitmap(logo);
                }
                addproductlay.setVisibility(View.VISIBLE);
                orderrequestlay.setVisibility(View.VISIBLE);
                allproductlay.setVisibility(View.VISIBLE);
            }
            else{
                headline.setText("Welcome, "+currentUserDataDoc.getString("FirstName"));
                Bitmap Logo=ImageStringOperation.getImage(currentUserDataDoc.getString("ProfilePic"));
                if(Logo!=null)
                    profileIcon.setImageBitmap(Logo);
                cartlay.setVisibility(View.VISIBLE);
                wishlitlay.setVisibility(View.VISIBLE);
                orderlistlay.setVisibility(View.VISIBLE);
            }
        }
        else{
            headline.setText("Welcome to GlowSkinCare");
            editproflay.setVisibility(View.VISIBLE);
            logout.setVisibility(View.VISIBLE);
        }
        addproductlay.setOnClickListener(view -> {
            Intent okIntent=new Intent(mContext,AddProduct.class);
            startActivity(okIntent);
            (requireActivity()).overridePendingTransition(0,0);
        });
        allproductlay.setOnClickListener(view -> {
            Intent okIntent=new Intent(mContext,ProductListDisplay.class);
            okIntent.putExtra("category",7);
            okIntent.putExtra("isSeller",true);
            startActivity(okIntent);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
            retrieveData();
    }
}
