package com.example.linkstation.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.linkstation.R;
import com.example.linkstation.databinding.ActivityMainBinding;
import com.example.linkstation.ui.AnalyticsFragment;
import com.example.linkstation.ui.DashboardFragment;
import com.example.linkstation.ui.EarnFragment;
import com.example.linkstation.ui.StationsFragment;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        replaceFragment(new DashboardFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.dashboard) {
                replaceFragment(new DashboardFragment());
            } else if (item.getItemId() == R.id.stations) {
                replaceFragment(new StationsFragment());
            } else if (item.getItemId() == R.id.analytics) {
                replaceFragment(new AnalyticsFragment());
            } else if (item.getItemId() == R.id.earn) {
                replaceFragment(new EarnFragment());
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
}
