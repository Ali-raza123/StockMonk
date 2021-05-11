package com.hp.ali.stockmonk.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hp.ali.stockmonk.HomeActivity;
import com.hp.ali.stockmonk.R;
import com.hp.ali.stockmonk.utils.SharedPreferencesManager;

import java.util.HashMap;

import static maes.tech.intentanim.CustomIntent.customType;

public class SignupActivity extends AppCompatActivity {
    EditText edt_email,edt_password,edt_confirm_password;
    Button btn_sign_up;
    private FirebaseAuth mAuth;
    public DatabaseReference userRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        userRef = FirebaseDatabase.getInstance().getReference().child("users");
        FirebaseApp.initializeApp(SignupActivity.this);
        mAuth = FirebaseAuth.getInstance();
        btn_sign_up =  findViewById(R.id.btn_sign_up);
        edt_email =  findViewById(R.id.edt_email);
        edt_password =  findViewById(R.id.edt_password);
        edt_confirm_password =  findViewById(R.id.edt_confirm_password);
        btn_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProgressDialog progressDialog  =  new ProgressDialog(SignupActivity.this);
                progressDialog.show();
                if(validation()){
                    String email =  edt_email.getText().toString();
                    String password =  edt_password.getText().toString();
                    mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                FirebaseUser user = mAuth.getCurrentUser();
                                
                                createUser(user);
                                progressDialog.dismiss();
                            }
                            else{
                                Toast.makeText(SignupActivity.this, "fail: "+task.getException().toString(), Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();

                            }

                        }
                    });

                }
                else{
                    progressDialog.dismiss();
                }

            }
        });
    }

    private void createUser(FirebaseUser user) {
        String userId =  user.getUid();
        HashMap hashMap =  new HashMap();
        hashMap.put("isPremiumMember",false);
        hashMap.put("startDate","null");
        hashMap.put("endDate","null");
        hashMap.put("email",edt_email.getText().toString());
        userRef.child(userId).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                SharedPreferencesManager.setUserID(SignupActivity.this,userId);
                Intent intent = new Intent(SignupActivity.this, HomeActivity.class);
                startActivity(intent);
                customType(SignupActivity.this,"left-to-right");
            }
        });
    }

    public boolean validation(){
        if(edt_email.getText().toString().isEmpty()||edt_password.getText().toString().isEmpty()||edt_confirm_password.getText().toString().isEmpty()){
            if(edt_email.getText().toString().isEmpty()){
                edt_email.setError("Email cannot be empty");
            }
            if(edt_password.getText().toString().isEmpty()){
                edt_password.setError("Email cannot be empty");
            }
            if(edt_confirm_password.getText().toString().isEmpty()){
                edt_confirm_password.setError("Email cannot be empty");
            }



        }
        String password =  edt_password.getText().toString();
        String  confirmpassword =  edt_confirm_password.getText().toString();
        if(!password.equals(confirmpassword)){
            edt_confirm_password.setError("Confirm Password not match");
        }
        else{
            return true;
        }
     return false;
    }
}