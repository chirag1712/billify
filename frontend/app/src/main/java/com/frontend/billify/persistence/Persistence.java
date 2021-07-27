package com.frontend.billify.persistence;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public class Persistence {
    public static final String PERS_USER_ID = "UserId";
    public static final String PERS_USER_NAME = "UserName";

    public static void saveUserId(Context context, int userId){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(PERS_USER_ID, userId);
        editor.apply();
    }

    public static int getUserId(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getInt(PERS_USER_ID, -1); // -1 means no userId
    }

    public static void clearLoginDetails(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.apply();
    }

    public static void saveUserName(Context context, String username){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(PERS_USER_NAME, username);
        editor.apply();
    }

    public static int getUserName(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getInt(PERS_USER_NAME, "NO USER"); // -1 means no userId
    }
}
