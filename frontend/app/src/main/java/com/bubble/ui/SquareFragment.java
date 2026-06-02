package com.bubble.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.bubble.R;
import com.bubble.ui.model.CardItem;
import com.bubble.utils.AvatarUtils;

import java.util.ArrayList;
import java.util.List;

public class SquareFragment extends Fragment {

    private ImageView ivAdd;
    private ImageView ivAvatarTop;
    private FrameLayout navFollow;
    private FrameLayout navBubble;
    private FrameLayout navBroadcast;

    // 三个子 Fragment
    private SquareFindFragment findFragment;
    private BubbleWallFragment bubbleWallFragment;
    private BroadcastFragment broadcastFragment;
    
    private Fragment currentChildFragment;
    
    // 记录当前选中的Tab索引（0=找搭子, 1=泡泡墙, 2=广播）
    private int lastSelectedTab = 0;

    // ActivityResultLauncher 用于接收 CreatePostActivity 的返回结果
    private final ActivityResultLauncher<Intent> createPostLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    String topicName = data.getStringExtra("new_post_topicName");
                    String address = data.getStringExtra("new_post_address");
                    String activityDate = data.getStringExtra("new_post_activityDate");
                    int partnerNumber = data.getIntExtra("new_post_partnerNumber", 1);
                    String description = data.getStringExtra("new_post_description");
                    List<String> wishTags = data.getStringArrayListExtra("new_post_wishTags");
                    List<String> banTags = data.getStringArrayListExtra("new_post_banTags");

                    if (topicName == null || address == null || activityDate == null) return;

                    // 拆分日期 2026-05-28 -> 2026,5,28
                    String[] dateParts = activityDate.split("-");
                    String year = dateParts.length > 0 ? dateParts[0] : "";
                    String month = dateParts.length > 1 ? dateParts[1] : "";
                    String day = dateParts.length > 2 ? dateParts[2] : "";

                    // subtitle 格式与示例数据一致: "2025,11,29 Shanghai"
                    String location = address.contains(",") ? address.substring(address.lastIndexOf(",") + 1).trim() : address;
                    String subtitle = year + "," + month + "," + day + " " + location;

                    // 构建标签列表（wish=对勾 ✓, ban=叉号 ✗）
                    List<CardItem.TagItem> tagItems = new ArrayList<>();
                    if (wishTags != null) {
                        for (String tag : wishTags) tagItems.add(new CardItem.TagItem(tag, true));
                    }
                    if (banTags != null) {
                        for (String tag : banTags) tagItems.add(new CardItem.TagItem(tag, false));
                    }

                    // 新发布的帖子只有发布者 1 人，avatarRightCount = 0
                    CardItem newCard = new CardItem(
                            topicName, subtitle, tagItems, 0,
                            year, month, day, location,
                            String.valueOf(partnerNumber), description,
                            wishTags != null ? wishTags : new ArrayList<>(),
                            banTags != null ? banTags : new ArrayList<>()
                    );

                    findFragment.addNewCardAtTop(newCard);
                    switchTab(0);
                }
            });

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
        ivAvatarTop = view.findViewById(R.id.iv_avatar_top);
        navFollow = view.findViewById(R.id.nav_follow);
        navBubble = view.findViewById(R.id.nav_bubble);
        navBroadcast = view.findViewById(R.id.nav_broadcast);
        
        // 加载用户头像
        loadUserAvatar();
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
            createPostLauncher.launch(new Intent(requireContext(), CreatePostActivity.class));
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
        lastSelectedTab = tabIndex;  // 记录当前Tab索引
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

    /**
     * 加载用户头像（使用 DiceBear 生成可爱卡通头像）
     */
    private void loadUserAvatar() {
        if (ivAvatarTop == null || getActivity() == null) return;
        
        com.bubble.utils.SharedPreferencesUtil spUtil = new com.bubble.utils.SharedPreferencesUtil(requireContext());
        Long userId = spUtil.getUserId();
        String avatarSeed = (userId != null) ? String.valueOf(userId) : String.valueOf(System.currentTimeMillis());
        
        Glide.with(requireContext())
                .load(AvatarUtils.getAvatarUrl(avatarSeed))
                .placeholder(R.drawable.ic_me_user)
                .error(R.drawable.ic_me_user)
                .circleCrop()
                .into(ivAvatarTop);
    }
}