package com.hp.ali.stockmonk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.hp.ali.stockmonk.model.Posts;
import com.hp.ali.stockmonk.ui.break_out.BreakOutFragment;
import com.hp.ali.stockmonk.ui.knowledge_base.PostDetailActivity;
import com.hp.ali.stockmonk.utils.SharedPreferencesManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static maes.tech.intentanim.CustomIntent.customType;

public class FavoritePost extends AppCompatActivity {
    String post_context;
   static DatabaseReference users_fav,post_ref;
    String userUID;
    ProgressDialog progressDialog;

    static private ArrayList<String> favorite_post_key;
    static private ArrayList<String> favorite_post_breakout;
    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_post);
        recyclerView =  findViewById(R.id.recyclerView);
        favorite_post_key =  new ArrayList<>();
        favorite_post_breakout =  new ArrayList<>();
        progressDialog  =  new ProgressDialog(FavoritePost.this);
        userUID = SharedPreferencesManager.getUserID(FavoritePost.this);

        users_fav = FirebaseDatabase.getInstance().getReference().child("favorite").child(userUID);


        users_fav.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    favorite_post_key.clear();
                    for(DataSnapshot ds: snapshot.getChildren()){
                        String key =  ds.getKey();
                        // key is stored in list
                        favorite_post_key.add(key);
                    }
                    Toast.makeText(FavoritePost.this, ""+favorite_post_key.get(1), Toast.LENGTH_SHORT).show();



//                    FavoritePost_Adapter favoritePost_adapter = new FavoritePost_Adapter(favorite_post_key,FavoritePost.this);
//                    recyclerView.setHasFixedSize(true);
//                    recyclerView.setLayoutManager(new LinearLayoutManager(FavoritePost.this));
//                    recyclerView.setAdapter(favoritePost_adapter);




                }
                for(int i = 0 ;i<favorite_post_key.size();i++){
                    //key 1  breakout is  fectched
                    String context =  favorite_post_key.get(i).toString();
                    //scanning breakout//
                    users_fav.child(context).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                //breakout all items feched//
                                for(DataSnapshot ds: snapshot.getChildren()){
                                    // item id fetched//
                                    String key =  ds.getKey();

                                    HashMap context =  new HashMap();
                                    context.put(context,key);
                                    favorite_post_breakout.add(key);
                                }




                            }
                            FavoritePost_Adapter favoritePost_adapter = new FavoritePost_Adapter(favorite_post_breakout, FavoritePost.this);
                            recyclerView.setHasFixedSize(true);
                            recyclerView.setLayoutManager(new LinearLayoutManager(FavoritePost.this));
                            recyclerView.setAdapter(favoritePost_adapter);


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    Log.d("list", "onCreate: "+favorite_post_breakout);




                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    public class FavoritePost_Adapter extends RecyclerView.Adapter<FavoritePost_Adapter.ViewHolder>{
        ArrayList<Posts> traders_post_list;
        ArrayList<String> breakout_KeyList;
        Context context;

        public FavoritePost_Adapter(ArrayList<String> breakout_KeyList, Context context) {
            this.breakout_KeyList = breakout_KeyList;
            this.context =  context;
        }
        @Override
        public FavoritePost_Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View listItem= layoutInflater.inflate(R.layout.index_post_item_layout, parent, false);
            FavoritePost_Adapter.ViewHolder viewHolder = new FavoritePost_Adapter.ViewHolder(listItem);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(FavoritePost_Adapter.ViewHolder holder, int position) {
            holder.setIsRecyclable(false);


            String key = breakout_KeyList.get(position).toString();

            Toast.makeText(context, ""+key, Toast.LENGTH_SHORT).show();


            users_fav.child(key).child(favorite_post_breakout.get(position)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        if(snapshot.child("explaination").exists()){
                            holder.btn_why.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String message =  snapshot.child("explaination").getValue().toString();
                                    Toast.makeText(context, ""+message, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else{
                            holder.btn_why.setVisibility(View.GONE);
                        }
                        holder.linear_lyt.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent =  new Intent(context, PostDetailActivity.class);
                                intent.putExtra("post_id",key);
                                intent.putExtra("ref",post_context);
                                context.startActivity(intent);
                                customType(context,"left-to-right");

                            }
                        });
                        holder.txt_post_title.setText(snapshot.child("title").getValue().toString());

                        holder.txt_post_detail.setText(snapshot.child("description").getValue().toString());
                        if(snapshot.child("imgUrl").exists()){
                            String url =  snapshot.child("imgUrl").getValue().toString();
                            StorageReference storage =  FirebaseStorage.getInstance().getReferenceFromUrl(url);
                            storage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Glide.with(context).load(uri).placeholder(R.drawable.ic_transparent).into(holder.post_img);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(context, ""+e.toString(), Toast.LENGTH_SHORT).show();

                                }
                            });
                            holder.lyt_img.setVisibility(View.VISIBLE);


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
                            holder.txt_time_ago.setText(String.valueOf(diffdays)+" days ago");
                        }
                        else{
                            if(diffHours!=0){
                                holder.txt_time_ago.setText(String.valueOf(diffHours)+" hours ago");
                            }
                            else{
                                if(diffMinutes!=0){
                                    holder.txt_time_ago.setText(String.valueOf(diffMinutes)+" minutes ago");
                                }
                                else{
                                    holder.txt_time_ago.setText(String.valueOf(diffSeconds)+" seconds ago");

                                }

                            }
                        }

                        progressDialog.dismiss();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });




















        }


        @Override
        public int getItemCount() {
            return favorite_post_breakout.size();

        }

        public  class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView post_img,img_like,img_fav,img_share;
            public TextView txt_post_title,txt_post_detail,txt_total_likes,txt_total_fav,txt_time_ago;
            public LinearLayout linear_lyt;
            CardView lyt_img;
            Button btn_why;
            public ViewHolder(View itemView) {
                super(itemView);
                this.post_img = (ImageView) itemView.findViewById(R.id.post_img);
                this.img_like = (ImageView) itemView.findViewById(R.id.img_like);
                this.img_share = (ImageView) itemView.findViewById(R.id.img_share);
                this.img_fav = (ImageView) itemView.findViewById(R.id.img_fav);
                this.txt_total_likes = (TextView) itemView.findViewById(R.id.txt_total_likes);
                this.txt_time_ago = (TextView) itemView.findViewById(R.id.txt_time_ago);
                this.txt_total_fav = (TextView) itemView.findViewById(R.id.txt_total_fav);
                this.lyt_img = (CardView) itemView.findViewById(R.id.lyt_img);
                this.btn_why = (Button) itemView.findViewById(R.id.btn_why);
                this.txt_post_title = (TextView) itemView.findViewById(R.id.txt_post_title);
                this.txt_post_detail = (TextView) itemView.findViewById(R.id.txt_post_detail);
                this.linear_lyt = (LinearLayout) itemView.findViewById(R.id.linear_lyt);
            }
        }
    }

}