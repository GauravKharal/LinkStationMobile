package com.example.linkstation.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.linkstation.R;
import com.example.linkstation.model.UserModel;
import com.example.linkstation.network.ApiService;
import com.example.linkstation.network.RetrofitClient;
import com.example.linkstation.utilities.TokenManager;
import com.example.linkstation.ui.viewmodels.DashboardViewModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileOptionsActivity extends AppCompatActivity {
    private Button btnLogOut;
    private FrameLayout progressOverlay;
    private LinearLayout changePassword;
    private LinearLayout editProfile;
    private TextView fullName;
    private TextView username;
    private ImageView avatar;
    private DashboardViewModel dashboardViewModel;

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_options);

        fullName = findViewById(R.id.fullName);
        username = findViewById(R.id.username);
        avatar = findViewById(R.id.avatar);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        swipeRefreshLayout.setOnRefreshListener(() -> refreshData());

        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        fetchUserDetails();

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        progressOverlay = findViewById(R.id.progressOverlay);

        changePassword = findViewById(R.id.changePassword);
        editProfile = findViewById(R.id.editProfile);

        editProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileOptionsActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });

        changePassword.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileOptionsActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
        });


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

    public void fetchUserDetails() {
        String token = TokenManager.getAccessToken(this);
        dashboardViewModel.fetchUserDetails(token, this);
        dashboardViewModel.getUserModel().observe(this, userModel -> {
            if (userModel != null) {
                if(userModel.getData().getUser().getFullName() == null){
                    fullName.setText(userModel.getData().getUser().getUsername());
                    username.setText("");
                }else{
                    fullName.setText(userModel.getData().getUser().getFullName());
                    username.setText(userModel.getData().getUser().getUsername());
                }
                if(userModel.getData().getUser().getAvatar() != null){
                    Glide.with(this)
                            .load(userModel.getData().getUser().getAvatar())
                            .placeholder(R.drawable.person)
                            .error(R.drawable.person)
                            .into(avatar);
                }
            }
        });
    }

    public void refreshData() {
        String token = TokenManager.getAccessToken(this);
        dashboardViewModel.fetchUserDetails(token, this);
        swipeRefreshLayout.setRefreshing(false);
    }


}