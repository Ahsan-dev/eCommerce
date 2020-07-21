package com.example.ecommerce;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecommerce.Model.Users;
import com.example.ecommerce.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    private Button loginBtn;
    private EditText nmbrEdt, passEdt;
    private CheckBox passCheck;
    private TextView forgetPass, adminTxt, notAdminTxt;

    private ProgressDialog loadingBar;
    private DatabaseReference databaseReference;

    private String PARENTDB_NAME = "Users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        loginBtn = findViewById(R.id.login_buttonId);
        nmbrEdt = findViewById(R.id.phn_numberEdtId);
        passEdt = findViewById(R.id.phn_passwordEdtId);
        passCheck = findViewById(R.id.logIncheckboxId);
        forgetPass = findViewById(R.id.forgetpassId);
        adminTxt = findViewById(R.id.admin_panel_text);
        notAdminTxt = findViewById(R.id.not_admin_panel_text);

        loadingBar = new ProgressDialog(this);


        Paper.init(this);



        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginaccess();
            }
        });

        adminTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginBtn.setText("Log in as an admin");
                adminTxt.setVisibility(View.INVISIBLE);
                notAdminTxt.setVisibility(View.VISIBLE);
                PARENTDB_NAME = "Admins";

            }
        });

        notAdminTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loginBtn.setText("Log in");
                adminTxt.setVisibility(View.VISIBLE);
                notAdminTxt.setVisibility(View.INVISIBLE);
                PARENTDB_NAME = "Users";

            }
        });

        forgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,ResetPasswordActivity.class);
                intent.putExtra("check","login");
                startActivity(intent);
            }
        });


    }

    private void loginaccess() {
       String number = nmbrEdt.getText().toString().trim();
        String password = passEdt.getText().toString().trim();

        nmbrEdt.setText("");
        passEdt.setText("");


        if(TextUtils.isEmpty(number)){
            nmbrEdt.setError("Plz enter your number.");
            nmbrEdt.requestFocus();
            return;
        }

        else if(TextUtils.isEmpty(password)){
            passEdt.setError("Plz enter your password.");
            passEdt.requestFocus();
            return;
        }

        else{
            loadingBar.setTitle("Login to your Account");
            loadingBar.setMessage("Plz wait, while we are checking the credentials");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            login(number,password);
        }
    }

    private void login( final String number, final  String password) {

        if(passCheck.isChecked()){
            Paper.book().write(Prevalent.userPhnKey,number);
            Paper.book().write(Prevalent.userPassKey,password);

        }

        databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.child(PARENTDB_NAME).child(number).exists()){



                    Users userData = dataSnapshot.child(PARENTDB_NAME).child(number).getValue(Users.class);

                    //Log.v("Number:",userData.getNmbr());


                    if(userData.getNumber().equals(number)){

                        if(userData.getPassword().equals(password)){

                            if(PARENTDB_NAME.equals("Users")){

                                Toast.makeText(getApplicationContext(),"Login Successful.",Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                Intent intent =new Intent(LoginActivity.this,HomeActivity.class);
                                Prevalent.currentOnlineUsers = userData;
                                startActivity(intent);

                            }
                            else if(PARENTDB_NAME.equals("Admins")){

                                Toast.makeText(getApplicationContext(),"Login as an admin Successful.",Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                startActivity(new Intent(LoginActivity.this,AdminCategoryActivitty.class));

                            }





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
