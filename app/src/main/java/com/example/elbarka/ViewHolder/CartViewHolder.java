package com.example.elbarka.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elbarka.Interface.ItemClickListner;
import com.example.elbarka.R;


public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    // we need to get all cart items from cartItems layout

    public TextView txtProductName , txtProductPrice , txtProductQuantity ;
    public ItemClickListner itemClickListner ;


    public CartViewHolder(@NonNull View itemView) {
        super(itemView);

        txtProductName = itemView.findViewById(R.id.order_user_name);
        txtProductPrice = itemView.findViewById(R.id.order_total_price);
        txtProductQuantity = itemView.findViewById(R.id.order_phone_number);

    }


    @Override
    public void onClick(View view) {

        itemClickListner.onClick(view , getAdapterPosition(), false);


    }

    public ItemClickListner getItemClickListner() {
        return itemClickListner;
    }
}
