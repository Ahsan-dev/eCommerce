package com.example.ecommerce;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class AdminNewProductActivity extends AppCompatActivity {

    private String categoryName, newProductName, newProductDetails, newProductPrice , saveCurrentDate, saveCurrentTime;
    private TextView categoryText;
    private ImageView newProductImg;
    private Button addnewProductBtn;
    private EditText newProductNameEdt,newProductDetailsEdt,newProductPriceEdt;
    private static final int GALLERYIMGREQUESTCODE = 1;
    private Uri imageUri;
    private String productRandomKey, downloadImageUrl;
    private DatabaseReference productDatabaseRef;
    private StorageReference productImagesRef;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_product);

        categoryText = findViewById(R.id.categoryTextId);
        newProductImg = findViewById(R.id.newProductImgId);
        addnewProductBtn = findViewById(R.id.addnewProductBtnId);
        newProductNameEdt = findViewById(R.id.productNameId);
        newProductDetailsEdt = findViewById(R.id.productDetailsId);
        newProductPriceEdt = findViewById(R.id.productPriceId);

        loadingBar = new ProgressDialog(this);


        categoryName = getIntent().getExtras().get("category").toString();
        categoryText.setText("Category: "+categoryName);

        productImagesRef = FirebaseStorage.getInstance().getReference().child("Products Images");
        productDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Products");




        newProductImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        addnewProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                validateProductData();

            }
        });
    }

    private void validateProductData() {

        newProductName = newProductNameEdt.getText().toString();
        newProductDetails = newProductDetailsEdt.getText().toString();
        newProductPrice = newProductPriceEdt.getText().toString();

        if(imageUri == null){
            Toast.makeText(getApplicationContext(),"Products Image is mandatrory...",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(newProductName)){
            newProductNameEdt.setError("Please enter product name...");
            newProductNameEdt.requestFocus();
            return;
        }
        else if(TextUtils.isEmpty(newProductDetails)){
            newProductDetailsEdt.setError("Please enter product details...");
            newProductDetailsEdt.requestFocus();
            return;
        }
        else if(TextUtils.isEmpty(newProductPrice)){
            newProductPriceEdt.setError("Please enter product price...");
            newProductPriceEdt.requestFocus();
            return;
        }

        else{
            loadingBar.setTitle("Adding new Products:");
            loadingBar.setMessage("Plz wait, while we are adding new product to the database");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            storeProductInformation();
        }


    }

    private void storeProductInformation() {

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM-dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        productRandomKey = saveCurrentDate+" "+saveCurrentTime;

        final StorageReference filePath = productImagesRef.child(imageUri.getLastPathSegment() + productRandomKey+".jpg");
        final UploadTask uploadTask = filePath.putFile(imageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loadingBar.dismiss();
                Toast.makeText(getApplicationContext(),"Error: "+e.toString(),Toast.LENGTH_SHORT).show();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {



                Toast.makeText(getApplicationContext(),"Products Image uploaded successfully... ",Toast.LENGTH_SHORT).show();


                final Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                       if(!task.isSuccessful()){
                           throw  task.getException();

                       }

                       downloadImageUrl = filePath.getDownloadUrl().toString();
                       return filePath.getDownloadUrl();


                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {

                        if(task.isSuccessful()){
                            downloadImageUrl = task.getResult().toString();
                            Toast.makeText(getApplicationContext(),"Products Image Url got successfully",Toast.LENGTH_SHORT).show();
                            saveProductInfoToDatabase();
                        }

                    }
                });

            }
        });
    }
    private void saveProductInfoToDatabase() {

        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("pid", productRandomKey);
        productMap.put("date", saveCurrentDate);
        productMap.put("time", saveCurrentTime);
        productMap.put("image", downloadImageUrl);
        productMap.put("category", categoryName);
        productMap.put("name", newProductName);
        productMap.put("details", newProductDetails);
        productMap.put("price", newProductPrice);

        productDatabaseRef.child(productRandomKey).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){

                            startActivity(new Intent(AdminNewProductActivity.this,AdminCategoryActivitty.class));
                            loadingBar.dismiss();
                            Toast.makeText(getApplicationContext(),"Products is updated to the database successfully...",Toast.LENGTH_SHORT).show();
                        }else {
                            loadingBar.dismiss();
                            Toast.makeText(getApplicationContext(),"Database Error: "+task.getException().toString(),Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }

    private void openGallery() {

        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,GALLERYIMGREQUESTCODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode == GALLERYIMGREQUESTCODE && resultCode == RESULT_OK && data != null){

            imageUri = data.getData();

            newProductImg.setImageURI(imageUri);

        }
    }
}
