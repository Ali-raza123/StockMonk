package com.hp.ali.stockmonk.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.hp.ali.stockmonk.HomeActivity;
import com.hp.ali.stockmonk.R;
import com.hp.ali.stockmonk.utils.SharedPreferencesManager;

import static maes.tech.intentanim.CustomIntent.customType;

public class LoginActivity extends AppCompatActivity {

    Button btn_sign_in,btn_sign_up;
    TextView txt_forgot_password,tvTagLine;
    EditText edt_email,edt_password;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        txt_forgot_password =  findViewById(R.id.txt_forgot_password);
        tvTagLine = findViewById(R.id.tvTagline);
        tvTagLine.setSelected(true);
        edt_email =  findViewById(R.id.edt_email);
        edt_password =  findViewById(R.id.edt_password);
        btn_sign_in =  findViewById(R.id.btn_sign_in);
        btn_sign_up =  findViewById(R.id.btn_sign_up);
        btn_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edt_email.getText().toString().isEmpty()||edt_password.getText().toString().isEmpty())
                {
                    if(edt_email.getText().toString().isEmpty()){
                        edt_email.setError("Email cannot be empty");
                    }
                    if(edt_password.getText().toString().isEmpty()){
                        edt_password.setError("Email cannot be empty");
                    }
                }
                else{
                    ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
                    progressDialog.show();
                    mAuth.signInWithEmailAndPassword(edt_email.getText().toString(),edt_password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            progressDialog.dismiss();
                            Intent intent =  new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                            String id =  FirebaseAuth.getInstance().getCurrentUser().getUid();
                            SharedPreferencesManager.registerUser(LoginActivity.this,id,edt_email.getText().toString());
                            SharedPreferencesManager.setUserloginTrue(LoginActivity.this);
                            customType(LoginActivity.this,"left-to-right");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, ""+e, Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });

        btn_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent  = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
                customType(LoginActivity.this,"left-to-right");
            }
        });

        txt_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
                customType(LoginActivity.this,"left-to-right");

            }
        });
    }

}