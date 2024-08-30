package com.example.linkstation.ui.activities;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.linkstation.R;
import com.example.linkstation.network.ApiService;
import com.example.linkstation.network.RetrofitClient;
import com.example.linkstation.utilities.TokenManager;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check login status

        if (!isNetworkAvailable()) {
            showNoInternetDialog();
            return;
        }
        String accessToken = TokenManager.getAccessToken(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
                if (accessToken != null) {
                    if (TokenManager.isAccessTokenExpired(SplashScreen.this)) {
                        try {
                            ApiService apiService = RetrofitClient.getClient(SplashScreen.this).create(ApiService.class);
                            TokenManager.refreshToken(SplashScreen.this, apiService);
                        } catch (Exception e) {
                            TokenManager.clearToken(getApplicationContext());
                            intent = new Intent(SplashScreen.this, LoginActivity.class);
                        }

                    } else {
                        // Token exists, user is already logged in
                        intent = new Intent(SplashScreen.this, MainActivity.class);

                    }

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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            Network network = connectivityManager.getActiveNetwork();
            if (network == null) {
                return false;
            }
            NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);
            return networkCapabilities != null && (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH));


        }
        return false;
    }

    private void showNoInternetDialog() {
        new AlertDialog.Builder(this).setTitle("No Internet Connection").setMessage("It looks like your internet connection is off. Please turn it on and try again.").setPositiveButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        }).setCancelable(false).show();
    }


}
