package com.example.ecommerce;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private Button createNowBtn;
    private EditText registerNameEdt,registerNmbrEdt,registerPassEdt;
    private String name,number,password;
    private ProgressDialog loadingBar;

    private DatabaseReference rootRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        createNowBtn = findViewById(R.id.register_buttonId);
        registerNameEdt = findViewById(R.id.register_nameEdtId);
        registerNmbrEdt = findViewById(R.id.register_numberEdtId);
        registerPassEdt = findViewById(R.id.register_passwordEdtId);
        loadingBar = new ProgressDialog(this);

        createNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();

                registerNameEdt.setText("");
                registerNmbrEdt.setText("");
                registerPassEdt.setText("");
            }
        });


    }

    private void createAccount() {

        name = registerNameEdt.getText().toString();
        number = registerNmbrEdt.getText().toString();
        password = registerPassEdt.getText().toString();

        if(TextUtils.isEmpty(name)){
            registerNameEdt.setError("Plz enter your name.");
            registerNameEdt.requestFocus();
            return;

        }

        if(TextUtils.isEmpty(number)){
            registerNmbrEdt.setError("Plz enter your number.");
            registerNmbrEdt.requestFocus();
            return;
        }

        if(TextUtils.isEmpty(password)){
            registerPassEdt.setError("Plz enter your password.");
            registerPassEdt.requestFocus();
            return;
        }

        else{

            loadingBar.setTitle("Create Account");
            loadingBar.setMessage("Plz wait, while we are checking the credentials");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            validatePhnNumber(name,number,password);

        }
    }

    private void validatePhnNumber(final String name, final String number, final String password) {

        rootRef = FirebaseDatabase.getInstance().getReference();

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(!(dataSnapshot.child("Users").child(number).exists())){

                    HashMap<String, Object> userMap = new HashMap<>();
                    userMap.put("number",number);
                    userMap.put("password",password);
                    userMap.put("name",name);

                    rootRef.child("Users").child(number).updateChildren(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(),"Congratulations... Your are Successfully registered",Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));

                            }else {
                                loadingBar.dismiss();
                                Toast.makeText(getApplicationContext(),"Network failed!!! plz try again later...",Toast.LENGTH_SHORT).show();

                            }

                        }
                    });




                }else {
                    Toast.makeText(getApplicationContext(),"This number "+number+" already used",Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(getApplicationContext(),"Plz try again using another phn number...",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this,MainActivity.class));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
