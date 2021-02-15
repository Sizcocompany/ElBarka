package com.example.elbarka.Admin;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elbarka.Model.Products;
import com.example.elbarka.R;
import com.example.elbarka.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class AdminCheckApproveNewProductActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager ;

    private DatabaseReference unApprovedProductsRef ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_check_approve_new_product);

        recyclerView = findViewById(R.id.SellersHomePendingProduct_checklist);
        recyclerView.setHasFixedSize(true);
        layoutManager =  new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        unApprovedProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");

    }

    @Override
    protected void onStart() {
        super.onStart();

        // now we will retrive all not approve products and add it in recycler view
        FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>()
         .setQuery(unApprovedProductsRef.orderByChild("productState").equalTo("Not Approved") , Products.class)
                .build();

        // will use recycler adapter to retrive all products from database
        FirebaseRecyclerAdapter<Products , ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int i, @NonNull Products model) {

                        holder.txtProductName.setText(model.getpName());
                        holder.txtProductDescription.setText(model.getDescription());
                        holder.txtProductPrice.setText(R.string.price+ model.getPrice() + "$");
                        Picasso.get().load(model.getImage()).into(holder.imageViewProduct);

                        // now when admin press on this item we will have dialog to approve or reject this order

                     holder.itemView.setOnClickListener(new View.OnClickListener() {
                         @Override
                         public void onClick(View v) {


                             // SHOW DIALOG FOR ADMIN
                             final String productID = model.getPid();

                             CharSequence options[] = new CharSequence[]{

                                     // show options to admin

                                     "Yes",
                                     "No"
                             };
                             AlertDialog.Builder builder = new AlertDialog.Builder(AdminCheckApproveNewProductActivity.this);
                             builder.setTitle(R.string.do_you_want_to_approve_this_product);
                             builder.setItems(options, new DialogInterface.OnClickListener() {
                                 @Override
                                 public void onClick(DialogInterface dialog, int position) {

                                     // if customer press yes which mean index 0 in array

                                     if(position == 0 ){

                                         changeProductSatat(productID);

                                     }if(position == 1 ){


                                     }


                                 }
                             });

                             builder.show();
                         }
                     });

                    }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout, parent, false);
                        ProductViewHolder holder = new ProductViewHolder(view);
                        return holder;
                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void changeProductSatat(String productID) {

        // now if custome press yes we wil change product state to approved
        unApprovedProductsRef.child(productID).child("productState").setValue("Approved").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){

                    Toast.makeText(AdminCheckApproveNewProductActivity.this, R.string.product_approved_successfully, Toast.LENGTH_SHORT).show();

                }else {

                    Toast.makeText(AdminCheckApproveNewProductActivity.this, R.string.please_try_again_later, Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}