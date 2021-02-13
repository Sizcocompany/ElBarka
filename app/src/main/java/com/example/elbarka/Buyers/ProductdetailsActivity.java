package com.example.elbarka.Buyers;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;

import com.example.elbarka.Model.Products;
import com.example.elbarka.Prevalent.Prevalent;
import com.example.elbarka.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProductdetailsActivity extends AppCompatActivity {


    private Button addTocartBtn;
    private ImageView product_Image;
    private ElegantNumberButton numberButton;
    private TextView product_name , product_Description , product_Price;
    private String productID = "" , state = "Normal";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productdetails);

        productID = getIntent().getStringExtra("pid");

        product_name = findViewById(R.id.seller_product_name_details);
        product_Description = findViewById(R.id.product_description_details);
        product_Price = findViewById(R.id.product_price_details);
        product_Image = findViewById(R.id.product_image_details);
        addTocartBtn = findViewById(R.id.pd_add_cart_Btn);
        numberButton = findViewById(R.id.number_btn);

        // retrive spasific product by product ID
        getProductDetails (productID);

        addTocartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if(state.equals("Order placed") || state.equals("Order shipped")){

                    Toast.makeText(ProductdetailsActivity.this, "you can purchase new order once your order shipped or confirmed", Toast.LENGTH_SHORT).show();

                }else {
                    // TO ADD PRODUCT TO CART
                    addingtoCartlist();
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        checkOrderState();
    }

    private void addingtoCartlist()
    {

        // this is to get date adn time of adding to cart list
        String saveCurrentDate , saveCurrentTime ;

        Calendar calforDate = Calendar.getInstance();

        SimpleDateFormat currentdate = new SimpleDateFormat("MMM dd , yyyy");
        saveCurrentDate = currentdate.format(calforDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calforDate.getTime());

       final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

        // add to database
        final HashMap<String, Object> cartMap = new HashMap<>();

        cartMap.put("pid" , productID);
        cartMap.put("pname" , product_name.getText().toString());
        cartMap.put("price" , product_Price.getText().toString());
        cartMap.put("date" , saveCurrentDate);
        cartMap.put("time" , saveCurrentTime);
        cartMap.put("quantity" , numberButton.getNumber());
        cartMap.put("discount" , "");

        cartListRef.child("User View").child(Prevalent.currentOnlineUsers.getPhone())
                .child("Products").child(productID)
                .updateChildren(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()) {

                            // if added sucssfuly in user View  will create new view for admin also to check user orders so below method to add sucssfully transaction in admin view which created
                            cartListRef.child("Admin View").child(Prevalent.currentOnlineUsers.getPhone())
                                    .child("Products").child(productID)
                                    .updateChildren(cartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    Toast.makeText(ProductdetailsActivity.this, "Add to the Cart List", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(ProductdetailsActivity.this , Home2Activity.class);
                                    startActivity(intent);
                                }
                            });
                        }
                    }
                });
    }

    private void getProductDetails(String productID) {

        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        productsRef.child(productID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    Products products = snapshot.getValue(Products.class);

                    product_name.setText(products.getpName());
                    product_Description.setText(products.getDescription());
                    product_Price.setText(products.getPrice());
                    Picasso.get().load(products.getImage()).into(product_Image);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void checkOrderState(){

        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference()
                .child("Orders").child(Prevalent.currentOnlineUsers.getPhone());


        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){

                    String shippingState = snapshot.child("state").getValue().toString();
                    String userName = snapshot.child("name").getValue().toString();

                    if (shippingState.equals("shipped")){

                        state = "Order shipped";

                    }else if(shippingState.equals("not shipped")){

                        state = "Order placed";

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}