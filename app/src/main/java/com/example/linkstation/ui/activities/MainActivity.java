package com.example.linkstation.ui.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.linkstation.R;
import com.example.linkstation.databinding.ActivityMainBinding;
import com.example.linkstation.ui.fragments.StationsFragment;
import com.example.linkstation.ui.fragments.AnalyticsFragment;
import com.example.linkstation.ui.fragments.DashboardFragment;
import com.example.linkstation.ui.fragments.EarnFragment;
import com.example.linkstation.ui.viewmodels.DashboardViewModel;
import com.example.linkstation.utilities.CustomProgressDialog;
import com.example.linkstation.utilities.TokenManager;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    private Fragment dashboadFragment;
    private Fragment stationsFragment;
    private Fragment analyticsFragment;
    private Fragment earnFragment;

    private Fragment activeFragment;


    private DashboardViewModel dashboardViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        //replaceFragment(new DashboardFragment());

        dashboadFragment = new DashboardFragment();
        stationsFragment = new StationsFragment();
        analyticsFragment = new AnalyticsFragment();
        earnFragment = new EarnFragment();

        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        fetchUserDetails(findViewById(R.id.btnProfileOptions));

        getSupportFragmentManager().beginTransaction()
                        .add(R.id.mainContent, earnFragment, "4").hide(earnFragment)
                        .add(R.id.mainContent, analyticsFragment, "3").hide(analyticsFragment)
                        .add(R.id.mainContent, stationsFragment, "2").hide(stationsFragment)
                        .add(R.id.mainContent, dashboadFragment, "1").commit();

        activeFragment = dashboadFragment;

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.dashboard) {
                showFragment(dashboadFragment);
            } else if (item.getItemId() == R.id.stations) {
                showFragment(stationsFragment);
            } else if (item.getItemId() == R.id.analytics) {
                showFragment(analyticsFragment);
            } else if (item.getItemId() == R.id.earn) {
                showFragment(earnFragment);
            }
            return true;
        });

        findViewById(R.id.create).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CreateStationActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.btnProfileOptions).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileOptionsActivity.class);
            startActivity(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainContent, fragment);
        fragmentTransaction.commit();
    }

    private void showFragment(Fragment fragment) {

            getSupportFragmentManager().beginTransaction()
                    .hide(activeFragment)
                    .show(fragment).commit();
            activeFragment = fragment;


    }

    public void fetchUserDetails(Button button) {
        Drawable drawable = ContextCompat.getDrawable(this,R.drawable.person);

        String token = TokenManager.getAccessToken(this);
        dashboardViewModel.fetchUserDetails(token, this);
        dashboardViewModel.getUserModel().observe(this, userModel -> {
            if (userModel != null) {
                if(userModel.getData().getUser().getAvatar() != null){
                    Glide.with(this)
                            .load(userModel.getData().getUser().getAvatar())
                            .into(new CustomTarget<Drawable>() {
                                      @Override
                                      public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                          button.setBackground(resource);
                                      }

                                      @Override
                                      public void onLoadCleared(@Nullable Drawable placeholder) {
                                          button.setBackground(drawable);

                                      }
                                  }
                            );
                }
            }
        });
    }

}
