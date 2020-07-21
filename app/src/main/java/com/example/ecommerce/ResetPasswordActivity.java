package com.example.ecommerce;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecommerce.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ResetPasswordActivity extends AppCompatActivity {

    private TextView resetTitle,resetSecurityTitle;
    private EditText rstPhoneEdt, rstQuestion1Edt, rstQuestion2Edt;
    private Button verifyBtn;
    private String check = "";
    private DatabaseReference securityUserRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        resetTitle = findViewById(R.id.reset_pass_set_security_TxtId);
        resetSecurityTitle = findViewById(R.id.reset_pass_set_security_ques_TxtId);
        rstPhoneEdt = findViewById(R.id.reset_pass_phone_number_EdtId);
        rstQuestion1Edt = findViewById(R.id.reset_pass_security_question1_EdtId);
        rstQuestion2Edt = findViewById(R.id.reset_pass_security_question2_EdtId);
        verifyBtn = findViewById(R.id.reset_pass_security_verify_button_Id);
        securityUserRef = FirebaseDatabase.getInstance().getReference().child("Users");

        check = getIntent().getStringExtra("check");




    }

    @Override
    protected void onStart() {
        super.onStart();



        if(check.equals("settings")){

            displayAnswers();

            resetTitle.setText("Set Security Answers");
            rstPhoneEdt.setVisibility(View.GONE);
            resetSecurityTitle.setText("Set Answers for these security questions below. In caase of resetting password these will be needed.");
            verifyBtn.setText("Set Answers");
            verifyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(TextUtils.isEmpty(rstQuestion1Edt.getText().toString())){
                        rstQuestion1Edt.setError("Please Answer this first Question");
                        rstQuestion1Edt.requestFocus();
                        return;
                    }
                   else if(TextUtils.isEmpty(rstQuestion2Edt.getText().toString())){
                        rstQuestion2Edt.setError("Please Answer this second Question");
                        rstQuestion2Edt.requestFocus();
                        return;
                    }else {
                        HashMap<String,Object> securityMap = new HashMap<>();
                        securityMap.put("answer1",rstQuestion1Edt.getText().toString().toLowerCase());
                        securityMap.put("answer2",rstQuestion2Edt.getText().toString().toLowerCase());

                        securityUserRef.child(Prevalent.currentOnlineUsers.getNumber()).child("SecurityAnswers").updateChildren(securityMap)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if(task.isSuccessful()){
                                            Toast.makeText(getApplicationContext(),"You have answered successfully",Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(ResetPasswordActivity.this,SettingsActivity.class));
                                            finish();
                                        }

                                    }
                                });
                    }



                }
            });

        }else{

            resetTitle.setText("Password verification");
            rstPhoneEdt.setVisibility(View.VISIBLE);
            resetSecurityTitle.setText("Answer these security questions below, you have set yet.");
            verifyBtn.setText("Verify");
            verifyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final String phoneUser = rstPhoneEdt.getText().toString();

                    if(TextUtils.isEmpty(rstQuestion1Edt.getText().toString())){
                        rstQuestion1Edt.setError("Please Answer this first Question");
                        rstQuestion1Edt.requestFocus();
                        return;
                    }
                    else if(TextUtils.isEmpty(rstQuestion2Edt.getText().toString())) {
                        rstQuestion2Edt.setError("Please Answer this second Question");
                        rstQuestion2Edt.requestFocus();
                        return;
                    }
                    else if (TextUtils.isEmpty(phoneUser)) {
                            rstPhoneEdt.setError("Please Enter Your Phone Number");
                            rstPhoneEdt.requestFocus();
                            return;
                    }else {

                        securityUserRef.child(phoneUser).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if(dataSnapshot.exists()){
                                    if(dataSnapshot.hasChild("SecurityAnswers")){

                                        String fAns = dataSnapshot.child("SecurityAnswers").child("answer1").getValue().toString();
                                        String sAns = dataSnapshot.child("SecurityAnswers").child("answer2").getValue().toString();

                                        if(fAns.equals(rstQuestion1Edt.getText().toString().toLowerCase()) && sAns.equals(rstQuestion2Edt.getText().toString().toLowerCase())){

                                            final AlertDialog.Builder builder = new AlertDialog.Builder(ResetPasswordActivity.this);
                                            builder.setTitle("Reset your password");
                                            builder.setCancelable(false);
                                            final LinearLayout linearEdt = new LinearLayout(ResetPasswordActivity.this);
                                            linearEdt.setOrientation(LinearLayout.VERTICAL);
                                            linearEdt.setPadding(25,25,25,25);
                                            final  EditText resetPassEdt = new EditText(ResetPasswordActivity.this);
                                            resetPassEdt.setHint("Enter new password");
                                            resetPassEdt.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                            linearEdt.addView(resetPassEdt);
                                            final  EditText resetConfirmPassEdt = new EditText(ResetPasswordActivity.this);
                                            resetConfirmPassEdt.setHint("Confirm new password");
                                            resetConfirmPassEdt.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                            linearEdt.addView(resetConfirmPassEdt);

                                            builder.setView(linearEdt);

                                            builder.setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    if(TextUtils.isEmpty(resetPassEdt.getText().toString())){
                                                        resetPassEdt.setError("Please Enter new password");
                                                        resetPassEdt.requestFocus();
                                                        return;
                                                    }
                                                    else if(TextUtils.isEmpty(resetConfirmPassEdt.getText().toString())) {
                                                        resetConfirmPassEdt.setError("Please confirm this new password");
                                                        resetConfirmPassEdt.requestFocus();
                                                        return;
                                                    }
                                                    else {
                                                        if(resetConfirmPassEdt.getText().toString().equals(resetPassEdt.getText().toString())){
                                                            //HashMap<String,Object> resetPassmap = new HashMap<>();
                                                            //resetPassmap.put("password",resetPassEdt.getText().toString());
                                                            securityUserRef.child(phoneUser).child("password").setValue(resetPassEdt.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if(task.isSuccessful()){
                                                                        Toast.makeText(getApplicationContext(),"Password has reset successfully",Toast.LENGTH_LONG).show();
                                                                        startActivity(new Intent(ResetPasswordActivity.this,LoginActivity.class));
                                                                        finish();
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }


                                                }
                                            });

                                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                    dialog.dismiss();

                                                }
                                            });

                                            builder.show();


                                        }else {

                                            Toast.makeText(getApplicationContext(),"Answers are wrong",Toast.LENGTH_LONG).show();

                                        }

                                    }else{
                                        Toast.makeText(getApplicationContext(),"You have not set security answers",Toast.LENGTH_LONG).show();
                                    }


                                }else {
                                    Toast.makeText(getApplicationContext(),"User with this Phone number does not exist",Toast.LENGTH_LONG).show();

                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });



                    }




                }
            });

        }
    }

    private void displayAnswers() {

        securityUserRef.child(Prevalent.currentOnlineUsers.getNumber()).child("SecurityAnswers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    String ans1 = dataSnapshot.child("answer1").getValue().toString();
                    String ans2 = dataSnapshot.child("answer2").getValue().toString();

                    rstQuestion1Edt.setText(ans1);
                    rstQuestion2Edt.setText(ans2);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}