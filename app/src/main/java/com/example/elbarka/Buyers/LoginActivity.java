package com.example.elbarka.Buyers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.elbarka.Admin.AdminCategoryActivity;
import com.example.elbarka.Model.Users;
import com.example.elbarka.Prevalent.Prevalent;
import com.example.elbarka.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
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
    private SignInButton googleSignInButton;
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth firebaseAuth ;
//    private CallbackManager callbackManager;
    private int RC_GOOGLE_SIGN_IN = 1 ;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;


    private String userDbName = "Users";
//    private String adminsDbName = "Admins";
    private CheckBox checkBoxRememeberMe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_login );

        firebaseAuth = FirebaseAuth.getInstance();
        inputePhoneNumber = findViewById( R.id.login_phone_number_inpute );
        inputePassword = findViewById( R.id.login_Password_inpute );
//        googleSignInButton = findViewById(R.id.google_SignInBtn);
        loginBtn = findViewById( R.id.login_btn_loginpage );
        adminLink = findViewById( R.id.login_admin_pannel_tv );
        notAdminLink = findViewById( R.id.login_not_admin_pannel_tv );
        loadingBar = new ProgressDialog( this );
        forgetPasswordTxt = findViewById(R.id.forget_Password_txt);
        checkBoxRememeberMe = findViewById( R.id.remember_me_chkBox );
        // to store customer data inorder to be used in remember me box
        Paper.init( this );

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .build();

//        googleSignInClient = GoogleSignIn.getClient(this , googleSignInOptions);


//        googleSignInButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                googleSignIn();
//            }
//        });

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
                loginBtn.setText( getString(R.string.login_admin) );
                adminLink.setVisibility( View.INVISIBLE );
                notAdminLink.setVisibility( View.VISIBLE );
                userDbName = "Admins";

            }
        } );

        notAdminLink.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginBtn.setText( getString(R.string.login) );
                adminLink.setVisibility( View.VISIBLE );
                notAdminLink.setVisibility( View.INVISIBLE );
                userDbName = "Users";

            }
        } );
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
//        updateUI(firebaseAuth.getCurrentUser());
       // facebookAuthenticationClass.updateUI(currentUser);
//        googleAuthenticationClass.updateUI(currentUser);
//        firebaseAuth.addAuthStateListener(firebaseAuthStateListener);
    }


    private void googleSignIn() {

        Intent googleSignInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(googleSignInIntent , RC_GOOGLE_SIGN_IN);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_GOOGLE_SIGN_IN) {

            Task<GoogleSignInAccount> signInTask = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount signInacc = signInTask.getResult(ApiException.class);

                AuthCredential authCredential = GoogleAuthProvider.getCredential(signInacc.getIdToken(), null);

                firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        Toast.makeText(getApplicationContext(), "Signed In successfully ", Toast.LENGTH_SHORT).show();
                        FirebaseUser user = firebaseAuth.getCurrentUser();
//                        updateUI(user);


                        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
                        if (acct != null) {
                            String personName = acct.getDisplayName();
                            String personGivenName = acct.getGivenName();
                            String personFamilyName = acct.getFamilyName();
                            String personEmail = acct.getEmail();
                            String personId = acct.getId();
                            Uri personPhoto = acct.getPhotoUrl();
                        }

                        startActivity(new Intent(getApplicationContext(), Home2Activity.class));

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

            } catch (ApiException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error happened please try again alter ", Toast.LENGTH_SHORT).show();
            }
        }


    }



    private void handleGoogleSignIn(Task<GoogleSignInAccount> googleSignInAccountTask) {

        try {
            GoogleSignInAccount googleSignInAccount = googleSignInAccountTask.getResult(ApiException.class);
            Toast.makeText(this, "Signed in With Google account Successfully", Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(googleSignInAccount);

        }
        catch (ApiException e){

            Toast.makeText(this, "Signed in With Google Failed try again later", Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(null);

        }
    }

    private void FirebaseGoogleAuth(GoogleSignInAccount googleSignInAccount) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken() , null);
        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    Toast.makeText(LoginActivity.this, "Successfully", Toast.LENGTH_SHORT).show();
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    updateUi(user);

                }else {

                    Toast.makeText(LoginActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    updateUi(null);


                }
            }
        });
    }

    private void updateUi(FirebaseUser user) {

        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if(googleSignInAccount != null){

            String googlePersonalName = googleSignInAccount.getDisplayName();
            Uri googlePersonalPic = googleSignInAccount.getPhotoUrl();

            Toast.makeText(this, "Welcome "+ googlePersonalName, Toast.LENGTH_SHORT).show();
//            Intent intent =  new Intent(LoginActivity.this , Home2Activity.class);
//            startActivity(intent);


        }

    }

    private void loginUser() {

        String phone = inputePhoneNumber.getText().toString();
        String password = inputePassword.getText().toString();

        if (TextUtils.isEmpty( phone ))
        {
            Toast.makeText( this, getString(R.string.please_enter_phone), Toast.LENGTH_SHORT ).show();
        }else if (TextUtils.isEmpty( password ))
        {
            Toast.makeText( this, getString(R.string.please_enter_password), Toast.LENGTH_SHORT ).show();
        }else {

            loadingBar.setTitle( getString(R.string.login_account) );
            loadingBar.setMessage(getString(R.string.please_wait_while_check_credantials) );
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
                                Toast.makeText( LoginActivity.this, getString(R.string.welcome_admin_you_are_logged_in_successfully), Toast.LENGTH_SHORT ).show();
                                loadingBar.dismiss();
                                Intent intent = new Intent( LoginActivity.this, AdminCategoryActivity.class );
                                startActivity( intent );
                                finish();

                            } else if (userDbName.equals( "Users" )) {
                                Toast.makeText( LoginActivity.this, getString(R.string.Logged_in_successfully), Toast.LENGTH_SHORT ).show();

                                loadingBar.dismiss();
                                Intent intent = new Intent( LoginActivity.this, Home2Activity.class );
                                // to save customer login data in currentonlineuser
                                Prevalent.currentOnlineUsers = userData ;
                                startActivity( intent );
                                finish();
                            }

                        } else {

                            Toast.makeText( LoginActivity.this, getString(R.string.incorrect_password), Toast.LENGTH_SHORT ).show();
                            loadingBar.dismiss();
                        }
                    }

                } else {

                    Toast.makeText( LoginActivity.this, getString(R.string.account_with) + phone + getString(R.string.not_exists), Toast.LENGTH_SHORT ).show();
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );
    }


}
