package com.hp.ali.stockmonk.ui.logout;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.hp.ali.stockmonk.R;
import com.hp.ali.stockmonk.login.LoginActivity;

import static maes.tech.intentanim.CustomIntent.customType;


public class LogoutFragment extends Fragment {
    Button btn_logout;




    public LogoutFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =   inflater.inflate(R.layout.fragment_logout, container, false);
        btn_logout =  view.findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent =  new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                customType(getContext(),"right-to-left");

            }
        });
        return view;

    }
}