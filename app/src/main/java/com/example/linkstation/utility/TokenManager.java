package com.example.linkstation.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.linkstation.model.RefreshTokenRequest;
import com.example.linkstation.model.UserModel;
import com.example.linkstation.network.ApiService;
import com.example.linkstation.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    public static void refreshToken(Context context, ApiService apiService){
        String refreshToken = getRefreshToken(context);
        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest(refreshToken);
        Call<UserModel> call = apiService.refreshToken(refreshTokenRequest);
        call.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(@NonNull Call<UserModel> call, @NonNull Response<UserModel> response) {
                if(response.isSuccessful() && response.body() != null){
                    String newAccessToken = response.body().getData().getAccessToken();
                    String newRefreshToken = response.body().getData().getRefreshToken();
                    saveToken(context, newAccessToken, newRefreshToken);
                }else {
                    Toast.makeText(context, "Failed to refresh token", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserModel> call, Throwable throwable) {
                Toast.makeText(context, "Failed to refresh token", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void clearToken(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(ACCESS_TOKEN_KEY);
        editor.remove(REFRESH_TOKEN_KEY);
        editor.apply();
    }

}
