package com.bubble.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.bubble.R;
import com.bubble.adapter.ChatBubbleAdapter;
import com.bubble.model.Bubble;
import com.bubble.model.PageData;
import com.bubble.model.Result;
import com.bubble.network.ApiClient;
import com.bubble.network.ApiService;
import com.bubble.utils.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
            Intent intent = new Intent(requireContext(), ChatConcreteActivity.class);
            intent.putExtra(ChatConcreteActivity.EXTRA_USER_ID, item.id);
            intent.putExtra(ChatConcreteActivity.EXTRA_USER_NAME, item.title);
            startActivity(intent);
        });

        swipeRefresh.setOnRefreshListener(() -> loadData());
    }

    /**
     * 从后端 API 加载 Bubble 列表
     */
    private void loadData() {
        swipeRefresh.setRefreshing(true);

        SharedPreferencesUtil spUtil = new SharedPreferencesUtil(requireContext());
        String token = spUtil.getToken();

        ApiService apiService = ApiClient.getApiService();
        apiService.getBubbleList("Bearer " + token, 1, 100, false).enqueue(new Callback<Result<PageData<Bubble>>>() {
            @Override
            public void onResponse(Call<Result<PageData<Bubble>>> call, Response<Result<PageData<Bubble>>> response) {
                swipeRefresh.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 200) {
                    PageData<Bubble> pageData = response.body().getData();
                    if (pageData != null && pageData.getRecords() != null) {
                        List<ChatBubbleAdapter.ChatBubbleItem> itemList = new ArrayList<>();
                        for (Bubble bubble : pageData.getRecords()) {
                            int msgCount = bubble.getMessageCount() != null ? bubble.getMessageCount() : 0;
                            itemList.add(new ChatBubbleAdapter.ChatBubbleItem(
                                    bubble.getName(),
                                    String.valueOf(bubble.getId()),
                                    R.drawable.ic_chat_listl,
                                    msgCount,
                                    bubble.getLastMessage()
                            ));
                        }
                        adapter.setData(itemList);
                    } else {
                        adapter.setData(new ArrayList<>());
                    }
                } else {
                    Toast.makeText(requireContext(), "加载失败", Toast.LENGTH_SHORT).show();
                    adapter.setData(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<Result<PageData<Bubble>>> call, Throwable t) {
                swipeRefresh.setRefreshing(false);
                Log.e("ChatFragment", "网络错误", t);
                Toast.makeText(requireContext(), "网络错误，无法加载", Toast.LENGTH_SHORT).show();
                adapter.setData(new ArrayList<>());
            }
        });
    }

    /**
     * dp 转 px
     */
    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}
