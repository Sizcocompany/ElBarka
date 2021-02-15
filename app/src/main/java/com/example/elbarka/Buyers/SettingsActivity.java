package com.example.elbarka.Buyers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.elbarka.Prevalent.Prevalent;
import com.example.elbarka.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private CircleImageView profileImageView;
    private TextView profileChangeBtn , closeBtn , saveBtn ;
    private EditText fullNameEditeText , userPhoneEditeText , addressEditeText ;
    private Button securityQuestionBtn ;

    private Uri imageUri;
    private String myUri = "";
    private StorageTask uploadTask ;
    private StorageReference storageProfilePicRef ;
    private String checker = "" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_settings );

        storageProfilePicRef = FirebaseStorage.getInstance().getReference().child( "Profile pictures" );
        profileImageView = findViewById( R.id.settings_profile_image );
        profileChangeBtn = findViewById( R.id.profile_image_change_Btn );
        closeBtn = findViewById( R.id.close_settingsBtn );
        saveBtn = findViewById( R.id.update_settings_Btn );
        fullNameEditeText = findViewById( R.id.settings_full_name );
        userPhoneEditeText = findViewById( R.id.settings_phone_number );
        addressEditeText = findViewById( R.id.settings_address );
        securityQuestionBtn = findViewById(R.id.security_question_btn);

        userInfoDisplay(profileImageView , fullNameEditeText , userPhoneEditeText , addressEditeText);

        fullNameEditeText.setText(Prevalent.currentOnlineUsers.getName());
        userPhoneEditeText.setText(Prevalent.currentOnlineUsers.getPhone());
        addressEditeText.setText(Prevalent.currentOnlineUsers.getAddress());


        closeBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        } );

        securityQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SettingsActivity.this , ForgetPasswordActivity.class);
                // we will send value of "check" from setting activity to forget password active with " login "
                intent.putExtra("check" , "settings");
                startActivity(intent);
            }
        });

        saveBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(checker.equals("clicked")){

                    // that means customer pressed on change pic btn and chnage his profile pic then add other data
                    userInfoSavedWithImage();

                } else {

                    // that mean customer didn't press on change profile image and update other data only
                    updateOnlyUserInfoWithoutImage();


                }
            }
        } );
        
        profileChangeBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checker = "clicked" ;

                CropImage.activity(imageUri)
                        .setAspectRatio( 1, 1 )
                        .start(SettingsActivity.this);
            }
        } );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult( requestCode, resultCode, data );

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null){

            // will save image in this "result"
            CropImage.ActivityResult result = CropImage.getActivityResult( data );

            // will sore result which is image Uri varlable  in imageUri
            imageUri =result.getUri();

            // set this Image in profile pic
            profileImageView.setImageURI( imageUri );

        }else {

            Toast.makeText( this, R.string.error+" "+R.string.please_try_again_later, Toast.LENGTH_SHORT ).show();

            // refresh activity
            startActivity( new Intent(SettingsActivity.this , SettingsActivity.class) );
            finish();
        }
    }

    private void userInfoSavedWithImage()
    {

        if(TextUtils.isEmpty( fullNameEditeText.getText().toString() )){

            Toast.makeText( this, R.string.name_is_mandatory, Toast.LENGTH_SHORT ).show();
        }else if(TextUtils.isEmpty( userPhoneEditeText.getText().toString() )){

            Toast.makeText( this, R.string.phone_is_mandatory, Toast.LENGTH_SHORT ).show();

        }else if(TextUtils.isEmpty( addressEditeText.getText().toString() )){

            Toast.makeText( this, R.string.address_is_mandatory, Toast.LENGTH_SHORT ).show();
        }else if(checker.equals( "clicked" )){

            uploadImage();
        }

    }

    private void uploadImage() {

        final ProgressDialog progressDialog = new ProgressDialog( this );

        progressDialog.setTitle(R.string.update_profile);
        progressDialog.setMessage(this.getResources().getString(R.string.please_wait_while_updating_your_account_info) );
        progressDialog.setCanceledOnTouchOutside( false );
        progressDialog.show();

        if ( imageUri != null){

            // this mean old image will be removed and will update with new one
            final StorageReference fileRef = storageProfilePicRef.child( Prevalent.currentOnlineUsers.getPhone() + ".jpg");

            uploadTask = fileRef.putFile( imageUri );

            uploadTask.continueWithTask( new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {

                    if(!task.isSuccessful()){

                        throw task.getException();
                    }

                    return fileRef.getDownloadUrl();
                }
                } ).addOnCompleteListener( new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){

                            Uri downloadUrl = task.getResult();
                            myUri = downloadUrl.toString();

                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child( "Users" );

                            HashMap<String, Object> hashMap = new HashMap<>();

                            hashMap.put( "name" , fullNameEditeText.getText().toString() );
                            hashMap.put( "address" , addressEditeText.getText().toString() );
                            hashMap.put( "phoneOrder" , userPhoneEditeText.getText().toString() );
                            hashMap.put( "image" , myUri );

                            // save info in database
                            ref.child( Prevalent.currentOnlineUsers.getPhone()).updateChildren( hashMap );

                            progressDialog.dismiss();

                            startActivity( new Intent(SettingsActivity.this , Home2Activity.class) );
                            Toast.makeText( SettingsActivity.this, R.string.profile_info_updated_sucssfully, Toast.LENGTH_SHORT ).show();

                            finish();

                        }else {

                            progressDialog.dismiss();
                            Toast.makeText( SettingsActivity.this, R.string.error+" "+R.string.please_try_again_later, Toast.LENGTH_SHORT ).show();
                        }
                    }
                } );

        }else {

            Toast.makeText( this, R.string.image_is_not_selected, Toast.LENGTH_SHORT ).show();
        }
    }

    private void updateOnlyUserInfoWithoutImage() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child( "Users" );

        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put( "name" , fullNameEditeText.getText().toString() );
        hashMap.put( "address" , addressEditeText.getText().toString() );
        hashMap.put( "phoneOrder" , userPhoneEditeText.getText().toString() );
        // save info in database
        ref.child( Prevalent.currentOnlineUsers.getPhone()).updateChildren( hashMap );

        startActivity( new Intent(SettingsActivity.this , Home2Activity.class) );
        Toast.makeText( SettingsActivity.this, R.string.profile_info_updated_sucssfully, Toast.LENGTH_SHORT ).show();

        finish();



        
    }

    private void userInfoDisplay(CircleImageView profileImageView, EditText fullNameEditeText, EditText userPhoneEditeText, EditText addressEditeText) {

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child( "Users").child( Prevalent.currentOnlineUsers.getPhone());

        usersRef.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // check if user phone is exists in database or not
                if(snapshot.exists()){

                    if(snapshot.child("image").exists()){

                        String image = snapshot.child( "image" ).getValue().toString();
                        String name = snapshot.child( "name" ).getValue().toString();
                        String address = snapshot.child( "address" ).getValue().toString();
                        String phone = snapshot.child( "phone" ).getValue().toString();

                        Picasso.get().load( image ).into( profileImageView );
                        fullNameEditeText.setText( name );
                        userPhoneEditeText.setText( phone );
                        addressEditeText.setText( address );
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );
    }


}