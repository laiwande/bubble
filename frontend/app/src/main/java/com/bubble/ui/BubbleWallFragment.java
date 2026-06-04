package com.bubble.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bubble.R;
import com.bubble.ui.model.BubbleInfo;

public class BubbleWallFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_square_bubblewall, container, false);
        initCardClickListeners(view);
        return view;
    }

    private void initCardClickListeners(View view) {
        // 第一个卡片：蛋蛋后杰迷
        view.findViewById(R.id.card_bubble_one).setOnClickListener(v -> {
            BubbleInfo bubble1 = new BubbleInfo(
                    "蛋蛋后杰迷",
                    "🎸 【蛋蛋后杰迷】— 一起摇滚，一起emo！🎤\n" +
                    "你是否听着周杰伦的歌长大？你是否对《七里香》《夜曲》倒背如流？你是否还在和朋友争论《最伟大的作品》到底是不是神专？\n" +
                    "🎶 不管你是怀旧派，还是新专吹，这里都是你的主场！\n" +
                    "🥚 蛋蛋后——00后，正好卡在听磁带懂黑胶的年代！\n" +
                    "👑 杰迷——无论几岁，心里都住着那个穿白T恤、戴鸭舌帽的周董！\n" +
                    "🎶来吧！加入【蛋蛋后杰迷】，我们一起哼唱《晴天》，聊聊哥的青春，再狠狠地为青春流一滴感动的泪！😭",
                    R.drawable.ic_square_bubblecardone
            );
            openBubbleDetail(bubble1);
        });

        // 第二个卡片：Swiftie Gals
        view.findViewById(R.id.card_bubble_two).setOnClickListener(v -> {
            BubbleInfo bubble2 = new BubbleInfo(
                    "Swiftie Gals",
                    "Welcome to \"Swiftie Gals\" - Where We Never Go Out of Style!\nWhether you're a Day 1 Swiftie, a Red-era romantic, a folklore/evermore poet, or a fresh TTPD convert, this is your safe space to geek out over every easter egg, bridge change, and Taylor's Version re-recording.\nFrom crying in the car to singing at the top of your lungs — we get it.\nNo judgment, just friendship, playlists, and theories. Let's stay up 'til 2am analyzing lyrics together! ✨",
                    R.drawable.ic_square_bubblecardone
            );
            openBubbleDetail(bubble2);
        });
    }

    private void openBubbleDetail(BubbleInfo bubbleInfo) {
        if (getActivity() == null) return;
        Intent intent = new Intent(getActivity(), BubbleDetailActivity.class);
        intent.putExtra(BubbleDetailActivity.EXTRA_BUBBLE_INFO, bubbleInfo);
        startActivity(intent);
    }
}
