package com.example.glowskincareapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.glowskincareapp.ImageStringOperation;
import com.example.glowskincareapp.PaymentPage;
import com.example.glowskincareapp.ProductPage;
import com.example.glowskincareapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

public class CartProductListRecyclerAdapter extends RecyclerView.Adapter<CartProductListRecyclerAdapter.CartProductListRecyclerViewHolder> {
    private final ArrayList<Product> productList;
    private final Context mContext;

    public CartProductListRecyclerAdapter(ArrayList<Product> products, Context mContext) {
        this.productList = products;
        this.mContext = mContext;
    }

    public class CartProductListRecyclerViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name;
        TextView price;
        Button checkout;
        TextView category;
        ImageView delete;
        CartProductListRecyclerViewHolder(View itemView){
            super(itemView);
            image = itemView.findViewById(R.id.productImageInCart);
            name= itemView.findViewById(R.id.productNameInCart);
            price= itemView.findViewById(R.id.productPriceInCart);
            checkout= itemView.findViewById(R.id.checkoutButtonInCart);
            category= itemView.findViewById(R.id.productCategoryInCart);
            delete= itemView.findViewById(R.id.deleteFromCartButtonInCart);
        }
        public void setProductImage(String image) {
            Bitmap b= ImageStringOperation.getImage(image);
            if(b!=null){
                this.image.setImageBitmap(b);
            }
        }
        public void setProductName(String Name){
            name.setText(Name);
        }
        public void setProductPrice(String Price){
            price.setText("Price: Kshs." + Price);
        }
        public void setProductCategory(String Category){
            category.setText("Category: "+ Category);
        }
        public void setOnClickListener(Product product){
            checkout.setOnClickListener(view -> {
                Intent okIntent = new Intent(mContext, PaymentPage.class);
                okIntent.putExtra("product", product);
                okIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(okIntent);
            });
            image.setOnClickListener(view -> {
                Intent kIntent = new Intent(mContext, ProductPage.class);
                kIntent.putExtra("product", product);
                kIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(kIntent);
            });
            delete.setOnClickListener(view -> FirebaseFirestore.getInstance().collection("Users").document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).collection("Cart").document(product.getProductId()).delete().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(mContext, "Product Removed from Cart", Toast.LENGTH_SHORT).show();
                    productList.remove(getAbsoluteAdapterPosition());
                    notifyItemRemoved(getAbsoluteAdapterPosition());
                    notifyItemRangeChanged(getAbsoluteAdapterPosition(),productList.size());
                } else {
                    Toast.makeText(mContext, "Failed to Remove from Cart", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> Toast.makeText(mContext, "Failed to Remove from Cart", Toast.LENGTH_SHORT).show()));
        }
    }

    @NonNull
    @Override
    public CartProductListRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.cartrecyclerproductitem, parent, false);
        return new CartProductListRecyclerViewHolder(view);
    }
    @Override
    public int getItemCount(){
        return productList==null?0:productList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull CartProductListRecyclerViewHolder holder, int position) {
        final Product product= productList.get(position);
        holder.setProductImage(product.getProductImage());
        holder.setProductName(product.getProductName());
        holder.setProductPrice(product.getProductPrice());
        holder.setProductCategory(product.getProductCategory());
        holder.setOnClickListener(product);
    }
}
