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
import android.graphics.Rect;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BubbleFragment extends Fragment {

    private FrameLayout bubbleContainer;
    private View telescopeView;
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
        telescopeView = view.findViewById(R.id.ic_telescope);

        // 等待布局完成后再生成泡泡
        bubbleContainer.post(this::generateRandomBubbles);
    }

    private void generateRandomBubbles() {
        int containerWidth = bubbleContainer.getWidth();
        int containerHeight = bubbleContainer.getHeight();

        if (containerWidth == 0) containerWidth = 1080;
        if (containerHeight == 0) containerHeight = 1400;

        // 计算望远镜相对于泡泡容器的禁区矩形（扩展一些边距确保不重叠）
        Rect telescopeZone = getTelescopeZone(containerWidth, containerHeight);

        // 定义最大泡泡半径 (像素)，所有泡泡不会超过这个尺寸
        float maxRadiusDp = 55f;
        int maxBubbleSize = (int) dpToPx(maxRadiusDp * 2); // 直径 = 2 * maxRadius

        // 根据容器大小动态计算可放置的泡泡数量
        int estimatedCount = estimateMaxBubbles(containerWidth, containerHeight, maxBubbleSize);
        int bubbleCount = Math.min(10 + random.nextInt(6), Math.max(estimatedCount, 15));

        List<BubblePlacement> placedBubbles = new ArrayList<>();

        for (int i = 0; i < bubbleCount; i++) {
            BubbleItem bubbleItem = BUBBLES.get(random.nextInt(BUBBLES.size()));

            // 所有的泡泡都使用统一的最大尺寸，或稍小的随机尺寸（但不超 maxBubbleSize）
            float sizeVariation = 0.85f + random.nextFloat() * 0.15f; // 0.85 ~ 1.0
            int baseSize = (int) (maxBubbleSize * sizeVariation);
            if (baseSize > maxBubbleSize) baseSize = maxBubbleSize;

            // 查找不重叠的位置（核心约束：中心距 >= 2*maxRadius，同时避开望远镜）
            BubblePlacement placement = getRandomPosition(containerWidth, containerHeight, baseSize, maxBubbleSize, placedBubbles, telescopeZone);
            if (placement != null) {
                placedBubbles.add(placement);

                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(baseSize, baseSize);
                params.leftMargin = placement.x;
                params.topMargin = placement.y;
                params.gravity = Gravity.TOP | Gravity.START;

                // 创建气泡容器
                FrameLayout bubbleContainerView = new FrameLayout(requireContext());
                bubbleContainerView.setLayoutParams(params);

                // 创建气泡背景图
                ImageView bubbleView = new ImageView(requireContext());
                bubbleView.setImageResource(R.drawable.ic_bubble_container);
                FrameLayout.LayoutParams bubbleImageParams = new FrameLayout.LayoutParams(baseSize, baseSize);
                bubbleView.setLayoutParams(bubbleImageParams);
                bubbleView.setScaleType(ImageView.ScaleType.FIT_XY);

                // 创建气泡内文字标签
                TextView textView = new TextView(requireContext());
                textView.setText(bubbleItem.text);
                textView.setGravity(Gravity.CENTER);
                textView.setTypeface(getResources().getFont(R.font.inter), android.graphics.Typeface.BOLD);
                textView.setTextColor(android.graphics.Color.WHITE);
                float textSize = 11f * sizeVariation + 4f;
                textView.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, textSize);

                FrameLayout.LayoutParams textParams = new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                textParams.gravity = Gravity.CENTER;
                textView.setLayoutParams(textParams);

                // 添加视图到容器
                bubbleContainerView.addView(bubbleView);
                bubbleContainerView.addView(textView);
                bubbleContainer.addView(bubbleContainerView);
            }
        }
    }

    /**
     * 获取望远镜组件相对于泡泡容器的禁区矩形
     */
    private Rect getTelescopeZone(int containerWidth, int containerHeight) {
        if (telescopeView == null) {
            return new Rect(); // 空禁区
        }

        int[] containerLoc = new int[2];
        int[] telescopeLoc = new int[2];
        bubbleContainer.getLocationOnScreen(containerLoc);
        telescopeView.getLocationOnScreen(telescopeLoc);

        // 望远镜在屏幕上的实际位置
        int telescopeLeft = telescopeLoc[0];
        int telescopeTop = telescopeLoc[1];
        int telescopeRight = telescopeLeft + telescopeView.getWidth();
        int telescopeBottom = telescopeTop + telescopeView.getHeight();

        // 转换为相对于 bubbleContainer 的坐标
        int relLeft = telescopeLeft - containerLoc[0];
        int relTop = telescopeTop - containerLoc[1];
        int relRight = telescopeRight - containerLoc[0];
        int relBottom = telescopeBottom - containerLoc[1];

        // 扩展禁区区域（增加边距，确保泡泡不会紧贴望远镜）
        int expandMargin = (int) dpToPx(30);
        return new Rect(
                relLeft - expandMargin,
                relTop - expandMargin,
                relRight + expandMargin,
                relBottom + expandMargin
        );
    }

    /**
     * 估算容器内最多能放多少个泡泡（基于圆 packing 密度约 0.5）
     */
    private int estimateMaxBubbles(int containerWidth, int containerHeight, int maxBubbleSize) {
        float padding = dpToPx(20);
        float availableWidth = containerWidth - padding * 2;
        float availableHeight = containerHeight - padding * 2;
        // 每个泡泡占用的格子面积约为 (2r)^2，考虑密度因子 0.55
        double areaPerBubble = Math.pow(maxBubbleSize * 1.1, 2) / 0.55;
        return Math.max(6, (int) ((availableWidth * availableHeight) / areaPerBubble));
    }

    /**
     * 获取不重叠的随机位置
     * 核心约束：任意两个泡泡的中心点距离 >= maxBubbleSize (即 2 * maxRadius)，且不与望远镜重叠
     *
     * @param containerWidth 容器宽度
     * @param containerHeight 容器高度
     * @param bubbleSize 当前泡泡的实际尺寸
     * @param maxSize 所有泡泡的最大允许尺寸（用于计算最小中心距）
     * @param placedBubbles 已放置的泡泡列表
     * @param telescopeZone 望远镜禁区（相对于容器坐标）
     * @return 合适的位置，如果找不到则返回 null
     */
    private BubblePlacement getRandomPosition(int containerWidth, int containerHeight, int bubbleSize,
                                              int maxSize, List<BubblePlacement> placedBubbles,
                                              Rect telescopeZone) {
        int maxAttempts = 300; // 增加尝试次数
        int attempts = 0;

        // 最小中心距 = maxSize (即 2*maxRadius)，确保任何泡泡之间不会重叠
        float minCenterDist = maxSize;
        int padding = (int) dpToPx(20);

        while (attempts < maxAttempts) {
            int x = padding + random.nextInt(Math.max(1, containerWidth - bubbleSize - padding * 2));
            int y = padding + random.nextInt(Math.max(1, containerHeight - bubbleSize - padding * 2));

            // 检查是否在望远镜禁区内
            if (isInTelescopeZone(x, y, bubbleSize, telescopeZone)) {
                attempts++;
                continue;
            }

            boolean tooClose = false;
            for (BubblePlacement placed : placedBubbles) {
                // 计算当前泡泡中心点与已放置泡泡中心点的距离
                float centerX = x + bubbleSize / 2f;
                float centerY = y + bubbleSize / 2f;
                float placedCenterX = placed.x + placed.size / 2f;
                float placedCenterY = placed.y + placed.size / 2f;

                double distance = Math.sqrt(Math.pow(centerX - placedCenterX, 2)
                        + Math.pow(centerY - placedCenterY, 2));

                // 核心约束：距离必须 >= minCenterDist (即 2 * maxRadius)
                if (distance < minCenterDist) {
                    tooClose = true;
                    break;
                }
            }

            if (!tooClose) {
                return new BubblePlacement(x, y, bubbleSize);
            }
            attempts++;
        }

        // 尝试使用泊松盘采样的网格搜索作为回退
        return getGridFallbackPosition(containerWidth, containerHeight, bubbleSize, maxSize, placedBubbles, telescopeZone);
    }

    /**
     * 网格回退布局：基于泊松盘采样思想，按网格逐步搜索可用位置
     */
    private BubblePlacement getGridFallbackPosition(int containerWidth, int containerHeight, int bubbleSize,
                                                    int maxSize, List<BubblePlacement> placedBubbles,
                                                    Rect telescopeZone) {
        float minCenterDist = maxSize;
        int padding = (int) dpToPx(20);

        // 使用较细的网格进行回退搜索
        int gridStep = (int) (minCenterDist * 0.4f); // 网格步长

        // 随机化起始位置，避免每次都从左上角开始
        int offsetX = random.nextInt(gridStep);
        int offsetY = random.nextInt(gridStep);

        for (int gx = offsetX; gx < containerWidth - bubbleSize; gx += gridStep) {
            for (int gy = offsetY; gy < containerHeight - bubbleSize; gy += gridStep) {
                int x = padding + gx;
                int y = padding + gy;

                if (x + bubbleSize > containerWidth - padding || y + bubbleSize > containerHeight - padding) {
                    continue;
                }

                // 检查是否在望远镜禁区内
                if (isInTelescopeZone(x, y, bubbleSize, telescopeZone)) {
                    continue;
                }

                boolean tooClose = false;
                for (BubblePlacement placed : placedBubbles) {
                    float centerX = x + bubbleSize / 2f;
                    float centerY = y + bubbleSize / 2f;
                    float placedCenterX = placed.x + placed.size / 2f;
                    float placedCenterY = placed.y + placed.size / 2f;

                    double distance = Math.sqrt(Math.pow(centerX - placedCenterX, 2)
                            + Math.pow(centerY - placedCenterY, 2));

                    if (distance < minCenterDist) {
                        tooClose = true;
                        break;
                    }
                }

                if (!tooClose) {
                    return new BubblePlacement(x, y, bubbleSize);
                }
            }
        }

        return null; // 放弃此泡泡，避免重叠
    }

    /**
     * 检查泡泡是否与望远镜禁区重叠（使用矩形相交检测）
     */
    private boolean isInTelescopeZone(int bubbleX, int bubbleY, int bubbleSize, Rect zone) {
        if (zone == null || zone.isEmpty()) {
            return false;
        }
        Rect bubbleRect = new Rect(bubbleX, bubbleY, bubbleX + bubbleSize, bubbleY + bubbleSize);
        return Rect.intersects(bubbleRect, zone);
    }

    private float dpToPx(float dp) {
        return dp * getResources().getDisplayMetrics().density;
    }

    private static class BubblePlacement {
        int x, y, size;
        BubblePlacement(int x, int y, int size) { this.x = x; this.y = y; this.size = size; }
    }

    private static class BubbleItem {
        String text;
        float size;
        BubbleItem(String text, float size) { this.text = text; this.size = size; }
    }
}
