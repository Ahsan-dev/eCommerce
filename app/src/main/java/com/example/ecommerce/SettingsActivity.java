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
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecommerce.Prevalent.Prevalent;
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

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private TextView closetxtBtn, updatetxtBtn,changeImgtxtBtn;
    private CircleImageView profileImgView;
    private EditText profNumberEdt, profNameEdt, profAddressEdt;
    private Button setSecurityBtn;
    private Uri imageUri;
    private String myUrl = "";
    private StorageReference ProfImageStoreRef;
    private String checker = "";
    private StorageTask uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        closetxtBtn = findViewById(R.id.settings_close_btn_txtId);
        updatetxtBtn = findViewById(R.id.settings_update_btn_txtId);
        profileImgView = findViewById(R.id.settings_profile_image_Id);
        profNumberEdt = findViewById(R.id.settings_profile_number_Edt_Id);
        profNameEdt = findViewById(R.id.settings_profile_name_Edt_Id);
        profAddressEdt = findViewById(R.id.settings_profile_address_Edt_Id);
        changeImgtxtBtn = findViewById(R.id.settings_change_profile_image_txtId);
        setSecurityBtn = findViewById(R.id.settings_profile_reset_security_btn_Id);

        ProfImageStoreRef = FirebaseStorage.getInstance().getReference().child("Profile Images");



        closetxtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        updatetxtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checker == "clicked"){
                    userInfoSavedWithImage();
                }else {
                    userInfoSaved();
                }
            }
        });


        changeImgtxtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checker = "clicked";
                CropImage.activity(imageUri)
                        .setAspectRatio(1,1)
                        .start(SettingsActivity.this);
            }
        });

        setSecurityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this,ResetPasswordActivity.class);
                intent.putExtra("check","settings");
                startActivity(intent);
            }
        });

        userInfoDisplay(profileImgView, profNumberEdt, profNameEdt, profAddressEdt);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data!=null){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();
            profileImgView.setImageURI(imageUri);
        }else {
            Toast.makeText(getApplicationContext(),"Error, Try again",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SettingsActivity.this,SettingsActivity.class));
            finish();
        }
    }

    private void userInfoSaved() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");

        HashMap<String,Object> userMap = new HashMap<>();
        userMap.put("name",profNameEdt.getText().toString());
        userMap.put("numberOrder",profNumberEdt.getText().toString());
        userMap.put("address",profAddressEdt.getText().toString());

        reference.child(Prevalent.currentOnlineUsers.getNumber()).updateChildren(userMap);

        startActivity(new Intent(SettingsActivity.this,MainActivity.class));
        Toast.makeText(getApplicationContext(),"Profile info updated successfully",Toast.LENGTH_SHORT).show();
        finish();




    }

    private void userInfoSavedWithImage() {

        if(TextUtils.isEmpty(profNameEdt.getText().toString())){
            profNameEdt.setError("Name is mandatory....");
            profNameEdt.requestFocus();
            return;
        }
        else if(TextUtils.isEmpty(profNumberEdt.getText().toString())){
            profNumberEdt.setError("Number is mandatory....");
            profNumberEdt.requestFocus();
            return;
        }
        else if(TextUtils.isEmpty(profAddressEdt.getText().toString())){
            profAddressEdt.setError("Address is mandatory....");
            profAddressEdt.requestFocus();
            return;
        }
        else if(checker.equals("clicked")){
            uploadImage();
        }

    }

    private void uploadImage() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Update Profile");
        progressDialog.setMessage("Please wait, while we are updating profile information..");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if(imageUri != null){

            final  StorageReference fileRef = ProfImageStoreRef.child(Prevalent.currentOnlineUsers.getNumber() + ".jpg");
            uploadTask = fileRef.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }

                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        myUrl = downloadUri.toString();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");

                        HashMap<String,Object> userMap = new HashMap<>();
                        userMap.put("name",profNameEdt.getText().toString());
                        userMap.put("numberOrder",profNumberEdt.getText().toString());
                        userMap.put("address",profAddressEdt.getText().toString());
                        userMap.put("image",myUrl);

                        reference.child(Prevalent.currentOnlineUsers.getNumber()).updateChildren(userMap);

                        progressDialog.dismiss();
                        startActivity(new Intent(SettingsActivity.this,HomeActivity.class));
                        Toast.makeText(getApplicationContext(),"Profile info updated successfully",Toast.LENGTH_SHORT).show();
                        finish();


                    }else {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),"Error to update",Toast.LENGTH_SHORT).show();


                    }
                }
            });




        }else {
            Toast.makeText(getApplicationContext(),"Image is not selected...",Toast.LENGTH_SHORT).show();

        }

    }

    private void userInfoDisplay(final CircleImageView profileImgView, final EditText profNumberEdt, final EditText profNameEdt, final EditText profAddressEdt) {

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUsers.getNumber());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(dataSnapshot.child("image").exists()){
                        String image = dataSnapshot.child("image").getValue().toString();
                        String name = dataSnapshot.child("name").getValue().toString();
                        String number = dataSnapshot.child("number").getValue().toString();
                        String address = dataSnapshot.child("address").getValue().toString();

                        Picasso.get().load(image).placeholder(R.drawable.profile).into(profileImgView);
                        profNumberEdt.setText(number);
                        profNameEdt.setText(name);
                        profAddressEdt.setText(address);


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }
}