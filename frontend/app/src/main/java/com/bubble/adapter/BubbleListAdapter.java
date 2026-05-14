package com.bubble.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bubble.R;
import java.util.List;

public class BubbleListAdapter extends RecyclerView.Adapter<BubbleListAdapter.BubbleViewHolder> {

    private List<BubbleItem> dataList;

    public static class BubbleItem {
        public String title;
        public String id;
        public int iconResId;
    }

    public BubbleListAdapter(List<BubbleItem> data) {
        this.dataList = data;
    }

    @NonNull
    @Override
    public BubbleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bubble_list, parent, false);
        return new BubbleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BubbleViewHolder holder, int position) {
        BubbleItem item = dataList.get(position);
        holder.tvTitle.setText(item.title);
        holder.tvId.setText(item.id);
        holder.ivIcon.setImageResource(item.iconResId);
    }

    @Override
    public int getItemCount() {
        return dataList != null ? dataList.size() : 0;
    }

    static class BubbleViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvTitle;
        TextView tvId;

        public BubbleViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_bubble_icon);
            tvTitle = itemView.findViewById(R.id.tv_bubble_title);
            tvId = itemView.findViewById(R.id.tv_bubble_id);
        }
    }
}
