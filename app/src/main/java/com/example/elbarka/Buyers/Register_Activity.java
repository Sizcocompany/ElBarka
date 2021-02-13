package com.example.elbarka.Buyers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.elbarka.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class Register_Activity extends AppCompatActivity {

    private ImageView register_Logo;
    private EditText inputeName, inputePhone, inputePassword;
    private Button createAccount_btn;
    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_register );

        register_Logo = findViewById( R.id.register_applogo );
        inputeName = findViewById( R.id.register__username_inpute );
        inputePhone = findViewById( R.id.register__phone_number_inpute );
        inputePassword = findViewById( R.id.register__Password_inpute);
        createAccount_btn = findViewById( R.id.register_btn_loginpage );
        loadingBar = new ProgressDialog( this );

        createAccount_btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        } );

        createAccount_btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAccount();
            }
        } );

    }

    private void CreateAccount() {

        String name = inputeName.getText().toString();
        String phone = inputePhone.getText().toString();
        String password = inputePassword.getText().toString();

        if(TextUtils.isEmpty( name ))
        {
            Toast.makeText( this, "Please Enter User Name", Toast.LENGTH_SHORT ).show();
        }else if (TextUtils.isEmpty( phone ))
        {
            Toast.makeText( this, "Please Enter Phone ", Toast.LENGTH_SHORT ).show();
        }else if (TextUtils.isEmpty( password ))
        {
            Toast.makeText( this, "Please Enter Password", Toast.LENGTH_SHORT ).show();
        }else{

            loadingBar.setTitle( "Create Account" );
            loadingBar.setMessage( " Please Wait , while check credantials ... " );
            loadingBar.setCanceledOnTouchOutside( false );
            loadingBar.show();

            validatePhoneNumber( name , phone , password);

        }
    }

    private void validatePhoneNumber(String name, String phone, String password) {

        final DatabaseReference rootRef ;
        rootRef = FirebaseDatabase.getInstance().getReference();

        rootRef.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // if this phone number not exists in data base
                if(!(snapshot.child( "Users" ).child( phone ).exists()))
                {
                    // will add this user in database

                    HashMap<String, Object> userdataMap = new HashMap<>();

                    userdataMap.put( "phone" , phone );
                    userdataMap.put( "password" , password );
                    userdataMap.put( "username" , name );

                    // adding user using unique key which is phone
                    rootRef.child( "Users").child( phone ).updateChildren( userdataMap )
                            .addOnCompleteListener( new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){

                                            Toast.makeText( Register_Activity.this, "Congratulations your account has been created  ", Toast.LENGTH_SHORT ).show();
                                            loadingBar.dismiss();

                                            Intent intent = new Intent(Register_Activity.this , LoginActivity.class);
                                            startActivity( intent );
                                            
                                    }
                                    else {

                                        loadingBar.dismiss();
                                        Toast.makeText( Register_Activity.this, "Network Error , please try again later ... ", Toast.LENGTH_SHORT ).show();
                                    }
                                }
                            } );
                }

                // if this phone number is exsits in database
                else {

                    Toast.makeText( Register_Activity.this, "this " + phone + " already exists", Toast.LENGTH_SHORT ).show();
                    loadingBar.dismiss();
                    Toast.makeText( Register_Activity.this, "Please use another phone number", Toast.LENGTH_SHORT ).show();

                    Intent intent = new Intent(Register_Activity.this , MainActivity.class);
                    startActivity( intent );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );

    }


}
