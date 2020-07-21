package com.example.ecommerce;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.ecommerce.Model.Products;
import com.example.ecommerce.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProductDetailsActivity extends AppCompatActivity {

    private FloatingActionButton addtoCartBtn;
    private ImageView productImg;
    private ElegantNumberButton numberButton;
    private TextView productDNameTxt, productDDetailsTxt, productDPriceTxt;
    private String proId = "", state = "Normal";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        addtoCartBtn = findViewById(R.id.product_details_fab_id);
        productImg = findViewById(R.id.product_details_imageId);
        numberButton = findViewById(R.id.product_details_ElegantBtnId);
        productDNameTxt = findViewById(R.id.product_details_NameId);
        productDDetailsTxt = findViewById(R.id.product_details_DescriptionId);
        productDPriceTxt = findViewById(R.id.product_details_PriceId);

        proId = getIntent().getStringExtra("pid");


        addtoCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(state.equals("Order Placed")||state.equals("Order Shipped")){

                    Toast.makeText(getApplicationContext(),"You can purchase more products, once you receive your last order",Toast.LENGTH_LONG).show();
                    startActivity(new Intent(ProductDetailsActivity.this,HomeActivity.class));

                }else {

                    addingToCartList();
                    startActivity(new Intent(ProductDetailsActivity.this,CartActivity.class));

                }


            }
        });

        getProductDetails(proId);




    }

    @Override
    protected void onStart() {
        super.onStart();
        checkingOrderState();
    }

    private void addingToCartList() {
        String saveCurrentDate, saveCurrentTime;
        Calendar callForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMMM dd,yyyy");
        saveCurrentDate = currentDate.format(callForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(callForDate.getTime());

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
        final HashMap<String,Object> cartMap = new HashMap<>();
        cartMap.put("pid",proId);
        cartMap.put("name",productDNameTxt.getText().toString());
        cartMap.put("price",productDPriceTxt.getText().toString());
        cartMap.put("date",saveCurrentDate);
        cartMap.put("time",saveCurrentTime);
        cartMap.put("quantity",numberButton.getNumber());
        cartMap.put("discount","");
        cartListRef.child("User View").child(Prevalent.currentOnlineUsers.getNumber()).child("Products").child(proId)
                .updateChildren(cartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){

                    cartListRef.child("Admin View").child(Prevalent.currentOnlineUsers.getNumber()).child("Products").child(proId)
                            .updateChildren(cartMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    Toast.makeText(getApplicationContext(),"Add to the cart successfully",Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(ProductDetailsActivity.this,HomeActivity.class));

                                }
                            });



                }

            }
        });







    }

    private void getProductDetails(String proId) {

        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("Products");
        productRef.child(proId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Products products = dataSnapshot.getValue(Products.class);
                    productDNameTxt.setText(products.getName());
                    productDDetailsTxt.setText(products.getDetails());
                    productDPriceTxt.setText(products.getPrice());
                    Picasso.get().load(products.getImage()).placeholder(R.drawable.books).into(productImg);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkingOrderState(){
        final DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentOnlineUsers.getNumber());
        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    String orderState = dataSnapshot.child("state").getValue().toString();


                    if(orderState.equals("Shipped")){

                        state = "Order Shipped";



                    }else if(orderState.equals("Not Shipped")){

                        state = "Order Placed";
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}