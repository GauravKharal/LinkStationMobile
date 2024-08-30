package com.example.linkstation.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.linkstation.R;
import com.example.linkstation.model.LinkItem;

import java.util.List;

public class PreviewLinksAdapter extends RecyclerView.Adapter<PreviewLinksAdapter.LinkViewHolder> {

    private List<LinkItem> linkItems;

    private Context context;


    public PreviewLinksAdapter(Context context, List<LinkItem> linkItems) {
        this.context = context;
        this.linkItems = linkItems;
    }

    @NonNull
    @Override
    public PreviewLinksAdapter.LinkViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.preview_links_list, parent, false);
        return new LinkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LinkViewHolder holder, int position) {
        LinkItem linkItem = linkItems.get(position);
        holder.bind(linkItem);
    }



    @Override
    public int getItemCount() {
        return linkItems.size();
    }


    public static class LinkViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTitle;
        private final ImageView imgIcon;

        public LinkViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvLinkTitle);
            imgIcon = itemView.findViewById(R.id.ivLinkImage);
        }

        public void bind(LinkItem linkItem) {
            imgIcon.setImageURI(linkItem.getImageUri());
            tvTitle.setText(linkItem.getTitle());
        }
    }

}
