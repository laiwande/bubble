package com.bubble.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bubble.R;
import com.bubble.model.PageData;
import com.bubble.model.PartnerPost;
import com.bubble.model.Result;
import com.bubble.network.ApiClient;
import com.bubble.network.ApiService;
import com.bubble.ui.adapter.CardAdapter;
import com.bubble.ui.model.CardItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SquareFindFragment extends Fragment {

    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerCards;
    private CardAdapter cardAdapter;
    private List<CardItem> cardList;

    private static final String TAG = "SquareFindFragment";
    private static final Gson gson = new Gson();
    private static final Type STRING_LIST_TYPE = new TypeToken<ArrayList<String>>(){}.getType();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_square_find, container, false);
        initViews(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadData();
    }

    private void initViews(View view) {
        swipeRefresh = view.findViewById(R.id.swipe_refresh);
        recyclerCards = view.findViewById(R.id.recycler_cards);

        recyclerCards.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerCards.setBackgroundColor(android.graphics.Color.TRANSPARENT);

        cardList = new ArrayList<>();
        cardAdapter = new CardAdapter(cardList);
        recyclerCards.setAdapter(cardAdapter);

        swipeRefresh.setColorSchemeColors(android.graphics.Color.WHITE);
        swipeRefresh.setProgressBackgroundColorSchemeColor(android.graphics.Color.parseColor("#6C63FF"));
        swipeRefresh.setOnRefreshListener(this::loadData);

        // 卡片点击事件 - 跳转到详情页
        cardAdapter.setOnCardClickListener((card, position) -> {
            Intent intent = new Intent(requireContext(), PostDetailActivity.class);
            intent.putExtra("title", card.getTitle());
            intent.putExtra("year", card.getYear());
            intent.putExtra("month", card.getMonth());
            intent.putExtra("day", card.getDay());
            intent.putExtra("location", card.getLocation());
            intent.putExtra("partnerNumber", card.getPartnerNumber());
            intent.putExtra("description", card.getDescription());
            intent.putStringArrayListExtra("wishTags", new ArrayList<>(card.getWishTags()));
            intent.putStringArrayListExtra("banTags", new ArrayList<>(card.getBanTags()));
            startActivity(intent);
        });
    }

    /**
     * 从后端 API 加载找搭子帖子列表
     */
    private void loadData() {
        swipeRefresh.setRefreshing(true);

        ApiService apiService = ApiClient.getApiService();
        apiService.getPartnerPostList(1, 100).enqueue(new Callback<Result<PageData<PartnerPost>>>() {
            @Override
            public void onResponse(Call<Result<PageData<PartnerPost>>> call, Response<Result<PageData<PartnerPost>>> response) {
                swipeRefresh.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 200) {
                    PageData<PartnerPost> pageData = response.body().getData();
                    if (pageData != null && pageData.getRecords() != null) {
                        cardList.clear();
                        for (PartnerPost post : pageData.getRecords()) {
                            cardList.add(convertPartnerPostToCardItem(post));
                        }
                        cardAdapter.notifyDataSetChanged();
                    }
                } else {
                    Log.e(TAG, "加载帖子列表失败: " + (response.body() != null ? response.body().getMessage() : "空响应"));
                    Toast.makeText(requireContext(), "加载失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Result<PageData<PartnerPost>>> call, Throwable t) {
                swipeRefresh.setRefreshing(false);
                Log.e(TAG, "网络错误", t);
                Toast.makeText(requireContext(), "网络错误，无法加载", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 将后端 PartnerPost 模型转换为前端 CardItem
     */
    private CardItem convertPartnerPostToCardItem(PartnerPost post) {
        // 解析日期: "2025-11-29" -> year=2025, month=11, day=29
        String year = "";
        String month = "";
        String day = "";
        String activityDate = post.getActivityDate();
        if (activityDate != null && !activityDate.isEmpty()) {
            String[] parts = activityDate.split("-");
            if (parts.length > 0) year = parts[0];
            if (parts.length > 1) month = parts[1];
            if (parts.length > 2) day = parts[2];
        }

        // 解析地址
        String address = post.getAddress() != null ? post.getAddress() : "";
        // subtitle 格式: "2025,11,29 Shanghai"
        String location = address.contains(",") ? address.substring(address.lastIndexOf(",") + 1).trim() : address;
        String subtitle = year + "," + month + "," + day + " " + location;

        // 构建标签列表
        List<CardItem.TagItem> tagItems = new ArrayList<>();
        List<String> wishTags = parseJsonStringList(post.getWishTags());
        List<String> banTags = parseJsonStringList(post.getBanTags());
        if (wishTags != null) {
            for (String tag : wishTags) tagItems.add(new CardItem.TagItem(tag, true));
        }
        if (banTags != null) {
            for (String tag : banTags) tagItems.add(new CardItem.TagItem(tag, false));
        }

        int partnerNumber = post.getPartnerNumber() != null ? post.getPartnerNumber() : 0;

        CardItem item = new CardItem(
                post.getTopicName() != null ? post.getTopicName() : "",
                subtitle,
                tagItems,
                0,
                year, month, day, location,
                String.valueOf(partnerNumber),
                post.getDescription() != null ? post.getDescription() : "",
                wishTags != null ? wishTags : new ArrayList<>(),
                banTags != null ? banTags : new ArrayList<>()
        );
        if (post.getUserId() != null) {
            item.setUserId(String.valueOf(post.getUserId()));
        }
        return item;
    }

    /**
     * 解析 JSON 字符串数组，如 "[\"girls\",\"20+\"]"
     */
    private List<String> parseJsonStringList(String json) {
        if (json == null || json.isEmpty() || json.equals("[]")) {
            return new ArrayList<>();
        }
        try {
            return gson.fromJson(json, STRING_LIST_TYPE);
        } catch (Exception e) {
            Log.e(TAG, "解析 JSON 列表失败: " + json, e);
            return new ArrayList<>();
        }
    }

    // 添加新卡片到列表（追加到末尾）
    public void addNewCard(CardItem newCard) {
        cardList.add(newCard);
        cardAdapter.notifyItemInserted(cardList.size() - 1);
        recyclerCards.scrollToPosition(cardList.size() - 1);
    }

    // 添加新卡片到列表顶部
    public void addNewCardAtTop(CardItem newCard) {
        cardList.add(0, newCard);
        cardAdapter.notifyItemInserted(0);
        recyclerCards.scrollToPosition(0);
    }
}
