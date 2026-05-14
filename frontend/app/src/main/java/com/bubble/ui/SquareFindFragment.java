package com.bubble.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bubble.R;
import com.bubble.ui.adapter.CardAdapter;
import com.bubble.ui.model.CardItem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SquareFindFragment extends Fragment {

    private RecyclerView recyclerCards;
    private CardAdapter cardAdapter;
    private ImageView ivAdd;
    private FrameLayout navFindPartner;
    private FrameLayout navBubble;
    private FrameLayout navBroadcast;
    private List<CardItem> cardList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_square_find, container, false);
        initViews(view);
        initRecyclerView(view);
        initListeners();
        return view;
    }

    private void initViews(View view) {
        ivAdd = view.findViewById(R.id.iv_add);
        navFindPartner = view.findViewById(R.id.nav_follow);
        navBubble = view.findViewById(R.id.nav_bubble);
        navBroadcast = view.findViewById(R.id.nav_broadcast);
    }

    private void initRecyclerView(View view) {
        recyclerCards = view.findViewById(R.id.recycler_cards);
        recyclerCards.setLayoutManager(new LinearLayoutManager(getContext()));
        
        cardList = getSampleData();
        cardAdapter = new CardAdapter(cardList);
        recyclerCards.setAdapter(cardAdapter);

        // 卡片点击事件 - 跳转到详情页 (Activity)
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

    private void initListeners() {
        // 加号按钮 - 跳转到创建页 (Activity)
        ivAdd.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), CreatePostActivity.class));
        });

        // 导航标签切换
        navFindPartner.setOnClickListener(v -> switchTab(0));
        navBubble.setOnClickListener(v -> switchTab(1));
        navBroadcast.setOnClickListener(v -> switchTab(2));
    }

    private void switchTab(int tabIndex) {
        navFindPartner.setBackgroundResource(tabIndex == 0 ? R.drawable.ic_square_black : R.drawable.bg_white_rounded);
        navBubble.setBackgroundResource(tabIndex == 1 ? R.drawable.ic_square_black : R.drawable.bg_white_rounded);
        navBroadcast.setBackgroundResource(tabIndex == 2 ? R.drawable.ic_square_black : R.drawable.bg_white_rounded);

        updateNavTextColor(navFindPartner, tabIndex == 0);
        updateNavTextColor(navBubble, tabIndex == 1);
        updateNavTextColor(navBroadcast, tabIndex == 2);

        if (tabIndex == 0) {
            // 找搭子 - 已经在当前页
        }
    }

    private void updateNavTextColor(FrameLayout navLayout, boolean isSelected) {
        if (navLayout.getChildCount() > 0 && navLayout.getChildAt(0) instanceof android.widget.TextView) {
            android.widget.TextView textView = (android.widget.TextView) navLayout.getChildAt(0);
            textView.setTextColor(isSelected ? 0xFFFFFFFF : 0xFF000000);
        }
    }

    // 添加新卡片到列表
    public void addNewCard(CardItem newCard) {
        cardList.add(newCard);
        cardAdapter.notifyItemInserted(cardList.size() - 1);
        recyclerCards.scrollToPosition(cardList.size() - 1);
    }

    private List<CardItem> getSampleData() {
        List<CardItem> cards = new ArrayList<>();
        
        // 卡片1 - 椅子乐团
        cards.add(new CardItem(
                "椅子乐团",
                "2025,11,29 Shanghai",
                Arrays.asList(
                        new CardItem.TagItem("girls", true),
                        new CardItem.TagItem("20+", true),
                        new CardItem.TagItem("The chairs", true),
                        new CardItem.TagItem("others", false)
                ),
                0,
                "2025", "11", "29",
                "Shanghai",
                "2",
                "Welcome to join us! 椅子乐团巡回演唱会，寻找志同道合的音乐伙伴一起享受现场！",
                Arrays.asList("girls", "20+", "The chairs"),
                Arrays.asList("others")
        ));

        // 卡片2 - 明日方舟
        cards.add(new CardItem(
                "明日方舟2025音律联觉",
                "2025,3,22 Shanghai",
                Arrays.asList(
                        new CardItem.TagItem("老玩家", true),
                        new CardItem.TagItem("练度高", true),
                        new CardItem.TagItem("海猫)", false)
                ),
                1,
                "2025", "3", "22",
                "Shanghai",
                "3",
                "音律联觉现场，寻找刀客塔一起为喜欢的角色打call！要求老玩家，练度高优先~",
                Arrays.asList("老玩家", "练度高"),
                Arrays.asList("海猫")
        ));

        // 卡片3 - Vaundy
        cards.add(new CardItem(
                "Vaundy",
                "2025,10,29 Tokyo",
                Arrays.asList(
                        new CardItem.TagItem("girls", true),
                        new CardItem.TagItem("会唱日语", true),
                        new CardItem.TagItem("只听舞女", false),
                        new CardItem.TagItem("30+", false)
                ),
                2,
                "2025", "10", "29",
                "Tokyo",
                "1",
                "Vaundy日本巡演东京站！寻找会唱日语歌的女生一起嗨！",
                Arrays.asList("girls", "会唱日语"),
                Arrays.asList("只听舞女", "30+")
        ));

        // 卡片4 - 夏日音乐节
        cards.add(new CardItem(
                "夏日音乐节",
                "2025,6,30 Qingdao",
                Arrays.asList(
                        new CardItem.TagItem("海边", true),
                        new CardItem.TagItem("摇滚", true),
                        new CardItem.TagItem("啤酒畅饮", true)
                ),
                2,
                "2025", "6", "30",
                "Qingdao",
                "4",
                "青岛夏日海滩音乐节！摇滚、海风、啤酒，寻找志同道合的朋友一起狂欢！",
                Arrays.asList("海边", "摇滚", "啤酒畅饮"),
                new ArrayList<>()
        ));

        // 卡片5 - Billie Eilish
        cards.add(new CardItem(
                "Billie Eilish",
                "2025,7,15 Los Angeles",
                Arrays.asList(
                        new CardItem.TagItem("欧美", true),
                        new CardItem.TagItem("流行", true),
                        new CardItem.TagItem("碧梨粉丝", true)
                ),
                1,
                "2025", "7", "15",
                "Los Angeles",
                "2",
                "Billie Eilish洛杉矶演唱会！寻找碧梨真粉一起去现场感受她的独特魅力！",
                Arrays.asList("欧美", "流行", "碧梨粉丝"),
                new ArrayList<>()
        ));

        // 卡片6 - 周杰伦
        cards.add(new CardItem(
                "周杰伦演唱会",
                "2025,8,20 Beijing",
                Arrays.asList(
                        new CardItem.TagItem("华语", true),
                        new CardItem.TagItem("20年老粉", true),
                        new CardItem.TagItem("全场大合唱", true)
                ),
                1,
                "2025", "8", "20",
                "Beijing",
                "1",
                "周杰伦北京演唱会！寻找20年老粉一起全场大合唱，回忆青春！",
                Arrays.asList("华语", "20年老粉", "全场大合唱"),
                new ArrayList<>()
        ));

        // 卡片7 - Aimer
        cards.add(new CardItem(
                "Aimer",
                "2025,9,12 Osaka",
                Arrays.asList(
                        new CardItem.TagItem("JPOP", true),
                        new CardItem.TagItem("天籁嗓音", true),
                        new CardItem.TagItem("社恐", false)
                ),
                2,
                "2025", "9", "12",
                "Osaka",
                "2",
                "Aimer大阪演唱会！寻找喜欢JPOP的朋友一起感受她的天籁嗓音！",
                Arrays.asList("JPOP", "天籁嗓音"),
                Arrays.asList("社恐")
        ));

        // 卡片8 - 五月天
        cards.add(new CardItem(
                "五月天演唱会",
                "2025,11,11 Taipei",
                Arrays.asList(
                        new CardItem.TagItem("五迷", true),
                        new CardItem.TagItem("蓝三", true),
                        new CardItem.TagItem("talking太长", false)
                ),
                1,
                "2025", "11", "11",
                "Taipei",
                "3",
                "五月天台北演唱会！寻找五迷一起加入五月天，永远不孤单！",
                Arrays.asList("五迷", "蓝三"),
                Arrays.asList("talking太长")
        ));

        // 卡片9 - RADWIMPS
        cards.add(new CardItem(
                "RADWIMPS",
                "2025,12,25 Tokyo",
                Arrays.asList(
                        new CardItem.TagItem("新海诚", true),
                        new CardItem.TagItem("日系摇滚", true),
                        new CardItem.TagItem("野田洋次郎", true)
                ),
                2,
                "2025", "12", "25",
                "Tokyo",
                "2",
                "RADWIMPS东京圣诞演唱会！新海诚电影主题曲演唱者，日系摇滚迷不要错过！",
                Arrays.asList("新海诚", "日系摇滚", "野田洋次郎"),
                new ArrayList<>()
        ));

        // 卡片10 - 温州音乐节
        cards.add(new CardItem(
                "温州音乐节",
                "2025,4,6 Wenzhou",
                Arrays.asList(
                        new CardItem.TagItem("girls", true),
                        new CardItem.TagItem("一个人", true),
                        new CardItem.TagItem("摇滚og真粉丝", true)
                ),
                1,
                "2025", "4", "6",
                "Wenzhou",
                "1",
                "温州本地音乐节！寻找摇滚OG真粉丝，一个人来的女生优先~",
                Arrays.asList("girls", "一个人", "摇滚og真粉丝"),
                new ArrayList<>()
        ));

        return cards;
    }
}
