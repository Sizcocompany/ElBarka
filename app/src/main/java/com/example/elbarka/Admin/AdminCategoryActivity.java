package com.example.elbarka.Admin;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.elbarka.Buyers.Home2Activity;
import com.example.elbarka.Buyers.MainActivity;
import com.example.elbarka.R;

import io.paperdb.Paper;

public class AdminCategoryActivity extends AppCompatActivity {

    private ImageView nuts, honey, dates, baked;
    private ImageView olive_oil, yamiesh, coffee, shoes;
//    private ImageView headPhone , laptops , watches , mobile_phone ;

    private Button checkOrdersBtn, adminLogoutBtn, maintainProductsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category);

        nuts = findViewById(R.id.nutes_image);
        honey = findViewById(R.id.honey_image);
        dates = findViewById(R.id.dates_image);
        baked = findViewById(R.id.baked_image);
        olive_oil = findViewById(R.id.olive_oil);
        yamiesh = findViewById(R.id.yamiseh_image);
        coffee = findViewById(R.id.coffee);
//        shoes = findViewById( R.id.shoes );
//        headPhone = findViewById( R.id.headphones_handfree);
//        laptops = findViewById( R.id.laptop_PC);
//        watches = findViewById( R.id.watches);
//        mobile_phone = findViewById( R.id.mobilePhones);
        checkOrdersBtn = findViewById(R.id.check_orders_btn);
        adminLogoutBtn = findViewById(R.id.admin_logout_btn);
        maintainProductsBtn = findViewById(R.id.maintain_btn);

        maintainProductsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategoryActivity.this, Home2Activity.class);
                // we will send to homeactivity with string to can defratiate between admin and user
                intent.putExtra("Admin", "Admin");
                startActivity(intent);
            }
        });

        adminLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // below to don't allow user to back again
                Paper.book().destroy();

                Intent intent = new Intent(AdminCategoryActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();


            }
        });

        checkOrdersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AdminCategoryActivity.this, AdminNewOrdersActivity.class);
                startActivity(intent);

            }
        });


        nuts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
                intent.putExtra("category", "nuts");
                startActivity(intent);
            }
        });

        honey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
                intent.putExtra("category", "honey");
                startActivity(intent);
            }
        });

        dates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
                intent.putExtra("category", "dates");
                startActivity(intent);
            }
        });

        baked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
                intent.putExtra("category", "baked");
                startActivity(intent);
            }
        });

        olive_oil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
                intent.putExtra("category", "olive oli");
                startActivity(intent);
            }
        });
//
        yamiesh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
                intent.putExtra("category", "yamiesh");
                startActivity(intent);
            }
        });

        coffee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
                intent.putExtra("category", "coffee");
                startActivity(intent);
            }
        });
//
//        shoes.setOnClickListener( new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent( AdminCategoryActivity.this , AdminAddNewProductActivity.class);
//                intent.putExtra( "category" , "Shoes" );
//                startActivity( intent );
//            }
//        } );
//
//        headPhone.setOnClickListener( new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent( AdminCategoryActivity.this , AdminAddNewProductActivity.class);
//                intent.putExtra( "category" , "HeadPhone Handfree" );
//                startActivity( intent );
//            }
//        } );
//
//        laptops.setOnClickListener( new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent( AdminCategoryActivity.this , AdminAddNewProductActivity.class);
//                intent.putExtra( "category" , "LapTops" );
//                startActivity( intent );
//            }
//        } );
//
//        watches.setOnClickListener( new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent( AdminCategoryActivity.this , AdminAddNewProductActivity.class);
//                intent.putExtra( "category" , "Watches" );
//                startActivity( intent );
//            }
//        } );
//
//        mobile_phone.setOnClickListener( new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent( AdminCategoryActivity.this , AdminAddNewProductActivity.class);
//                intent.putExtra( "category" , "Mobile Phones" );
//                startActivity( intent );
//            }
//        } );


    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.really_exit))
                    .setMessage(getString(R.string.are_you_sure_you_want_to_exit))
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface arg0, int arg1) {
                            setResult(RESULT_OK, new Intent().putExtra("EXIT", true));
                            finish();
                        }

                    }).create().show();
        }
    }
}