package com.hp.ali.stockmonk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hp.ali.stockmonk.ui.knowledge_base.PostDetailActivity;
import com.hp.ali.stockmonk.utils.SharedPreferencesManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static maes.tech.intentanim.CustomIntent.customType;

public class TestActivity extends AppCompatActivity {

    DatabaseReference users_fav,postref;
    String userUID;
    LinearLayout linear_lyt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        linear_lyt =  findViewById(R.id.linear_lyt);
        userUID = SharedPreferencesManager.getUserID(TestActivity.this);
        users_fav = FirebaseDatabase.getInstance().getReference().child("favorite").child(userUID);
        users_fav.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    String  tablekey = ds.getKey();
                    for(DataSnapshot ds1:ds.getChildren()){
                        String  postkey =  ds1.getKey();
                        String isPostfavorite =  ds1.child("isPostfavorite").getValue().toString();
                        if(isPostfavorite.equalsIgnoreCase("true")){
                            infalateView(tablekey,postkey);
                        }


                    }
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void infalateView(String tablekey, String postkey){

        LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.favorite_post_inflate_layout,null);
        LinearLayout lyt = view.findViewById(R.id.linear_lyt);
        Button btn_delete =  view.findViewById(R.id.btn_delete);
        TextView txt_post_title =  view.findViewById(R.id.txt_post_title);
        TextView txt_post_detail =  view.findViewById(R.id.txt_post_detail);
        ImageView post_img = view.findViewById(R.id.post_img);
        CardView lyt_img =  view.findViewById(R.id.lyt_img);
        TextView txt_time_ago =  view.findViewById(R.id.txt_time_ago);
        postref =  FirebaseDatabase.getInstance().getReference().child(tablekey).child(postkey);
        postref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               String title =  snapshot.child("title").getValue().toString();
                String description =snapshot.child("description").getValue().toString();
                txt_post_title.setText(title);
                txt_post_detail.setText(description);
                if(snapshot.child("imgUrl").exists()){
                    String url =  snapshot.child("imgUrl").getValue().toString();
                    StorageReference storage =  FirebaseStorage.getInstance().getReferenceFromUrl(url);
                    storage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(view.getContext()).load(uri).placeholder(R.drawable.ic_transparent).into(post_img);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {


                        }
                    });
                    lyt_img.setVisibility(View.VISIBLE);


                }
                Time t = new Time(Time.getCurrentTimezone());
                t.setToNow();
                String date1 = t.format("%d/%m/%y");
                Date date = new Date(System.currentTimeMillis());
                SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss", Locale.ENGLISH);
                String var = dateFormat.format(date);
                String currentTime = date1+" "+var;
                String dateStart = snapshot.child("dateTime").getValue().toString();
                String dateStop  = currentTime;

                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy HH:mm:ss");

                Date d1 = null;
                Date d2 = null;
                try {
                    d1 = format.parse(dateStart);
                    d2 = format.parse(dateStop);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                long diff = d2.getTime() - d1.getTime();
                long diffSeconds = diff / 1000 % 60;
                long diffMinutes = diff / (60 * 1000) % 60;
                long diffHours = diff / (60 * 60 * 1000);
                long diffdays = diff / (24 * 60 * 60 * 1000);
                if(diffdays!=0){
                    txt_time_ago.setText(String.valueOf(diffdays)+" days ago");
                }
                else{
                    if(diffHours!=0){
                        txt_time_ago.setText(String.valueOf(diffHours)+" hours ago");
                    }
                    else{
                        if(diffMinutes!=0){
                            txt_time_ago.setText(String.valueOf(diffMinutes)+" minutes ago");
                        }
                        else{
                            txt_time_ago.setText(String.valueOf(diffSeconds)+" seconds ago");

                        }

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        lyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(view.getContext(), PostDetailActivity.class);
                intent.putExtra("post_id",postkey);
                intent.putExtra("ref",tablekey);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                view.getContext().startActivity(intent);
                customType(TestActivity.this,"left-to-right");
            }
        });
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                users_fav.child(tablekey).child(postkey).removeValue();
                linear_lyt.removeView(view);
            }
        });
        linear_lyt.addView(view);
    }

}