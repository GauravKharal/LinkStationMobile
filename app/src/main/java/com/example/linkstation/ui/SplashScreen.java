package com.example.linkstation.ui;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.linkstation.R;
import com.example.linkstation.utility.TokenManager;

public class SplashScreen extends AppCompatActivity {

    private static final String PREFS_NAME = "MyAppPrefs";
    private static final String ACCESS_TOKEN_KEY = "accessToken";
    private static final String REFRESH_TOKEN_KEY = "refreshToken";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check login status

        String accessToken = TokenManager.getAccessToken(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if (accessToken != null) {
                    // Token exists, user is already logged in
                    intent = new Intent(SplashScreen.this, MainActivity.class);
                } else {
                    // No token, user needs to log in
                    intent = new Intent(SplashScreen.this, LoginActivity.class);
                }
                startActivity(intent);
                finish();
            }
             // Finish SplashScreen activity so the user can't navigate back to it
        }, 2000);  // 2000 milliseconds = 2 seconds delay

    }

}
