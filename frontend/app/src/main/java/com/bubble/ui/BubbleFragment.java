package com.bubble.ui;

import android.content.Intent;
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
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.bubble.R;
import com.bubble.model.Bubble;
import com.bubble.model.PageData;
import com.bubble.model.Result;
import com.bubble.network.ApiClient;
import com.bubble.network.ApiService;
import com.bubble.utils.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BubbleFragment extends Fragment {

    private FrameLayout bubbleContainer;
    private View telescopeView;
    private Random random = new Random();
    private ApiService apiService;

    private static final List<BubbleItem> BUBBLES = new ArrayList<>();

    // 静态默认数据（API 加载失败时使用 fallback）
    static {
        BUBBLES.add(new BubbleItem(-1L, "临港大学生", 1.3f));
        BUBBLES.add(new BubbleItem(-1L, "蛋蛋后杰迷", 1.2f));
        BUBBLES.add(new BubbleItem(-1L, "张国荣影迷", 1.5f));
        BUBBLES.add(new BubbleItem(-1L, "明日方舟", 1.1f));
        BUBBLES.add(new BubbleItem(-1L, "OOR", 0.8f));
        BUBBLES.add(new BubbleItem(-1L, "movie", 0.9f));
        BUBBLES.add(new BubbleItem(-1L, "guitar", 0.85f));
        BUBBLES.add(new BubbleItem(-1L, "Disney", 0.9f));
        BUBBLES.add(new BubbleItem(-1L, "胡闹厨房", 0.65f));
        BUBBLES.add(new BubbleItem(-1L, "迷跑计划", 0.7f));
        BUBBLES.add(new BubbleItem(-1L, "魔芋爽", 0.65f));
        BUBBLES.add(new BubbleItem(-1L, "Jove", 0.55f));
        BUBBLES.add(new BubbleItem(-1L, "soup", 0.5f));
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
        apiService = ApiClient.getApiService();

        // 先从后端加载 Bubble 列表
        loadBubblesFromApi();
    }

    /**
     * 从后端 API 加载 Bubble 列表，成功后替换静态数据并重新渲染
     */
    private void loadBubblesFromApi() {
        SharedPreferencesUtil spUtil = new SharedPreferencesUtil(requireContext());
        String token = spUtil.getToken();
        apiService.getBubbleList("Bearer " + token, 1, 50, false).enqueue(new Callback<Result<PageData<Bubble>>>() {
            @Override
            public void onResponse(Call<Result<PageData<Bubble>>> call, Response<Result<PageData<Bubble>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 200) {
                    PageData<Bubble> pageData = response.body().getData();
                    if (pageData != null && pageData.getRecords() != null && !pageData.getRecords().isEmpty()) {
                        updateBubblesFromApi(pageData.getRecords());
                        return;
                    }
                }
                // API 失败或数据为空，使用静态 fallback
                renderBubbles();
            }

            @Override
            public void onFailure(Call<Result<PageData<Bubble>>> call, Throwable t) {
                // 网络错误，使用静态 fallback
                renderBubbles();
            }
        });
    }

    /**
     * 用 API 数据替换 BUBBLES 列表，然后重新渲染
     */
    private void updateBubblesFromApi(List<Bubble> bubbleList) {
        BUBBLES.clear();
        float minSize = 0.35f;
        float maxSize = 1.5f;
        float range = maxSize - minSize;
        int count = bubbleList.size();

        for (int i = 0; i < count; i++) {
            Bubble apiBubble = bubbleList.get(i);
            // 根据位置分配大小，让靠前的稍微大一点
            float sizeRatio = maxSize - (range * i / Math.max(count - 1, 1));
            BUBBLES.add(new BubbleItem(apiBubble.getId(), apiBubble.getName(), sizeRatio));
        }

        renderBubbles();
    }

    /**
     * 渲染气泡（从 BUBBLES 列表生成）
     */
    private void renderBubbles() {
        // 等待布局完成后再生成泡泡
        bubbleContainer.post(this::generateRandomBubbles);
    }

    private void generateRandomBubbles() {
        int containerWidth = bubbleContainer.getWidth();
        int containerHeight = bubbleContainer.getHeight();

        if (containerWidth == 0) containerWidth = 1080;
        if (containerHeight == 0) containerHeight = 1400;

        // 清空泡泡容器（望远镜在布局中是 bubble_container 的兄弟节点，不会被移除）
        bubbleContainer.removeAllViews();

        // 计算望远镜相对于泡泡容器的禁区矩形（扩展一些边距确保不重叠）
        Rect telescopeZone = getTelescopeZone(containerWidth, containerHeight);

        // 定义最大泡泡半径 (像素)，所有泡泡不会超过这个尺寸
        float maxRadiusDp = 55f;
        int maxBubbleSize = (int) dpToPx(maxRadiusDp * 2); // 直径 = 2 * maxRadius

        // 根据容器大小动态计算可放置的泡泡数量
        int estimatedCount = estimateMaxBubbles(containerWidth, containerHeight, maxBubbleSize);
        int bubbleCount = Math.min(10 + random.nextInt(6), Math.max(estimatedCount, 15));

        List<BubblePlacement> placedBubbles = new ArrayList<>();

        for (int i = 0; i < bubbleCount && i < BUBBLES.size(); i++) {
            BubbleItem bubbleItem = BUBBLES.get(i);

            // 所有的泡泡都使用统一的最大尺寸，或稍小的随机尺寸（但不超 maxBubbleSize）
            float sizeVariation = (bubbleItem.size / 1.5f) * (0.92f + random.nextFloat() * 0.16f);
            sizeVariation = Math.max(0.22f, Math.min(1.0f, sizeVariation));
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
                textView.setTypeface(ResourcesCompat.getFont(requireContext(), R.font.inter), android.graphics.Typeface.BOLD);
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

                // 设置点击事件跳转到 BubbleDetailActivity
                final Long bubbleId = bubbleItem.id;
                final String bubbleName = bubbleItem.text;
                bubbleContainerView.setOnClickListener(v -> {
                    if (bubbleId != -1L) {
                        Intent intent = new Intent(requireContext(), BubbleDetailActivity.class);
                        intent.putExtra(BubbleDetailActivity.EXTRA_BUBBLE_ID, bubbleId);
                        intent.putExtra(BubbleDetailActivity.EXTRA_BUBBLE_NAME, bubbleName);
                        startActivity(intent);
                    }
                });

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
     */
    private BubblePlacement getRandomPosition(int containerWidth, int containerHeight, int bubbleSize,
                                              int maxSize, List<BubblePlacement> placedBubbles,
                                              Rect telescopeZone) {
        int maxAttempts = 300;
        int attempts = 0;

        float minCenterDist = maxSize;
        int padding = (int) dpToPx(20);

        while (attempts < maxAttempts) {
            int x = padding + random.nextInt(Math.max(1, containerWidth - bubbleSize - padding * 2));
            int y = padding + random.nextInt(Math.max(1, containerHeight - bubbleSize - padding * 2));

            if (isInTelescopeZone(x, y, bubbleSize, telescopeZone)) {
                attempts++;
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
            attempts++;
        }

        return getGridFallbackPosition(containerWidth, containerHeight, bubbleSize, maxSize, placedBubbles, telescopeZone);
    }

    /**
     * 网格回退布局
     */
    private BubblePlacement getGridFallbackPosition(int containerWidth, int containerHeight, int bubbleSize,
                                                    int maxSize, List<BubblePlacement> placedBubbles,
                                                    Rect telescopeZone) {
        float minCenterDist = maxSize;
        int padding = (int) dpToPx(20);

        int gridStep = (int) (minCenterDist * 0.4f);

        int offsetX = random.nextInt(gridStep);
        int offsetY = random.nextInt(gridStep);

        for (int gx = offsetX; gx < containerWidth - bubbleSize; gx += gridStep) {
            for (int gy = offsetY; gy < containerHeight - bubbleSize; gy += gridStep) {
                int x = padding + gx;
                int y = padding + gy;

                if (x + bubbleSize > containerWidth - padding || y + bubbleSize > containerHeight - padding) {
                    continue;
                }

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

        return null;
    }

    /**
     * 检查泡泡是否与望远镜禁区重叠
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

    /**
     * 用 Rect 替代 android.graphics.Rect，避免冲突
     */
    private static class Rect {
        int left, top, right, bottom;

        Rect() {}
        Rect(int left, int top, int right, int bottom) {
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
        }

        boolean isEmpty() {
            return left >= right || top >= bottom;
        }

        static boolean intersects(Rect a, Rect b) {
            return a.left < b.right && b.left < a.right
                    && a.top < b.bottom && b.top < a.bottom;
        }
    }

    private static class BubbleItem {
        long id;
        String text;
        float size;
        BubbleItem(long id, String text, float size) { this.id = id; this.text = text; this.size = size; }
    }
}
