package com.example.linkstation.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class TokenManager {
    private static final String PREFS_NAME = "MyAppPrefs";
    private static final String ACCESS_TOKEN_KEY = "accessToken";
    private static final String REFRESH_TOKEN_KEY = "refreshToken";

    public static void saveToken(Context context, String accessToken, String refreshToken){
        Log.d("TokenManager", "Saving tokens: AccessToken=" + accessToken + " RefreshToken=" + refreshToken);
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ACCESS_TOKEN_KEY,accessToken);
        editor.putString(REFRESH_TOKEN_KEY,refreshToken);
        editor.apply();
    }

    public static String getAccessToken(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(ACCESS_TOKEN_KEY,null);
    }
    public static String getRefreshToken(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(REFRESH_TOKEN_KEY,  null);
    }

    public static void clearToken(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(ACCESS_TOKEN_KEY);
        editor.remove(REFRESH_TOKEN_KEY);
        editor.apply();
    }

}
