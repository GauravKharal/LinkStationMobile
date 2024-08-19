package com.example.linkstation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DropdownAdapter extends RecyclerView.Adapter<DropdownAdapter.SocialsViewHolder> {
    private final List<String> socialsMenuItems;
    private final OnItemClickListener listener;

    // Interface for handling item clicks
    public interface OnItemClickListener {
        void onItemClick(String social);
    }

    // Constructor initializes list and click listener
    public DropdownAdapter(List<String> socialsMenuItems, OnItemClickListener listener) {
        this.socialsMenuItems = new ArrayList<>(socialsMenuItems);
        this.listener = listener;
    }

    @NonNull
    @Override
    public SocialsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the view for each social media item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.socials_item, parent, false);
        return new SocialsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SocialsViewHolder holder, int position) {
        String social = socialsMenuItems.get(position);
        holder.bind(social);

        // Set the click listener for each item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(social);
            }
        });
    }

    @Override
    public int getItemCount() {
        return socialsMenuItems.size();
    }

    // Remove an item from the list and notify the adapter
    public void removeItem(String social) {
        int position = socialsMenuItems.indexOf(social);
        if (position >= 0) {
            socialsMenuItems.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, socialsMenuItems.size());
        }
    }

    // Add an item to the list and notify the adapter
    public void addItem(String social) {
        if (!socialsMenuItems.contains(social)) {
            socialsMenuItems.add(social);
            notifyItemInserted(socialsMenuItems.size() - 1);
        }
    }

    // ViewHolder class to manage the view for each item
    public static class SocialsViewHolder extends RecyclerView.ViewHolder {
        private final TextView socialName;

        public SocialsViewHolder(View itemView) {
            super(itemView);
            socialName = itemView.findViewById(R.id.socialName);
        }

        public void bind(String social) {
            socialName.setText(social);
        }
    }
}
