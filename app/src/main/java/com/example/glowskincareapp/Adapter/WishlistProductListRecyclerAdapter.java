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
import com.example.glowskincareapp.ProductPage;
import com.example.glowskincareapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

public class WishlistProductListRecyclerAdapter extends RecyclerView.Adapter<WishlistProductListRecyclerAdapter.WishlistProductListRecyclerViewHolder> {
    private final ArrayList<Product> productList;
    private final Context mContext;

    public WishlistProductListRecyclerAdapter(ArrayList<Product> products, Context mContext) {
        this.productList = products;
        this.mContext = mContext;
    }

    public class WishlistProductListRecyclerViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name;
        TextView price;
        Button cart;
        TextView category;
        ImageView delete;

        WishlistProductListRecyclerViewHolder(View itemView){
            super(itemView);
            image = itemView.findViewById(R.id.productImageInWishlist);
            name= itemView.findViewById(R.id.productNameInWishlist);
            price= itemView.findViewById(R.id.productPriceInWishlist);
            cart= itemView.findViewById(R.id.addToCartButtonInWishlist);
            category= itemView.findViewById(R.id.productCategoryInWishlist);
            delete= itemView.findViewById(R.id.deleteFromWishlistButtonInWishlist);
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
            price.setText("Price:Kshs. "+Price);
        }
        public void setProductCategory(String Category){
            category.setText("Category: "+Category);
        }
        public void setOnClickListener(Product product){
            cart.setOnClickListener(view -> FirebaseFirestore.getInstance().collection("Users").document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).collection("Cart").document(product.getProductId()).set(product).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(mContext, "Product Added to Cart", Toast.LENGTH_SHORT).show();
                    cart.setEnabled(false);
                } else {
                    Toast.makeText(mContext, "Failed to add to Cart", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> Toast.makeText(mContext, "Failed to add to Cart", Toast.LENGTH_SHORT).show()));
            image.setOnClickListener(view -> {
                Intent kIntent = new Intent(mContext, ProductPage.class);
                kIntent.putExtra("product", product);
                kIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(kIntent);
            });
            delete.setOnClickListener(view -> FirebaseFirestore.getInstance().collection("Users").document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).collection("Wishlist").document(product.getProductId()).delete().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(mContext, "Product Removed from Wishlist", Toast.LENGTH_SHORT).show();
                    productList.remove(getBindingAdapterPosition());
                    notifyItemRemoved(getBindingAdapterPosition());
                    notifyItemRangeChanged(getBindingAdapterPosition(),productList.size());
                } else {
                    Toast.makeText(mContext, "Failed to Remove from Wishlist", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> Toast.makeText(mContext, "Failed to Remove from Wishlist", Toast.LENGTH_SHORT).show()));
        }
    }

    @NonNull
    @Override
    public WishlistProductListRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.wishlistrecyclerproductitem, parent, false);
        return new WishlistProductListRecyclerViewHolder(view);
    }
    @Override
    public int getItemCount(){
        return productList==null?0:productList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull WishlistProductListRecyclerViewHolder holder, int position) {
        final Product product= productList.get(position);
        holder.setProductImage(product.getProductImage());
        holder.setProductName(product.getProductName());
        holder.setProductPrice(product.getProductPrice());
        holder.setProductCategory(product.getProductCategory());
        holder.setOnClickListener(product);
    }
}
