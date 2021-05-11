package com.hp.ali.stockmonk.ui.knowledge_base;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.hp.ali.stockmonk.R;
import com.hp.ali.stockmonk.model.Updates;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class PostDetailActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    private ArrayList<Updates> post_updates;
    String post_id,ref;
    DatabaseReference postRef,postUpdates;
    TextView txt_title,txt_date_time,txt_desciption;
    ImageView img_post,img_back;
    static  Uri imageuri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        post_updates =  new ArrayList<>();
        img_back = findViewById(R.id.img_back);
        txt_title = findViewById(R.id.txt_title);
        txt_date_time = findViewById(R.id.txt_date_time);
        img_post = findViewById(R.id.img_post);
        txt_desciption = findViewById(R.id.txt_desciption);
        recyclerView =  findViewById(R.id.recyclerView);
        post_id =  getIntent().getStringExtra("post_id");
        Log.d("id", "onCreate: "+post_id);
        ref =  getIntent().getStringExtra("ref");
        postRef = FirebaseDatabase.getInstance().getReference().child(ref).child(post_id);
        postUpdates = FirebaseDatabase.getInstance().getReference().child(ref).child(post_id).child("updates");
        postRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String postTitle =  snapshot.child("title").getValue().toString();
                    String desciption =  snapshot.child("description").getValue().toString();
                    String dateTime = snapshot.child("dateTime").getValue().toString();
                    if(snapshot.child("imgUrl").exists()){
                        String imageUrl = snapshot.child("imgUrl").getValue().toString();
                        if(!imageUrl.equals("null")){
                            img_post.setVisibility(View.VISIBLE);

                            StorageReference storage =  FirebaseStorage.getInstance().getReferenceFromUrl(snapshot.child("imgUrl").getValue().toString());
                            storage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    imageuri =  uri;
                                    Glide.with(PostDetailActivity.this).load(imageuri).placeholder(R.drawable.ic_transparent).into(img_post);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(PostDetailActivity.this, ""+e.toString(), Toast.LENGTH_SHORT).show();

                                }
                            });



                        }

                    }
                    img_post.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            BigImage(imageuri);
                        }
                    });
                    txt_title.setText(postTitle);
                    txt_date_time.setText(dateTime);
                    txt_desciption.setText(desciption);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        postUpdates.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                post_updates.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    Updates updates =  ds.getValue(Updates.class);
                    post_updates.add(updates);
                }
                PostDetailAdapter postDetailAdapter =  new PostDetailAdapter(post_updates,PostDetailActivity.this);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(PostDetailActivity.this));
                recyclerView.setAdapter(postDetailAdapter);




            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


    }
    private void BigImage(Uri image) {
        LayoutInflater inflater = LayoutInflater.from(PostDetailActivity.this);
        View view = inflater.inflate(R.layout.img_alert_dialog,null);
        ImageView imgfull = view.findViewById(R.id.imgfull);
        Glide.with(getApplicationContext()).load(image).into(imgfull);
        AlertDialog alertDialog = new AlertDialog.Builder(PostDetailActivity.this)
                .setView(view)
                .create();
        alertDialog.show();
    }

    public  class PostDetailAdapter extends RecyclerView.Adapter<PostDetailAdapter.ViewHolder>{
        ArrayList<Updates> post_updates;
        Context context;

        public PostDetailAdapter(ArrayList<Updates> post_updates, Context context) {
            this.post_updates = post_updates;
            this.context =  context;
        }
        @Override
        public PostDetailAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View listItem= layoutInflater.inflate(R.layout.post_detail_item_layout, parent, false);
            ViewHolder viewHolder = new ViewHolder(listItem);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            holder.txt_post_title.setText(post_updates.get(position).getTitle());
            holder.txt_post_detail.setText(post_updates.get(position).getDescription());
            if(Boolean.getBoolean(post_updates.get(position).getImgUrl())){

                holder.lyt_img.setVisibility(View.VISIBLE);
                Glide.with(context).load(post_updates.get(position).getImgUrl()).placeholder(R.drawable.ic_transparent).into(holder.post_img);

            }

            Time t = new Time(Time.getCurrentTimezone());
            t.setToNow();
            String date1 = t.format("%d/%m/%y");

            Date date = new Date(System.currentTimeMillis());
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss", Locale.ENGLISH);
            String var = dateFormat.format(date);
            String currentTime = date1+" "+var;
            String dateStart = post_updates.get(position).getDateTime();
            String dateStop  = currentTime;

            @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy HH:mm:ss");

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


        }


        @Override
        public int getItemCount() {
            return post_updates.size();
        }

        public  class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView post_img;
            public TextView txt_post_title,txt_post_detail,txt_time_ago;
            public LinearLayout linear_lyt;
            CardView lyt_img;
            Button btn_why;
            public ViewHolder(View itemView) {
                super(itemView);
                this.post_img = (ImageView) itemView.findViewById(R.id.post_img);
                this.btn_why = (Button) itemView.findViewById(R.id.btn_why);
                this.linear_lyt = (LinearLayout) itemView.findViewById(R.id.linear_lyt);
                this.lyt_img = (CardView) itemView.findViewById(R.id.lyt_img);
                this.txt_post_title = (TextView) itemView.findViewById(R.id.txt_post_title);
                this.txt_post_detail = (TextView) itemView.findViewById(R.id.txt_post_detail);
                this.txt_time_ago = (TextView) itemView.findViewById(R.id.txt_time_ago);

            }
        }
    }
}