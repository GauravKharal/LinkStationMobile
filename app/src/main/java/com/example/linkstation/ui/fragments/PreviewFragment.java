package com.example.linkstation.ui.fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.linkstation.R;
import com.example.linkstation.model.LinkItem;
import com.example.linkstation.model.StationModel;
import com.example.linkstation.ui.adapters.LinksAdapter;
import com.example.linkstation.ui.adapters.PreviewLinksAdapter;

import java.util.ArrayList;
import java.util.List;

public class PreviewFragment extends DialogFragment {

    private StationModel stationModel;

    private Button btnBack;
    private ImageView ivStationImage;
    private TextView tvStationTitle;
    private TextView tvStationDescription;
    private RecyclerView rvLinks;
    private ImageView instagram;
    private ImageView facebook;
    private ImageView twitter;
    private ImageView youtube;
    private ImageView backgroundImage;

    public PreviewFragment() {
        // Required empty public constructor
    }

    public static PreviewFragment newInstance(StationModel stationModel) {
        PreviewFragment fragment = new PreviewFragment();
        Bundle args = new Bundle();
        args.putSerializable("stationModel", stationModel);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_preview, container, false);

        if (getArguments() != null) {
            stationModel = (StationModel) getArguments().getSerializable("stationModel");
        }

        initializeUiComponents(view);

        if (stationModel != null) {
            setViewValues();
        }

        btnBack.setOnClickListener(v -> dismiss());


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Make the dialog full-screen
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    private void initializeUiComponents(View view) {
        btnBack = view.findViewById(R.id.btnBack);
        ivStationImage = view.findViewById(R.id.ivStationImage);
        tvStationTitle = view.findViewById(R.id.tvStationTitle);
        tvStationDescription = view.findViewById(R.id.tvStationDescription);
        rvLinks = view.findViewById(R.id.rvLinks);
        instagram = view.findViewById(R.id.instagram);
        facebook = view.findViewById(R.id.facebook);
        twitter = view.findViewById(R.id.twitter);
        youtube = view.findViewById(R.id.youtube);
        backgroundImage = view.findViewById(R.id.backgroundImage);
        backgroundImage.setRenderEffect(RenderEffect.createBlurEffect(100f,100f, Shader.TileMode.CLAMP));
    }

    private void setViewValues() {
        Glide.with(this)
                .load(stationModel.getData().getStation().getStationImage())
                .into(ivStationImage);
        Glide.with(this)
                .load(stationModel.getData().getStation().getStationImage())
                .into(backgroundImage);
        tvStationTitle.setText(stationModel.getData().getStation().getStationTitle());
        tvStationDescription.setText(stationModel.getData().getStation().getStationDescription());

        setupRecyclerView();
        setupSocials();
    }

    private void setupRecyclerView() {
        List<LinkItem> linkItems = new ArrayList<>();
        for (StationModel.Data.Link link : stationModel.getData().getLinks()) {
            linkItems.add(new LinkItem(Uri.fromFile(link.getLinkImage()), link.getTitle(), Uri.parse(link.getUrl())));
        }
        PreviewLinksAdapter linksAdapter = new PreviewLinksAdapter(getContext(), linkItems);
        rvLinks.setLayoutManager(new LinearLayoutManager(getContext()));
        rvLinks.setAdapter(linksAdapter);


    }

    private void setupSocials() {
        if (stationModel.getData().getStation().getInstagram() != null) {
            if (!stationModel.getData().getStation().getInstagram().isEmpty()) {
                instagram.setVisibility(View.VISIBLE);
            } else {
                instagram.setVisibility(View.GONE);
            }
        } else {
            instagram.setVisibility(View.GONE);
        }

        if (stationModel.getData().getStation().getFacebook() != null) {
            if (!stationModel.getData().getStation().getFacebook().isEmpty()) {
                facebook.setVisibility(View.VISIBLE);
            } else {
                facebook.setVisibility(View.GONE);
            }
        } else {
            facebook.setVisibility(View.GONE);
        }

        if (stationModel.getData().getStation().getTwitter() != null) {
            if (!stationModel.getData().getStation().getTwitter().isEmpty()) {
                twitter.setVisibility(View.VISIBLE);
            } else {
                twitter.setVisibility(View.GONE);
            }
        } else {
            twitter.setVisibility(View.GONE);
        }

        if (stationModel.getData().getStation().getYoutube() != null) {
            if (!stationModel.getData().getStation().getYoutube().isEmpty()) {
                youtube.setVisibility(View.VISIBLE);
            } else {
                youtube.setVisibility(View.GONE);
            }
        } else {
            youtube.setVisibility(View.GONE);
        }

    }
}