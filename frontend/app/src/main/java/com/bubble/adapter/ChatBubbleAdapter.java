package com.bubble.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bubble.R;
import java.util.ArrayList;
import java.util.List;

public class ChatBubbleAdapter extends RecyclerView.Adapter<ChatBubbleAdapter.ChatBubbleViewHolder> {

    private List<ChatBubbleItem> dataList = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(ChatBubbleItem item, int position);
    }

    public static class ChatBubbleItem {
        public String title;
        public String id;
        public int imageResId;

        public ChatBubbleItem(String title, String id, int imageResId) {
            this.title = title;
            this.id = id;
            this.imageResId = imageResId;
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setData(List<ChatBubbleItem> data) {
        this.dataList = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChatBubbleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_bubble, parent, false);
        return new ChatBubbleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatBubbleViewHolder holder, int position) {
        ChatBubbleItem item = dataList.get(position);
        holder.tvTitle.setText(item.title);
        holder.tvId.setText(item.id);
        holder.ivBubble.setImageResource(item.imageResId);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    static class ChatBubbleViewHolder extends RecyclerView.ViewHolder {
        FrameLayout bubbleContainer;
        ImageView ivBubble;
        TextView tvTitle;
        TextView tvId;
        ImageView ivLine;

        public ChatBubbleViewHolder(@NonNull View itemView) {
            super(itemView);
            bubbleContainer = itemView.findViewById(R.id.bubble_container);
            ivBubble = itemView.findViewById(R.id.iv_bubble);
            tvTitle = itemView.findViewById(R.id.tv_bubble_title);
            tvId = itemView.findViewById(R.id.tv_bubble_id);
            ivLine = itemView.findViewById(R.id.iv_line);
        }
    }

}
