package com.bubble.ui;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.bubble.R;
import com.bubble.adapter.ChatBubbleAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ChatFragment extends Fragment {

    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private ChatBubbleAdapter adapter;
    private LinearLayout tagsContainer;

    private int selectedTagPosition = 0;

    // 分类标签数据（可从 API 或配置获取）
    private String[] tagNames = {"all", "idol", "music", "dance", "others"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupTags();
        setupRecyclerView();
        loadData();
    }

    private void initViews(View view) {
        swipeRefresh = view.findViewById(R.id.swipe_refresh);
        recyclerView = view.findViewById(R.id.rv_chat_list);
        tagsContainer = view.findViewById(R.id.layout_tags_container);

        swipeRefresh.setColorSchemeColors(Color.WHITE);
        swipeRefresh.setProgressBackgroundColorSchemeColor(Color.parseColor("#6C63FF"));
    }

    /**
     * 动态生成分类标签（非硬编码）
     */
    private void setupTags() {
        tagsContainer.removeAllViews();

        for (int i = 0; i < tagNames.length; i++) {
            TextView tagView = createTagView(tagNames[i], i == 0);
            final int position = i;
            tagView.setOnClickListener(v -> onTagClicked(position));
            tagsContainer.addView(tagView);
        }
    }

    /**
     * 创建单个标签视图
     */
    private TextView createTagView(String text, boolean isSelected) {
        TextView textView = new TextView(requireContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 12, 0);
        textView.setLayoutParams(params);
        textView.setPadding(20, 4, 20, 4);
        textView.setText(text);
        textView.setTextSize(15);
        textView.setGravity(Gravity.CENTER);

        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(dpToPx(8));
        if (isSelected) {
            drawable.setColor(Color.parseColor("#000000"));
            textView.setTextColor(Color.WHITE);
        } else {
            drawable.setColor(Color.parseColor("#D9D9D9"));
            textView.setTextColor(Color.BLACK);
        }
        textView.setBackground(drawable);

        return textView;
    }

    /**
     * 标签点击事件
     */
    private void onTagClicked(int position) {
        if (position == selectedTagPosition) return;

        selectedTagPosition = position;
        updateTagUI();
        loadData();
    }

    /**
     * 更新所有标签的 UI 状态
     */
    private void updateTagUI() {
        for (int i = 0; i < tagsContainer.getChildCount(); i++) {
            TextView tagView = (TextView) tagsContainer.getChildAt(i);
            boolean isSelected = (i == selectedTagPosition);

            GradientDrawable drawable = (GradientDrawable) tagView.getBackground();
            if (isSelected) {
                drawable.setColor(Color.parseColor("#000000"));
                tagView.setTextColor(Color.WHITE);
            } else {
                drawable.setColor(Color.parseColor("#D9D9D9"));
                tagView.setTextColor(Color.BLACK);
            }
        }
    }

    /**
     * 设置 RecyclerView
     */
    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setBackgroundColor(Color.TRANSPARENT);
        recyclerView.setHasFixedSize(true);
        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        adapter = new ChatBubbleAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener((item, position) -> {
            // TODO: 处理 bubble 点击事件
        });

        swipeRefresh.setOnRefreshListener(() -> loadData());
    }

    /**
     * 加载数据（非硬编码，可替换为网络请求）
     * TODO: 替换为实际的数据源（API/数据库）
     */
    private void loadData() {
        swipeRefresh.setRefreshing(true);

        List<ChatBubbleAdapter.ChatBubbleItem> dataList = fetchBubbleData();
        adapter.setData(dataList);

        swipeRefresh.setRefreshing(false);
    }

    /**
     * 获取 Bubble 数据（示例）
     * 实际项目中应从 API 或 Room 数据库获取
     */
    private List<ChatBubbleAdapter.ChatBubbleItem> fetchBubbleData() {
        List<ChatBubbleAdapter.ChatBubbleItem> dataList = new ArrayList<>();
        Random random = new Random();

        // TODO: 替换为真实数据源
        // 例如: apiService.getBubblesByTag(tagNames[selectedTagPosition])
        String tag = tagNames[selectedTagPosition];

        // 固定生成20个气泡 (i=0 到 i=19, 共20个)
        int totalBubbles = 20;

        // 随机生成 x (1-10)，表示第一段的数量
        int x = 1 + random.nextInt(10);

        // 随机生成 n (1-10)，表示第二段的数量
        int n = 1 + random.nextInt(10);

        for (int i = 0; i < totalBubbles; i++) {
            String title = "Bubble " + (i + 1);
            String number = "12398479623" + String.format("%02d", i + 1);
            int imageResId;

            if (i <= x) {
                // i=0 到 i=x：使用 ic_chat_listl.webp
                imageResId = R.drawable.ic_chat_listl;
            } else if (i <= x + n) {
                // i=x+1 到 i=x+n：使用 ic_chat_listm.webp
                imageResId = R.drawable.ic_chat_listm;
            } else {
                // 剩余部分：使用 ic_chat_lists.webp
                imageResId = R.drawable.ic_chat_lists;
            }

            dataList.add(new ChatBubbleAdapter.ChatBubbleItem(title, number, imageResId));
        }

        return dataList;
    }

    /**
     * dp 转 px
     */
    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}
