package com.example.ecommerce;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class AdminCategoryActivitty extends AppCompatActivity {

    private ImageView tshirtsImg, sportsImg, femaleImg, sweatersImg;
    private ImageView glassesImg, pursesImg, hatsImg, shoesImg;
    private ImageView headphonesImg, laptopsImg, watchImg, mobileImg;
    private Button adminnewOrderBtn, adminLogOutBtn,adminMngBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category_activitty);

        tshirtsImg = findViewById(R.id.tshirtsImageId);
        sportsImg = findViewById(R.id.sportsImageId);
        femaleImg = findViewById(R.id.female_dressesImageId);
        sweatersImg = findViewById(R.id.sweatherImageId);
        glassesImg = findViewById(R.id.glassesImageId);
        pursesImg = findViewById(R.id.purses_bagsImageId);
        hatsImg = findViewById(R.id.hatsImageId);
        shoesImg = findViewById(R.id.shoessImageId);
        headphonesImg = findViewById(R.id.headphonessImageId);
        laptopsImg = findViewById(R.id.laptopsImageId);
        watchImg = findViewById(R.id.watchesImageId);
        mobileImg = findViewById(R.id.mobilesImageId);
        adminnewOrderBtn = findViewById(R.id.admin_new_order_BtnId);
        adminLogOutBtn = findViewById(R.id.admin_logout_BtnId);
        adminMngBtn = findViewById(R.id.admin_manage_BtnId);

        tshirtsImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategoryActivitty.this,AdminNewProductActivity.class);
                intent.putExtra("category","t-Shirts");
                startActivity(intent);
            }
        });

        sportsImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategoryActivitty.this,AdminNewProductActivity.class);
                intent.putExtra("category","Sports t-Shirts");
                startActivity(intent);
            }
        });

        femaleImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategoryActivitty.this,AdminNewProductActivity.class);
                intent.putExtra("category","Female products");
                startActivity(intent);
            }
        });

        sweatersImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategoryActivitty.this,AdminNewProductActivity.class);
                intent.putExtra("category","Sweaters");
                startActivity(intent);
            }
        });

        glassesImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategoryActivitty.this,AdminNewProductActivity.class);
                intent.putExtra("category","Glasses");
                startActivity(intent);
            }
        });

        pursesImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategoryActivitty.this,AdminNewProductActivity.class);
                intent.putExtra("category","Female purses");
                startActivity(intent);
            }
        });

        hatsImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategoryActivitty.this,AdminNewProductActivity.class);
                intent.putExtra("category","Hats");
                startActivity(intent);
            }
        });

        shoesImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategoryActivitty.this,AdminNewProductActivity.class);
                intent.putExtra("category","Shoes");
                startActivity(intent);
            }
        });


        headphonesImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategoryActivitty.this,AdminNewProductActivity.class);
                intent.putExtra("category","Headphones");
                startActivity(intent);
            }
        });

        laptopsImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategoryActivitty.this,AdminNewProductActivity.class);
                intent.putExtra("category","Laptop & PC");
                startActivity(intent);
            }
        });

        watchImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategoryActivitty.this,AdminNewProductActivity.class);
                intent.putExtra("category","Watches");
                startActivity(intent);
            }
        });

        mobileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategoryActivitty.this,AdminNewProductActivity.class);
                intent.putExtra("category","Mobile phones");
                startActivity(intent);
            }
        });

        adminMngBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AdminCategoryActivitty.this,HomeActivity.class);
                intent.putExtra("admin","Admin");
                startActivity(intent);

            }
        });

        adminnewOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminCategoryActivitty.this,AdminNewOrderActivity.class));
            }
        });

        adminLogOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategoryActivitty.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

    }
}
