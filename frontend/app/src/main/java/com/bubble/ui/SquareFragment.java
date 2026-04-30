package com.bubble.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class SquareFragment extends Fragment {

    private RecyclerView recyclerCards;
    private CardAdapter cardAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_square, container, false);
        initRecyclerView(view);
        return view;
    }

    private void initRecyclerView(View view) {
        recyclerCards = view.findViewById(R.id.recycler_cards);
        recyclerCards.setLayoutManager(new LinearLayoutManager(getContext()));
        
        cardAdapter = new CardAdapter(getSampleData());
        recyclerCards.setAdapter(cardAdapter);
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
                0
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
                1
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
                2
        ));

        // 卡片4 - 温州音乐节
        cards.add(new CardItem(
                "夏日音乐节",
                "2025,6,30 Qingdao",
                Arrays.asList(
                        new CardItem.TagItem("海边", true),
                        new CardItem.TagItem("摇滚", true),
                        new CardItem.TagItem("啤酒畅饮", true)
                ),
                2
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
                1
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
                1
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
                2
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
                1
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
                2
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
                1
        ));

        return cards;
    }
}
