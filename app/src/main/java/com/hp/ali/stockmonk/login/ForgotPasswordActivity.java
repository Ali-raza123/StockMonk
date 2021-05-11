package com.hp.ali.stockmonk.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.hp.ali.stockmonk.R;

import static maes.tech.intentanim.CustomIntent.customType;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText edt_email;
    Button btn_submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        btn_submit =  findViewById(R.id.btn_submit);
        edt_email  = findViewById(R.id.edt_email);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email =  edt_email.getText().toString();
                resetPassword(email);
            }
        });

    }

    private void resetPassword(String email) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ForgotPasswordActivity.this, "Link succesfully sent to your gmail", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ForgotPasswordActivity.this, SplashAcitivity.class);
                            startActivity(intent);
                            customType(ForgotPasswordActivity.this,"fadein-to-fadeout");
                        }
                    }
                });

    }

}