package com.hp.ali.stockmonk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hp.ali.stockmonk.login.LoginActivity;
import com.hp.ali.stockmonk.model.Updates;
import com.hp.ali.stockmonk.ui.knowledge_base.PostDetailActivity;

import java.util.ArrayList;

import static maes.tech.intentanim.CustomIntent.customType;

public class FavoriteActivity extends AppCompatActivity {
    ImageView img_back;
    Button btn_knowledgebase,btn_intraday,btn_index_position,btn_breakout,btn_premium;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        btn_knowledgebase =  findViewById(R.id.btn_knowledgebase);
        btn_knowledgebase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(FavoriteActivity.this,FavoritePost.class);
                intent.putExtra("context","KnowledgeBase");
                startActivity(intent);
                customType(FavoriteActivity.this,"left-to-right");
            }
        });
        btn_intraday =  findViewById(R.id.btn_intraday);
        btn_intraday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(FavoriteActivity.this,FavoritePost.class);
                intent.putExtra("context","Intraday");
                startActivity(intent);
                customType(FavoriteActivity.this,"left-to-right");
            }
        });
        btn_index_position =  findViewById(R.id.btn_index_position);
        btn_index_position.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(FavoriteActivity.this,FavoritePost.class);
                intent.putExtra("context","IndexPosition");
                startActivity(intent);
                customType(FavoriteActivity.this,"left-to-right");
            }
        });
        btn_breakout =  findViewById(R.id.btn_breakout);
        btn_breakout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(FavoriteActivity.this,FavoritePost.class);
                intent.putExtra("context","breakouts");
                startActivity(intent);
                customType(FavoriteActivity.this,"left-to-right");
            }
        });
        btn_premium =  findViewById(R.id.btn_premium);
        btn_premium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent =  new Intent(FavoriteActivity.this,FavoritePost.class);
//                intent.putExtra("context","premium");
//                startActivity(intent);
//                customType(FavoriteActivity.this,"left-to-right");
            }
        });
        img_back =  findViewById(R.id.img_back);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             onBackPressed();
            }
        });

    }
}