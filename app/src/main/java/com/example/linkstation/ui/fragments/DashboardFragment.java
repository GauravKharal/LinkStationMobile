package com.example.linkstation.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.linkstation.model.StationModel;
import com.example.linkstation.R;
import com.example.linkstation.ui.adapters.StationAdapter;
import com.example.linkstation.utilities.TokenManager;
import com.example.linkstation.ui.viewmodels.DashboardViewModel;

import java.util.ArrayList;
import java.util.List;


public class DashboardFragment extends Fragment {

    private String username;
    private String avatar;
    private String totalMonthlyClicks;

    private TextView tvUsername;
    private TextView tvTotalMonthlyClicks;
    private ImageView ivAvatar;

    private SwipeRefreshLayout swipeRefreshLayout;

    private List<StationModel.Data.Station> stationsList = new ArrayList<>();
    private StationAdapter stationAdapter;
    private RecyclerView recyclerView;

    private DashboardViewModel dashboardViewModel;

    public DashboardFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        String token = TokenManager.getAccessToken(getActivity());
        dashboardViewModel.fetchUserDetails(token, getActivity());
        dashboardViewModel.fetchLatestPublishedStations(token, getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeVariables(view);
        //getUserDetails();
        setupRecyclerView();
        //getLatestPublishedStations();

        dashboardViewModel.getUserModel().observe(getViewLifecycleOwner(), userModel -> {
            if (userModel != null) {
                username = userModel.getData().getUser().getUsername();
                avatar = userModel.getData().getUser().getAvatar();
                //totalMonthlyClicks = userModel.getData().getUser().getTotalMonthlyClicks();
                setUserDetails();
            }
        });

        dashboardViewModel.getStations().observe(getViewLifecycleOwner(), stations -> {
            stationsList.clear();
            stationsList.addAll(stations);
            stationAdapter.notifyDataSetChanged();
        });


        swipeRefreshLayout.setOnRefreshListener(() -> {
            refreshData();
        });

    }

    public void initializeVariables(View view) {
        tvUsername = view.findViewById(R.id.tvUsername);
        tvTotalMonthlyClicks = view.findViewById(R.id.tvTotalMonthlyClicks);
        ivAvatar = view.findViewById(R.id.ivAvatar);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
    }


    public void setUserDetails() {
        tvUsername.setText(username);
        if (avatar != null) {
            Glide.with(getActivity()).load(avatar).placeholder(R.drawable.person).error(R.drawable.person).into(ivAvatar);
        }
        tvTotalMonthlyClicks.setText(totalMonthlyClicks);
    }


    private void setupRecyclerView() {
        recyclerView = getView().findViewById(R.id.latest_published_stations_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        stationAdapter = new StationAdapter(stationsList, R.layout.latest_published_stations);
        recyclerView.setAdapter(stationAdapter);
    }

    private void refreshData() {
        String token = TokenManager.getAccessToken(getActivity());
        dashboardViewModel.fetchUserDetails(token, getActivity());
        dashboardViewModel.fetchLatestPublishedStations(token, getActivity());
        swipeRefreshLayout.setRefreshing(false);
    }



}