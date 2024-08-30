package com.example.linkstation.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.linkstation.R;
import com.example.linkstation.model.RegisterRequest;
import com.example.linkstation.model.UserModel;
import com.example.linkstation.network.ApiService;
import com.example.linkstation.network.RetrofitClient;
import com.example.linkstation.utilities.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText etEmail, etUsername, etPassword, etConfirmPassword;
    private ProgressBar progressBar;
    private FrameLayout progressOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        etEmail = findViewById(R.id.etEmail);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        progressBar = findViewById(R.id.progressBar);
        progressOverlay = findViewById(R.id.progressOverlay);


        findViewById(R.id.btnRegister).setOnClickListener(v -> registerUser());

        findViewById(R.id.tvLogin).setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void registerUser() {
        String email = etEmail.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show progress bar and overlay, and disable register button
        progressOverlay.setVisibility(View.VISIBLE);
        findViewById(R.id.btnRegister).setEnabled(false);

        RegisterRequest registerRequest = new RegisterRequest(email, username, password);

        ApiService apiService = RetrofitClient.getClient(RegisterActivity.this).create(ApiService.class);
        Call<UserModel> call = apiService.registerUser(registerRequest);
        call.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(@NonNull Call<UserModel> call, @NonNull Response<UserModel> response) {
                if (response.isSuccessful()){
                    UserModel userModelResponse = response.body();
                    if (userModelResponse != null && userModelResponse.getData() != null){

                            String accessToken = userModelResponse.getData().getAccessToken();
                            String refreshToken = userModelResponse.getData().getRefreshToken();
                            TokenManager.saveToken(getApplicationContext(),accessToken,refreshToken);
                            Intent registerIntent = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(registerIntent);
                            finish();


                    }else{
                        Toast.makeText(RegisterActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();

                    }
                }else{
                    Toast.makeText(RegisterActivity.this,"Registration Failed",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<UserModel> call, @NonNull Throwable t) {
                progressOverlay.setVisibility(View.GONE);
                findViewById(R.id.btnRegister).setEnabled(true);
                Toast.makeText(RegisterActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }
}
