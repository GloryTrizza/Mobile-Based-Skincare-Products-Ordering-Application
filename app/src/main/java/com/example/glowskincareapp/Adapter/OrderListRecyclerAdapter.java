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
import com.example.glowskincareapp.R;
import com.example.glowskincareapp.Shipping;

import java.util.ArrayList;

public class OrderListRecyclerAdapter extends RecyclerView.Adapter<OrderListRecyclerAdapter.OrderListRecyclerViewHolder> {
    private final ArrayList<Order> orderList;
    private final Context mContext;

    public OrderListRecyclerAdapter(ArrayList<Order> orders, Context mContext) {
        this.orderList = orders;
        this.mContext = mContext;
    }

    public class OrderListRecyclerViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView name;
        TextView price;

        TextView address,status, mobileno;
        ConstraintLayout container;

        OrderListRecyclerViewHolder(View itemView){
            super(itemView);
            img= itemView.findViewById(R.id.orderListImage);
            name= itemView.findViewById(R.id.orderListName);
            price= itemView.findViewById(R.id.orderListPrice);
            address= itemView.findViewById(R.id.orderListAddress);
            status= itemView.findViewById(R.id.orderListStatus);
            mobileno = itemView.findViewById(R.id.orderListMobile);
            container= itemView.findViewById(R.id.orderListItemContainer);
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
            price.setText("Price: Kshs."+Price);
        }
        public void setProductAddress(String Address){address.setText("Address: "+Address);}
        public void setProductStatus(String Status){status.setText("Status:"+Status);}
        public void setMobileno(String Transaction){
            mobileno.setText("Mobile Number: "+Transaction);}
        public void setOnClickListener(Order order){

            container.setOnClickListener(view -> {
                Intent okIntent = new Intent(mContext, Shipping.class);
                okIntent.putExtra("order", order);
                okIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(okIntent);
            });
        }
    }

    @NonNull
    @Override
    public OrderListRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.orderlistrecycleritem, parent, false);
        return new OrderListRecyclerViewHolder(view);
    }
    @Override
    public int getItemCount(){
        return orderList==null?0:orderList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull OrderListRecyclerViewHolder holder, int position) {
        final Order order= orderList.get(position);
        holder.setProductImage(order.getProductImage());
        holder.setProductName(order.getProductName());
        holder.setProductPrice(order.getProductPrice());
        holder.setProductAddress(order.getDeliveryAddress());
        holder.setProductStatus(order.getStatus());
        holder.setMobileno(order.getTransactionNo());
        holder.setOnClickListener(order);
    }
}
