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
import com.bubble.R;

public class SquareFragment extends Fragment {

    private ImageView ivAdd;
    private FrameLayout navFollow;
    private FrameLayout navBubble;
    private FrameLayout navBroadcast;

    // 三个子 Fragment
    private SquareFindFragment findFragment;
    private BubbleWallFragment bubbleWallFragment;
    private BroadcastFragment broadcastFragment;
    
    private Fragment currentChildFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_square, container, false);
        initViews(view);
        initListeners();
        initChildFragments();
        return view;
    }

    private void initViews(View view) {
        ivAdd = view.findViewById(R.id.iv_add);
        navFollow = view.findViewById(R.id.nav_follow);
        navBubble = view.findViewById(R.id.nav_bubble);
        navBroadcast = view.findViewById(R.id.nav_broadcast);
    }

    private void initChildFragments() {
        findFragment = new SquareFindFragment();
        bubbleWallFragment = new BubbleWallFragment();
        broadcastFragment = new BroadcastFragment();

        // 初始显示找搭子
        getChildFragmentManager()
                .beginTransaction()
                .add(R.id.child_fragment_container, findFragment, "find")
                .add(R.id.child_fragment_container, bubbleWallFragment, "bubble")
                .add(R.id.child_fragment_container, broadcastFragment, "broadcast")
                .hide(bubbleWallFragment)
                .hide(broadcastFragment)
                .commit();
        
        currentChildFragment = findFragment;
        updateNavStyle(0);
    }

    private void initListeners() {
        // 加号按钮 - 只在找搭子界面跳转
        ivAdd.setOnClickListener(v -> {
            if (currentChildFragment == bubbleWallFragment) {
                // 泡泡墙界面 - 不做任何反应
                return;
            }
            startActivity(new Intent(requireContext(), CreatePostActivity.class));
        });

        // 导航标签切换
        navFollow.setOnClickListener(v -> switchTab(0));
        navBubble.setOnClickListener(v -> switchTab(1));
        navBroadcast.setOnClickListener(v -> switchTab(2));
    }

    private void switchTab(int tabIndex) {
        Fragment target;
        switch (tabIndex) {
            case 0:
                target = findFragment;
                break;
            case 1:
                target = bubbleWallFragment;
                break;
            case 2:
                target = broadcastFragment;
                break;
            default:
                return;
        }

        if (target == currentChildFragment) return;

        getChildFragmentManager()
                .beginTransaction()
                .hide(currentChildFragment)
                .show(target)
                .commit();
        
        currentChildFragment = target;
        updateNavStyle(tabIndex);
    }

    private void updateNavStyle(int selectedTab) {
        // 更新导航按钮样式
        updateNavButton(navFollow, selectedTab == 0);
        updateNavButton(navBubble, selectedTab == 1);
        updateNavButton(navBroadcast, selectedTab == 2);
        
        // 广播界面隐藏 add 图标
        if (ivAdd != null) {
            ivAdd.setVisibility(selectedTab == 2 ? View.GONE : View.VISIBLE);
        }
    }

    private void updateNavButton(FrameLayout navLayout, boolean isSelected) {
        if (navLayout.getChildCount() > 0) {
            View bg = navLayout.getChildAt(0);
            if (bg instanceof ImageView) {
                ((ImageView) bg).setImageResource(isSelected ? 
                    R.drawable.ic_square_black_bg : R.drawable.ic_square_white_bg);
            }
            if (navLayout.getChildCount() > 1) {
                View text = navLayout.getChildAt(1);
                if (text instanceof android.widget.TextView) {
                    ((android.widget.TextView) text).setTextColor(isSelected ? 
                        0xFFFFFFFF : 0xFF000000);
                }
            }
        }
    }

    /**
     * 重置到找搭子页面
     * 当从 Square 切换到底部其他导航时使用
     */
    public void resetToFind() {
        if (currentChildFragment != findFragment) {
            getChildFragmentManager()
                    .beginTransaction()
                    .hide(currentChildFragment)
                    .show(findFragment)
                    .commit();
            currentChildFragment = findFragment;
            updateNavStyle(0);
        }
    }
}