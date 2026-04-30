package com.bubble.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bubble.R;
import com.bubble.ui.model.CardItem;
import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    private List<CardItem> cardList;

    public CardAdapter(List<CardItem> cardList) {
        this.cardList = cardList;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_card, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        CardItem card = cardList.get(position);
        holder.bind(card);
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    public void updateList(List<CardItem> newList) {
        this.cardList = newList;
        notifyDataSetChanged();
    }

    static class CardViewHolder extends RecyclerView.ViewHolder {
        private final ImageView avatar;
        private final TextView tvTitle;
        private final TextView tvSubtitle;
        private final ImageView bgWhitestab;
        private final LinearLayout tagsContainer;
        private final LinearLayout avatarsContainer;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvSubtitle = itemView.findViewById(R.id.tv_subtitle);
            bgWhitestab = itemView.findViewById(R.id.bg_whitetab);
            tagsContainer = itemView.findViewById(R.id.tags_container);
            avatarsContainer = itemView.findViewById(R.id.avatars_container);
        }

        public void bind(CardItem card) {
            tvTitle.setText(card.getTitle());
            tvSubtitle.setText(card.getSubtitle());
            
            // 动态调整whitetab背景宽度
            tvTitle.post(() -> {
                int textWidth = tvTitle.getWidth();
                int padding = dpToPx(itemView.getContext(), 24); // 左右padding各12dp
                int minWidth = dpToPx(itemView.getContext(), 60); // 最小宽度
                int newWidth = Math.max(textWidth + padding, minWidth);
                
                ViewGroup.LayoutParams params = bgWhitestab.getLayoutParams();
                params.width = newWidth;
                bgWhitestab.setLayoutParams(params);
            });

            // 动态添加标签
            tagsContainer.removeAllViews();
            List<CardItem.TagItem> tags = card.getTags();
            for (int i = 0; i < tags.size(); i++) {
                CardItem.TagItem tag = tags.get(i);
                View tagView = createTagView(itemView.getContext(), tag);
                tagsContainer.addView(tagView);
            }

            // 动态添加右侧头像
            avatarsContainer.removeAllViews();
            int avatarCount = card.getAvatarRightCount();
            for (int i = 0; i < avatarCount; i++) {
                ImageView avatarView = createAvatarView(itemView.getContext());
                avatarsContainer.addView(avatarView);
            }
        }

        private View createTagView(android.content.Context context, CardItem.TagItem tag) {
            LinearLayout tagLayout = new LinearLayout(context);
            tagLayout.setOrientation(LinearLayout.HORIZONTAL);
            tagLayout.setGravity(android.view.Gravity.CENTER_VERTICAL);
            
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    dpToPx(context, 24)
            );
            if (tagsContainer.getChildCount() > 0) {
                params.setMarginStart(dpToPx(context, 7));
            }
            tagLayout.setLayoutParams(params);
            tagLayout.setBackgroundResource(R.drawable.bg_blacktab);
            tagLayout.setPadding(dpToPx(context, 6), 0, dpToPx(context, 10), 0);

            // 图标
            ImageView icon = new ImageView(context);
            icon.setLayoutParams(new LinearLayout.LayoutParams(
                    dpToPx(context, tag.isCheck() ? 18 : 16),
                    dpToPx(context, tag.isCheck() ? 18 : 16)
            ));
            icon.setImageResource(tag.isCheck() ? R.drawable.ic_square_right : R.drawable.ic_cross_white);
            tagLayout.addView(icon);

            // 文字
            TextView text = new TextView(context);
            LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            textParams.setMarginStart(dpToPx(context, 3));
            text.setLayoutParams(textParams);
            text.setText(tag.getText());
            text.setTextColor(0xFFFFFFFF);
            text.setTextSize(12);
            text.setTypeface(null, android.graphics.Typeface.BOLD);
            text.setSingleLine(true);
            tagLayout.addView(text);

            return tagLayout;
        }

        private ImageView createAvatarView(android.content.Context context) {
            ImageView avatar = new ImageView(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    dpToPx(context, 40),
                    dpToPx(context, 40)
            );
            params.setMarginEnd(dpToPx(context, 4));
            avatar.setLayoutParams(params);
            avatar.setBackgroundResource(R.drawable.bg_circle);
            avatar.setScaleType(ImageView.ScaleType.CENTER_CROP);
            avatar.setImageResource(R.drawable.ic_me_user);
            return avatar;
        }

        private int dpToPx(android.content.Context context, int dp) {
            return (int) (dp * context.getResources().getDisplayMetrics().density);
        }
    }
}
