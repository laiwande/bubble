package com.bubble.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.bubble.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import java.util.ArrayList;

public class CardDetailFragment extends Fragment {

    // 顶部导航
    private ImageView ivBack;
    private ImageView ivAvatar;
    private ImageView ivAdd;
    private FrameLayout navFindPartner;
    private FrameLayout navBubble;
    private FrameLayout navBroadcast;

    // 卡片内容
    private TextView tvTopicName;
    private TextView tvAddress;
    private TextView tvYear;
    private TextView tvMonth;
    private TextView tvDay;
    private TextView tvPartnerValue;
    private TextView tvDescription;
    private ChipGroup chipGroupWish;
    private ChipGroup chipGroupBan;
    private FrameLayout flTalk;

    // 数据
    private String title = "";
    private String year = "";
    private String month = "";
    private String day = "";
    private String location = "";
    private String partnerNumber = "";
    private String description = "";
    private ArrayList<String> wishTags = new ArrayList<>();
    private ArrayList<String> banTags = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_square_find_card_detail, container, false);

        // 获取传递的数据
        Bundle args = getArguments();
        if (args != null) {
            title = args.getString("title", "");
            year = args.getString("year", "");
            month = args.getString("month", "");
            day = args.getString("day", "");
            location = args.getString("location", "");
            partnerNumber = args.getString("partnerNumber", "1");
            description = args.getString("description", "");
            wishTags = args.getStringArrayList("wishTags");
            banTags = args.getStringArrayList("banTags");
            if (wishTags == null) wishTags = new ArrayList<>();
            if (banTags == null) banTags = new ArrayList<>();
        }

        initViews(view);
        initListeners();
        displayData();

        return view;
    }

    private void initViews(View view) {
        // 顶部导航
        ivBack = view.findViewById(R.id.iv_back);
        ivAvatar = view.findViewById(R.id.iv_avatar_top);
        ivAdd = view.findViewById(R.id.iv_add);
        navFindPartner = view.findViewById(R.id.nav_find_partner);
        navBubble = view.findViewById(R.id.nav_bubble);
        navBroadcast = view.findViewById(R.id.nav_broadcast);

        // 卡片内容
        tvTopicName = view.findViewById(R.id.tv_topic_name);
        tvAddress = view.findViewById(R.id.tv_address);
        tvYear = view.findViewById(R.id.tv_year);
        tvMonth = view.findViewById(R.id.tv_month);
        tvDay = view.findViewById(R.id.tv_day);
        tvPartnerValue = view.findViewById(R.id.tv_partner_value);
        tvDescription = view.findViewById(R.id.tv_description);
        chipGroupWish = view.findViewById(R.id.chip_group_wish);
        chipGroupBan = view.findViewById(R.id.chip_group_ban);
        flTalk = view.findViewById(R.id.fl_talk);
    }

    private void initListeners() {
        // 返回按钮
        ivBack.setOnClickListener(v -> goBackToSquare());

        // 头像点击
        ivAvatar.setOnClickListener(v -> {
            Toast.makeText(getContext(), "个人中心", Toast.LENGTH_SHORT).show();
        });

        // 加号按钮 - 跳转到创建页
        ivAdd.setOnClickListener(v -> {
            SquareCreateFragment createFragment = new SquareCreateFragment();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, createFragment)
                    .addToBackStack(null)
                    .commit();
        });

        // 导航标签切换
        navFindPartner.setOnClickListener(v -> switchTab(0));
        navBubble.setOnClickListener(v -> switchTab(1));
        navBroadcast.setOnClickListener(v -> switchTab(2));

        // Talk 按钮
        flTalk.setOnClickListener(v -> {
            Toast.makeText(getContext(), "发起聊天", Toast.LENGTH_SHORT).show();
        });
    }

    private void displayData() {
        // 设置文本内容，如果有数据则显示，否则显示 hint
        tvTopicName.setText(title);
        tvAddress.setText(location);
        tvYear.setText(year);
        tvMonth.setText(month);
        tvDay.setText(day);
        tvPartnerValue.setText(partnerNumber);
        tvDescription.setText(description);

        // 显示 Wish 标签
        chipGroupWish.removeAllViews();
        for (String tag : wishTags) {
            addChip(chipGroupWish, tag, true);
        }

        // 显示 Ban 标签
        chipGroupBan.removeAllViews();
        for (String tag : banTags) {
            addChip(chipGroupBan, tag, false);
        }
    }

    private void addChip(ChipGroup chipGroup, String text, boolean isWish) {
        Chip chip = new Chip(getContext());
        chip.setText(text);
        chip.setTextSize(11);
        chip.setTextColor(Color.WHITE);
        chip.setChipBackgroundColor(ContextCompat.getColorStateList(requireContext(), R.color.black));
        // Wish 用对勾，Ban 用叉号
        chip.setChipIconResource(isWish ? R.drawable.ic_check_white : R.drawable.ic_square_wrong);
        chip.setChipIconTint(null);
        // Wish 对勾图标放大一号，Ban 错号调小一号
        chip.setChipIconSize(isWish ? 20 : 14);
        // 减小内边距让标签更扁
        chip.setChipStartPadding(4);
        chip.setChipEndPadding(4);
        chip.setChipMinHeight(24);
        chip.setTextStartPadding(2);
        chip.setTextEndPadding(2);
        chip.setCloseIconVisible(false);
        chip.setClickable(false);
        chip.setFocusable(false);
        chipGroup.addView(chip);
    }

    private void switchTab(int tabIndex) {
        navFindPartner.setBackgroundResource(tabIndex == 0 ? R.drawable.ic_square_black : R.drawable.bg_white_rounded);
        navBubble.setBackgroundResource(tabIndex == 1 ? R.drawable.ic_square_black : R.drawable.bg_white_rounded);
        navBroadcast.setBackgroundResource(tabIndex == 2 ? R.drawable.ic_square_black : R.drawable.bg_white_rounded);

        updateNavTextColor(navFindPartner, tabIndex == 0);
        updateNavTextColor(navBubble, tabIndex == 1);
        updateNavTextColor(navBroadcast, tabIndex == 2);

        if (tabIndex == 0) {
            // 找搭子 - 返回列表页
            goBackToSquare();
        } else {
            Toast.makeText(getContext(), "切换到" + (tabIndex == 1 ? "泡泡墙" : "广播"), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateNavTextColor(FrameLayout navLayout, boolean isSelected) {
        TextView textView = (TextView) navLayout.getChildAt(0);
        if (textView != null) {
            textView.setTextColor(isSelected ? 0xFFFFFFFF : 0xFF000000);
        }
    }

    private void goBackToSquare() {
        // 返回 Square 主界面（找搭子列表页）
        requireActivity().getSupportFragmentManager().popBackStack();
    }
}
