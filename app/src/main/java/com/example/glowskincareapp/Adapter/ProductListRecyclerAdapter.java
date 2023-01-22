package com.example.glowskincareapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.glowskincareapp.ImageStringOperation;
import com.example.glowskincareapp.ProductPage;
import com.example.glowskincareapp.R;

import java.util.ArrayList;

public class ProductListRecyclerAdapter extends RecyclerView.Adapter<ProductListRecyclerAdapter.ProductListRecyclerViewHolder> {
    private final ArrayList<Product> productList;
    private final Context mContext;
    private final boolean isSeller;

    public ProductListRecyclerAdapter(ArrayList<Product> products, Context mContext,boolean isSeller) {
        this.productList = products;
        this.mContext = mContext;
        this.isSeller=isSeller;
    }

    public class ProductListRecyclerViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView name;
        TextView price;
        ConstraintLayout container;
        TextView sales,category;
        ProductListRecyclerViewHolder(View itemView){
            super(itemView);
            img= itemView.findViewById(R.id.productImageInProductList);
            name= itemView.findViewById(R.id.productNameInProductList);
            price= itemView.findViewById(R.id.productPriceInProductList);
            container= itemView.findViewById(R.id.containerInProductList);
            sales= itemView.findViewById(R.id.totalSalesInProductList);
            category= itemView.findViewById(R.id.productCategoryInProductList);
        }
        public void setProductImage(String image) {
            Bitmap b= ImageStringOperation.getImage(image);
            if(b!=null){
                img.setImageBitmap(b);
            }
        }
        public void setProductName(String Name){
            name.setText(Name);
        }
        public void setProductPrice(String Price){
            price.setText("Price: Kshs. "+Price);
        }
        public void setProductCategory(String Category){
            if(isSeller){
                category.setText("Category: " + Category);
            }
            else
                category.setVisibility(View.GONE);
        }
        public void setProductSales(String Sales){
            if(isSeller){
                sales.setText("Total sales: "+Sales);
            }
            else
                sales.setVisibility(View.GONE);
        }
        public void setOnClickListener(Product product){
            if(!isSeller) {
                container.setOnClickListener(view -> {
                    Intent okIntent = new Intent(mContext, ProductPage.class);
                    okIntent.putExtra("product", product);
                    okIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(okIntent);
                });
            }
        }
    }

    @NonNull
    @Override
    public ProductListRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.productrecycleritem, parent, false);
        return new ProductListRecyclerViewHolder(view);
    }
    @Override
    public int getItemCount(){
        return productList==null?0:productList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ProductListRecyclerViewHolder holder, int position) {
        final Product product= productList.get(position);
        holder.setProductImage(product.getProductImage());
        holder.setProductName(product.getProductName());
        holder.setProductPrice(product.getProductPrice());
        holder.setProductCategory(product.getProductCategory());
        holder.setProductSales(product.getProductSellCount()+"");
        holder.setOnClickListener(product);
    }
}
