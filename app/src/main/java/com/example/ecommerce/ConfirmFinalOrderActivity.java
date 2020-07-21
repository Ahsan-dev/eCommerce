package com.example.ecommerce;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ecommerce.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ConfirmFinalOrderActivity extends AppCompatActivity {

    private EditText shipmentNameTxt, shipmentPhoneTxt, shipmentAddressTxt, shipmentCityTxt;
    private Button shipmentConfirmBtn;
    private String totalPriceString = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);

        totalPriceString = getIntent().getStringExtra("TotalPrice");

        shipmentNameTxt = findViewById(R.id.shipment_user_name_txtId);
        shipmentPhoneTxt = findViewById(R.id.shipment_user_phone_txtId);
        shipmentAddressTxt = findViewById(R.id.shipment_user_address_txtId);
        shipmentCityTxt = findViewById(R.id.shipment_user_city_txtId);
        shipmentConfirmBtn = findViewById(R.id.shipment_confirm_Btn_Id);

        shipmentConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Check();
            }
        });



    }

    private void Check() {

        if(TextUtils.isEmpty(shipmentNameTxt.getText().toString())){
            shipmentNameTxt.setError("Your Name is mandatory");
            shipmentNameTxt.requestFocus();
            return;
        }
        else if(TextUtils.isEmpty(shipmentPhoneTxt.getText().toString())){
            shipmentPhoneTxt.setError("Your Phone Number is mandatory");
            shipmentPhoneTxt.requestFocus();
            return;
        }
        else if(TextUtils.isEmpty(shipmentAddressTxt.getText().toString())){
            shipmentAddressTxt.setError("Your Address is mandatory");
            shipmentAddressTxt.requestFocus();
            return;
        }
        else if(TextUtils.isEmpty(shipmentCityTxt.getText().toString())){
            shipmentCityTxt.setError("Your City is mandatory");
            shipmentCityTxt.requestFocus();
            return;
        }else {
            confirmOrderNow();
        }

    }

    private void confirmOrderNow() {

        String saveCurrentDate, saveCurrentTime;
        Calendar callForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMMM dd,yyyy");
        saveCurrentDate = currentDate.format(callForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(callForDate.getTime());

        final DatabaseReference OrderRef = FirebaseDatabase.getInstance().getReference().child("Orders");
        final HashMap<String,Object> orderMap = new HashMap<>();
        orderMap.put("totalPrice",totalPriceString);
        orderMap.put("CustomerName",shipmentNameTxt.getText().toString());
        orderMap.put("CustomerPhone",shipmentPhoneTxt.getText().toString());
        orderMap.put("CustomerAddress",shipmentAddressTxt.getText().toString());
        orderMap.put("CustomerCity",shipmentCityTxt.getText().toString());
        orderMap.put("time",saveCurrentTime);
        orderMap.put("date",saveCurrentDate);
        orderMap.put("state","Not Shipped");
        OrderRef.child(Prevalent.currentOnlineUsers.getNumber())
                .updateChildren(orderMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){
                            FirebaseDatabase.getInstance().getReference().child("Cart List")
                                    .child("User View")
                                    .child(Prevalent.currentOnlineUsers.getNumber())
                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){
                                        Toast.makeText(getApplicationContext(),"Your final order has been placed successfully",Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(ConfirmFinalOrderActivity.this,HomeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }

                                }
                            });
                        }

                    }
                });



    }
}