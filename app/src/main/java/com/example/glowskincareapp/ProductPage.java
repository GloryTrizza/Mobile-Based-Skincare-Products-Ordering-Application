package com.example.glowskincareapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.glowskincareapp.Adapter.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;

public class ProductPage extends AppCompatActivity {

    TextView pname,pcat,pdesc,pprice,pdate,sname;
    ImageView plogo,slogo;
    ConstraintLayout sellerContainer;
    Button cartButton,wishlistButton;
    Product product;
    FirebaseFirestore firestore;
    FirebaseUser currentUser;
    Boolean addedToWishlist,addedToCart;
    Button checkout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_page);
        pname= findViewById(R.id.productNameInProductPage);
        pcat= findViewById(R.id.productCategoryInProductPage);
        pdesc= findViewById(R.id.productDescInProductPage);
        pprice= findViewById(R.id.productPriceInProductPage);
        pdate= findViewById(R.id.productDateInProductPage);
        sname= findViewById(R.id.sellerNameInProductPage);
        sellerContainer= findViewById(R.id.brandInfoInProductPage);
        plogo= findViewById(R.id.productImageInProductPage);
        slogo= findViewById(R.id.sellerLogoInProductPage);
        cartButton= findViewById(R.id.addToCartButtonInProductPage);
        wishlistButton= findViewById(R.id.wishlistButtonInProductPage);
        checkout= findViewById(R.id.checkoutButtonInProductPage);

        Intent callingIntent=getIntent();
        product=(Product) callingIntent.getSerializableExtra("product");

        pname.setText(product.getProductName());
        pcat.setText(product.getProductCategory());
        pdesc.setText(product.getProductDescription());
        pprice.setText("Kshs. "+product.getProductPrice());
        pdate.setText(new SimpleDateFormat("dd/MM/yyyy").format(product.getProductAddedDate()));
        sname.setText(product.getProductSeller());
        plogo.setImageBitmap(ImageStringOperation.getImage(product.getProductImage()));
        slogo.setImageBitmap(ImageStringOperation.getImage(product.getProductSellerLogo()));

        sellerContainer.setOnClickListener(view -> {
            Intent okIntent=new Intent(ProductPage.this,ProductListDisplay.class);
            okIntent.putExtra("category",7);
            okIntent.putExtra("currentUserUid",product.getProductSellerUid());
            okIntent.putExtra("subHeading","All Products of " +product.getProductSeller());
            startActivity(okIntent);
        });
        currentUser= FirebaseAuth.getInstance().getCurrentUser();
        firestore=FirebaseFirestore.getInstance();
        if(currentUser!=null) {
            checkWishlist();
            checkCart();
        }
        else{
            addedToCart=false;
            addedToWishlist=false;
        }

        checkout.setOnClickListener(view -> {
            Intent okIntnet=new Intent(ProductPage.this,PaymentPage.class);
            okIntnet.putExtra("product",product);
            startActivity(okIntnet);
        });

        cartButton.setOnClickListener(view -> {
            if(!addedToCart) {
                if (currentUser == null) {
                    Toast.makeText(ProductPage.this, "Please Login First to Proceed", Toast.LENGTH_SHORT).show();
                } else {
                    firestore.collection("Users").document(currentUser.getUid()).collection("Cart").document(product.getProductId()).set(product).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(ProductPage.this, "Product Added to Cart", Toast.LENGTH_SHORT).show();
                            addedToCart=true;
                            checkout.setVisibility(View.VISIBLE);
                            cartButton.setText("Remove from Cart");
                        } else {
                            Toast.makeText(ProductPage.this, "Failed to add to Cart", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(e -> Toast.makeText(ProductPage.this, "Failed to add to Cart", Toast.LENGTH_SHORT).show());
                }
            }
            else{
                if (currentUser == null) {
                    Toast.makeText(ProductPage.this, "Please Login First to Proceed", Toast.LENGTH_SHORT).show();
                } else {
                    firestore.collection("Users").document(currentUser.getUid()).collection("Cart").document(product.getProductId()).delete().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(ProductPage.this, "Product Removed from Cart", Toast.LENGTH_SHORT).show();
                            addedToCart=false;
                            cartButton.setText("Add to Cart");
                            checkout.setVisibility(View.INVISIBLE);
                        } else {
                            Toast.makeText(ProductPage.this, "Failed to Remove from Cart", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(e -> Toast.makeText(ProductPage.this, "Failed to Remove from Cart", Toast.LENGTH_SHORT).show());
                }
            }
        });

        wishlistButton.setOnClickListener(view -> {
            if(!addedToWishlist) {
                if (currentUser == null) {
                    Toast.makeText(ProductPage.this, "Please Login First to Proceed", Toast.LENGTH_SHORT).show();
                } else {
                    firestore.collection("Users").document(currentUser.getUid()).collection("Wishlist").document(product.getProductId()).set(product).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(ProductPage.this, "Product Added to Wishlist", Toast.LENGTH_SHORT).show();
                            addedToWishlist=true;
                            wishlistButton.setText("Remove from Wishlist");
                        } else {
                            Toast.makeText(ProductPage.this, "Failed to add to Wishlist", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(e -> Toast.makeText(ProductPage.this, "Failed to add to Wishlist", Toast.LENGTH_SHORT).show());
                }
            }
            else{
                if (currentUser == null) {
                    Toast.makeText(ProductPage.this, "Please Login First to Proceed", Toast.LENGTH_SHORT).show();
                } else {
                    firestore.collection("Users").document(currentUser.getUid()).collection("Wishlist").document(product.getProductId()).delete().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(ProductPage.this, "Product Removed from Wishlist", Toast.LENGTH_SHORT).show();
                            addedToWishlist=false;
                            wishlistButton.setText("Add to wishlist");
                        } else {
                            Toast.makeText(ProductPage.this, "Failed to Remove from Wishlist", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(e -> Toast.makeText(ProductPage.this, "Failed to Remove from Wishlist", Toast.LENGTH_SHORT).show());
                }
            }
        });

        updateSellerInfo();
    }

    private void updateSellerInfo(){
        firestore.collection("Users").document(product.getProductSellerUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                DocumentSnapshot doc=task.getResult();
                if(doc.exists()){
                    product.setProductSellerLogo(doc.getString("BrandLogo"));
                    Bitmap b=ImageStringOperation.getImage(product.getProductSellerLogo());
                    if(b!=null)
                        slogo.setImageBitmap(b);
                    product.setProductSeller(doc.getString("BrandName"));
                }
            }
        }).addOnFailureListener(e -> Log.w("Error","Update Seller",e));
    }
    void checkWishlist(){
        firestore.collection("Users").document(currentUser.getUid()).collection("Wishlist").document(product.getProductId()).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                if(task.getResult().exists()){
                    addedToWishlist=true;
                    wishlistButton.setText("Remove from Wishlist");

                }
                else {
                    addedToWishlist = false;
                    wishlistButton.setText("Add to Wishlist");
                }
            }
        }).addOnFailureListener(e -> addedToWishlist=false);
    }
    void checkCart(){
        firestore.collection("Users").document(currentUser.getUid()).collection("Cart").document(product.getProductId()).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                if(task.getResult().exists()){
                    addedToCart=true;
                    cartButton.setText("Remove from Cart");
                    checkout.setVisibility(View.VISIBLE);
                }
                else{
                    addedToCart=false;
                    cartButton.setText("Add to Cart");
                }
            }
        }).addOnFailureListener(e -> addedToCart=false);
    }
}
