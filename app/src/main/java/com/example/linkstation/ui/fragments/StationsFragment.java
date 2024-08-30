package com.example.linkstation.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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


    public StationsFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stationsViewModel = new ViewModelProvider(this).get(StationsViewModel.class);

        String token = TokenManager.getAccessToken(getActivity());
        stationsViewModel.fetchStations(token, 1, 10, getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stations, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeVariables(view);

        setupRecyclerView();

        stationsViewModel.getStations().observe(getViewLifecycleOwner(), stations -> {
            stationList.clear();
            stationList.addAll(stations);
            stationAdapter.notifyDataSetChanged();
        });
    }

    public void initializeVariables(View view) {}

    private void setupRecyclerView(){
        recyclerView = getView().findViewById(R.id.rvStations);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        stationAdapter = new StationAdapter(stationList, R.layout.green_stations_list);
        recyclerView.setAdapter(stationAdapter);
    }
}