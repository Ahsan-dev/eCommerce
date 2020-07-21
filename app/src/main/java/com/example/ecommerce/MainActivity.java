package com.example.ecommerce;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecommerce.Model.Users;
import com.example.ecommerce.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private ProgressDialog loadingBar;
    private DatabaseReference databaseReference;

    Button loginBtn,registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        loginBtn = findViewById(R.id.main_login_buttonId);
        registerBtn = findViewById(R.id.main_joinnow_buttonId);

        Paper.init(this);
        loadingBar = new ProgressDialog(this);


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,RegisterActivity.class));
            }
        });



        String userPhoneKey = Paper.book().read(Prevalent.userPhnKey);
        String userPassKey = Paper.book().read(Prevalent.userPassKey);

        if(userPhoneKey != "" && userPassKey != ""){
            if(!TextUtils.isEmpty(userPhoneKey) && !TextUtils.isEmpty(userPassKey)){
                allowDirectAccessToLogIn(userPhoneKey,userPassKey);

                loadingBar.setTitle("Already logged in...");
                loadingBar.setMessage("Plz wait, You will be redirected to your home page...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

            }
        }



    }

    private void allowDirectAccessToLogIn(final String number,final String password) {

        databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.child("Users").child(number).exists()){



                    Users userData = dataSnapshot.child("Users").child(number).getValue(Users.class);

                    //Log.v("Number:",userData.getNmbr());


                    if(userData.getNumber().equals(number)){

                        if(userData.getPassword().equals(password)){

                            Toast.makeText(getApplicationContext(),"Login Successful.",Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();

                            Prevalent.currentOnlineUsers = userData;
                            startActivity(new Intent(MainActivity.this,HomeActivity.class));



                        }else {

                            Toast.makeText(getApplicationContext(),"Password is incorrect.",Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();

                        }

                    }

                }else {
                    Toast.makeText(getApplicationContext(),"Account with this number "+number+" does not exist",Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
