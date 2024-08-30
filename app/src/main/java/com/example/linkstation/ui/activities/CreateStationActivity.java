package com.example.linkstation.ui.activities;

import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.linkstation.R;
import com.example.linkstation.model.LinkItem;
import com.example.linkstation.model.StationModel;
import com.example.linkstation.ui.adapters.LinksAdapter;
import com.example.linkstation.ui.fragments.PublishFragment;
import com.example.linkstation.utilities.CustomDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class CreateStationActivity extends AppCompatActivity {
    // UI components
    private ImageButton addStationImage;
    private RecyclerView linksRecyclerView;
    private Button btnLinksRemove;
    private EditText stationTitle;
    private EditText stationDescription;
    private Uri stationImageUri;


    // Data structures for managing links
    private List<Integer> selectedPositions;
    private List<LinkItem> links;


    // Adapters
    private LinksAdapter linksAdapter;

    // Activity Result Launcher for picking images
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    private ActivityResultLauncher<PickVisualMediaRequest> pickMediaDialog;
    private ActivityResultLauncher<PickVisualMediaRequest> pickMediaDialog2;


    private LinearLayout addLink;
    private ConstraintLayout main;

    //add_link_dialog variables
    private View addLinkDialog;
    private ImageButton addImageDialog;
    private EditText etTitle;
    private EditText etUrl;
    private Button btnTick;
    private Button btnCross;
    private Uri selectedImageUriDialog;
    private ItemTouchHelper itemTouchHelper;

    //edit link dialog variables
    private View addLinkDialog2;
    private ImageButton addImageDialog2;
    private EditText etTitle2;
    private EditText etUrl2;
    private Button btnTick2;
    private Button btnCross2;
    private Uri selectedImageUriDialog2;
    private boolean isClicked = false;

    // socials_dialog variable
    private View socialsDialog;
    private ImageView ivInstagram;
    private ImageView ivFacebook;
    private ImageView ivTwitter;
    private ImageView ivYoutube;
    private EditText etInstagram;
    private EditText etFacebook;
    private EditText etTwitter;
    private EditText etYoutube;
    private Button btnSocialsTick;
    private Button btnSocialsCross;

    // socials
    private ImageView instagram;
    private ImageView facebook;
    private ImageView twitter;
    private ImageView youtube;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_create_station);


        // Initialize UI components and data structures
        initializeUIComponents();
        initializeActivityResultLaunchers();
        initializeLinksList();

        // Handle back button events
        setupBackButtonHandling();
        setupNativeBackButtonHandling();

        // Set up image picker button
        setupImagePickerButton();

        // Set up the add link button
        setupAddLinkButton();

        // Set up dropdown menu for social media selection
        setupSocials();


        // Set up the RecyclerView for displaying links
        setupRecyclerView();

        // Set up the Remove Button
        setupRemoveButton();

        // Set up the item touch helper for the RecyclerView
        setupItemTouchHelper();

        // Set up the save button
        setupSaveButton();

        // Adjust layout for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });
    }

    // Initialize UI components
    private void initializeUIComponents() {
        addStationImage = findViewById(R.id.addStationImage);
        linksRecyclerView = findViewById(R.id.rvLinks);
        btnLinksRemove = findViewById(R.id.btnLinksRemove);
        stationTitle = findViewById(R.id.stationTitle);
        stationDescription = findViewById(R.id.stationDescription);
        main = findViewById(R.id.main);
    }


    // Initialize the activity result launcher for picking images
    private void initializeActivityResultLaunchers() {
        // Register the launcher for the main image picker
        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: " + uri);
                Glide.with(this).load(uri).into(addStationImage);
                stationImageUri = uri;
            } else {
                Log.d("PhotoPicker", "No media selected");
            }
        });


        // Register the launcher for the add link dialog image picker
        pickMediaDialog = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: " + uri);
                Glide.with(CreateStationActivity.this).load(uri).into(addImageDialog);
                selectedImageUriDialog = uri;
            } else {
                Log.d("PhotoPicker", "No media selected");
            }
        });

        // Register the launcher for the edit link dialog image picker
        pickMediaDialog2 = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: " + uri);
                Glide.with(CreateStationActivity.this).load(uri).into(addImageDialog2);
                selectedImageUriDialog2 = uri;
            } else {
                Log.d("PhotoPicker", "No media selected");
            }
        });
    }

    // Initialize the list for links and its adapter
    private void initializeLinksList() {
        selectedPositions = new ArrayList<>();
    }

    // Set up back button handling with an alert dialog
    private void setupBackButtonHandling() {
        findViewById(R.id.btnBack).setOnClickListener(v -> {
            CustomDialog dialog = new CustomDialog(v.getContext());
            dialog.setTitle("Confirm Exit").setMessage("Are you sure you want to exit?").setPositiveButton(true, "Yes", v1 -> finish()).setNegativeButton(true, "No", v1 -> dialog.dismiss()).show();
        });
    }

    // Set up native back button handling with an alert dialog
    private void setupNativeBackButtonHandling() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                CustomDialog dialog = new CustomDialog(CreateStationActivity.this);
                dialog.setTitle("Confirm Exit").setMessage("Are you sure you want to exit?").setPositiveButton(true, "Yes", v1 -> finish()).setNegativeButton(true, "No", v1 -> dialog.dismiss()).show();
            }
        });
    }

    // Set up image picker button to trigger the activity result launcher
    private void setupImagePickerButton() {
        addStationImage.setOnClickListener(v -> pickMedia.launch(new PickVisualMediaRequest.Builder().setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE).build()));
    }

    // Set up dropdown menu for selecting social media platforms
    private void setupSocials() {
        LinearLayout socials = findViewById(R.id.socials);
        instagram = findViewById(R.id.instagram);
        facebook = findViewById(R.id.facebook);
        twitter = findViewById(R.id.twitter);
        youtube = findViewById(R.id.youtube);

        socials.setOnClickListener(v -> {
            main.setEnabled(false);
            showSocialsDialog();
        });
    }

    private void showSocialsDialog() {
        // Inflate and create the dialog
        socialsDialog = LayoutInflater.from(this).inflate(R.layout.socials_dialog, null);
        AlertDialog dialog = new AlertDialog.Builder(this, R.style.CustomAlertDialog).setView(socialsDialog).create();
        setupDialogWindow(dialog);

        // Initialize dialog components
        initializeDialogComponents();

        // Set initial values and focus listeners
        setupInitialValuesAndListeners();

        // Handle tick and cross button clicks
        btnSocialsTick.setOnClickListener(v -> handleSocialsTick(dialog));
        btnSocialsCross.setOnClickListener(v -> dialog.dismiss());

        // Handle dialog dismiss
        dialog.setOnDismissListener(dialogInterface -> main.setEnabled(true));
    }

    private void setupDialogWindow(AlertDialog dialog) {
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        // Convert 350dp to pixels and set dialog dimensions
        int widthInPx = (int) (350 * getResources().getDisplayMetrics().density + 0.5f);
        dialog.getWindow().setLayout(widthInPx, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    private void initializeDialogComponents() {
        btnSocialsCross = socialsDialog.findViewById(R.id.btnCross);
        btnSocialsTick = socialsDialog.findViewById(R.id.btnTick);
        ivInstagram = socialsDialog.findViewById(R.id.ivInstagram);
        ivFacebook = socialsDialog.findViewById(R.id.ivFacebook);
        ivTwitter = socialsDialog.findViewById(R.id.ivTwitter);
        ivYoutube = socialsDialog.findViewById(R.id.ivYoutube);
        etInstagram = socialsDialog.findViewById(R.id.etInstagram);
        etFacebook = socialsDialog.findViewById(R.id.etFacebook);
        etTwitter = socialsDialog.findViewById(R.id.etTwitter);
        etYoutube = socialsDialog.findViewById(R.id.etYoutube);
    }

    private void setupInitialValuesAndListeners() {
        setupSocialMediaField(instagram, ivInstagram, etInstagram);
        setupSocialMediaField(facebook, ivFacebook, etFacebook);
        setupSocialMediaField(twitter, ivTwitter, etTwitter);
        setupSocialMediaField(youtube, ivYoutube, etYoutube);
    }

    private void setupSocialMediaField(View socialMediaView, ImageView iconView, EditText editText) {
        if (socialMediaView.getVisibility() == View.VISIBLE) {
            iconView.setBackgroundTintList(ColorStateList.valueOf(0xFF000000));
            editText.setText(socialMediaView.getContentDescription());
        } else {
            iconView.setBackgroundTintList(ColorStateList.valueOf(0x80000000));
            editText.setText("");
        }

        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if (editText.getText().toString().isEmpty()) {
                    iconView.setBackgroundTintList(ColorStateList.valueOf(0x80000000));
                } else {
                    iconView.setBackgroundTintList(ColorStateList.valueOf(0xFF000000));
                }
            }
        });
    }

    private void handleSocialsTick(AlertDialog dialog) {
        if (usernameValidation(etInstagram) || usernameValidation(etFacebook) || usernameValidation(etTwitter) || usernameValidation(etYoutube)) {
            CustomDialog dialog1 = new CustomDialog(this);
            dialog1.setTitle("Invalid Username(s)")
                    .setMessage("Please enter valid username(s).")
                    .setPositiveButton(true, "OK", v -> dialog1.dismiss()).
                    setNegativeButton(false, "", null)
                    .show();
        } else {
            updateSocialMediaView(instagram, etInstagram);
            updateSocialMediaView(facebook, etFacebook);
            updateSocialMediaView(twitter, etTwitter);
            updateSocialMediaView(youtube, etYoutube);
            dialog.dismiss();
        }

    }

    private boolean usernameValidation(EditText et) {
        if (et.getText().toString().isEmpty()) {
            return false;
        } else {
            String username = et.getText().toString().trim();
            return !isValidUsername(username);
        }
    }

    private boolean isValidUsername(String username) {
        String usernameRegex = "^[a-zA-Z0-9_]{3,15}$";
        return username.matches(usernameRegex);
    }

    private void updateSocialMediaView(ImageView socialMediaView, EditText editText) {
        if (!editText.getText().toString().isEmpty()) {
            socialMediaView.setVisibility(View.VISIBLE);
            socialMediaView.setContentDescription(editText.getText().toString().trim());
        } else {
            socialMediaView.setVisibility(View.GONE);
            socialMediaView.setContentDescription("");
        }
    }


    // Set up the add link button
    private void setupAddLinkButton() {
        addLink = findViewById(R.id.addLink);
        addLink.setOnClickListener(v -> {


            main.setEnabled(false);

            // Inflate the add link dialog layout
            addLinkDialog = LayoutInflater.from(this).inflate(R.layout.add_link_dialog, null);

            // Create the dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
            builder.setView(addLinkDialog);
            AlertDialog dialog = builder.create();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            // Convert 350dp to pixels
            int widthInDp = 350;
            float scale = getResources().getDisplayMetrics().density;
            int widthInPx = (int) (widthInDp * scale + 0.5f); // Convert dp to pixels

            // Set the width and height of the dialog
            dialog.getWindow().setLayout(widthInPx, WindowManager.LayoutParams.WRAP_CONTENT);


            // Initialize dialog components
            btnTick = addLinkDialog.findViewById(R.id.btnTick);
            btnCross = addLinkDialog.findViewById(R.id.btnCross);
            etTitle = addLinkDialog.findViewById(R.id.etTitle);
            etUrl = addLinkDialog.findViewById(R.id.etUrl);
            addImageDialog = addLinkDialog.findViewById(R.id.addImage);

            // Handle addImage click in the dialog
            addImageDialog.setOnClickListener(v1 -> {
                pickMediaDialog.launch(new PickVisualMediaRequest.Builder().setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE).build());
            });


            // Handle the tick button click in the dialog
            btnTick.setOnClickListener(v1 -> {

                // Initialize the data in addLinkDialog
                String title = etTitle.getText().toString();
                String urlString = etUrl.getText().toString();
                Uri url = Uri.parse(urlString);
                Uri imageUri = selectedImageUriDialog;

                // Validate the input
                if (title.isEmpty() || urlString.isEmpty() || imageUri == null) {
                    // Show a custom dialog for missing information
                    CustomDialog dialog1 = new CustomDialog(v1.getContext());
                    dialog1.setTitle("Missing Information").setMessage("Please fill in all fields.").setPositiveButton(true, "OK", v2 -> dialog1.dismiss()).setNegativeButton(false, "", null).show();
                } else if (!Patterns.WEB_URL.matcher(urlString).matches()) {
                    // Show a custom dialog for invalid URL
                    CustomDialog dialog1 = new CustomDialog(v1.getContext());
                    dialog1.setTitle("Invalid URL").setMessage("Please enter a valid URL.").setPositiveButton(true, "OK", v2 -> dialog1.dismiss()).setNegativeButton(false, "", null).show();

                } else {
                    // Update the create page with the new link details
                    updateCreatePage(title, url, imageUri);

                    // Dismiss the dialog after updating the create page
                    dialog.dismiss();
                }


            });

            // Handle the cross button click in the dialog
            btnCross.setOnClickListener(v1 -> {
                // Get the current values from the fields
                String title = etTitle.getText().toString();
                String urlString = etUrl.getText().toString();
                Uri imageUri = selectedImageUriDialog;

                // Show a custom dialog for confirmation
                CustomDialog dialog1 = new CustomDialog(v1.getContext());
                if (!title.isEmpty() || !urlString.isEmpty() || imageUri != null) {
                    dialog1.setTitle("Confirm Dismiss").setMessage("You have not saved this link. Are you sure you want to dismiss?").setPositiveButton(true, "Yes", v2 -> {
                        dialog.dismiss();
                        main.setEnabled(true);
                    }).setNegativeButton(true, "No", v2 -> dialog1.dismiss()).show();
                } else {
                    dialog.dismiss();
                }
            });


            // Handle dialog dismiss
            dialog.setOnDismissListener(dialogInterface -> {
                main.setEnabled(true);
            });
        });
    }

    // Update the create page with the new link details
    private void updateCreatePage(String title, Uri url, Uri imageUri) {
        // Update the create page with the new link details
        LinkItem newLink = new LinkItem(imageUri, title, url);
        linksAdapter.addLink(newLink);
    }

    // Set up the RecyclerView for displaying links
    private void setupRecyclerView() {
        links = new ArrayList<>();


        linksRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        linksAdapter = new LinksAdapter(CreateStationActivity.this,links, this::onLinkLongClicked, viewHolder -> {
            if (selectedPositions.isEmpty()) {
                itemTouchHelper.startDrag(viewHolder);
            }
        }, this::onLinkClicked);
        linksRecyclerView.setAdapter(linksAdapter);
    }


    // Handle long click on a link
    private boolean onLinkLongClicked(int position) {
        boolean isSelected;

        if (selectedPositions.contains(position)) {
            selectedPositions.remove((Integer) position);
            isSelected = false;
        } else {
            selectedPositions.add(position);
            isSelected = true;
        }

        if (!selectedPositions.isEmpty()) {
            // Handle Remove Button
            btnLinksRemove.setBackgroundTintList(ColorStateList.valueOf(0xA0000000));
            btnLinksRemove.setEnabled(true);
        } else {
            btnLinksRemove.setBackgroundTintList(ColorStateList.valueOf(0x40000000));
            btnLinksRemove.setEnabled(false);
        }

        linksAdapter.notifyItemChanged(position);

        // Return true to indicate that the long click has been handled
        return isSelected;
    }

    // Handle click on a link
    private void onLinkClicked(int position) {
        LinkItem clickedItem = links.get(position);
        showEditLinkDialog(clickedItem);
    }

    private void showEditLinkDialog(LinkItem linkItem) {
        // Create and show the edit link dialog
        addLinkDialog2 = LayoutInflater.from(this).inflate(R.layout.add_link_dialog, null);

        // Find views in the dialog layout
        addImageDialog2 = addLinkDialog2.findViewById(R.id.addImage);
        etTitle2 = addLinkDialog2.findViewById(R.id.etTitle);
        etUrl2 = addLinkDialog2.findViewById(R.id.etUrl);

        // Populate the dialog with link details
        Glide.with(this).load(linkItem.getImageUri()).into(addImageDialog2);
        etTitle2.setText(linkItem.getTitle());
        etUrl2.setText(linkItem.getUrl().toString());

        // Show the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
        builder.setView(addLinkDialog2);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        // Convert 350dp to pixels
        int widthInDp = 350;
        float scale = getResources().getDisplayMetrics().density;
        int widthInPx = (int) (widthInDp * scale + 0.5f); // Convert dp to pixels

        // Set the width and height of the dialog
        dialog.getWindow().setLayout(widthInPx, WindowManager.LayoutParams.WRAP_CONTENT);


        // Handle addImage click in the dialog
        addImageDialog2.setOnClickListener(v1 -> {
            isClicked = true;
            pickMediaDialog2.launch(new PickVisualMediaRequest.Builder().setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE).build());
        });

        // Handle the tick button click in the dialog
        btnTick2 = addLinkDialog2.findViewById(R.id.btnTick);
        btnTick2.setOnClickListener(v -> {


            // Initialize the data in addLinkDialog
            String title = etTitle2.getText().toString();
            String urlString = etUrl2.getText().toString();
            Uri url = Uri.parse(urlString);
            Uri imageUri;
            if (isClicked) {
                imageUri = selectedImageUriDialog2;
            } else {
                imageUri = linkItem.getImageUri();
            }

            // Validate the input
            if (title.isEmpty() || urlString.isEmpty() || imageUri == null) {
                // Show a custom dialog for missing information
                CustomDialog dialog1 = new CustomDialog(v.getContext());
                dialog1.setTitle("Missing Information").setMessage("Please fill in all fields.").setPositiveButton(true, "OK", v2 -> dialog1.dismiss()).setNegativeButton(false, "", null).show();
            } else if (!Patterns.WEB_URL.matcher(urlString).matches()) {
                // Show a custom dialog for invalid URL
                CustomDialog dialog1 = new CustomDialog(v.getContext());
                dialog1.setTitle("Invalid URL").setMessage("Please enter a valid URL.").setPositiveButton(true, "OK", v2 -> dialog1.dismiss()).setNegativeButton(false, "", null).show();

            } else {
                // Update the link details in the adapter
                linkItem.setTitle(title);
                linkItem.setUrl(url);
                linkItem.setImageUri(imageUri);
                linksAdapter.notifyItemChanged(links.indexOf(linkItem));
                dialog.dismiss();

            }
        });

        // Handle the cross button click in the dialog
        btnCross2 = addLinkDialog2.findViewById(R.id.btnCross);
        btnCross2.setOnClickListener(v -> dialog.dismiss());
    }

    //Handle Remove Button Click
    private void setupRemoveButton() {
        btnLinksRemove.setOnClickListener(v -> {
            // Confirmation Dialog
            CustomDialog dialog = new CustomDialog(v.getContext());
            dialog.setTitle("Confirm Deletion").setMessage("Are you sure you want to delete the selected links?").setPositiveButton(true, "Yes", v1 -> {
                // Remove the selected links from the adapter
                linksAdapter.removeAllSelected(selectedPositions);

                // Clear the selected positions
                selectedPositions.clear();

                // Disable the Remove Button
                btnLinksRemove.setBackgroundTintList(ColorStateList.valueOf(0x40000000));
                btnLinksRemove.setEnabled(false);
            }).setNegativeButton(true, "No", v1 -> dialog.dismiss()).show();
        });
    }

    // Set up the item touch helper for the RecyclerView
    private void setupItemTouchHelper() {
        ItemTouchHelper.Callback callback = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                return makeMovementFlags(dragFlags, 0);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
                linksAdapter.onItemMove(fromPosition, toPosition);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }

            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                super.onSelectedChanged(viewHolder, actionState);
                if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                    viewHolder.itemView.setAlpha(0.7f);  // Reduce opacity to give a "lifted" effect
                }
            }

            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                viewHolder.itemView.setAlpha(1.0f);  // Reset opacity
            }

        };
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(linksRecyclerView);
    }

    private void setupSaveButton() {
        Button btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> {
            if (checkIfEmptyForSaveButton()) {
                StationModel stationModel = gatherDataFromCreatePage();
                PublishFragment publishFragment = new PublishFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("stationModel", stationModel);
                publishFragment.setArguments(bundle);
                publishFragment.show(getSupportFragmentManager(), "PublishFragment");
            } else {
                CustomDialog dialog = new CustomDialog(v.getContext());
                dialog.setTitle("Missing Information").setMessage("Please fill in all fields.").setPositiveButton(true, "OK", v1 -> dialog.dismiss()).setNegativeButton(false, "", null).show();

            }
        });
    }


    private boolean checkIfEmptyForSaveButton() {
        boolean isStationImageSet = addStationImage.getDrawable() != null;
        boolean isStationTitleSet = !stationTitle.getText().toString().isEmpty();
        boolean isLinksRecyclerViewPopulated = linksAdapter.getItemCount() > 0;

        return isStationImageSet && isStationTitleSet && isLinksRecyclerViewPopulated;
    }

    private StationModel gatherDataFromCreatePage() {
        File stationImageFile = new File(getRealPathFromURI(stationImageUri));
        StationModel.Data.Station station = new StationModel.Data.Station(
                "",
                stationTitle.getText().toString(),
                stationDescription.getText().toString(),
                stationImageFile,
                "",
                true,
                (String) instagram.getContentDescription(),
                (String) twitter.getContentDescription(),
                (String) facebook.getContentDescription(),
                (String) youtube.getContentDescription()
        );

        LinksAdapter adapter = (LinksAdapter) linksRecyclerView.getAdapter();
        List<StationModel.Data.Link> links = adapter != null ? adapter.getLinksForStationModel() : new ArrayList<>();

        List<File> linkImages = new ArrayList<>();
        for (StationModel.Data.Link link : links) {
            linkImages.add(link.getLinkImage());
        }

        StationModel stationModel = new StationModel();
        StationModel.Data data = new StationModel.Data();
        data.setStation(station);
        data.setLinks(links);
        data.setLinkImages(linkImages);
        stationModel.setData(data);

        return stationModel;

    }

    private String getRealPathFromURI(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = this.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String filePath = cursor.getString(column_index);
            cursor.close();
            return filePath;
        }
        return null;
    }


}
