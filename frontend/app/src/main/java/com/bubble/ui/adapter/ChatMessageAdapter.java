package com.bubble.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bubble.R;
import com.bubble.model.Message;
import com.bubble.utils.AvatarUtils;

import java.util.ArrayList;
import java.util.List;

public class ChatMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;

    private List<Message> messages = new ArrayList<>();
    private Long currentUserId;

    public void setMessages(List<Message> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }

    public void addMessage(Message message) {
        this.messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    public void setCurrentUserId(Long currentUserId) {
        this.currentUserId = currentUserId;
    }

    public int getMessageCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message msg = messages.get(position);
        boolean isSent = (currentUserId != null && msg.getSenderId() != null
                && currentUserId.equals(msg.getSenderId()));
        return isSent ? VIEW_TYPE_SENT : VIEW_TYPE_RECEIVED;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_message_sent, parent, false);
            return new SentViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_message_received, parent, false);
            return new ReceivedViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message msg = messages.get(position);
        String nickname = msg.getNickname() != null ? msg.getNickname() : "";
        String avatarUrl = msg.getAvatar();
        String senderIdStr = msg.getSenderId() != null ? String.valueOf(msg.getSenderId()) : "";

        if (holder instanceof SentViewHolder) {
            SentViewHolder vh = (SentViewHolder) holder;
            vh.tvNickname.setText(nickname);
            vh.tvMessage.setText(msg.getContent());
            loadAvatar(vh.ivAvatar, avatarUrl, senderIdStr);
        } else if (holder instanceof ReceivedViewHolder) {
            ReceivedViewHolder vh = (ReceivedViewHolder) holder;
            vh.tvNickname.setText(nickname);
            vh.tvMessage.setText(msg.getContent());
            loadAvatar(vh.ivAvatar, avatarUrl, senderIdStr);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    private void loadAvatar(ImageView imageView, String avatarUrl, String seed) {
        // 优先使用真实头像URL，否则用 DiceBear 生成
        String loadUrl = (avatarUrl != null && !avatarUrl.isEmpty())
                ? avatarUrl
                : AvatarUtils.getAvatarUrl(seed);

        Glide.with(imageView.getContext())
                .load(loadUrl)
                .placeholder(R.drawable.ic_me_user)
                .error(R.drawable.ic_me_user)
                .circleCrop()
                .into(imageView);
    }

    static class SentViewHolder extends RecyclerView.ViewHolder {
        TextView tvNickname;
        TextView tvMessage;
        ImageView ivAvatar;

        SentViewHolder(View itemView) {
            super(itemView);
            tvNickname = itemView.findViewById(R.id.tv_nickname);
            tvMessage = itemView.findViewById(R.id.tv_message);
            ivAvatar = itemView.findViewById(R.id.iv_avatar);
        }
    }

    static class ReceivedViewHolder extends RecyclerView.ViewHolder {
        TextView tvNickname;
        TextView tvMessage;
        ImageView ivAvatar;

        ReceivedViewHolder(View itemView) {
            super(itemView);
            tvNickname = itemView.findViewById(R.id.tv_nickname);
            tvMessage = itemView.findViewById(R.id.tv_message);
            ivAvatar = itemView.findViewById(R.id.iv_avatar);
        }
    }
}
