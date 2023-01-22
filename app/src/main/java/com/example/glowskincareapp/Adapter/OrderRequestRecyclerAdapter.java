package com.example.glowskincareapp.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.glowskincareapp.ImageStringOperation;
import com.example.glowskincareapp.R;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrderRequestRecyclerAdapter extends RecyclerView.Adapter<OrderRequestRecyclerAdapter.OrderRequestRecyclerViewHolder> {
    private final ArrayList<Order> orderList;
    private final Context mContext;

    public OrderRequestRecyclerAdapter(ArrayList<Order> orders, Context mContext) {
        this.orderList = orders;
        this.mContext = mContext;
    }

    public class OrderRequestRecyclerViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name;
        TextView price;
        Button approve,reject;
        TextView address,status, mobilenumber;
        LinearLayout container;
        OrderRequestRecyclerViewHolder(View itemView){
            super(itemView);
            image = itemView.findViewById(R.id.orderRequestImage);
            name= itemView.findViewById(R.id.orderRequestName);
            price= itemView.findViewById(R.id.orderRequestPrice);
            address= itemView.findViewById(R.id.orderRequestAddress);
            status= itemView.findViewById(R.id.orderRequestStatus);
            mobilenumber = itemView.findViewById(R.id.orderMobileNumber);
            approve= itemView.findViewById(R.id.orderRequestApproveOrder);
            container= itemView.findViewById(R.id.orderRequestAcceptRejectC);
            reject= itemView.findViewById(R.id.orderRequestRejectOrder);
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
            price.setText("Price: Kshs. "+Price);
        }
        public void setProductAddress(String Address){address.setText("Address: "+Address);}
        public void setProductStatus(String Status){status.setText("Status:"+Status);
            if(Status.equals("Approved") || Status.equals("Rejected")){
                container.setVisibility(View.GONE);
            }
            else if(Status.equals("Pending")){
                container.setVisibility(View.VISIBLE);
            }
        }
        public void setMobilenumber(String MobileNo){
            mobilenumber.setText("Customer Phone Number: " + MobileNo);}
        public void setOnClickListener(Order order){

            approve.setOnClickListener(view -> {
                Map<String,Object> map=new HashMap<>();
                map.put("Status","Approved");
                FirebaseFirestore.getInstance().collection("Orders").document(order.getTransactionNo()).update(map).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(mContext, "Approved", Toast.LENGTH_SHORT).show();
                        setProductStatus("Approved");
                        FirebaseFirestore.getInstance().collection("Products").document(order.getProductId()).update("ProductSellCount", FieldValue.increment(1));
                    }
                });
            });
            reject.setOnClickListener(view -> {
                Map<String,Object> map=new HashMap<>();
                map.put("Status","Rejected");
                FirebaseFirestore.getInstance().collection("Orders").document(order.getTransactionNo()).update(map).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(mContext, "Rejected", Toast.LENGTH_SHORT).show();
                        setProductStatus("Rejected");
                    }
                });
            });
        }
    }

    @NonNull
    @Override
    public OrderRequestRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.orderrequestrecycleritem, parent, false);
        return new OrderRequestRecyclerViewHolder(view);
    }
    @Override
    public int getItemCount(){
        return orderList==null?0:orderList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull OrderRequestRecyclerViewHolder holder, int position) {
        final Order order= orderList.get(position);
        holder.setProductImage(order.getProductImage());
        holder.setProductName(order.getProductName());
        holder.setProductPrice(order.getProductPrice());
        holder.setProductAddress(order.getDeliveryAddress());
        holder.setProductStatus(order.getStatus());
        holder.setMobilenumber(order.getTransactionNo());
        holder.setOnClickListener(order);
    }
}
