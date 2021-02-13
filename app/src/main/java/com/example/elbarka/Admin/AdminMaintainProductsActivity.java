package com.example.elbarka.Admin;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.elbarka.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class AdminMaintainProductsActivity extends AppCompatActivity {

    private EditText name , price , description ;
    private Button applyChangeBtn , deleteProductBtn ; ;
    private ImageView productImageView;

    // add string to recieve ID in it from home Activity in intent action like we do in another activity
    private String productID = "";
    // get refrance from database
    private DatabaseReference productRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_maintain_products);

        productID = getIntent().getStringExtra("pid");

        productRef = FirebaseDatabase.getInstance().getReference().child("Products").child(productID);

        applyChangeBtn = findViewById(R.id.apply_changes_Btn);
        name = findViewById(R.id.product_name_details_Maintain);
        price = findViewById(R.id.product_price_Maintain);
        description = findViewById(R.id.product_description_Maintain);
        productImageView = findViewById(R.id.product_image_Maintain);
        deleteProductBtn = findViewById(R.id.delete_product_Btn);
        

        applyChangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                applychangesbyAdmin();
            }
        });

        displaySpecificProductInfo();


        deleteProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CharSequence options[] = new CharSequence[]{

                        "Yes",
                        "No"
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(AdminMaintainProductsActivity.this);
                builder.setTitle("Are You sure?");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // if 0 which means admin press yes
                        if(which == 0 ){
                            deleteThisProduct();
                        }else {
                            finish();
                        }
                    }
                });
                builder.show();
            }
        });
    }

    private void deleteThisProduct() {

        productRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                Intent intent = new Intent(AdminMaintainProductsActivity.this , AdminCategoryActivity.class);
                startActivity(intent);
                // to don't let custome go back again
                finish();

                Toast.makeText(AdminMaintainProductsActivity.this, "Product deleted successfully", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void applychangesbyAdmin() {

        // now we will get data from fields

       String pName = name.getText().toString();
       String pPrice = price.getText().toString();
       String pDescription = description.getText().toString();

       if(pName.equals("")){

           Toast.makeText(this, "Please enter product name ", Toast.LENGTH_SHORT).show();
       }else if(pPrice.equals("")){

           Toast.makeText(this, "Please enter product Price ", Toast.LENGTH_SHORT).show();
       }else if(pDescription.equals("")){

           Toast.makeText(this, "Please enter product Description", Toast.LENGTH_SHORT).show();
       }else {

           // now we will save change with hashmap as we done in admin add new product activity under saveProductInfoToDataBase()

           HashMap<String, Object> productMap = new HashMap<>();

           productMap.put( "pid" , productID );
           productMap.put( "pName" , pName );
           productMap.put( "description" , pDescription );
           productMap.put( "price" , pPrice );

           productRef.updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
               @Override
               public void onComplete(@NonNull Task<Void> task) {
                   
                   if(task.isSuccessful()){

                       Toast.makeText(AdminMaintainProductsActivity.this, "Changes applied Successfully  ", Toast.LENGTH_SHORT).show();

                       Intent intent = new Intent(AdminMaintainProductsActivity.this , AdminCategoryActivity.class);
                       startActivity(intent);
                       // to can't go back again
                       finish();
                       
                   }else {
                       
                       
                   }

               }
           });
       }




    }

    private void displaySpecificProductInfo() {

        productRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(snapshot.exists()){

                    // now will retriv data like we written in adminAddNewProductActivity in method saveProductInfoToDataBase()

                    String pName = snapshot.child("pName").getValue().toString();
                    String pPrice = snapshot.child("price").getValue().toString();
                    String pDescription = snapshot.child("description").getValue().toString();
                    String pImage = snapshot.child("image").getValue().toString();

                    // now let's display it
                    name.setText(pName);
                    price.setText(pPrice);
                    description.setText(pDescription);
                    Picasso.get().load(pImage).into(productImageView);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}