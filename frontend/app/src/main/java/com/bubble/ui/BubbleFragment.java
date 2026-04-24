package com.bubble.ui;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bubble.R;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BubbleFragment extends Fragment {

    private FrameLayout bubbleContainer;
    private Random random = new Random();

    private static final List<BubbleItem> BUBBLES = new ArrayList<>();

    static {
        BUBBLES.add(new BubbleItem("临港大学生", 1.3f));
        BUBBLES.add(new BubbleItem("蛋蛋后杰迷", 1.2f));
        BUBBLES.add(new BubbleItem("张国荣影迷", 1.5f));
        BUBBLES.add(new BubbleItem("明日方舟", 1.1f));
        BUBBLES.add(new BubbleItem("OOR", 0.8f));
        BUBBLES.add(new BubbleItem("movie", 0.9f));
        BUBBLES.add(new BubbleItem("guitar", 0.85f));
        BUBBLES.add(new BubbleItem("Disney", 0.9f));
        BUBBLES.add(new BubbleItem("胡闹厨房", 0.65f));
        BUBBLES.add(new BubbleItem("迷跑计划", 0.7f));
        BUBBLES.add(new BubbleItem("魔芋爽", 0.65f));
        BUBBLES.add(new BubbleItem("Jove", 0.55f));
        BUBBLES.add(new BubbleItem("soup", 0.5f));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bubble, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bubbleContainer = view.findViewById(R.id.bubble_container);

        // 等待布局完成后再生成泡泡
        bubbleContainer.post(this::generateRandomBubbles);
    }

    private void generateRandomBubbles() {
        int containerWidth = bubbleContainer.getWidth();
        int containerHeight = bubbleContainer.getHeight();

        if (containerWidth == 0) containerWidth = 1080;
        if (containerHeight == 0) containerHeight = 1400;

        int bubbleCount = 10 + random.nextInt(6);
        List<Point> usedPositions = new ArrayList<>();
        int minDistance = 100;

        for (int i = 0; i < bubbleCount; i++) {
            BubbleItem bubbleItem = BUBBLES.get(random.nextInt(BUBBLES.size()));
            float sizeMultiplier = 0.9f + random.nextFloat() * 0.3f;
            float finalSize = bubbleItem.size * sizeMultiplier;
            int baseSize = (int) (dpToPx(90 * finalSize));

            Point position = getRandomPosition(containerWidth, containerHeight, baseSize, usedPositions, minDistance);
            usedPositions.add(position);

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(baseSize, baseSize);
            params.leftMargin = position.x;
            params.topMargin = position.y;
            params.gravity = Gravity.TOP | Gravity.START;

            ImageView bubbleView = new ImageView(requireContext());
            bubbleView.setImageResource(R.drawable.ic_bubble_container);
            bubbleView.setLayoutParams(params);
            bubbleView.setScaleType(ImageView.ScaleType.FIT_XY);

            bubbleContainer.addView(bubbleView);
        }
    }

    private Point getRandomPosition(int containerWidth, int containerHeight, int bubbleSize, List<Point> usedPositions, int minDistance) {
        int maxAttempts = 100;
        int attempts = 0;

        while (attempts < maxAttempts) {
            int padding = bubbleSize / 2;
            int x = padding + random.nextInt(Math.max(1, containerWidth - bubbleSize - padding * 2));
            int y = padding + random.nextInt(Math.max(1, containerHeight - bubbleSize - padding * 2));

            Point newPoint = new Point(x, y);
            boolean tooClose = false;
            for (Point p : usedPositions) {
                double distance = Math.sqrt(Math.pow(newPoint.x - p.x, 2) + Math.pow(newPoint.y - p.y, 2));
                if (distance < minDistance) {
                    tooClose = true;
                    break;
                }
            }
            if (!tooClose) return newPoint;
            attempts++;
        }

        int padding = bubbleSize / 4;
        return new Point(
                padding + random.nextInt(Math.max(1, containerWidth - bubbleSize - padding * 2)),
                padding + random.nextInt(Math.max(1, containerHeight - bubbleSize - padding * 2))
        );
    }

    private float dpToPx(float dp) {
        return dp * getResources().getDisplayMetrics().density;
    }

    private static class Point {
        int x, y;
        Point(int x, int y) { this.x = x; this.y = y; }
    }

    private static class BubbleItem {
        String text;
        float size;
        BubbleItem(String text, float size) { this.text = text; this.size = size; }
    }
}
