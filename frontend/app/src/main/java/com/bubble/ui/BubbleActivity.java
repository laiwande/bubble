package com.bubble.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bubble.R;
import com.bubble.utils.SharedPreferencesUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BubbleActivity extends AppCompatActivity implements BottomNavigationView.BottomNavigationListener {

    private FrameLayout bubbleContainer;
    private BottomNavigationView bottomNavigationView;
    private Random random = new Random();
    private SharedPreferencesUtil sharedPreferencesUtil;

    // Bubble data - text and size
    private static final List<BubbleItem> BUBBLES = new ArrayList<>();

    static {
        // Large bubbles
        BUBBLES.add(new BubbleItem("临港大学生", 1.3f));
        BUBBLES.add(new BubbleItem("蛋蛋后杰迷", 1.2f));
        BUBBLES.add(new BubbleItem("张国荣影迷", 1.5f));
        BUBBLES.add(new BubbleItem("明日方舟", 1.1f));

        // Medium bubbles
        BUBBLES.add(new BubbleItem("OOR", 0.8f));
        BUBBLES.add(new BubbleItem("movie", 0.9f));
        BUBBLES.add(new BubbleItem("guitar", 0.85f));
        BUBBLES.add(new BubbleItem("Disney", 0.9f));

        // Small bubbles
        BUBBLES.add(new BubbleItem("胡闹厨房", 0.65f));
        BUBBLES.add(new BubbleItem("迷跑计划", 0.7f));
        BUBBLES.add(new BubbleItem("魔芋爽", 0.65f));

        // Extra small bubbles
        BUBBLES.add(new BubbleItem("Jove", 0.55f));
        BUBBLES.add(new BubbleItem("soup", 0.5f));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bubble);

        sharedPreferencesUtil = new SharedPreferencesUtil(this);

        // Check if user is logged in
        if (!sharedPreferencesUtil.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        bubbleContainer = findViewById(R.id.bubble_container);

        // Initialize bottom navigation
        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setListener(this);
        bottomNavigationView.setActiveTab(R.id.tab_bubble);

        // Wait for layout to be ready before generating bubbles
        bubbleContainer.post(this::generateRandomBubbles);
    }

    private void generateRandomBubbles() {
        int containerWidth = bubbleContainer.getWidth();
        int containerHeight = bubbleContainer.getHeight();

        // Fallback if still 0
        if (containerWidth == 0) containerWidth = 1080;
        if (containerHeight == 0) containerHeight = 1400;

        // Generate 10-15 random bubbles
        int bubbleCount = 10 + random.nextInt(6);
        List<Point> usedPositions = new ArrayList<>();
        int minDistance = 100; // Minimum distance between bubbles

        for (int i = 0; i < bubbleCount; i++) {
            // Pick a random bubble from the list
            BubbleItem bubbleItem = BUBBLES.get(random.nextInt(BUBBLES.size()));

            // Random size multiplier (0.9 - 1.2)
            float sizeMultiplier = 0.9f + random.nextFloat() * 0.3f;
            float finalSize = bubbleItem.size * sizeMultiplier;

            // Base size in dp (convert to pixels approximately)
            int baseSize = (int) (dpToPx(90 * finalSize));

            // Find a random position that doesn't overlap too much
            Point position = getRandomPosition(containerWidth, containerHeight, baseSize, usedPositions, minDistance);
            usedPositions.add(position);

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    baseSize,
                    baseSize
            );

            params.leftMargin = position.x;
            params.topMargin = position.y;
            params.gravity = Gravity.TOP | Gravity.START;

            // Create bubble view
            ImageView bubbleView = new ImageView(this);
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
            // Random position with some padding
            int padding = bubbleSize / 2;
            int x = padding + random.nextInt(Math.max(1, containerWidth - bubbleSize - padding * 2));
            int y = padding + random.nextInt(Math.max(1, containerHeight - bubbleSize - padding * 2));

            Point newPoint = new Point(x, y);

            // Check distance from all existing positions
            boolean tooClose = false;
            for (Point p : usedPositions) {
                double distance = Math.sqrt(Math.pow(newPoint.x - p.x, 2) + Math.pow(newPoint.y - p.y, 2));
                if (distance < minDistance) {
                    tooClose = true;
                    break;
                }
            }

            if (!tooClose) {
                return newPoint;
            }

            attempts++;
        }

        // Fallback: return a random position even if it might overlap
        int padding = bubbleSize / 4;
        return new Point(
                padding + random.nextInt(Math.max(1, containerWidth - bubbleSize - padding * 2)),
                padding + random.nextInt(Math.max(1, containerHeight - bubbleSize - padding * 2))
        );
    }

    private int dpToPx(float dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    @Override
    public void onBubbleTabClicked() {
        // Already on bubble page, do nothing
    }

    @Override
    public void onSquareTabClicked() {
        Toast.makeText(BubbleActivity.this, "广场功能即将推出", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onChatTabClicked() {
        Toast.makeText(BubbleActivity.this, "聊天功能即将推出", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAddTabClicked() {
        Toast.makeText(BubbleActivity.this, "发布功能即将推出", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMeTabClicked() {
        startActivity(new Intent(BubbleActivity.this, MeActivity.class));
    }

    // Simple point class
    private static class Point {
        int x;
        int y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    // Bubble data class
    private static class BubbleItem {
        String text;
        float size;

        BubbleItem(String text, float size) {
            this.text = text;
            this.size = size;
        }
    }
}
