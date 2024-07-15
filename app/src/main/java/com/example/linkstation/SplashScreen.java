package com.example.linkstation;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

//@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Ensure the view with ID 'main' exists in your activity_splash_screen.xml
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Intent to navigate to LoginActivity after the splash screen
        new Handler().postDelayed(() -> {
            Intent splashScreenIntent = new Intent(SplashScreen.this, LoginActivity.class);
            startActivity(splashScreenIntent);
            finish();  // Finish SplashScreen activity so the user can't navigate back to it
        }, 2000);  // 2000 milliseconds = 2 seconds delay
    }
}
