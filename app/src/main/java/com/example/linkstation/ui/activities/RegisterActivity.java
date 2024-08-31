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
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.linkstation.R;
import com.example.linkstation.model.RegisterRequest;
import com.example.linkstation.model.UserModel;
import com.example.linkstation.network.ApiService;
import com.example.linkstation.network.RetrofitClient;
import com.example.linkstation.utilities.CustomDialog;
import com.example.linkstation.utilities.CustomProgressDialog;
import com.example.linkstation.utilities.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText etEmail, etUsername, etPassword, etConfirmPassword;
    private ProgressBar progressBar;
    private FrameLayout progressOverlay;
    private ConstraintLayout main;

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
        main = findViewById(R.id.main);


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
            CustomDialog dialog = new CustomDialog(this);
            dialog.setTitle("Error")
                    .setMessage("Please fill in all fields")
                    .setPositiveButton(true, "Ok", v -> dialog.dismiss())
                    .setNegativeButton(false, "", null)
                    .show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            CustomDialog dialog = new CustomDialog(this);
            dialog.setTitle("Error")
                    .setMessage("Passwords do not match")
                    .setPositiveButton(true, "Ok", v -> dialog.dismiss())
                    .setNegativeButton(false, "", null)
                    .show();
            return;
        }

        CustomProgressDialog progressDialog = new CustomProgressDialog(this);
        progressDialog.setTitle("Registering User");
        progressDialog.show();


        findViewById(R.id.btnRegister).setEnabled(false);
        main.setEnabled(false);

        RegisterRequest registerRequest = new RegisterRequest(email, username, password);

        ApiService apiService = RetrofitClient.getClient(RegisterActivity.this).create(ApiService.class);
        Call<UserModel> call = apiService.registerUser(registerRequest);
        call.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(@NonNull Call<UserModel> call, @NonNull Response<UserModel> response) {
                if (response.isSuccessful()) {
                    UserModel userModelResponse = response.body();
                    if (userModelResponse != null && userModelResponse.getData() != null) {

                        String accessToken = userModelResponse.getData().getAccessToken();
                        String refreshToken = userModelResponse.getData().getRefreshToken();
                        TokenManager.saveToken(RegisterActivity.this, accessToken, refreshToken);
                        Intent registerIntent = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(registerIntent);
                        progressDialog.dismiss();
                        findViewById(R.id.btnRegister).setEnabled(true);
                        main.setEnabled(true);
                        finish();


                    } else {
                        progressDialog.dismiss();
                        findViewById(R.id.btnRegister).setEnabled(true);
                        main.setEnabled(true);
                        CustomDialog dialog = new CustomDialog(RegisterActivity.this);
                        dialog.setTitle("Error")
                                .setMessage("Registration Failed")
                                .setPositiveButton(true, "Ok", v -> dialog.dismiss())
                                .setNegativeButton(false, "", null)
                                .show();

                    }
                } else {
                    progressDialog.dismiss();
                    findViewById(R.id.btnRegister).setEnabled(true);
                    main.setEnabled(true);
                    CustomDialog dialog = new CustomDialog(RegisterActivity.this);
                    dialog.setTitle("Error")
                            .setMessage("Registration Failed")
                            .setPositiveButton(true, "Ok", v -> dialog.dismiss())
                            .setNegativeButton(false, "", null)
                            .show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserModel> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                main.setEnabled(true);
                findViewById(R.id.btnRegister).setEnabled(true);
                CustomDialog dialog = new CustomDialog(RegisterActivity.this);
                dialog.setTitle("Error")
                        .setMessage("Registration Failed")
                        .setPositiveButton(true, "Ok", v -> dialog.dismiss())
                        .setNegativeButton(false, "", null)
                        .show();
            }
        });


    }
}
