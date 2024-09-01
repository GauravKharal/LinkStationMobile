package com.example.linkstation.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.linkstation.R;
import com.example.linkstation.model.StationViewsModel;

import java.util.List;

public class StationViewsAdapter extends RecyclerView.Adapter<StationViewsAdapter.StationViewsViewHolder> {
    private List<StationViewsModel.Data> stationViewsList;
    private int layout;

    public StationViewsAdapter(List<StationViewsModel.Data> stationViewsList, int layout) {
        this.stationViewsList = stationViewsList;
        this.layout = layout;
    }

    @Override
    public StationViewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View stationView = LayoutInflater.from(parent.getContext())
                .inflate(layout, parent, false);
        return new StationViewsViewHolder(stationView);
    }

    @Override
    public void onBindViewHolder(StationViewsViewHolder holder, int position) {
        StationViewsModel.Data currentStation = stationViewsList.get(position);
        holder.stationURL.setText(currentStation.getStation().getStationUrl());
        holder.stationTitle.setText(currentStation.getStation().getStationTitle());
        holder.viewsCount.setText(String.valueOf(currentStation.getTotalViews()));
    }

    @Override
    public int getItemCount() {
        return stationViewsList.size();
    }

    public static class StationViewsViewHolder extends RecyclerView.ViewHolder {
        private final TextView stationURL;
        private final TextView stationTitle;
        private final TextView viewsCount;

        public StationViewsViewHolder(View stationView) {
            super(stationView);
            stationURL = stationView.findViewById(R.id.stationURL);
            stationTitle = stationView.findViewById(R.id.stationTitle);
            viewsCount = stationView.findViewById(R.id.viewsCount);
        }
    }


}
