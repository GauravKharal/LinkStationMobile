package com.example.linkstation.ui.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.linkstation.R;
import com.example.linkstation.model.ChangePasswordRequest;
import com.example.linkstation.network.ApiService;
import com.example.linkstation.network.RetrofitClient;
import com.example.linkstation.utilities.CustomDialog;
import com.example.linkstation.utilities.CustomProgressDialog;
import com.example.linkstation.utilities.TokenManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText etCurrentPassword;
    private EditText etNewPassword;
    private EditText etConfirmNewPassword;
    private Button btnChangePassword;

    private Button btnBack;

    private String currentPassword;
    private String newPassword;
    private String confirmNewPassword;

    private CustomProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_password);


        initializeLayoutVariables();

        setupBtnChangePassword();
        btnBack.setOnClickListener(v -> finish());


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });
    }

    public void initializeLayoutVariables() {
        etCurrentPassword = findViewById(R.id.etCurrentPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmNewPassword = findViewById(R.id.etConfirmNewPassword);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnBack = findViewById(R.id.btnBack);

        progressDialog = new CustomProgressDialog(this);
    }

    public void setupBtnChangePassword() {
        btnChangePassword.setOnClickListener(v -> {
            progressDialog.setTitle("Changing Password");
            progressDialog.show();

            initializeVariables();
            if (isValidPassword(currentPassword) && isValidPassword(newPassword) && isValidPassword(confirmNewPassword)) {
                if (newPassword.equals(confirmNewPassword)) {
                        changePassword();
                } else {
                    progressDialog.dismiss();
                    CustomDialog dialog = new CustomDialog(this);
                    dialog.setTitle("Password Mismatch")
                            .setMessage("New password and confirm password do not match")
                            .setPositiveButton(true, "OK", v1 -> dialog.dismiss())
                            .setNegativeButton(false, null, null)
                            .show();
                }
            } else {
                progressDialog.dismiss();
                CustomDialog dialog = new CustomDialog(this);
                dialog.setTitle("Invalid Password")
                        .setMessage("Please enter a valid password")
                        .setPositiveButton(true, "OK", v1 -> dialog.dismiss())
                        .setNegativeButton(false, null, null)
                        .show();
            }
        });
    }

    public void initializeVariables() {
        currentPassword = etCurrentPassword.getText().toString();
        newPassword = etNewPassword.getText().toString();
        confirmNewPassword = etConfirmNewPassword.getText().toString();
    }

    public boolean isValidPassword(String password) {
        // Define the regex pattern
        String passwordPattern = "^[a-zA-Z0-9@#$%&+]{8,}$";

        // Create a Pattern object
        Pattern pattern = Pattern.compile(passwordPattern);

        // Match the input password with the regex pattern
        Matcher matcher = pattern.matcher(password);

        // Return whether the password matches the pattern
        return matcher.matches();
    }


    public void changePassword() {
        // Change password in database
        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);
        String token = TokenManager.getAccessToken(this);
        ChangePasswordRequest request = new ChangePasswordRequest(currentPassword, newPassword);

        Call<ResponseBody> call = apiService.changePassword(token, request);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.code() == 400) {
                    progressDialog.dismiss();
                    CustomDialog dialog = new CustomDialog(ChangePasswordActivity.this);
                    dialog.setTitle("Incorrect Current Password")
                            .setMessage("Please enter correct Current password")
                            .setPositiveButton(true, "OK", v1 -> dialog.dismiss())
                            .setNegativeButton(false, null, null)
                            .show();
                } else if (response.code() == 401) {
                    progressDialog.dismiss();
                    CustomDialog dialog = new CustomDialog(ChangePasswordActivity.this);
                    dialog.setTitle("Password Mismatch")
                            .setMessage("New password and Current password cannot be same")
                            .setPositiveButton(true, "OK", v1 -> dialog.dismiss())
                            .setNegativeButton(false, null, null)
                            .show();
                } else if (response.code() == 200) {
                    progressDialog.dismiss();
                    finish();
                    Toast.makeText(ChangePasswordActivity.this, "Password changed successfully", Toast.LENGTH_SHORT).show();

                } else {
                    progressDialog.dismiss();
                    CustomDialog dialog = new CustomDialog(ChangePasswordActivity.this);
                    dialog.setTitle("Error")
                            .setMessage("Something went wrong")
                            .setPositiveButton(true, "OK", v1 -> dialog.dismiss())
                            .setNegativeButton(false, null, null)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                progressDialog.dismiss();
                CustomDialog dialog = new CustomDialog(ChangePasswordActivity.this);
                dialog.setTitle("Error")
                        .setMessage("Something went wrong")
                        .setPositiveButton(true, "OK", v1 -> dialog.dismiss())
                        .setNegativeButton(false, null, null)
                        .show();
            }
        });

    }

}