package com.example.linkstation.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.linkstation.R;
import com.example.linkstation.model.LinkItem;
import com.example.linkstation.model.StationModel;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LinksAdapter extends RecyclerView.Adapter<LinksAdapter.LinkViewHolder> {

    private List<LinkItem> linkItems;

    private OnStartDragListener dragStartListener;
    private OnLinkLongClickListener longClickListener;
    private OnLinkClickListener linkClickListener;

    private Context context;

    public interface OnStartDragListener {
        void onStartDrag(RecyclerView.ViewHolder viewHolder);
    }

    public interface OnLinkLongClickListener {
        boolean onLinkLongClick(int position);
    }
    public interface OnLinkClickListener {
        void onLinkClick(int position);
    }


    public LinksAdapter(Context context, List<LinkItem> linkItems, OnLinkLongClickListener longClickListener, OnStartDragListener dragStartListener, OnLinkClickListener linkClickListener) {
        this.context = context;
        this.linkItems = linkItems;
        this.longClickListener = longClickListener;
        this.dragStartListener = dragStartListener;
        this.linkClickListener = linkClickListener;
    }

    @NonNull
    @Override
    public LinkViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.added_link, parent, false);
        return new LinkViewHolder(view);
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(LinkViewHolder holder, int position) {
        LinkItem linkItem = linkItems.get(position);
        holder.bind(linkItem);

        // Set click listener for the item
        holder.itemView.setOnClickListener(v -> {
            linkClickListener.onLinkClick(holder.getAdapterPosition());
        });


        // Set long press listener for selection
        holder.itemView.setOnLongClickListener(v -> {
            boolean isSelected = longClickListener.onLinkLongClick(holder.getAdapterPosition());
            if (isSelected) {
                holder.itemView.setBackgroundResource(R.drawable.button_white_sharp_selected);
            } else {
                holder.itemView.setBackgroundResource(R.drawable.button_white_sharp);
            }
            linkItem.setSelected(isSelected);
            notifyItemChanged(position);
            return true;
        });


        // Set touch listener on the drag handle
        holder.dragHandle.setOnTouchListener((v, event) -> {
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                dragStartListener.onStartDrag(holder);
            }
            return false;
        });

    }

    @Override
    public int getItemCount() {
        return linkItems.size();
    }

    public void addLink(LinkItem link) {
        linkItems.add(link);
        notifyItemInserted(linkItems.size() - 1);
    }

    public void removeItem(int position) {
        linkItems.remove(position);
        notifyItemRemoved(position);
    }

    public void removeAllSelected(List<Integer> selectedPositions) {

        List<LinkItem> itemsToRemove = new ArrayList<>();
        for (Integer pos : selectedPositions) {
            itemsToRemove.add(linkItems.get(pos));
        }
        linkItems.removeAll(itemsToRemove);


        // Notify the adapter that the data set has changed
        notifyDataSetChanged();

    }

    public static class LinkViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTitle;
        private final TextView tvUrl;
        private final ImageView imgIcon;
        private final ImageView dragHandle;

        public LinkViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvUrl = itemView.findViewById(R.id.tvUrl);
            imgIcon = itemView.findViewById(R.id.imgIcon);
            dragHandle = itemView.findViewById(R.id.dragHandle);
        }

        public void bind(LinkItem linkItem) {
            imgIcon.setImageURI(linkItem.getImageUri());
            tvTitle.setText(linkItem.getTitle());
            tvUrl.setText(linkItem.getUrl().toString());
        }
    }


    public void onItemMove(int fromPosition, int toPosition) {
        Collections.swap(linkItems, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    public List<StationModel.Data.Link> getLinksForStationModel() {
        List<StationModel.Data.Link> links = new ArrayList<>();

        for (int i = 0; i < linkItems.size(); i++) {

            LinkItem linkItem = linkItems.get(i);
            File linkImageFile = new File(getRealPathFromURI(linkItem.getImageUri()));
            StationModel.Data.Link link = new StationModel.Data.Link(
                    linkItem.getUrl().toString(),
                    linkItem.getTitle(),
                    linkImageFile,
                    i
            );
            links.add(link);
        }
        return links;
    }
    private String getRealPathFromURI(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String filePath = cursor.getString(column_index);
            cursor.close();
            return filePath;
        }
        return null;
    }

}
