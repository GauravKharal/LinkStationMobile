package com.example.linkstation.ui.fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.linkstation.R;
import com.example.linkstation.model.StationModel;
import com.example.linkstation.ui.adapters.StationAdapter;
import com.example.linkstation.ui.viewmodels.StationsViewModel;
import com.example.linkstation.utilities.TokenManager;

import java.util.ArrayList;
import java.util.List;

public class StationsFragment extends Fragment {

    private List<StationModel.Data.Station> recentStationsList = new ArrayList<>();
    private List<StationModel.Data.Station> popularStationsList = new ArrayList<>();

    private StationAdapter recentAdapter;
    private StationAdapter popularAdapter;

    private RecyclerView recyclerView;

    private StationsViewModel stationsViewModel;

    private TextView recentsTab;
    private TextView viewsTab;

    private String token;

    private int page1 = 1;
    private int page2 = 1;
    private final int size = 10;

    private boolean isRecentsSelected = true;

    public StationsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stationsViewModel = new ViewModelProvider(this).get(StationsViewModel.class);
        token = TokenManager.getAccessToken(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stations, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeVariables(view);
        setupRecyclerView();
        handleTabs();
        loadInitialData();
    }

    private void initializeVariables(View view) {
        recentsTab = view.findViewById(R.id.recents_tab);
        viewsTab = view.findViewById(R.id.views_tab);
        switchTab(isRecentsSelected ? recentsTab : viewsTab, isRecentsSelected ? viewsTab : recentsTab);
    }

    private void setupRecyclerView() {
        recyclerView = getView().findViewById(R.id.rvStations);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize adapters for both recent and popular lists
        recentAdapter = new StationAdapter(recentStationsList, R.layout.green_stations_list);
        popularAdapter = new StationAdapter(popularStationsList, R.layout.green_stations_list);

        // Set the initial adapter
        recyclerView.setAdapter(isRecentsSelected ? recentAdapter : popularAdapter);

        recyclerView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (!recyclerView.canScrollVertically(1)) {
                if (isRecentsSelected) {
                    page1++;
                    stationsViewModel.fetchLatestStations(token, page1, size, getActivity());
                }else{
                    page2++;
                    stationsViewModel.fetchPopularStations(token, page2, size, getActivity());
                }
            }
        });
    }

    private void handleTabs() {
        recentsTab.setOnClickListener(v -> {
            if (!isRecentsSelected) {
                isRecentsSelected = true;
                switchTab(recentsTab, viewsTab);
                recyclerView.setAdapter(recentAdapter);  // Set the adapter for recent stations
                if (recentStationsList.isEmpty()) {
                    stationsViewModel.fetchLatestStations(token, page1, size, getActivity());
                } else {
                    recentAdapter.notifyDataSetChanged();
                }
            }
        });

        viewsTab.setOnClickListener(v -> {
            if (isRecentsSelected) {
                isRecentsSelected = false;
                switchTab(viewsTab, recentsTab);
                recyclerView.setAdapter(popularAdapter);  // Set the adapter for popular stations
                if (popularStationsList.isEmpty()) {
                    stationsViewModel.fetchPopularStations(token, page2, size, getActivity());
                } else {
                    popularAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void switchTab(TextView thisTab, TextView otherTab) {
        thisTab.setEnabled(false);
        otherTab.setEnabled(true);
        thisTab.setBackgroundResource(R.drawable.tab_blacksection);
        thisTab.setTextColor(Color.parseColor("#ffdc01"));
        otherTab.setBackgroundResource(R.drawable.tab_graysection);
        otherTab.setTextColor(Color.parseColor("#000000"));
    }

    private void loadInitialData() {
            stationsViewModel.fetchLatestStations(token, page1, size, getActivity());
            stationsViewModel.getLatestStations().observe(getViewLifecycleOwner(), stations -> {
                recentStationsList.addAll(stations);
                recentAdapter.notifyDataSetChanged();
            });

            stationsViewModel.fetchPopularStations(token, page2, size, getActivity());
            stationsViewModel.getPopularStations().observe(getViewLifecycleOwner(), stations -> {
                popularStationsList.addAll(stations);
                popularAdapter.notifyDataSetChanged();
            });

    }
}
