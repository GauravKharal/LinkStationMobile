package com.example.linkstation.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.linkstation.R;
import com.example.linkstation.model.UserModel;
import com.example.linkstation.network.ApiService;
import com.example.linkstation.network.RetrofitClient;
import com.example.linkstation.utility.TokenManager;

import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileOptionsActivity extends AppCompatActivity {
    private Button btnLogOut;
    private FrameLayout progressOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_options);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        progressOverlay = findViewById(R.id.progressOverlay);


        btnLogOut = findViewById(R.id.btnLogOut);
        btnLogOut.setOnClickListener(v -> logOutUser());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });
    }

    private void logOutUser() {

        progressOverlay.setVisibility(View.VISIBLE);
        btnLogOut.setEnabled(false);
        ApiService apiService = RetrofitClient.getClient(ProfileOptionsActivity.this).create(ApiService.class);

        String accessToken = TokenManager.getAccessToken(ProfileOptionsActivity.this);

        Call<UserModel> call = apiService.logoutUser(accessToken);
        call.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(@NonNull Call<UserModel> call, @NonNull Response<UserModel> response) {
                TokenManager.clearToken(getApplicationContext());

                Intent intent = new Intent(ProfileOptionsActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(@NonNull Call<UserModel> call, @NonNull Throwable t) {
                progressOverlay.setVisibility(View.GONE);
                btnLogOut.setEnabled(true);
                Toast.makeText(ProfileOptionsActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}