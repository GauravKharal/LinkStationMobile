package com.example.linkstation.ui.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.linkstation.R;
import com.example.linkstation.model.CreateStationRequest;
import com.example.linkstation.model.StationModel;
import com.example.linkstation.network.ApiService;
import com.example.linkstation.network.RetrofitClient;
import com.example.linkstation.ui.activities.MainActivity;
import com.example.linkstation.utilities.CustomDialog;
import com.example.linkstation.utilities.CustomProgressDialog;
import com.example.linkstation.utilities.TokenManager;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PublishFragment extends BottomSheetDialogFragment {

    // Important Data Components from this Fragment
    private Button btnPublish;
    private RadioGroup rgVisibility;
    private EditText stationIdentifier;
    private CheckBox cbTAC;
    private LinearLayout btnPreview;

    private ConstraintLayout main;
    private ConstraintLayout activityMain;
    CustomProgressDialog progressDialog;

    // station model
    private StationModel stationModel;
    ApiService apiService;



    public PublishFragment() {
        // Required empty public constructor
    }

    public static PublishFragment newInstance(StationModel stationModel) {
        PublishFragment fragment = new PublishFragment();
        Bundle args = new Bundle();
        args.putSerializable("stationModel", stationModel);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            stationModel = (StationModel) getArguments().getSerializable("stationModel");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_publish, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rgVisibility = view.findViewById(R.id.rgVisibility);
        stationIdentifier = view.findViewById(R.id.stationIdentifier);
        cbTAC = view.findViewById(R.id.cbTAC);
        btnPublish = view.findViewById(R.id.btnPublish);
        main = view.findViewById(R.id.main);
        btnPreview = view.findViewById(R.id.preview);

        activityMain = getActivity().findViewById(R.id.main);

        handlePreview();

        progressDialog = new CustomProgressDialog(getActivity());
        progressDialog.setTitle("");



        btnPublish.setOnClickListener(v -> {
            main.setEnabled(false);
            activityMain.setEnabled(false);
            progressDialog.show();
            boolean isTacChecked = cbTAC.isChecked();
            String stationUrl = stationIdentifier.getText().toString().trim();
            if (isTacChecked && !stationUrl.isEmpty() && isValidUrl(stationUrl)) {
                stationUrlValidation(stationUrl);
            } else if (!isTacChecked) {
                CustomDialog dialog = new CustomDialog(getActivity());
                dialog.setTitle("Terms and Conditions")
                        .setMessage("You must accept the terms and conditions to publish a station")
                        .setPositiveButton(true, "Ok", v1 -> {
                            main.setEnabled(true);
                            activityMain.setEnabled(true);
                            progressDialog.dismiss();
                            dialog.dismiss();
                        })
                        .setNegativeButton(false, "", null)
                        .show();
            } else if (stationUrl.isEmpty()) {
                CustomDialog dialog = new CustomDialog(getActivity());
                dialog.setTitle("Warning")
                        .setMessage("Station url cannot be empty")
                        .setPositiveButton(true, "Ok", v1 -> {
                            main.setEnabled(true);
                            activityMain.setEnabled(true);
                            progressDialog.dismiss();
                            dialog.dismiss();
                        })
                        .setNegativeButton(false, "", null)
                        .show();
            } else {
                CustomDialog dialog = new CustomDialog(getActivity());
                dialog.setTitle("Warning")
                        .setMessage("Invalid station url")
                        .setPositiveButton(true, "Ok", v1 -> {
                            main.setEnabled(true);
                            activityMain.setEnabled(true);
                            progressDialog.dismiss();
                            dialog.dismiss();
                        })
                        .setNegativeButton(false, "", null)
                        .show();
            }

        });
    }

    private void stationUrlValidation(String stationUrl) {
        main.setEnabled(false);
        activityMain.setEnabled(false);
        progressDialog.setTitle("Validating Station Url");
        apiService = RetrofitClient.getClient(getActivity()).create(ApiService.class);
        Call<ResponseBody> call = apiService.getStation("/api/v1/station/s/" + stationUrl);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("Response", response.toString());
                int statusCode = response.code();
                if (statusCode == 200) {
                    progressDialog.dismiss();
                    CustomDialog dialog = new CustomDialog(getActivity());
                    dialog.setTitle("Warning")
                            .setMessage("This station already exists")
                            .setPositiveButton(true, "Ok", v1 -> {
                                dialog.dismiss();
                                main.setEnabled(true);
                                activityMain.setEnabled(true);

                            })
                            .setNegativeButton(false, "", null)
                            .show();

                } else if (statusCode == 404) {
                    prepareAndSendData();
                } else {
                    progressDialog.dismiss();
                    CustomDialog dialog = new CustomDialog(getActivity());
                    dialog.setTitle("Warning")
                            .setMessage("Something went wrong")
                            .setPositiveButton(true, "Ok", v1 -> {
                                dialog.dismiss();
                                main.setEnabled(true);
                                activityMain.setEnabled(true);
                            })
                            .setNegativeButton(false, "", null)
                            .show();
                }
            }


            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                progressDialog.dismiss();
                CustomDialog dialog = new CustomDialog(getActivity());
                dialog.setTitle("Warning")
                        .setMessage("Something went wrong")
                        .setPositiveButton(true, "Ok", v1 -> {
                            dialog.dismiss();
                            main.setEnabled(true);
                            activityMain.setEnabled(true);
                        })
                        .setNegativeButton(false, "", null)
                        .show();
            }
        });
    }

    public boolean isValidUrl(String url) {
        String usernamePattern = "^[a-zA-Z0-9._]{4,25}$";
        return url.matches(usernamePattern);
    }


    private void prepareAndSendData() {
        progressDialog.setTitle("Uploading Station");

        progressDialog.setProgress(10);
        DataBundle data = prepareData();

        progressDialog.setProgress(50);
        sendData(data);
    }


    private DataBundle prepareData() {
        // Create the request model
        CreateStationRequest createStationRequest = new CreateStationRequest();
        boolean visibility = rgVisibility.getCheckedRadioButtonId() == R.id.rbPublic;
        String stationUrl = stationIdentifier.getText().toString();

        createStationRequest.setStationUrl(stationUrl);
        createStationRequest.setStationTitle(stationModel.getData().getStation().getStationTitle());
        createStationRequest.setStationDescription(stationModel.getData().getStation().getStationDescription());
        createStationRequest.setInstagram(stationModel.getData().getStation().getInstagram());
        createStationRequest.setFacebook(stationModel.getData().getStation().getFacebook());
        createStationRequest.setTwitter(stationModel.getData().getStation().getTwitter());
        createStationRequest.setYoutube(stationModel.getData().getStation().getYoutube());
        createStationRequest.setVisibility(visibility);

        // Populate links part
        createStationRequest.setLinks(new CreateStationRequest.Links());
        for (int i = 0; i < stationModel.getData().getLinks().size(); i++) {
            createStationRequest.getLinks().getUrl().add(stationModel.getData().getLinks().get(i).getUrl());
            createStationRequest.getLinks().getTitle().add(stationModel.getData().getLinks().get(i).getTitle());
            createStationRequest.getLinks().getPosition().add(String.valueOf(i + 1));
        }

        // Prepare MultipartBody.Part for stationImage and linkImages
        final MultipartBody.Part stationImagePart;
        final List<MultipartBody.Part> linkImagesParts = new ArrayList<>();

        // Check if there is a station image and add it
        if (stationModel.getData().getStation().getStationImage() != null) {
            File stationImageFile = new File(stationModel.getData().getStation().getStationImage().getPath());
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), stationImageFile);
            stationImagePart = MultipartBody.Part.createFormData("stationImage", stationImageFile.getName(), requestFile);
        } else {
            stationImagePart = null;
        }

        // Add link images to the list
        for (File linkImageFile : stationModel.getData().getLinkImages()) {
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), linkImageFile);
            MultipartBody.Part linkImagePart = MultipartBody.Part.createFormData("linkImages", linkImageFile.getName(), requestFile);
            linkImagesParts.add(linkImagePart);
        }

        // Convert individual fields to RequestBody
        RequestBody stationUrlBody = RequestBody.create(MediaType.parse("text/plain"), stationUrl);
        RequestBody stationTitleBody = RequestBody.create(MediaType.parse("text/plain"), stationModel.getData().getStation().getStationTitle());
        RequestBody stationDescriptionBody = RequestBody.create(MediaType.parse("text/plain"), stationModel.getData().getStation().getStationDescription());
        RequestBody instagramBody = RequestBody.create(MediaType.parse("text/plain"), stationModel.getData().getStation().getInstagram());
        RequestBody facebookBody = RequestBody.create(MediaType.parse("text/plain"), stationModel.getData().getStation().getFacebook());
        RequestBody twitterBody = RequestBody.create(MediaType.parse("text/plain"), stationModel.getData().getStation().getTwitter());
        RequestBody youtubeBody = RequestBody.create(MediaType.parse("text/plain"), stationModel.getData().getStation().getYoutube());
        RequestBody visibilityBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(visibility));

        // Convert links to RequestBody lists
        List<RequestBody> linkUrls = new ArrayList<>();
        List<RequestBody> linkTitles = new ArrayList<>();
        List<RequestBody> linkPositions = new ArrayList<>();

        for (int i = 0; i < createStationRequest.getLinks().getUrl().size(); i++) {
            linkUrls.add(RequestBody.create(MediaType.parse("text/plain"), createStationRequest.getLinks().getUrl().get(i)));
            linkTitles.add(RequestBody.create(MediaType.parse("text/plain"), createStationRequest.getLinks().getTitle().get(i)));
            linkPositions.add(RequestBody.create(MediaType.parse("text/plain"), createStationRequest.getLinks().getPosition().get(i)));
        }

        return new DataBundle(createStationRequest, stationImagePart, linkImagesParts, stationUrlBody, stationTitleBody, stationDescriptionBody, instagramBody, facebookBody,twitterBody,youtubeBody, visibilityBody, linkUrls, linkTitles, linkPositions);
    }

    // Helper class to hold the prepared data
    private class DataBundle {
        CreateStationRequest createStationRequest;
        MultipartBody.Part stationImagePart;
        List<MultipartBody.Part> linkImagesParts;
        RequestBody stationUrlBody;
        RequestBody stationTitleBody;
        RequestBody stationDescriptionBody;
        RequestBody instagramBody;
        RequestBody facebookBody;
        RequestBody twitterBody;
        RequestBody youtubeBody;
        RequestBody visibilityBody;
        List<RequestBody> linkUrls;
        List<RequestBody> linkTitles;
        List<RequestBody> linkPositions;

        DataBundle(CreateStationRequest createStationRequest, MultipartBody.Part stationImagePart, List<MultipartBody.Part> linkImagesParts,
                   RequestBody stationUrlBody, RequestBody stationTitleBody, RequestBody stationDescriptionBody, RequestBody instagramBody, RequestBody facebookBody, RequestBody twitterBody, RequestBody youtubeBody, RequestBody visibilityBody,
                   List<RequestBody> linkUrls, List<RequestBody> linkTitles, List<RequestBody> linkPositions) {
            this.createStationRequest = createStationRequest;
            this.stationImagePart = stationImagePart;
            this.linkImagesParts = linkImagesParts;
            this.stationUrlBody = stationUrlBody;
            this.stationTitleBody = stationTitleBody;
            this.stationDescriptionBody = stationDescriptionBody;
            this.instagramBody = instagramBody;
            this.facebookBody = facebookBody;
            this.twitterBody = twitterBody;
            this.youtubeBody = youtubeBody;
            this.visibilityBody = visibilityBody;
            this.linkUrls = linkUrls;
            this.linkTitles = linkTitles;
            this.linkPositions = linkPositions;
        }
    }

    private void sendData(DataBundle data) {
        // Retrieve the access token
        String accessToken = TokenManager.getAccessToken(getActivity());

        // Execute the network request in the background using ExecutorService
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            progressDialog.setProgress(70);
            // Perform the network request here synchronously
            try {
                Response<StationModel> response = apiService.createStation(
                        accessToken,
                        data.stationImagePart,
                        data.linkImagesParts,
                        data.stationUrlBody,
                        data.stationTitleBody,
                        data.stationDescriptionBody,
                        data.instagramBody,
                        data.facebookBody,
                        data.twitterBody,
                        data.youtubeBody,
                        data.visibilityBody,
                        data.linkUrls,
                        data.linkTitles,
                        data.linkPositions
                ).execute();

                progressDialog.setProgress(90);

                handler.post(() -> {
                    // Update the UI on the main thread
                    if (response.isSuccessful()) {
                        progressDialog.setProgress(100);
                        Toast.makeText(getActivity(), "Station Published", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();

                        // Redirect to the dashboard
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    } else {
                        Toast.makeText(getActivity(), "idk what went wrong", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                handler.post(() -> {
                    // Handle the failure
                    Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                });
            }
        });
    }


    private void handlePreview(){
        btnPreview.setOnClickListener(v -> {
            PreviewFragment previewFragment = new PreviewFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("stationModel", stationModel);
            previewFragment.setArguments(bundle);

            previewFragment.show(getParentFragmentManager(),"PreviewFragment");
        });

    }


}