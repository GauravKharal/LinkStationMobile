package com.example.linkstation.ui.activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.linkstation.R;
import com.example.linkstation.network.ApiService;
import com.example.linkstation.network.RetrofitClient;
import com.example.linkstation.ui.viewmodels.DashboardViewModel;
import com.example.linkstation.ui.viewmodels.UserViewModel;
import com.example.linkstation.utilities.CustomDatePickerDialog;
import com.example.linkstation.utilities.CustomDialog;
import com.example.linkstation.utilities.CustomProgressDialog;
import com.example.linkstation.utilities.TokenManager;

import org.w3c.dom.Text;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    // UI components
    private Button btnBack;
    private ImageButton avatar;
    private ConstraintLayout main;
    private Uri avatarUri;
    private EditText etFullName;
    private TextView tvUsername;
    private TextView tvDateOfBirth;
    private TextView tvEmail;
    private Button btnDateOfBirth;
    private Button btnSave;


    // Calendar Dialog components
    private Calendar calendar;
    private int year, month, day;

    // Activity Result Launcher for picking images
    private ActivityResultLauncher<PickVisualMediaRequest> pickAvatar;


    // Add Image Dialog
    private View addAvatarDialog;
    private ImageButton addImage;
    private Button btnTick;
    private Button btnCross;
    private AlertDialog dialog;

    private DashboardViewModel dashboardViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);

        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);


        initializeUIComponents();

        fetchUserDetails();

        setupBackButtonHandling();

        initializeActivityResultLaunchers();

        avatarHandling();

        initializeCalendar();

        btnDateOfBirthHandling();

        btnSaveHandling();


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initializeUIComponents() {
        main = findViewById(R.id.main);
        btnBack = findViewById(R.id.btnBack);
        avatar = findViewById(R.id.avatar);
        etFullName = findViewById(R.id.etFullName);
        tvUsername = findViewById(R.id.tvUsername);
        tvDateOfBirth = findViewById(R.id.tvDateOfBirth);
        tvEmail = findViewById(R.id.tvEmail);
        btnDateOfBirth = findViewById(R.id.btnDateOfBirth);
        btnSave = findViewById(R.id.btnSave);
    }

    @SuppressLint("DefaultLocale")
    public void fetchUserDetails() {
        String token = TokenManager.getAccessToken(this);
        dashboardViewModel.fetchUserDetails(token, this);
        dashboardViewModel.getUserModel().observe(this, userModel -> {
            if (userModel != null) {
                etFullName.setText(userModel.getData().getUser().getFullName());
                tvUsername.setText(userModel.getData().getUser().getUsername());
                tvEmail.setText(userModel.getData().getUser().getEmail());

                Date dateOfBirth = userModel.getData().getUser().getDateOfBirth();
                if (dateOfBirth != null) {
                    String formattedDate;
                    formattedDate = String.format("%04d.%02d.%02d", dateOfBirth.getYear() + 1900, dateOfBirth.getMonth() + 1, dateOfBirth.getDate());
                    btnDateOfBirth.setText(formattedDate);
                }
                ;

                if (userModel.getData().getUser().getAvatar() != null) {
                    Glide.with(this)
                            .load(userModel.getData().getUser().getAvatar())
                            .placeholder(R.drawable.person)
                            .error(R.drawable.person)
                            .into(avatar);
                }
            }
        });
    }

    private void setupBackButtonHandling() {
        btnBack.setOnClickListener(v -> {
            CustomDialog dialog = new CustomDialog(v.getContext());
            dialog.setTitle("Confirm Exit")
                    .setMessage("Are you sure you want to exit?")
                    .setPositiveButton(true, "Yes", v1 -> finish())
                    .setNegativeButton(true, "No", v1 -> dialog.dismiss())
                    .show();
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                CustomDialog dialog = new CustomDialog(EditProfileActivity.this);
                dialog.setTitle("Confirm Exit")
                        .setMessage("Are you sure you want to exit?")
                        .setPositiveButton(true, "Yes", v1 -> finish())
                        .setNegativeButton(true, "No", v1 -> dialog.dismiss())
                        .show();
            }
        });
    }

    private void initializeActivityResultLaunchers() {
        pickAvatar = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            if (uri != null) {
                Glide.with(this).load(uri).into(addImage);
                avatarUri = uri;
            }
        });
    }

    private void avatarHandling() {
        avatar.setOnClickListener(v -> {

            main.setEnabled(false);

            addAvatarDialog = getLayoutInflater().inflate(R.layout.add_avatar_dialog, null);

            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
            builder.setView(addAvatarDialog);
            dialog = builder.create();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

            int widthInDp = 350;
            float scale = getResources().getDisplayMetrics().density;
            int widthInPx = (int) (widthInDp * scale + 0.5f);

            dialog.getWindow().setLayout(widthInPx, WindowManager.LayoutParams.WRAP_CONTENT);

            addImage = addAvatarDialog.findViewById(R.id.addImage);
            btnTick = addAvatarDialog.findViewById(R.id.btnTick);
            btnCross = addAvatarDialog.findViewById(R.id.btnCross);

            addImage.setOnClickListener(v1 -> {
                pickAvatar.launch(new PickVisualMediaRequest.Builder().setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE).build());
            });

            btnTick.setOnClickListener(v1 -> {
                Uri imageUri = avatarUri;

                if (imageUri == null) {
                    CustomDialog dialog1 = new CustomDialog(v1.getContext());
                    dialog1.setTitle("Missing Information")
                            .setMessage("Please select an image.")
                            .setPositiveButton(true, "OK", v2 -> dialog1.dismiss())
                            .setNegativeButton(false, "", null)
                            .show();
                } else {
                    sendAvatarToBackend(imageUri);

                }
            });


        });
    }

    private void sendAvatarToBackend(Uri avatarUri) {
        CustomProgressDialog progressDialog = new CustomProgressDialog(this);
        progressDialog.setTitle("Uploading Avatar");
        progressDialog.show();

        // Convert avatarUri into file
        File avatarFile = new File(getRealPathFromURI(avatarUri));
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), avatarFile);
        MultipartBody.Part avatarPart = MultipartBody.Part.createFormData("avatar", avatarFile.getName(), requestFile);

        // Get accessToken
        String token = TokenManager.getAccessToken(this);

        // Send avatar to backend
        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);
        Call<ResponseBody> call = apiService.updateAvatar(token, avatarPart);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                int statusCode = response.code();
                if (statusCode == 200) {
                    progressDialog.dismiss();
                    Glide.with(EditProfileActivity.this).load(avatarUri).into(avatar);
                    dialog.dismiss();
                    Toast.makeText(EditProfileActivity.this, "Avatar Uploaded Successfully", Toast.LENGTH_SHORT).show();
                } else if (statusCode == 400) {
                    progressDialog.dismiss();
                    CustomDialog dialog1 = new CustomDialog(EditProfileActivity.this);
                    dialog1.setTitle("Error")
                            .setMessage("Avatar File Missing")
                            .setPositiveButton(true, "OK", v2 -> dialog1.dismiss())
                            .setNegativeButton(false, "", null)
                            .show();
                } else if (statusCode == 401) {
                    progressDialog.dismiss();
                    CustomDialog dialog1 = new CustomDialog(EditProfileActivity.this);
                    dialog1.setTitle("Error")
                            .setMessage("Error Uploading Avatar")
                            .setPositiveButton(true, "OK", v2 -> dialog1.dismiss())
                            .setNegativeButton(false, "", null)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {

            }
        });
    }

    private String getRealPathFromURI(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
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

    private void initializeCalendar() {
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
    }


    @SuppressLint("DefaultLocale")
    private void btnDateOfBirthHandling() {
        btnDateOfBirth.setOnClickListener(v -> {
            CustomDatePickerDialog datePickerDialog = new CustomDatePickerDialog(this);
            if (btnDateOfBirth.getText().toString().equals("YYYY.MM.DD")) {
                datePickerDialog.setDate(1999, 1, 1);
            } else {
                int year = Integer.parseInt(btnDateOfBirth.getText().toString().split("\\.")[0]);
                int month = Integer.parseInt(btnDateOfBirth.getText().toString().split("\\.")[1]);
                int day = Integer.parseInt(btnDateOfBirth.getText().toString().split("\\.")[2]);
                datePickerDialog.setDate(year, month - 1, day);
            }
            datePickerDialog.setPositiveButton(v1 -> {
                btnDateOfBirth.setText(String.format("%d.%d.%d", datePickerDialog.getYear(), datePickerDialog.getMonth() + 1, datePickerDialog.getDay()));
            });
            datePickerDialog.show();
        });
    }

    private void btnSaveHandling() {
        btnSave.setOnClickListener(v -> {
            String fullName = etFullName.getText().toString().trim();
            String date = btnDateOfBirth.getText().toString().trim();
            Date dateOfBirth = null;


            // Validate Full Name
            if (fullName.isEmpty() || !fullName.matches("[a-zA-Z\\s]+")) {
                showDialog("Error", "Please enter a valid full name (letters and spaces only).");
                return;
            }

            // Validate Date of Birth
            if (date.equals("YYYY.MM.DD")) {
                showDialog("Error", "Please select a date of birth.");
                return;
            }

            // Parse and validate date
            try {
                String[] dateParts = date.split("\\.");
                int year = Integer.parseInt(dateParts[0]);
                int month = Integer.parseInt(dateParts[1]) - 1; // Month in Calendar starts from 0
                int day = Integer.parseInt(dateParts[2]);

                Calendar dob = Calendar.getInstance();
                dob.set(year, month, day);

                Calendar today = Calendar.getInstance();

                if (dob.after(today)) {
                    showDialog("Error", "Date of birth cannot be in the future.");
                    return;
                }


                dateOfBirth = dob.getTime();

            } catch (Exception e) {
                showDialog("Error", "Invalid date format. Please select a valid date of birth.");
                return;
            }

            sendData(fullName, dateOfBirth);

        });
    }

    // Helper method to show a custom dialog for error messages
    private void showDialog(String title, String message) {
        CustomDialog dialog = new CustomDialog(this);
        dialog.setTitle(title)
                .setMessage(message)
                .setPositiveButton(true, "OK", v2 -> dialog.dismiss())
                .setNegativeButton(false, "", null)
                .show();
    }

    private void sendData(String fullName, Date dateOfBirth) {
        UserViewModel userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Observe the update success status
        userViewModel.getUpdateSuccess().observe(this, success -> {
            if (Boolean.TRUE.equals(success)) {
                // Update was successful
                finish();
                ProfileOptionsActivity profileOptionsActivity = new ProfileOptionsActivity();
                profileOptionsActivity.refreshData();
                Toast.makeText(this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
            } else {
                // Update failed
                showDialog("Error", "Something went wrong");
            }
        });

        // Make the call to update user details
        userViewModel.updateUserDetails(TokenManager.getAccessToken(this), fullName, dateOfBirth, this);
    }



}