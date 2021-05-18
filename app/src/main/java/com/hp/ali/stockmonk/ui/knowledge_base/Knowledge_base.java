package com.hp.ali.stockmonk.ui.knowledge_base;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hp.ali.stockmonk.R;
import com.hp.ali.stockmonk.model.Posts;
import com.hp.ali.stockmonk.ui.break_out.BreakOutFragment;
import com.hp.ali.stockmonk.utils.SharedPreferencesManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static maes.tech.intentanim.CustomIntent.customType;

public class Knowledge_base extends Fragment {
    private ArrayList<Posts> Knowledgebase_post_list;
    private ArrayList<String> Knowledgebase_KeyList;
    static DatabaseReference Knowledgebase_ref;
    static DatabaseReference like_ref;
    static DatabaseReference fav_ref;
    RecyclerView recyclerView;
    static ProgressDialog progressDialog;
    static ProgressDialog progressDialog1;
    static String userUID;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_knowledge_base, container, false);
        userUID = SharedPreferencesManager.getUserID(getContext());
        progressDialog  =  new ProgressDialog(getContext());
        progressDialog1  =  new ProgressDialog(getContext());
        progressDialog.show();
        NestedScrollView scrollView  =  view.findViewById(R.id.scrollView);
        recyclerView =  view.findViewById(R.id.recyclerView);
        Knowledgebase_post_list =  new ArrayList<>();
        Knowledgebase_KeyList =  new ArrayList<>();
        Knowledgebase_ref =  FirebaseDatabase.getInstance().getReference().child("KnowledgeBase");
        like_ref =  FirebaseDatabase.getInstance().getReference().child("likes");
        fav_ref =  FirebaseDatabase.getInstance().getReference().child("favorite");
        Knowledgebase_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Knowledgebase_post_list.clear();
                    Knowledgebase_KeyList.clear();
                    for(DataSnapshot ds: snapshot.getChildren()){
                        String key =  ds.getKey();
                        Knowledgebase_KeyList.add(key);
                        Posts post =  ds.getValue(Posts.class);
                        Knowledgebase_post_list.add(post);
                    }
                    Collections.reverse(Knowledgebase_post_list);
                    Collections.reverse(Knowledgebase_KeyList);
                    Knowledge_Adapter knowledge_adapter = new Knowledge_Adapter(Knowledgebase_post_list,Knowledgebase_KeyList,getContext());
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    recyclerView.setAdapter(knowledge_adapter);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }



    public  class Knowledge_Adapter extends RecyclerView.Adapter<Knowledge_Adapter.ViewHolder>{
        ArrayList<Posts> traders_post_list;
        ArrayList<String> breakout_KeyList;
        Context context;

        public Knowledge_Adapter(ArrayList<Posts> traders_post_list,ArrayList<String> breakout_KeyList, Context context) {
            this.traders_post_list = traders_post_list;
            this.breakout_KeyList = breakout_KeyList;
            this.context =  context;
        }
        @Override
        public Knowledge_Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View listItem= layoutInflater.inflate(R.layout.index_post_item_layout, parent, false);
            Knowledge_Adapter.ViewHolder viewHolder = new Knowledge_Adapter.ViewHolder(listItem);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(Knowledge_Adapter.ViewHolder holder, int position) {
            String key = breakout_KeyList.get(position).toString();


            like_ref.child(userUID).child("KnowledgeBase").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint("UseCompatLoadingForDrawables")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        if(snapshot.child("isPostLiked").getValue().toString().equalsIgnoreCase("true")){
                            holder.img_like.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_like_blue));



                        }
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            fav_ref.child(userUID).child("KnowledgeBase").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint("UseCompatLoadingForDrawables")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        if(snapshot.child("isPostfavorite").getValue().toString().equalsIgnoreCase("true")){
                            holder.img_fav.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_fav_red));



                        }

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            holder.setIsRecyclable(false);

            if(traders_post_list.get(position).getLikes()!=null){
                holder.txt_total_likes.setText(traders_post_list.get(position).getLikes());
            }
            if(traders_post_list.get(position).getFavorite()!=null){
                holder.txt_total_fav.setText(traders_post_list.get(position).getFavorite());
            }


            holder.linear_lyt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent =  new Intent(context, PostDetailActivity.class);
                    intent.putExtra("post_id",key);
                    intent.putExtra("ref","KnowledgeBase");
                    context.startActivity(intent);
                    customType(context,"left-to-right");

                }
            });

            if(traders_post_list.get(position).getExplanation()!=null){
                holder.btn_why.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String message =  traders_post_list.get(position).getExplanation().toString();
                        Dialog dialog = new Dialog(context);
                        dialog.setContentView(R.layout.hint_dialog_layout);
                        TextView txt_hint_description =  dialog.findViewById(R.id.txt_hint_description);
                        txt_hint_description.setText(message);
                        dialog.show();
                    }
                });
            }
            else{
                holder.btn_why.setVisibility(View.GONE);
            }

            holder.img_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, traders_post_list.get(position).getDescription());
                    sendIntent.setType("text/plain");
                    Intent shareIntent = Intent.createChooser(sendIntent, null);
                    context.startActivity(shareIntent);
                }
            });
            holder.txt_post_title.setText(traders_post_list.get(position).getTitle());
            holder.txt_post_detail.setText(traders_post_list.get(position).getDescription());
            if(!traders_post_list.get(position).getImgUrl().isEmpty()){
                String url =  traders_post_list.get(position).getImgUrl();
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
            holder.img_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    like_ref.child(userUID).child("KnowledgeBase").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                        @SuppressLint("UseCompatLoadingForDrawables")
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(!snapshot.exists()){
                                HashMap hashMap =  new HashMap();
                                hashMap.put("isPostLiked","true");
                                int newLikeValue = Integer.parseInt(holder.txt_total_likes.getText().toString())+1;
                                holder.txt_total_likes.setText(String.valueOf(newLikeValue));
                                Knowledgebase_ref.child(key).child("likes").setValue(String.valueOf(newLikeValue));
                                like_ref.child(userUID).child("KnowledgeBase").child(key).setValue(hashMap);
                                holder.img_like.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_like_blue));

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            });
            holder.img_fav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fav_ref.child(userUID).child("KnowledgeBase").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                        @SuppressLint("UseCompatLoadingForDrawables")
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(!snapshot.exists()){
                                HashMap hashMap =  new HashMap();
                                hashMap.put("isPostfavorite","true");
                                int newFavoriteValue = Integer.parseInt(holder.txt_total_fav.getText().toString())+1;
                                holder.txt_total_fav.setText(String.valueOf(newFavoriteValue));
                                Knowledgebase_ref.child(key).child("favorite").setValue(String.valueOf(newFavoriteValue));
                                fav_ref.child(userUID).child("KnowledgeBase").child(key).setValue(hashMap);
                                holder.img_fav.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_fav_red));

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });



                }
            });

            Time t = new Time(Time.getCurrentTimezone());
            t.setToNow();
            String date1 = t.format("%d/%m/%y");

            Date date = new Date(System.currentTimeMillis());
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss", Locale.ENGLISH);
            String var = dateFormat.format(date);
            String currentTime = date1+" "+var;

            String dateStart = traders_post_list.get(position).getDateTime();
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
            progressDialog1.dismiss();




        }


        @Override
        public int getItemCount() {
            return traders_post_list.size();
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