package com.example.linkstation.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.linkstation.R;
import com.example.linkstation.model.LoginRequest;
import com.example.linkstation.model.UserModel;
import com.example.linkstation.network.ApiService;
import com.example.linkstation.network.RetrofitClient;
import com.example.linkstation.utilities.TokenManager;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private FrameLayout progressOverlay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);



        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressOverlay = findViewById(R.id.progressOverlay);

        btnLogin.setOnClickListener(v -> loginUser());

        TextView tvSignUp = findViewById(R.id.tvSignUp);
        tvSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });


    }

    private void loginUser() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        progressOverlay.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);

        ApiService apiService = RetrofitClient.getClient(LoginActivity.this).create(ApiService.class);

        LoginRequest loginRequest = new LoginRequest(username, password);

        Call<UserModel> call = apiService.loginUser(loginRequest);
        call.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(@NonNull Call<UserModel> call, @NonNull Response<UserModel> response) {
                if (response.isSuccessful()) {
                    UserModel userModelResponse = response.body();
                    if (userModelResponse != null && userModelResponse.getData() != null) {
                        String accessToken = userModelResponse.getData().getAccessToken();
                        String refreshToken = userModelResponse.getData().getRefreshToken();
                        TokenManager.saveToken(getApplicationContext(), accessToken, refreshToken);
                        Intent loginIntent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(loginIntent);
                        finish();

                    } else {
                        progressOverlay.setVisibility(View.GONE);
                        findViewById(R.id.btnLogin).setEnabled(true);
                        Toast.makeText(LoginActivity.this, "Login failed:"+ response.message(), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    progressOverlay.setVisibility(View.GONE);
                    findViewById(R.id.btnLogin).setEnabled(true);
                    if (response.code() == 401) {
                        Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                    } else if (response.code() == 402) {
                        Toast.makeText(LoginActivity.this, "User does not Exist", Toast.LENGTH_SHORT).show();
                    } else if (response.code()==404) {
                        Toast.makeText(LoginActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onFailure(@NonNull Call<UserModel> call, @NonNull Throwable t) {
                progressOverlay.setVisibility(View.GONE);
                findViewById(R.id.btnLogin).setEnabled(true);
                Toast.makeText(LoginActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });

    }
}
