package com.hp.ali.stockmonk.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hp.ali.stockmonk.HomeActivity;
import com.hp.ali.stockmonk.R;
import com.onesignal.OneSignal;

import static maes.tech.intentanim.CustomIntent.customType;

public class SplashAcitivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_acitivity);
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
        Handler handler =  new Handler();
        FirebaseUser auth =  FirebaseAuth.getInstance().getCurrentUser();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if(auth!=null){
                    Intent intent =  new Intent(SplashAcitivity.this, HomeActivity.class);
                    startActivity(intent);
                    customType(SplashAcitivity.this,"left-to-right");
                }
                else
                {
                    Intent intent =  new Intent(SplashAcitivity.this, LoginActivity.class);
                    startActivity(intent);
                    customType(SplashAcitivity.this,"left-to-right");

                }

            }
        },3000);
    }
}