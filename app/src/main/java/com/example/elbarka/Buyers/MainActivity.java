package com.example.elbarka.Buyers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.example.elbarka.Admin.AdminHomeActivity;
import com.example.elbarka.Model.Users;
import com.example.elbarka.Prevalent.Prevalent;
import com.example.elbarka.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private Button login_Btn, joinNow_Btn;
    private ProgressDialog loadingBar;
    private TextView sellerBegin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        joinNow_Btn = findViewById(R.id.main_Join_now_btn);
        login_Btn = findViewById(R.id.main_login_btn);
        Paper.init(this);
        loadingBar = new ProgressDialog(this);


        login_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        joinNow_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Register_Activity.class);
                startActivity(intent);
            }
        });



        String userPhoneKey = Paper.book().read(Prevalent.UserPhoneKey);
        String userPasswordKey = Paper.book().read(Prevalent.UserPasswordKey);

        if (userPhoneKey != "" && userPasswordKey != "") {
            if (!TextUtils.isEmpty(userPhoneKey) && !TextUtils.isEmpty(userPasswordKey)) {

                allowAccessToAccount(userPhoneKey, userPasswordKey);

                loadingBar.setTitle("Already logged in");
                loadingBar.setMessage("Please Wait ");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // check if firebase not equal null send seller to home page
        if (firebaseUser != null) {

            Intent intent = new Intent(MainActivity.this, Home2Activity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    private void allowAccessToAccount(final String phone, final String password) {

        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child("Users").child(phone).exists() ) {

                    Users userData = snapshot.child("Users").child(phone).getValue(Users.class);

                    if (userData.getPhone().equals(phone)) {

                        if (userData.getPassword().equals(password)) {

                            Toast.makeText(MainActivity.this, "You are already logged in User  ... ", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();

                            Intent intent = new Intent(MainActivity.this, Home2Activity.class);
                            Prevalent.currentOnlineUsers = userData;
                            startActivity(intent);
                        } else {

                            Toast.makeText(MainActivity.this, "incorrect  password", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }

                } else if ( snapshot.child("Admins").child(phone).exists()){
                    Users adminData = snapshot.child("Admins").child(phone).getValue(Users.class);

                    if (adminData.getPhone().equals(phone)) {

                        if (adminData.getPassword().equals(password)) {

                            Toast.makeText(MainActivity.this, "You are already logged in Admin  ... ", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();

                            Intent intent = new Intent(MainActivity.this, AdminHomeActivity.class);
                            Prevalent.currentOnlineUsers = adminData;
                            startActivity(intent);
                        } else  {

                            Toast.makeText(MainActivity.this, "incorrect admin password", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }

                }else {

                    Toast.makeText(MainActivity.this, "Account with " + phone + " not exists ", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}