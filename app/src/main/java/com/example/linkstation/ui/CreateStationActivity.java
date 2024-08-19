package com.example.linkstation.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.linkstation.DropdownAdapter;
import com.example.linkstation.R;
import com.example.linkstation.SocialLinksAdapter;
import com.example.linkstation.model.SocialMedia;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CreateStationActivity extends AppCompatActivity {
    // UI components
    private ImageButton addImage;
    private RecyclerView recyclerView;
    private RecyclerView socialLinksRecyclerView;

    // Data structures for managing social media links
    private Set<String> addedSocials;
    private List<SocialMedia> socialMediaList;

    // Adapters
    private DropdownAdapter dropdownAdapter;
    private SocialLinksAdapter socialLinksAdapter;

    // Activity Result Launcher for picking images
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_station);

        // Initialize UI components and data structures
        initializeUIComponents();
        initializeSocialMediaList();
        initializeRecyclerViews();
        initializeActivityResultLaunchers();

        // Handle back button events
        setupBackButtonHandling();
        setupNativeBackButtonHandling();

        // Set up image picker button
        setupImagePickerButton();

        // Set up dropdown menu for social media selection
        setupDropdownMenu();

        // Adjust layout for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });
    }

    // Initialize UI components
    private void initializeUIComponents() {
        addImage = findViewById(R.id.addImage);
        recyclerView = findViewById(R.id.rvSocials);
        socialLinksRecyclerView = findViewById(R.id.socialLinksRecyclerView);
    }

    // Initialize the list for social media links and its adapter
    private void initializeSocialMediaList() {
        addedSocials = new HashSet<>();
        socialMediaList = new ArrayList<>();
        socialLinksAdapter = new SocialLinksAdapter(socialMediaList);
    }

    // Initialize RecyclerViews and their adapters
    private void initializeRecyclerViews() {
        socialLinksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        socialLinksRecyclerView.setAdapter(socialLinksAdapter);

        List<String> socialsMenuItems = loadSocialsMenuItems();
        dropdownAdapter = new DropdownAdapter(socialsMenuItems, this::addSocialMediaView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(dropdownAdapter);
        recyclerView.setVisibility(View.GONE);
    }

    // Initialize the activity result launcher for picking images
    private void initializeActivityResultLaunchers() {
        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: " + uri);
                Glide.with(this).load(uri).transform(new CircleCrop()).into(addImage);
            } else {
                Log.d("PhotoPicker", "No media selected");
            }
        });
    }

    // Set up back button handling with an alert dialog
    private void setupBackButtonHandling() {
        findViewById(R.id.btnBack).setOnClickListener(v -> new AlertDialog.Builder(this).setMessage("Are you sure you want to go back?").setPositiveButton("Yes", (dialog, which) -> finish()).setNegativeButton("No", null).show());
    }

    // Set up native back button handling with an alert dialog
    private void setupNativeBackButtonHandling() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                new AlertDialog.Builder(CreateStationActivity.this).setMessage("Are you sure you want to go back?").setPositiveButton("Yes", (dialog, which) -> finish()).setNegativeButton("No", null).show();
            }
        });
    }

    // Set up image picker button to trigger the activity result launcher
    private void setupImagePickerButton() {
        addImage.setOnClickListener(v -> pickMedia.launch(new PickVisualMediaRequest.Builder().setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE).build()));
    }

    // Set up dropdown menu for selecting social media platforms

    private void setupDropdownMenu() {
        LinearLayout socialsDropdown = findViewById(R.id.socialsDropdown);
        socialsDropdown.setOnClickListener(v -> {
            if (recyclerView.getVisibility() == View.VISIBLE) {
                recyclerView.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
            }
        });

        // Set touch listener on root view to detect taps outside the dropdown
        View mainLayout = findViewById(R.id.main);
        mainLayout.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (recyclerView.getVisibility() == View.VISIBLE) {
                    // Hide dropdown if touch is outside of it
                    recyclerView.setVisibility(View.GONE);
                }
                // Call performClick to ensure accessibility services handle the click properly
                v.performClick();
            }
            return true; // Consume touch event
        });
    }

    // Load available social media platforms
    private List<String> loadSocialsMenuItems() {
        List<String> socialsMenuItems = new ArrayList<>();
        socialsMenuItems.add("Instagram");
        socialsMenuItems.add("Facebook");
        socialsMenuItems.add("Twitter");
        socialsMenuItems.add("YouTube");
        return socialsMenuItems;
    }

    // Add a new social media link view to the list
    private void addSocialMediaView(String social) {
        if (!addedSocials.contains(social)) {
            int iconResId = getIconForSocial(social);
            SocialMedia newSocialMedia = new SocialMedia(iconResId, social);
            socialMediaList.add(newSocialMedia);
            socialLinksAdapter.notifyItemInserted(socialMediaList.size() - 1);
            addedSocials.add(social);
            dropdownAdapter.removeItem(social);
            recyclerView.setVisibility(View.GONE);
        }
    }

    // Get the appropriate icon resource ID for the selected social media platform
    private int getIconForSocial(String social) {
        switch (social) {
            case "Instagram":
                return R.drawable.ic_instagram;
            case "Facebook":
                return R.drawable.ic_facebook;
            case "Twitter":
                return R.drawable.ic_twitter;
            case "YouTube":
                return R.drawable.ic_youtube;
            default:
                return 0;
        }
    }

    // Add a removed social media platform back to the dropdown menu
    public void addSocialBackToDropdown(String social) {
        if (dropdownAdapter != null) {
            dropdownAdapter.addItem(social);
            addedSocials.remove(social);
        } else {
            Log.e("CreateStationActivity", "DropdownAdapter is null");
        }
    }
}
