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

public class SplashScreen extends AppCompatActivity {

    private static final String PREFS_NAME = "MyAppPrefs";
    private static final String KEY_ACCESS_TOKEN = "accessToken";
    private static final String KEY_REFRESH_TOKEN = "refreshToken";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Check login status
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String accessToken = sharedPreferences.getString(KEY_ACCESS_TOKEN, null);

        new Handler().postDelayed(() -> {
            Intent intent;
            if (accessToken != null) {
                // Token exists, user is already logged in
                intent = new Intent(SplashScreen.this, MainActivity.class);
            } else {
                // No token, user needs to log in
                intent = new Intent(SplashScreen.this, LoginActivity.class);
            }
            startActivity(intent);
            finish();  // Finish SplashScreen activity so the user can't navigate back to it
        }, 2000);  // 2000 milliseconds = 2 seconds delay
    }
}
