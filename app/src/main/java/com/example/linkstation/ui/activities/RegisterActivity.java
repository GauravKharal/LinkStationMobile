package com.example.linkstation.ui.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import com.example.linkstation.model.RegisterOTPRequest;
import com.example.linkstation.model.RegisterRequest;
import com.example.linkstation.model.UserModel;
import com.example.linkstation.network.ApiService;
import com.example.linkstation.network.RetrofitClient;
import com.example.linkstation.utilities.CustomDialog;
import com.example.linkstation.utilities.CustomProgressDialog;
import com.example.linkstation.utilities.OTPDialog;
import com.example.linkstation.utilities.TokenManager;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText etEmail, etUsername, etPassword, etConfirmPassword;
    private ProgressBar progressBar;
    private FrameLayout progressOverlay;
    private ConstraintLayout main;

    private String email, username, password;

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


        findViewById(R.id.btnRegister).setOnClickListener(v -> {
            inputValidation();
            getOtp();
        });

        findViewById(R.id.tvLogin).setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void inputValidation() {
        email = etEmail.getText().toString().trim();
        username = etUsername.getText().toString().trim();
        password = etPassword.getText().toString().trim();
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

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            CustomDialog dialog = new CustomDialog(this);
            dialog.setTitle("Error")
                    .setMessage("Invalid email address")
                    .setPositiveButton(true, "Ok", v -> dialog.dismiss())
                    .setNegativeButton(false, "", null)
                    .show();
            return;
        }

        if (password.length() < 8) {
            CustomDialog dialog = new CustomDialog(this);
            dialog.setTitle("Error")
                    .setMessage("Password must be at least 8 characters long")
                    .setPositiveButton(true, "Ok", v -> dialog.dismiss())
                    .setNegativeButton(false, "", null)
                    .show();
            return;
        }

        if (username.length() < 6) {
            CustomDialog dialog = new CustomDialog(this);
            dialog.setTitle("Error")
                    .setMessage("Username must be at least 6 characters long")
                    .setPositiveButton(true, "Ok", v -> dialog.dismiss())
                    .setNegativeButton(false, "", null)
                    .show();
            return;
        }

        String usernameRegex = "^(?!.*\\.\\.)(?!.*\\.$)[a-zA-Z0-9._]{6,30}(?<!\\.)$";
        if (!username.matches(usernameRegex)) {
            CustomDialog dialog = new CustomDialog(this);
            dialog.setTitle("Error")
                    .setMessage("Username must contain only letters, numbers, and underscores, and must not have consecutive dots or end with a dot.")
                    .setPositiveButton(true, "Ok", v -> dialog.dismiss())
                    .setNegativeButton(false, "", null)
                    .show();
            return;
        }


        String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";

        if (!password.matches(passwordRegex)) {
            CustomDialog dialog = new CustomDialog(this);
            dialog.setTitle("Error")
                    .setMessage("Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character")
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
        }
    }

    private void getOtp() {
        CustomProgressDialog progressDialog = new CustomProgressDialog(this);
        progressDialog.setTitle("Getting OTP");
        progressDialog.show();

        RegisterOTPRequest registerOTPRequest = new RegisterOTPRequest(email,username);

        ApiService apiService = RetrofitClient.getClient(RegisterActivity.this).create(ApiService.class);
        Call<ResponseBody> call = apiService.registerOtp(registerOTPRequest);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressDialog.dismiss();
                if(response.code() == 200) {
                    OTPDialog otpDialog = new OTPDialog(RegisterActivity.this, new OTPDialog.OTPDialogListener() {
                        @Override
                        public void onOTPEntered(String otp) {
                            registerUser(otp);
                        }

                        @Override
                        public void onResendOtp() {
                            getOtp();
                        }
                    });
                    otpDialog.show();
                    otpDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                } else if (response.code() == 400) {
                    CustomDialog dialog = new CustomDialog(RegisterActivity.this);
                    dialog.setTitle("Error")
                            .setMessage("Username and Email Required")
                            .setPositiveButton(true, "Ok", v -> dialog.dismiss())
                            .setNegativeButton(false, "", null)
                            .show();
                } else if (response.code() == 402) {
                    CustomDialog dialog = new CustomDialog(RegisterActivity.this);
                    dialog.setTitle("Error")
                            .setMessage("User with that Username already exists")
                            .setPositiveButton(true, "Ok", v -> dialog.dismiss())
                            .setNegativeButton(false, "", null)
                            .show();
                    
                } else if (response.code() == 401) {
                    CustomDialog dialog = new CustomDialog(RegisterActivity.this);
                    dialog.setTitle("Error")
                            .setMessage("User with that Email already exists")
                            .setPositiveButton(true, "Ok", v -> dialog.dismiss())
                            .setNegativeButton(false, "", null)
                            .show();                    
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                progressDialog.dismiss();
                CustomDialog dialog = new CustomDialog(RegisterActivity.this);
                dialog.setTitle("Error")
                        .setMessage("Registration Failed")
                        .setPositiveButton(true, "Ok", v -> dialog.dismiss())
                        .setNegativeButton(false, "", null)
                        .show();
            }
        });

    }

    private void registerUser(String otp) {


        CustomProgressDialog progressDialog = new CustomProgressDialog(this);
        progressDialog.setTitle("Registering User");
        progressDialog.show();


        findViewById(R.id.btnRegister).setEnabled(false);
        main.setEnabled(false);

        RegisterRequest registerRequest = new RegisterRequest(email, username, password, otp);

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
                        TokenManager.saveToken(getApplicationContext(), accessToken, refreshToken);
                        Intent registerIntent = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(registerIntent);
                        progressDialog.dismiss();
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
