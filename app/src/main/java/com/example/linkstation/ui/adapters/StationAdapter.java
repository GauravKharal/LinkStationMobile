package com.example.linkstation.ui.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.linkstation.R;
import com.example.linkstation.model.StationModel;

import java.util.List;

public class StationAdapter extends RecyclerView.Adapter<StationAdapter.StationViewHolder> {
    private final List<StationModel.Data.Station> stations;
    private final int layout;

    public StationAdapter(List<StationModel.Data.Station> stations, int layout){
        this.stations = stations;
        this.layout = layout;
    }


    @NonNull
    @Override
    public StationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View stationView = LayoutInflater.from(parent.getContext())
                .inflate(layout, parent, false);
        return new StationViewHolder(stationView);
    }

    @Override
    public void onBindViewHolder(@NonNull StationViewHolder holder, int position) {
        StationModel.Data.Station currentStation = stations.get(position);
        holder.stationURL.setText(currentStation.getStationUrl());
        holder.stationTitle.setText(currentStation.getStationTitle());
        Glide.with(holder.itemView.getContext())
                .load(currentStation.getImage())
                .into(holder.stationImage);
        holder.viewsCount.setText(String.valueOf(currentStation.getViews()));
    }

    @Override
    public int getItemCount() { // Use the standard method name
        return stations.size();
    }

    public static class StationViewHolder extends RecyclerView.ViewHolder {
        private final TextView stationURL;
        private final TextView stationTitle;
        private final ImageView stationImage;
        private final TextView viewsCount;

        public StationViewHolder(@NonNull View stationView) {
            super(stationView);
            stationURL = stationView.findViewById(R.id.stationURL);
            stationTitle = stationView.findViewById(R.id.stationTitle);
            stationImage = stationView.findViewById(R.id.stationImage);
            viewsCount = stationView.findViewById(R.id.viewsCount);
        }

        public void bind(StationModel.Data.Station station) { // Binding method in ViewHolder
            stationURL.setText(station.getStationUrl());
            stationTitle.setText(station.getStationTitle());
            Glide.with(itemView.getContext())
                    .load(station.getStationImage())
                    .into(stationImage);
        }
    }
}
