package com.bubble.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bubble.R;
import java.util.List;
import java.util.Random;

public class BubbleAdapter extends RecyclerView.Adapter<BubbleAdapter.BubbleViewHolder> {

    private List<BubbleItem> bubbleList;
    private OnItemClickListener clickListener;

    public interface OnItemClickListener {
        void onItemClick(BubbleItem item, int position);
    }

    public static class BubbleItem {
        public String title;
        public String number;
        public int starCount;

        public BubbleItem(String title, String number, int starCount) {
            this.title = title;
            this.number = number;
            this.starCount = starCount;
        }
    }

    public BubbleAdapter(List<BubbleItem> bubbleList) {
        this.bubbleList = bubbleList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public BubbleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bubble, parent, false);
        return new BubbleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BubbleViewHolder holder, int position) {
        BubbleItem item = bubbleList.get(position);

        holder.tvBubble.setText(item.title);
        holder.tvNumber.setText(item.number);
        generateStars(holder.starsContainer, item.starCount, position);

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onItemClick(item, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bubbleList.size();
    }

    /**
     * 动态生成星星，保持与原布局一致的视觉效果
     */
    private void generateStars(LinearLayout container, int starCount, int seed) {
        container.removeAllViews();

        Random random = new Random(seed);

        // 预定义的16颗星星位置配置（更紧凑的排列）
        float[][] positions = {
                {12f, 8f}, {18f, 7f}, {24f, 8f}, {30f, 9f},   // 第一排 4颗
                {10f, 13f}, {17f, 12f}, {24f, 13f}, {31f, 14f},  // 第二排 4颗
                {11f, 18f}, {18f, 17f}, {25f, 18f}, {32f, 19f},  // 第三排 4颗
                {11f, 23f}, {18f, 22f}, {25f, 24f}, {31f, 23f}   // 第四排 4颗
        };

        int count = Math.min(starCount, positions.length);
        for (int i = 0; i < count; i++) {
            ImageView star = new ImageView(container.getContext());
            star.setImageResource(R.drawable.ic_chat_starmin);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    (int) (10 * container.getContext().getResources().getDisplayMetrics().density),
                    (int) (10 * container.getContext().getResources().getDisplayMetrics().density)
            );
            params.leftMargin = (int) positions[i][0];
            params.topMargin = (int) positions[i][1];
            star.setLayoutParams(params);

            // 随机旋转角度（-50 到 60 度之间）
            star.setRotation(random.nextFloat() * 110 - 50);

            container.addView(star);
        }
    }

    static class BubbleViewHolder extends RecyclerView.ViewHolder {
        ImageView icBubble;
        TextView tvBubble;
        TextView tvNumber;
        ImageView icChatLine;
        LinearLayout starsContainer;

        BubbleViewHolder(View itemView) {
            super(itemView);
            icBubble = itemView.findViewById(R.id.ic_bubble);
            tvBubble = itemView.findViewById(R.id.tv_bubble);
            tvNumber = itemView.findViewById(R.id.tv_number);
            icChatLine = itemView.findViewById(R.id.ic_chat_line);
            starsContainer = itemView.findViewById(R.id.stars_container);
        }
    }
}
