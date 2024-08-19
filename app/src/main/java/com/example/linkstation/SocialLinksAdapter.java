package com.example.linkstation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.linkstation.model.SocialMedia;
import com.example.linkstation.ui.CreateStationActivity;

import java.util.List;

public class SocialLinksAdapter extends RecyclerView.Adapter<SocialLinksAdapter.ViewHolder> {

    private final List<SocialMedia> socialLinksList;

    // Constructor to initialize the adapter with a list of social media items
    public SocialLinksAdapter(List<SocialMedia> socialLinksList) {
        this.socialLinksList = socialLinksList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the layout for individual items
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.socials_link, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SocialMedia socialMedia = socialLinksList.get(position);
        // Set the icon and hint text for the current item
        holder.iconView.setImageResource(socialMedia.getIcon());
        holder.usernameEditText.setHint(socialMedia.getUsername() + " Username");

        // Handle the delete button click
        holder.btnDelete.setOnClickListener(v -> {
            // Check if the position is valid
            if (position != RecyclerView.NO_POSITION && position < socialLinksList.size()) {
                // Notify CreateStationActivity to add the social media back to the dropdown
                ((CreateStationActivity) holder.itemView.getContext()).addSocialBackToDropdown(socialMedia.getUsername());
                // Remove item from the list and notify the adapter
                socialLinksList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, socialLinksList.size());
            } else {
                // Log an error if the position is invalid
                Log.e("SocialLinksAdapter", "Invalid position: " + position);
            }
        });
    }

    @Override
    public int getItemCount() {
        // Return the size of the social media list
        return socialLinksList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iconView;
        EditText usernameEditText;
        Button btnDelete;

        // ViewHolder constructor to initialize views
        public ViewHolder(View itemView) {
            super(itemView);
            iconView = itemView.findViewById(R.id.socialIcon);
            usernameEditText = itemView.findViewById(R.id.socialUsername);
            btnDelete = itemView.findViewById(R.id.socialDelete);
        }
    }
}
