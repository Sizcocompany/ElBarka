package com.example.elbarka.Buyers;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.elbarka.Model.Cart;
import com.example.elbarka.Prevalent.Prevalent;
import com.example.elbarka.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ConfirmFinalOrderActivity extends AppCompatActivity {

    private EditText nameEditText , phoneEditText , addressEditText , cityEditText ;
    private Button confirmOrderBtn;

    private String totalAmount = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);

        // recieve total price from cart activity sent in intent
        totalAmount = getIntent().getStringExtra("Total Price");
        Toast.makeText(this, R.string.total_price + totalAmount, Toast.LENGTH_SHORT).show();

        nameEditText = findViewById(R.id.shapment_name);
        phoneEditText = findViewById(R.id.shapment_phoneNumber);
        addressEditText = findViewById(R.id.shapment_Address);
        cityEditText = findViewById(R.id.shapment_City);
        confirmOrderBtn = findViewById(R.id.confirm_final_order_btn);

        confirmOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                check();

            }
        });

    }

    private void check() {

        if (TextUtils.isEmpty(nameEditText.getText().toString())) {

            Toast.makeText(this, R.string.please_enter_your_full_name, Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(phoneEditText.getText().toString())) {

            Toast.makeText(this, R.string.please_enter_your_phone_number, Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(addressEditText.getText().toString())) {

            Toast.makeText(this, R.string.please_enter_your_address, Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(cityEditText.getText().toString())) {

            Toast.makeText(this, R.string.please_enter_your_city, Toast.LENGTH_SHORT).show();
        }else {

            confirmOrder();

        }
    }

    private void confirmOrder() {

        final String saveCurrentDate , saveCurrentTime ;

        Calendar calforDate = Calendar.getInstance();

        SimpleDateFormat currentdate = new SimpleDateFormat("MMM dd , yyyy");
        saveCurrentDate = currentdate.format(calforDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calforDate.getTime());

        final DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference()
                .child("Orders").child(Prevalent.currentOnlineUsers.getPhone());

        HashMap<String, Object> orderMap = new HashMap<>();
        orderMap.put("totalAmount" , totalAmount);
        orderMap.put("name" , nameEditText.getText().toString());
        orderMap.put("phone" , phoneEditText.getText().toString());
        orderMap.put("address" , addressEditText.getText().toString());
        orderMap.put("city" , cityEditText.getText().toString());
        orderMap.put("date" , saveCurrentDate);
        orderMap.put("time" , saveCurrentTime);
        orderMap.put("state" , "not shipped");

        orderRef.updateChildren(orderMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                FirebaseDatabase.getInstance().getReference()
                        .child("Cart List").child("User View")
                        .child(Prevalent.currentOnlineUsers.getPhone())
                        .removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){

                                    Toast.makeText(ConfirmFinalOrderActivity.this, R.string.your_final_order_has_been_placed_successfully, Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent( ConfirmFinalOrderActivity.this , Home2Activity.class);
                                    // below step to don't let customer to back again to this activity
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();

                                }

                            }
                        });

            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ConfirmFinalOrderActivity.this, CartActivity.class);
        startActivity(intent);
        finish();
    }
}