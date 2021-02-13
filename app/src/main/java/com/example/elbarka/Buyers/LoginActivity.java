package com.example.elbarka.Buyers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.elbarka.Admin.AdminCategoryActivity;
import com.example.elbarka.Model.Users;
import com.example.elbarka.Prevalent.Prevalent;
import com.example.elbarka.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    private EditText inputePhoneNumber, inputePassword;
    private Button loginBtn  ;
    private ProgressDialog loadingBar;
    private TextView adminLink , notAdminLink , forgetPasswordTxt ;

    private String userDbName = "Users";
//    private String adminsDbName = "Admins";
    private CheckBox checkBoxRememeberMe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_login );

        inputePhoneNumber = findViewById( R.id.login_phone_number_inpute );
        inputePassword = findViewById( R.id.login_Password_inpute );
        loginBtn = findViewById( R.id.login_btn_loginpage );
        adminLink = findViewById( R.id.login_admin_pannel_tv );
        notAdminLink = findViewById( R.id.login_not_admin_pannel_tv );
        loadingBar = new ProgressDialog( this );
        forgetPasswordTxt = findViewById(R.id.forget_Password_txt);
        checkBoxRememeberMe = findViewById( R.id.remember_me_chkBox );
        // to store customer data inorder to be used in remember me box
        Paper.init( this );

        loginBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loginUser();
            }
        } );

        forgetPasswordTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LoginActivity.this , ForgetPasswordActivity.class);
                // we will send value of "check" from loginactivity to forget password active with " login "
                intent.putExtra("check" , "login");
                startActivity(intent);
            }
        });
        adminLink.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginBtn.setText( "Login Admin" );
                adminLink.setVisibility( View.INVISIBLE );
                notAdminLink.setVisibility( View.VISIBLE );
                userDbName = "Admins";

            }
        } );

        notAdminLink.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginBtn.setText( "Login" );
                adminLink.setVisibility( View.VISIBLE );
                notAdminLink.setVisibility( View.INVISIBLE );
                userDbName = "Users";

            }
        } );
    }

    private void loginUser() {

        String phone = inputePhoneNumber.getText().toString();
        String password = inputePassword.getText().toString();

        if (TextUtils.isEmpty( phone ))
        {
            Toast.makeText( this, "Please Enter Phone ", Toast.LENGTH_SHORT ).show();
        }else if (TextUtils.isEmpty( password ))
        {
            Toast.makeText( this, "Please Enter Password", Toast.LENGTH_SHORT ).show();
        }else {

            loadingBar.setTitle( "Login Account" );
            loadingBar.setMessage( " Please Wait , while check credantials ... " );
            loadingBar.setCanceledOnTouchOutside( false );
            loadingBar.show();

            allowAccessToAccount (phone , password);
        }
    }

    private void allowAccessToAccount(String phone, String password) {

        // we need to store user phone and password in key to be retrive in remember me checkbox

        if (checkBoxRememeberMe.isChecked()) {

            // now store phone and password in phone memory
            Paper.book().write( Prevalent.UserPhoneKey, phone );
            Paper.book().write( Prevalent.UserPasswordKey, password );
//
//            if(userDbName.equals("Admins")){
//                Paper.book().write( Prevalent.AdminPhoneKey, phone );
//                Paper.book().write( Prevalent.AdminPasswordKey, password );
//
//            }

        }

        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();

        rootRef.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child(userDbName).child( phone ).exists()) {

                    Users userData = snapshot.child(userDbName).child( phone ).getValue( Users.class );

                    if (userData.getPhone().equals( phone )) {

                        if (userData.getPassword().equals( password )) {

                            if (userDbName.equals( "Admins" )) {
                                Toast.makeText( LoginActivity.this, "welcome Admin you are Logged in Successfully  ... ", Toast.LENGTH_SHORT ).show();
                                loadingBar.dismiss();
                                Intent intent = new Intent( LoginActivity.this, AdminCategoryActivity.class );
                                startActivity( intent );
                            } else if (userDbName.equals( "Users" )) {
                                Toast.makeText( LoginActivity.this, "Logged in Successfully ... ", Toast.LENGTH_SHORT ).show();

                                loadingBar.dismiss();
                                Intent intent = new Intent( LoginActivity.this, Home2Activity.class );
                                // to save customer login data in currentonlineuser
                                Prevalent.currentOnlineUsers = userData ;
                                startActivity( intent );
                            }

                        } else {

                            Toast.makeText( LoginActivity.this, "incorrect password", Toast.LENGTH_SHORT ).show();
                            loadingBar.dismiss();
                        }
                    }

                } else {

                    Toast.makeText( LoginActivity.this, "Account with " + phone + " not exists ", Toast.LENGTH_SHORT ).show();
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );
    }
    }
