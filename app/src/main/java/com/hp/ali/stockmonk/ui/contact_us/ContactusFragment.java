package com.hp.ali.stockmonk.ui.contact_us;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.hp.ali.stockmonk.R;


public class ContactusFragment extends Fragment {
    ImageView contact_us;



    public ContactusFragment() {
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_contactus, container, false);
        contact_us =  view.findViewById(R.id.contact_us);
        Glide.with(this).load(getResources().getDrawable(R.drawable.contact_us)).into(contact_us);

        return view;
    }
}