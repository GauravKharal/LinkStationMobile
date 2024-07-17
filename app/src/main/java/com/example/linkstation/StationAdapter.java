package com.example.linkstation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StationAdapter extends RecyclerView.Adapter<StationAdapter.StationViewHolder> {
    private final List<Station> stations;

    public StationAdapter(List<Station> stations){
        this.stations = stations;
    }

    @NonNull
    @Override
    public StationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View stationView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.latest_published_stations, parent, false);
        return new StationViewHolder(stationView);
    }

    @Override
    public void onBindViewHolder(@NonNull StationViewHolder holder, int position) {
        Station currentStation = stations.get(position);
        holder.stationURL.setText(currentStation.getStationURL());
        holder.stationTitle.setText(currentStation.getStationTitle());
    }

    @Override
    public int getItemCount() { // Use the standard method name
        return stations.size();
    }

    public static class StationViewHolder extends RecyclerView.ViewHolder {
        private final TextView stationURL; // Declare as final
        private final TextView stationTitle; // Declare as final

        public StationViewHolder(@NonNull View stationView) {
            super(stationView);
            stationURL = stationView.findViewById(R.id.stationURL);
            stationTitle = stationView.findViewById(R.id.stationTitle);
        }

        public void bind(Station station) { // Binding method in ViewHolder
            stationURL.setText(station.getStationURL());
            stationTitle.setText(station.getStationTitle());
        }
    }
}
