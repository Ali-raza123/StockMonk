package com.hp.ali.stockmonk.ui.premium;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.format.Time;
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
import com.hp.ali.stockmonk.model.Posts;
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


public class PremiumFragment extends Fragment {

    private ArrayList<Posts> premium_post_list;
    private ArrayList<String> premium_KeyList;
     DatabaseReference user_ref;
    static DatabaseReference premium_ref;
    static DatabaseReference premium_like_ref;
    static DatabaseReference premium_fav_ref;
    RecyclerView recyclerView;
    static ProgressDialog progressDialog;
    static ProgressDialog progressDialog1;
    static String userUID;
    String value;
    LinearLayout lyt_contactus,lyt_recyclerview;
    Button btn_whatsapp,btn_telegram;


    public PremiumFragment() {
    }

    @SuppressLint("ObsoleteSdkInt")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_premium, container, false);
        btn_telegram =  view.findViewById(R.id.btn_telegram);
        btn_whatsapp =  view.findViewById(R.id.btn_whatsapp);
        btn_whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number = "+918217064678";
                String url = "https://api.whatsapp.com/send?phone="+number;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {

                // Handle the back button event
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), callback);
        lyt_contactus  =  view.findViewById(R.id.lyt_contactus);
        lyt_recyclerview  =  view.findViewById(R.id.lyt_recyclerview);
        userUID = SharedPreferencesManager.getUserID(getContext());
        user_ref =  FirebaseDatabase.getInstance().getReference().child("users").child(userUID);
        user_ref.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               boolean v = snapshot.child("isPremiumMember").exists();
               if(v){
                String value =  snapshot.child("isPremiumMember").getValue().toString();
                   if(value.equalsIgnoreCase("false")){
                       lyt_recyclerview.setVisibility(View.GONE);
                       lyt_contactus.setVisibility(View.VISIBLE);
                   }
                   else{
                       lyt_recyclerview.setVisibility(View.VISIBLE);
                       lyt_contactus.setVisibility(View.GONE);
                       progressDialog  =  new ProgressDialog(getContext());
                       progressDialog1  =  new ProgressDialog(getContext());
                       progressDialog.show();
                       NestedScrollView scrollView  =  view.findViewById(R.id.scrollView);
                       recyclerView =  view.findViewById(R.id.recyclerView);
                       premium_post_list =  new ArrayList<>();
                       premium_KeyList =  new ArrayList<>();
                       premium_ref =  FirebaseDatabase.getInstance().getReference().child("premium");
                       premium_like_ref =  FirebaseDatabase.getInstance().getReference().child("likes");
                       premium_fav_ref =  FirebaseDatabase.getInstance().getReference().child("favorite");
                       premium_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                           @Override
                           public void onDataChange(@NonNull DataSnapshot snapshot) {
                               if(snapshot.exists()){
                                   premium_post_list.clear();
                                   premium_KeyList.clear();
                                   for(DataSnapshot ds: snapshot.getChildren()){
                                       String key =  ds.getKey();
                                       premium_KeyList.add(key);
                                       Posts post =  ds.getValue(Posts.class);
                                       premium_post_list.add(post);
                                   }
                                   Collections.reverse(premium_post_list);
                                   Collections.reverse(premium_KeyList);
                                   PremiumAdapater premiumAdapater = new PremiumAdapater(premium_post_list,premium_KeyList,getContext());
                                   recyclerView.setHasFixedSize(true);
                                   recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                   recyclerView.setAdapter(premiumAdapater);


                               }
                           }

                           @Override
                           public void onCancelled(@NonNull DatabaseError error) {

                           }
                       });
                       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                           scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                               @Override
                               public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                                   int bottom =   (scrollView.getChildAt(scrollView.getChildCount() - 1)).getHeight()-scrollView.getHeight()-scrollY;
                                   if(scrollY==0){



                                       //top detected
                                   }
                                   if(bottom==0){

                                       progressDialog1.show();
                                       premium_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                           @Override
                                           public void onDataChange(@NonNull DataSnapshot snapshot) {
                                               if(snapshot.exists()){
                                                   premium_post_list.clear();
                                                   premium_KeyList.clear();
                                                   for(DataSnapshot ds: snapshot.getChildren()){
                                                       String key =  ds.getKey();
                                                       premium_KeyList.add(key);
                                                       Posts post =  ds.getValue(Posts.class);
                                                       premium_post_list.add(post);
                                                   }
                                                   Collections.reverse(premium_post_list);
                                                   Collections.reverse(premium_KeyList);
                                                   PremiumAdapater premiumAdapater = new PremiumAdapater(premium_post_list,premium_KeyList,getContext());
                                                   recyclerView.setHasFixedSize(true);
                                                   recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                                   recyclerView.setAdapter(premiumAdapater);


                                               }
                                           }

                                           @Override
                                           public void onCancelled(@NonNull DatabaseError error) {

                                           }
                                       });
                                   }


                               }
                           });
                       }

                   }
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });
        return view;
    }

    public class PremiumAdapater extends RecyclerView.Adapter<PremiumAdapater.ViewHolder>{
        ArrayList<Posts> traders_post_list;
        ArrayList<String> premium_KeyList;
        Context context;

        public PremiumAdapater(ArrayList<Posts> traders_post_list,ArrayList<String> premium_KeyList, Context context) {
            this.traders_post_list = traders_post_list;
            this.premium_KeyList = premium_KeyList;
            this.context =  context;
        }
        @Override
        public PremiumAdapater.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View listItem= layoutInflater.inflate(R.layout.index_post_item_layout, parent, false);
          ViewHolder viewHolder = new ViewHolder(listItem);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            String key = premium_KeyList.get(position).toString();


            premium_like_ref.child(userUID).child("premium").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
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

            premium_fav_ref.child(userUID).child("premium").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
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
                    intent.putExtra("ref","premium");
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


            if(traders_post_list.get(position).getImgUrl()!=null){
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
                    premium_like_ref.child(userUID).child("breakouts").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                        @SuppressLint("UseCompatLoadingForDrawables")
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(!snapshot.exists()){
                                HashMap hashMap =  new HashMap();
                                hashMap.put("isPostLiked","true");
                                int newLikeValue = Integer.parseInt(holder.txt_total_likes.getText().toString())+1;
                                holder.txt_total_likes.setText(String.valueOf(newLikeValue));
                                premium_ref.child(key).child("likes").setValue(String.valueOf(newLikeValue));
                                premium_like_ref.child(userUID).child("breakouts").child(key).setValue(hashMap);
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
                    premium_fav_ref.child(userUID).child("breakouts").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                        @SuppressLint("UseCompatLoadingForDrawables")
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(!snapshot.exists()){
                                HashMap hashMap =  new HashMap();
                                hashMap.put("isPostfavorite","true");
                                int newFavoriteValue = Integer.parseInt(holder.txt_total_fav.getText().toString())+1;
                                holder.txt_total_fav.setText(String.valueOf(newFavoriteValue));
                                premium_ref.child(key).child("favorite").setValue(String.valueOf(newFavoriteValue));
                                premium_fav_ref.child(userUID).child("breakouts").child(key).setValue(hashMap);
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