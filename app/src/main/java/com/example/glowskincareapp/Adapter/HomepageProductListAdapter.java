package com.example.glowskincareapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.glowskincareapp.ImageStringOperation;
import com.example.glowskincareapp.ProductPage;
import com.example.glowskincareapp.R;

import java.util.ArrayList;

public class HomepageProductListAdapter extends RecyclerView.Adapter<HomepageProductListAdapter.HomepageProductListViewHolder> {
    private final ArrayList<Product> productList;
    private final Context mContext;

    public HomepageProductListAdapter(ArrayList<Product> products, Context mContext) {
        this.productList = products;
        this.mContext = mContext;
    }

    public class HomepageProductListViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name;
        TextView price;
        CardView container;
        HomepageProductListViewHolder(View itemView){
            super(itemView);
            image = itemView.findViewById(R.id.homepageRecyclerItemImage);
            name= itemView.findViewById(R.id.homepageRecyclerItemName);
            price= itemView.findViewById(R.id.homepageRecyclerItemPrice);
            container= itemView.findViewById(R.id.productRecyclerItemContainerHomepage);
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
            price.setText("Price: Kshs. " + Price);
        }
        public void setOnClickListener(Product product){
                container.setOnClickListener(view -> {
                    Intent okIntent = new Intent(mContext, ProductPage.class);
                    okIntent.putExtra("product", product);
                    okIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(okIntent);
                });
        }
    }

    @NonNull
    @Override
    public HomepageProductListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.homepageproductrecycleritem, parent, false);
        return new HomepageProductListViewHolder(view);
    }
    @Override
    public int getItemCount(){
        return productList==null?0:productList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull HomepageProductListViewHolder holder, int position) {
        final Product product= productList.get(position);
        Log.i("GT","Count: " +position);
        holder.setProductImage(product.getProductImage());
        holder.setProductName(product.getProductName());
        holder.setProductPrice(product.getProductPrice());
        holder.setOnClickListener(product);
    }
}
