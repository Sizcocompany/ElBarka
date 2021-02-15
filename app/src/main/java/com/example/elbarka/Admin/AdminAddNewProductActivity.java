package com.example.elbarka.Admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.elbarka.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminAddNewProductActivity extends AppCompatActivity {

    private String categoryName , productName , description , price , saveCurrentDate , saveCurrentTime ;
    private ImageView inputeProduct_image;
    private EditText inputeProductName, inputeProductDescription, inputeProductPrice;
    private Button addNewProductBtn;
    private static final int gallaryPick =1 ;
    private Uri imageUri ;
    private String productRandomKey , downloadImageUrl ;
    private ProgressDialog loadingBar;

    // create folder in storage
    private StorageReference productImageRef;

    // create new section or node inside database
    private DatabaseReference productRef ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_admin_add_new_product );

        categoryName = getIntent().getExtras().get( "category" ).toString();
        // create folder called product Images in fire store to store images
        productImageRef = FirebaseStorage.getInstance().getReference().child( "Product Images " );

        // now will create new folder inside firebase database called product to store all product inside as users ad admins
        productRef = FirebaseDatabase.getInstance().getReference().child("Products");

        inputeProduct_image = findViewById( R.id.seller_select_product_image);
        inputeProductName = findViewById( R.id.seller_product_name_details);
        inputeProductDescription = findViewById( R.id.seller_product_description);
        inputeProductPrice = findViewById( R.id.seller_product_price);
        addNewProductBtn = findViewById( R.id.seller_add_newProduct_btn);
        loadingBar = new ProgressDialog( this );

        inputeProduct_image.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openGallary();
            }
        } );

        addNewProductBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                validateProductData();

            }
        } );


    }



    private void openGallary() {
        Intent gallaryIntent = new Intent();
        gallaryIntent.setAction( Intent.ACTION_GET_CONTENT );
        gallaryIntent.setType( "image/*" );
        startActivityForResult( gallaryIntent , gallaryPick );
    }

    // store pic in firebase


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );

        if(requestCode==gallaryPick && resultCode == RESULT_OK && data != null){

            imageUri = data.getData();
            inputeProduct_image.setImageURI( imageUri );
        }
    }

    private void validateProductData()
    {

        productName = inputeProductName.getText().toString();
        description = inputeProductDescription.getText().toString();
        price = inputeProductPrice.getText().toString();

        if( imageUri == null){

            Toast.makeText( this, R.string.please_add_image, Toast.LENGTH_SHORT ).show();
        }else if(TextUtils.isEmpty( productName )){

            Toast.makeText( this, R.string.please_add_product_name, Toast.LENGTH_SHORT ).show();
        }else if(TextUtils.isEmpty( description )){

            Toast.makeText( this, R.string.please_add_product_description, Toast.LENGTH_SHORT ).show();
        }else if(TextUtils.isEmpty( price )){

            Toast.makeText( this, R.string.please_add_product_price, Toast.LENGTH_SHORT ).show();
        }else {

            storeProductInformation();
        }

    }

    private void storeProductInformation() {

        loadingBar.setTitle( R.string.adding_new_product );
        loadingBar.setMessage(this.getResources().getString(R.string.dear_admin_please_wait_while_we_are_adding_product_now));
        loadingBar.setCanceledOnTouchOutside( false );
        loadingBar.show();

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd , yyyy");
        saveCurrentDate = currentDate.format( calendar.getTime() );

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format( calendar.getTime() );

        productRandomKey = saveCurrentDate + saveCurrentTime ;

        // store link of image in firestore database

        StorageReference filePath = productImageRef.child( imageUri.getLastPathSegment() + productRandomKey + ".jpg");

        final UploadTask uploadTask = filePath.putFile( imageUri );

        // in case of fauilure in appload

        uploadTask.addOnFailureListener( new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                String massage = e.toString();
                Toast.makeText( AdminAddNewProductActivity.this, R.string.error + massage, Toast.LENGTH_SHORT ).show();
                loadingBar.dismiss();

            }
        } ).addOnSuccessListener( new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Toast.makeText( AdminAddNewProductActivity.this, R.string.product_image_uploaded_successfully, Toast.LENGTH_SHORT ).show();

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                        if(!task.isSuccessful()){

                            throw task.getException();

                        }

                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        return  filePath.getDownloadUrl();
                    }
                } ).addOnCompleteListener( new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {

                        downloadImageUrl = task.getResult().toString();
                        Toast.makeText( AdminAddNewProductActivity.this, R.string.getting_product_image_URL_successfully, Toast.LENGTH_SHORT ).show();

                        // now will store all product info in database

                        saveProductInfoToDataBase();
                    }
                } );
            }
        } );
    }

    private void saveProductInfoToDataBase() {

        HashMap<String, Object> productMap = new HashMap<>();

        productMap.put( "pid" , productRandomKey );
        productMap.put( "date" , saveCurrentDate );
        productMap.put( "time" , saveCurrentTime );
        productMap.put( "category" , categoryName );
        productMap.put( "image" , downloadImageUrl );
        productMap.put( "pName" , productName );
        productMap.put( "description" , description );
        productMap.put( "price" , price );

        productRef.child( productRandomKey ).updateChildren( productMap ).
                addOnCompleteListener( new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){

                            Intent intent = new Intent( AdminAddNewProductActivity.this , AdminCategoryActivity.class);
                            startActivity( intent );

                            loadingBar.dismiss();
                            Toast.makeText( AdminAddNewProductActivity.this, R.string.product_is_added_successfully, Toast.LENGTH_SHORT ).show();
                        }else {

                            loadingBar.dismiss();
                            String massege = task.getException().toString();
                            Toast.makeText( AdminAddNewProductActivity.this, R.string.error + massege, Toast.LENGTH_SHORT ).show();
                        }

                    }
                } );
    }
}