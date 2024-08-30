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

    private List<StationModel.Data.Station> stationList = new ArrayList<>();
    private StationAdapter stationAdapter;
    private RecyclerView recyclerView;

    private StationsViewModel stationsViewModel;

    private TextView recentsTab;
    private TextView viewsTab;

    private String token;


    private int page = 1;
    private int size = 10;


    public StationsFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stationsViewModel = new ViewModelProvider(this).get(StationsViewModel.class);

        token = TokenManager.getAccessToken(getActivity());
        stationsViewModel.fetchRecentStations(token,page , size, getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stations, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeVariables(view);
        handleTabs();
        setupRecyclerView();

        stationsViewModel.getStations().observe(getViewLifecycleOwner(), stations -> {
            stationList.clear();
            stationList.addAll(stations);
            stationAdapter.notifyDataSetChanged();
        });
    }

    private void initializeVariables(View view) {
        recentsTab = view.findViewById(R.id.recents_tab);
        viewsTab = view.findViewById(R.id.views_tab);
        switchTab(recentsTab, viewsTab);
    }

    private void handleTabs() {
        recentsTab.setOnClickListener(v -> {
            switchTab(recentsTab, viewsTab);
        });
        viewsTab.setOnClickListener(v -> {
            switchTab(viewsTab, recentsTab);
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

    private void setupRecyclerView() {
        recyclerView = getView().findViewById(R.id.rvStations);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        stationAdapter = new StationAdapter(stationList, R.layout.green_stations_list);
        recyclerView.setAdapter(stationAdapter);
    }
}