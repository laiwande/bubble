package com.bubble.ui;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.bubble.R;
import com.bubble.databinding.ActivityBubbleDetailBinding;
import com.bubble.ui.model.BubbleInfo;
import com.google.android.material.chip.Chip;
import java.util.ArrayList;

public class BubbleDetailActivity extends AppCompatActivity {

    private ActivityBubbleDetailBinding binding;

    public static final String EXTRA_BUBBLE_LABELS = "BUBBLE_LABELS";
    public static final String EXTRA_BUBBLE_NAME = "BUBBLE_NAME";
    public static final String EXTRA_AGE = "AGE";
    public static final String EXTRA_GENDER = "GENDER";
    public static final String EXTRA_BUBBLE_INFO = "BUBBLE_INFO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityBubbleDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initViews();
        loadBubbleLabels();
        setupClickListeners();
    }

    private void initViews() {
        // 返回按钮
        binding.btnBack.setOnClickListener(v -> finish());

        // 设置按钮（预留功能）
        binding.btnSetting.setOnClickListener(v -> {
            // TODO: 打开设置界面
        });

        // 接收 Bubble Name 并显示
        String bubbleName = getIntent().getStringExtra(EXTRA_BUBBLE_NAME);
        if (bubbleName != null && !bubbleName.isEmpty()) {
            binding.tvChatName.setText(bubbleName);
        }

        // 接收 Age 并显示
        String age = getIntent().getStringExtra(EXTRA_AGE);
        if (age != null && !age.isEmpty()) {
            binding.tvAgeValue.setText(age);
        }

        // 接收 Gender 并显示
        int genderProgress = getIntent().getIntExtra(EXTRA_GENDER, -1);
        if (genderProgress >= 0) {
            int femalePercent = 100 - genderProgress;
            int malePercent = genderProgress;
            String genderText = femalePercent + "%F / " + malePercent + "%M";
            binding.tvGenderValue.setText(genderText);
        }

        // 接收 BubbleInfo 并显示泡泡卡片（优先级高于单独的字段）
        BubbleInfo bubbleInfo = (BubbleInfo) getIntent().getSerializableExtra(EXTRA_BUBBLE_INFO);
        if (bubbleInfo != null) {
            displayBubbleCard(bubbleInfo);
        } else if (bubbleName != null && !bubbleName.isEmpty()) {
            // 兼容 CreateActivity 跳转过来时没有 BubbleInfo 的情况
            binding.tvBubbleCardName.setText(bubbleName);
            binding.tvBubbleCardContent.setText("🎸 【" + bubbleName + "】— 一起加入吧！🎤");
        }
    }

    /**
     * 显示泡泡卡片信息（从 BubbleWallFragment 点击跳转时使用）
     */
    private void displayBubbleCard(BubbleInfo info) {
        // 显示名称
        binding.tvBubbleCardName.setText(info.getName());
        // 显示内容
        binding.tvBubbleCardContent.setText(info.getContent());
        // 切换背景图
        binding.ivBubbleCardBackground.setImageResource(info.getCardBackgroundResId());
        // 同时更新顶部标题
        binding.tvChatName.setText(info.getName());
    }

    /**
     * 从 Intent 接收标签数据并显示到 ChipGroup
     */
    private void loadBubbleLabels() {
        ArrayList<String> bubbleLabels = getIntent().getStringArrayListExtra(EXTRA_BUBBLE_LABELS);

        if (bubbleLabels != null && !bubbleLabels.isEmpty()) {
            for (String label : bubbleLabels) {
                addChipToGroup(label);
            }
        }
    }

    /**
     * 添加 Chip 到 ChipGroup
     */
    private void addChipToGroup(String tag) {
        Chip chip = new Chip(this);
        chip.setText(tag);
        chip.setTextSize(8);
        chip.setTextColor(0xFFFFFFFF);
        chip.setChipBackgroundColor(android.content.res.ColorStateList.valueOf(0xFF333333));

        chip.setChipStartPadding(-5);
        chip.setChipEndPadding(0);
        chip.setTextStartPadding(0);
        chip.setTextEndPadding(0);
        chip.setIconStartPadding(1);
        chip.setChipMinHeight(18);

        chip.setOnClickListener(v -> {
            binding.chipGroupBubbleLabels.removeView(chip);
        });

        binding.chipGroupBubbleLabels.addView(chip);
    }

    private void setupClickListeners() {
        // 其他点击事件可在此添加
    }
}
