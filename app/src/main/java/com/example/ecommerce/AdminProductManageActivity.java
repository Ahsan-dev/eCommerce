package com.example.ecommerce;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class AdminProductManageActivity extends AppCompatActivity {

    private TextView categoryTxt;
    private EditText manageNameEdt, mngDetailsEdt, mngPriceEdt;
    private ImageView mngImageView;
    private Button mngModifyBtn, mngDltBtn;
    private String proId = "";
    private DatabaseReference productsMngRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_product_manage);

        categoryTxt = findViewById(R.id.admin_manage_categoryTextId);
        manageNameEdt = findViewById(R.id.admin_manage_ProductNameId);
        mngDetailsEdt = findViewById(R.id.admin_manage_ProductDetailsId);
        mngPriceEdt = findViewById(R.id.admin_manage_ProductPriceId);
        mngImageView = findViewById(R.id.admin_manage_ProductImgId);
        mngModifyBtn = findViewById(R.id.admin_manage_modify_product_BtnId);
        mngDltBtn = findViewById(R.id.admin_manage_delete_product_BtnId);
        proId = getIntent().getStringExtra("pid");

        productsMngRef = FirebaseDatabase.getInstance().getReference().child("Products").child(proId);


        mngModifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifyProduct();
            }
        });

        mngDltBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productsMngRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        Toast.makeText(getApplicationContext(),"Product deleted successfully..",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AdminProductManageActivity.this,AdminCategoryActivitty.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();

                    }
                });
            }
        });





    }

    private void modifyProduct() {

        if(TextUtils.isEmpty(manageNameEdt.getText().toString())){
            manageNameEdt.setError("Product name is mandatory");
            manageNameEdt.requestFocus();
            return;
        }
        else if(TextUtils.isEmpty(mngDetailsEdt.getText().toString())){
            mngDetailsEdt.setError("Product name is mandatory");
            mngDetailsEdt.requestFocus();
            return;
        }
        else if(TextUtils.isEmpty(mngPriceEdt.getText().toString())){
            mngPriceEdt.setError("Product name is mandatory");
            mngPriceEdt.requestFocus();
            return;
        }
        else {

            HashMap<String, Object> productModifyMap = new HashMap<>();
            productModifyMap.put("pid", proId);
            productModifyMap.put("name", manageNameEdt.getText().toString());
            productModifyMap.put("details", mngDetailsEdt.getText().toString());
            productModifyMap.put("price", mngPriceEdt.getText().toString());

            productsMngRef.updateChildren(productModifyMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    Toast.makeText(getApplicationContext(),"Product modified successfully..",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AdminProductManageActivity.this,AdminCategoryActivitty.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();

                }
            });




        }
    }

    @Override
    protected void onStart() {
        super.onStart();


        productsMngRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    String pName = dataSnapshot.child("name").getValue().toString();
                    String pDetails = dataSnapshot.child("details").getValue().toString();
                    String pPrice = dataSnapshot.child("price").getValue().toString();
                    String pImage = dataSnapshot.child("image").getValue().toString();
                    String pCategory=dataSnapshot.child("category").getValue().toString();

                    categoryTxt.setText(pCategory);
                    manageNameEdt.setText(pName);
                    mngDetailsEdt.setText(pDetails);
                    mngPriceEdt.setText(pPrice);

                    Picasso.get().load(pImage).placeholder(R.drawable.applogo).into(mngImageView);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}