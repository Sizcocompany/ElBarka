package com.example.elbarka.Buyers;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elbarka.Model.Cart;
import com.example.elbarka.Prevalent.Prevalent;
import com.example.elbarka.R;
import com.example.elbarka.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CartActivity extends AppCompatActivity {

    private TextView totalAmount , txtMsg1;
    private Button nextProceedStep;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private int overTotalPrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.orders_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

       txtMsg1 = findViewById(R.id.msg1);
        nextProceedStep = findViewById(R.id.next_process_btn);
        totalAmount = findViewById(R.id.page_title);


        nextProceedStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                totalAmount.setText(getString(R.string.total_price)+ String.valueOf(overTotalPrice)+ " LE");

                Intent intent = new Intent(CartActivity.this , ConfirmFinalOrderActivity.class);
                // sent total price to confirm activity
                intent.putExtra("Total Price" , String.valueOf(overTotalPrice));
                startActivity(intent);
                finish();

            }
        });

    }

    // start to display recycler view for cart items
    @Override
    protected void onStart() {

        super.onStart();

        // to check order state from database
        checkOrderState();


        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

        FirebaseRecyclerOptions<Cart> options = new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(cartListRef.child("User View")
                        .child(Prevalent.currentOnlineUsers.getPhone()).child("Products"), Cart.class).build();


        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter =
                new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CartViewHolder holder, int i, @NonNull Cart cart) {

                        holder.txtProductQuantity.setText(getString(R.string.quantity) + cart.getQuantity());
                        holder.txtProductPrice.setText(getString(R.string.price) + cart.getPrice() + " LE");
                        holder.txtProductName.setText(cart.getPname());


                        // this is to calculate all product price
                        int oneTypeProdutTotalPrice = ((Integer.valueOf(cart.getPrice()))) * Integer.valueOf(cart.getQuantity());
                        overTotalPrice = overTotalPrice + oneTypeProdutTotalPrice ;
                        totalAmount.setText(getString(R.string.total_price)+ String.valueOf(overTotalPrice));


                        // here will allow user to delete , remove or edite item from Cart view

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                CharSequence options[] = new CharSequence[]
                                        {

                                                "Edite",
                                                "Remove"
                                        };

                                AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                                builder.setTitle(getString(R.string.cart_option));

                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        if (i == 0) {

                                            Intent intent = new Intent(CartActivity.this, ProductdetailsActivity.class);
                                            // we will send usert to productdatadetails activity with the id of product id need to edite to open on this productdatadetails activity

                                            intent.putExtra("pid", cart.getPid());
                                            startActivity(intent);
                                        }

                                        if (i == 1) {

                                            cartListRef.child("User View").child(Prevalent.currentOnlineUsers.getPhone())
                                                    .child("Products").child(cart.getPid())
                                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if (task.isSuccessful()) {

                                                        Toast.makeText(CartActivity.this, getString(R.string.item_removed_successfully), Toast.LENGTH_SHORT).show();

                                                        Intent intent = new Intent(CartActivity.this, Home2Activity.class);

                                                        startActivity(intent);

                                                    }

                                                }
                                            });
                                        }


                                    }
                                });
                                builder.show();
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout, parent, false);
                        CartViewHolder holder = new CartViewHolder(view);
                        return holder;
                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
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

                        totalAmount.setText(getString(R.string.dear) + userName + "\n"+R.string.order_is_shipped_successfully);
                        recyclerView.setVisibility(View.GONE);
                        txtMsg1.setVisibility(View.VISIBLE);
                        txtMsg1.setText(getString(R.string.congratulation_your_final_order_has_been_shipped_successfully_soon_will_received_your_order_your_location));
                        nextProceedStep.setVisibility(View.GONE);

                        Toast.makeText(CartActivity.this, getString(R.string.you_can_purchase_again_once_you_received_your_product), Toast.LENGTH_SHORT).show();

                    }else if(shippingState.equals("not shipped")){

                        totalAmount.setText(getString(R.string.dear) + userName + "\n"+getString(R.string.Order_is_still_not_shipped_please_wait));
                        recyclerView.setVisibility(View.GONE);
                        txtMsg1.setVisibility(View.VISIBLE);
                        nextProceedStep.setVisibility(View.GONE);

                        Toast.makeText(CartActivity.this, getString(R.string.you_can_purchase_again_once_you_received_your_product), Toast.LENGTH_SHORT).show();

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(CartActivity.this, Home2Activity.class);
        startActivity(intent);
        finish();
    }
}