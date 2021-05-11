package com.hp.ali.stockmonk.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {

    public static final String mypreference = "mypref";


    public static boolean registerUser(Context context, String id,String email){
      SharedPreferences sharedpreferences = context.getSharedPreferences(mypreference, Context.MODE_PRIVATE);
      SharedPreferences.Editor editor =  sharedpreferences.edit();
        editor.putString(id, id);
        editor.putString(email, email);
        editor.commit();
        return true;
    }

    public static void setUserID(Context context, String id){
        SharedPreferences sharedpreferences = context.getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor =  sharedpreferences.edit();
        editor.putString("userID", id);
        editor.commit();

    }
    public static String getUserID(Context context){
        SharedPreferences sharedpreferences = context.getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        String id =  sharedpreferences.getString("userID","null");
        return id;

    }

    public static boolean setUserloginTrue(Context context){
        SharedPreferences sharedpreferences = context.getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor =  sharedpreferences.edit();
        editor.putBoolean("isUserLogin",true);
        editor.apply();
        return true;
    }

    public static boolean IsUserLogin(Context context){
        SharedPreferences sharedpreferences = context.getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        boolean value = sharedpreferences.getBoolean("isUserLogin",false);
        if(value==true){
            return  true;
        }
        return false;
    }

    public static boolean setUserloginfalse(Context context){
        SharedPreferences sharedpreferences = context.getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor =  sharedpreferences.edit();
        editor.putBoolean("isUserLogin",false);
        editor.apply();
        return true;
    }

    public static String return_user_id(Context context){
        SharedPreferences sharedpreferences = context.getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        String id =  sharedpreferences.getString("firstName","null");
        return id;
    }

    public static boolean setUserId(Context context, String id){
        SharedPreferences sharedpreferences = context.getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor =  sharedpreferences.edit();
        editor.putString("ID",id);
        editor.commit();
        return true;
    }

    public static String getUserId(Context context){
        SharedPreferences sharedpreferences = context.getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        String id = sharedpreferences.getString("ID","null");
        return id;
    }

    public static String getUserEmail(Context context){
        SharedPreferences sharedpreferences = context.getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        String id = sharedpreferences.getString("email","null");
        return id;
    }

    public static String getUserName(Context context){
        SharedPreferences sharedpreferences = context.getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        String id = sharedpreferences.getString("userName","null");
        return id;
    }



    public static void set_side_navigation_true(Context context){
        SharedPreferences sharedpreferences = context.getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor =  sharedpreferences.edit();
        editor.putBoolean("side_Navigation",true);
    }

    public static boolean get_side_navigation_status(Context context){
        SharedPreferences sharedpreferences = context.getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        Boolean value = sharedpreferences.getBoolean("side_Navigation",false);
        return value;
    }

    public static void set_bottom_navigation_true(Context context){
        SharedPreferences sharedpreferences = context.getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor =  sharedpreferences.edit();
        editor.putBoolean("bottom_Navigation",true);
        editor.commit();
    }

    public static boolean get_bottom_navigation_status(Context context){
        SharedPreferences sharedpreferences = context.getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        Boolean value = sharedpreferences.getBoolean("bottom_Navigation",false);
        return value;
    }


}
