package com.bubble.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bubble.R;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BubbleActivity extends AppCompatActivity {

    private FrameLayout bubbleContainer;
    private Random random = new Random();

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

        bubbleContainer = findViewById(R.id.bubble_container);

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
        List<BubblePlacement> placedBubbles = new ArrayList<>();

        for (int i = 0; i < bubbleCount; i++) {
            // Pick a random bubble from the list
            BubbleItem bubbleItem = BUBBLES.get(random.nextInt(BUBBLES.size()));

            // Random size multiplier (0.9 - 1.2)
            float sizeMultiplier = 0.9f + random.nextFloat() * 0.3f;
            float finalSize = bubbleItem.size * sizeMultiplier;

            // Base size in dp (convert to pixels approximately)
            int baseSize = (int) (dpToPx(90 * finalSize));

            // Find a random position that doesn't overlap
            BubblePlacement placement = getRandomPosition(containerWidth, containerHeight, baseSize, placedBubbles);
            placedBubbles.add(placement);

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    baseSize,
                    baseSize
            );

            params.leftMargin = placement.x;
            params.topMargin = placement.y;
            params.gravity = Gravity.TOP | Gravity.START;

            // Create bubble container (FrameLayout to hold both image and text)
            FrameLayout bubbleContainerView = new FrameLayout(this);
            bubbleContainerView.setLayoutParams(params);

            // Create bubble background image
            ImageView bubbleView = new ImageView(this);
            bubbleView.setImageResource(R.drawable.ic_bubble_container);
            FrameLayout.LayoutParams bubbleImageParams = new FrameLayout.LayoutParams(
                    baseSize,
                    baseSize
            );
            bubbleView.setLayoutParams(bubbleImageParams);
            bubbleView.setScaleType(ImageView.ScaleType.FIT_XY);

            // Create text label inside bubble
            TextView textView = new TextView(this);
            textView.setText(bubbleItem.text);
            textView.setGravity(Gravity.CENTER);
            textView.setTypeface(getResources().getFont(R.font.inter));
            textView.setTextColor(0xFF333333);
            // Font size proportional to bubble size (base: 90dp -> 14sp, scaled by finalSize)
            float textSize = 10f * finalSize + 4f; // Range roughly 9.5sp ~ 19sp
            textView.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, textSize);

            FrameLayout.LayoutParams textParams = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            textParams.gravity = Gravity.CENTER;
            textView.setLayoutParams(textParams);

            // Add views to container
            bubbleContainerView.addView(bubbleView);
            bubbleContainerView.addView(textView);

            bubbleContainer.addView(bubbleContainerView);
        }
    }

    /**
     * Find a random position for the bubble that doesn't overlap with existing bubbles
     * Uses dynamic distance calculation based on bubble sizes
     */
    private BubblePlacement getRandomPosition(int containerWidth, int containerHeight, int bubbleSize, List<BubblePlacement> placedBubbles) {
        int maxAttempts = 200;
        int attempts = 0;

        // Minimum gap between bubbles (in pixels)
        int minGap = dpToPx(15);

        while (attempts < maxAttempts) {
            // Random position with padding from edges
            int padding = dpToPx(10);
            int x = padding + random.nextInt(Math.max(1, containerWidth - bubbleSize - padding * 2));
            int y = padding + random.nextInt(Math.max(1, containerHeight - bubbleSize - padding * 2));

            boolean tooClose = false;
            for (BubblePlacement placed : placedBubbles) {
                // Calculate minimum required distance between centers
                // Based on the sum of radii plus a gap
                float minDist = (placed.size + bubbleSize) / 2f + minGap;

                // Calculate actual distance between centers
                // Center of new bubble
                float centerX = x + bubbleSize / 2f;
                float centerY = y + bubbleSize / 2f;
                // Center of existing bubble
                float placedCenterX = placed.x + placed.size / 2f;
                float placedCenterY = placed.y + placed.size / 2f;

                double distance = Math.sqrt(Math.pow(centerX - placedCenterX, 2) + Math.pow(centerY - placedCenterY, 2));

                if (distance < minDist) {
                    tooClose = true;
                    break;
                }
            }

            if (!tooClose) {
                return new BubblePlacement(x, y, bubbleSize);
            }

            attempts++;
        }

        // Fallback: use grid-based placement if random fails
        return getGridFallbackPosition(containerWidth, containerHeight, bubbleSize, placedBubbles);
    }

    /**
     * Grid-based fallback placement when random positioning fails
     */
    private BubblePlacement getGridFallbackPosition(int containerWidth, int containerHeight, int bubbleSize, List<BubblePlacement> placedBubbles) {
        int gridSize = dpToPx(20); // Grid cell size
        int cols = containerWidth / gridSize;
        int rows = containerHeight / gridSize;
        int minGap = dpToPx(10);

        // Try each grid position
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int x = col * gridSize;
                int y = row * gridSize;

                // Check bounds
                if (x + bubbleSize > containerWidth || y + bubbleSize > containerHeight) {
                    continue;
                }

                // Check overlap
                boolean tooClose = false;
                for (BubblePlacement placed : placedBubbles) {
                    float minDist = (placed.size + bubbleSize) / 2f + minGap;
                    float centerX = x + bubbleSize / 2f;
                    float centerY = y + bubbleSize / 2f;
                    float placedCenterX = placed.x + placed.size / 2f;
                    float placedCenterY = placed.y + placed.size / 2f;

                    double distance = Math.sqrt(Math.pow(centerX - placedCenterX, 2) + Math.pow(centerY - placedCenterY, 2));
                    if (distance < minDist) {
                        tooClose = true;
                        break;
                    }
                }

                if (!tooClose) {
                    return new BubblePlacement(x, y, bubbleSize);
                }
            }
        }

        // Final fallback: place at bottom-right area
        int x = Math.max(0, containerWidth - bubbleSize - dpToPx(20));
        int y = Math.max(0, containerHeight - bubbleSize - dpToPx(20));
        return new BubblePlacement(x, y, bubbleSize);
    }

    private int dpToPx(float dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    // Bubble placement data class (stores position AND size)
    private static class BubblePlacement {
        int x;
        int y;
        int size;

        BubblePlacement(int x, int y, int size) {
            this.x = x;
            this.y = y;
            this.size = size;
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
