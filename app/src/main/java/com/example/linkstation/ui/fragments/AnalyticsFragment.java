package com.example.linkstation.ui.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.linkstation.R;
import com.example.linkstation.model.StationModel;
import com.example.linkstation.model.StationViewsModel;
import com.example.linkstation.model.ViewsModel;
import com.example.linkstation.ui.activities.DiscoverActivity;
import com.example.linkstation.ui.adapters.StationAdapter;
import com.example.linkstation.ui.adapters.StationViewsAdapter;
import com.example.linkstation.ui.viewmodels.StationsViewModel;
import com.example.linkstation.utilities.TokenManager;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.highlight.Highlight;

import java.util.ArrayList;
import java.util.List;

public class AnalyticsFragment extends Fragment {

    private LineChart lineChart;
    private StationsViewModel stationsViewModel;
    private LineDataSet dataSet;

    private TextView totalViews, tab7Days, tab14Days, tab28Days;
    private int selectedDays = 7; // Default selected days

    private RecyclerView rvStations;
    private List<StationViewsModel.Data> stationList = new ArrayList<>();
    private StationViewsAdapter stationViewsAdapter;

    private StationViewsModel stationViewsModel;

    public static AnalyticsFragment newInstance() {
        return new AnalyticsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the ViewModel
        stationsViewModel = new ViewModelProvider(this).get(StationsViewModel.class);

        String token = TokenManager.getAccessToken(getContext());
        stationsViewModel.fetchMyPopularStations(token, getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_analytics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        totalViews = view.findViewById(R.id.totalViews);
        tab7Days = view.findViewById(R.id.tab7Days);
        tab14Days = view.findViewById(R.id.tab14Days);
        tab28Days = view.findViewById(R.id.tab28Days);
        lineChart = view.findViewById(R.id.lineChart);

        rvStations = view.findViewById(R.id.rvStations);
        rvStations.setLayoutManager(new LinearLayoutManager(getContext()));
        stationViewsAdapter = new StationViewsAdapter(stationList, R.layout.top_stations);
        rvStations.setAdapter(stationViewsAdapter);
        stationsViewModel.getMyPopularStations().observe(getViewLifecycleOwner(), stationViews -> {
                stationList.clear();
                stationList.addAll(stationViews);
                stationViewsAdapter.notifyDataSetChanged();

        });



        // Disable zoom and dragging on the chart
        lineChart.setScaleEnabled(false);
        lineChart.setPinchZoom(false);
        lineChart.setDragEnabled(false);

        // Set up click listeners for time period tabs
        setupTabClickListeners();

        view.findViewById(R.id.discoverStations).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), DiscoverActivity.class);
            startActivity(intent);
        });

        // Observe the viewsLiveData from the ViewModel
        stationsViewModel.getTotalViews().observe(getViewLifecycleOwner(), new Observer<List<ViewsModel.Data>>() {
            @Override
            public void onChanged(List<ViewsModel.Data> viewsData) {
                if (viewsData != null) {
                    populateChart(viewsData);
                    updateTotalViews(viewsData);
                }
            }
        });

        // Fetch initial data (Example: fetch data for the default selected days)
        fetchAndDisplayData();
    }

    // Set up click listeners for the tab TextViews
    private void setupTabClickListeners() {
        tab7Days.setOnClickListener(v -> {
            tab7Days.setEnabled(false);
            tab7Days.setBackgroundResource(R.drawable.tab_blacksection);
            tab7Days.setTextColor(Color.parseColor("#ffdc01"));
            tab14Days.setEnabled(true);
            tab14Days.setBackgroundResource(R.drawable.tab_graysection);
            tab14Days.setTextColor(Color.parseColor("#000000"));
            tab28Days.setEnabled(true);
            tab28Days.setBackgroundResource(R.drawable.tab_graysection);
            tab28Days.setTextColor(Color.parseColor("#000000"));

            selectedDays = 7;
            fetchAndDisplayData();
        });

        tab14Days.setOnClickListener(v -> {
            tab14Days.setEnabled(false);
            tab14Days.setBackgroundResource(R.drawable.tab_blacksection);
            tab14Days.setTextColor(Color.parseColor("#ffdc01"));
            tab7Days.setEnabled(true);
            tab7Days.setBackgroundResource(R.drawable.tab_graysection);
            tab7Days.setTextColor(Color.parseColor("#000000"));
            tab28Days.setEnabled(true);
            tab28Days.setBackgroundResource(R.drawable.tab_graysection);
            tab28Days.setTextColor(Color.parseColor("#000000"));

            selectedDays = 14;
            fetchAndDisplayData();
        });

        tab28Days.setOnClickListener(v -> {
            tab28Days.setEnabled(false);
            tab28Days.setBackgroundResource(R.drawable.tab_blacksection);
            tab28Days.setTextColor(Color.parseColor("#ffdc01"));
            tab7Days.setEnabled(true);
            tab7Days.setBackgroundResource(R.drawable.tab_graysection);
            tab7Days.setTextColor(Color.parseColor("#000000"));
            tab14Days.setEnabled(true);
            tab14Days.setBackgroundResource(R.drawable.tab_graysection);
            tab14Days.setTextColor(Color.parseColor("#000000"));

            selectedDays = 28;
            fetchAndDisplayData();
        });
    }

    // Method to fetch and display data for the selected time period
    private void fetchAndDisplayData() {
        String token = TokenManager.getAccessToken(getContext());
        stationsViewModel.fetchViewsData(token, selectedDays, getContext());
    }

    // Method to update total views
    private void updateTotalViews(List<ViewsModel.Data> viewsData) {
        int total = 0;
        for (ViewsModel.Data data : viewsData) {
            total += data.getTotalViews();
        }
        totalViews.setText(String.valueOf(total));
    }

    // Method to populate the LineChart
    private void populateChart(List<ViewsModel.Data> viewsData) {
        ArrayList<Entry> entries = new ArrayList<>();

        for (int i = 0; i < viewsData.size(); i++) {
            ViewsModel.Data data = viewsData.get(i);
            entries.add(new Entry(i, data.getTotalViews())); // Use index as X value and totalViews as Y value
        }

        if (dataSet == null) {
            dataSet = new LineDataSet(entries, "Views");
            dataSet.setColor(Color.BLUE);
            dataSet.setLineWidth(2f);
            dataSet.setDrawValues(false);  // Initially, we won't show values

            LineData lineData = new LineData(dataSet);
            lineChart.setData(lineData);

            // Customizations for minimalistic look
            styleLineChart();
        } else {
            // Update the data set with new entries
            dataSet.setValues(entries);
            lineChart.getData().notifyDataChanged();
            lineChart.notifyDataSetChanged();
        }

        lineChart.invalidate(); // Refresh the chart
    }

    // Chart styling method
    private void styleLineChart() {
        lineChart.getXAxis().setDrawLabels(false); // Hide X-axis labels
        lineChart.getAxisLeft().setTypeface(ResourcesCompat.getFont(requireContext(), R.font.space_grotesk)); // Set custom font for Left Y-axis labels
        lineChart.getAxisRight().setDrawLabels(false); // Hide right Y-axis labels
        lineChart.getXAxis().setDrawAxisLine(false); // Hide X-axis line
        lineChart.getAxisLeft().setDrawAxisLine(false); // Hide left Y-axis line
        lineChart.getAxisRight().setDrawAxisLine(false); // Hide right Y-axis line
        lineChart.getXAxis().setDrawGridLines(false); // Hide grid lines
        lineChart.setBackgroundColor(Color.WHITE); // Set background color
        lineChart.setBorderColor(Color.TRANSPARENT); // Set border color
        lineChart.setBorderWidth(0); // Set border width to 0
        lineChart.getDescription().setEnabled(false); // Hide description
        lineChart.getLegend().setEnabled(false); // Hide legend

        // Add the listener to display the value when a point is clicked
        lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                // Enable value display for the clicked point
                dataSet.setDrawValues(true);
                dataSet.setValueTextColor(Color.BLACK);
                dataSet.setValueTextSize(8f);
                dataSet.setValueTypeface(ResourcesCompat.getFont(requireContext(), R.font.space_mono_bold));
                lineChart.invalidate();  // Refresh the chart
            }

            @Override
            public void onNothingSelected() {
                dataSet.setDrawValues(false);  // Hide values when nothing is selected
                lineChart.invalidate();  // Refresh the chart
            }
        });
    }
}
