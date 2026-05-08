package com.bubble.ui;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.bubble.R;
import com.bubble.ui.model.CardItem;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SquareCreateFragment extends Fragment {

    // 顶部标题栏
    private ImageView ivMenu;
    private ImageView ivAvatarTop;
    private ImageView ivAdd;

    // 顶部导航
    private FrameLayout navFindPartner;
    private FrameLayout navBubble;
    private FrameLayout navBroadcast;

    // 卡片内容
    private CardView cardTopicName;
    private CardView cardAddress;
    private CardView cardYear;
    private CardView cardMonth;
    private CardView cardDay;
    private CardView cardPartnerNumber;
    private CardView cardDescription;
    private EditText etTopicName;
    private EditText etAddress;
    private EditText etYear;
    private EditText etMonth;
    private EditText etDay;
    private EditText etPartnerNumber;
    private EditText etDescription;

    // Wish/Ban
    private FrameLayout flWishAdd;
    private FrameLayout flBanAdd;
    private ChipGroup chipGroupWish;
    private ChipGroup chipGroupBan;

    // 提交按钮
    private ImageView ivSubmit;

    // 数据
    private List<String> wishTags = new ArrayList<>();
    private List<String> banTags = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_square_find_create, container, false);
        initViews(view);
        initListeners();
        return view;
    }

    private void initViews(View view) {
        // 顶部标题栏
        ivMenu = view.findViewById(R.id.iv_menu);
        ivAvatarTop = view.findViewById(R.id.iv_avatar_top);
        ivAdd = view.findViewById(R.id.iv_add);

        // 顶部导航
        navFindPartner = view.findViewById(R.id.nav_find_partner);
        navBubble = view.findViewById(R.id.nav_bubble);
        navBroadcast = view.findViewById(R.id.nav_broadcast);

        // 卡片内容
        cardTopicName = view.findViewById(R.id.card_topic_name);
        cardAddress = view.findViewById(R.id.card_address);
        cardYear = view.findViewById(R.id.card_year);
        cardMonth = view.findViewById(R.id.card_month);
        cardDay = view.findViewById(R.id.card_day);
        cardPartnerNumber = view.findViewById(R.id.card_partner_number);
        cardDescription = view.findViewById(R.id.card_description);
        etTopicName = view.findViewById(R.id.et_topic_name);
        etAddress = view.findViewById(R.id.et_address);
        etYear = view.findViewById(R.id.et_year);
        etMonth = view.findViewById(R.id.et_month);
        etDay = view.findViewById(R.id.et_day);
        etPartnerNumber = view.findViewById(R.id.et_partner_number);
        etDescription = view.findViewById(R.id.et_description);

        // Wish/Ban
        flWishAdd = view.findViewById(R.id.fl_wish_add);
        flBanAdd = view.findViewById(R.id.fl_ban_add);
        chipGroupWish = view.findViewById(R.id.chip_group_wish);
        chipGroupBan = view.findViewById(R.id.chip_group_ban);

        // 提交按钮
        ivSubmit = view.findViewById(R.id.iv_submit);
    }

    private void initListeners() {
        // 返回按钮
        ivMenu.setOnClickListener(v -> {
            // 返回到 SquareFindFragment
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        // 头像点击
        ivAvatarTop.setOnClickListener(v -> {
            Toast.makeText(getContext(), "个人中心", Toast.LENGTH_SHORT).show();
        });

        // 加号按钮 - 创建新帖子
        ivAdd.setOnClickListener(v -> {
            clearForm();
            Toast.makeText(getContext(), "新建", Toast.LENGTH_SHORT).show();
        });

        // 导航标签切换
        navFindPartner.setOnClickListener(v -> switchTab(0));
        navBubble.setOnClickListener(v -> switchTab(1));
        navBroadcast.setOnClickListener(v -> switchTab(2));

        // Wish 添加标签
        flWishAdd.setOnClickListener(v -> showAddTagDialog(true));

        // Ban 添加标签
        flBanAdd.setOnClickListener(v -> showAddTagDialog(false));

        // 小卡片点击聚焦
        setupCardFocus(cardTopicName, etTopicName);
        setupCardFocus(cardAddress, etAddress);
        setupCardFocus(cardYear, etYear);
        setupCardFocus(cardMonth, etMonth);
        setupCardFocus(cardDay, etDay);
        setupCardFocus(cardPartnerNumber, etPartnerNumber);
        setupCardFocus(cardDescription, etDescription);

        // 提交按钮
        ivSubmit.setOnClickListener(v -> submitPost());
    }

    private void switchTab(int tabIndex) {
        // 更新导航样式
        navFindPartner.setBackgroundResource(tabIndex == 0 ? R.drawable.ic_square_black : R.drawable.bg_white_rounded);
        navBubble.setBackgroundResource(tabIndex == 1 ? R.drawable.ic_square_black : R.drawable.bg_white_rounded);
        navBroadcast.setBackgroundResource(tabIndex == 2 ? R.drawable.ic_square_black : R.drawable.bg_white_rounded);

        // 更新文字颜色
        updateNavTextColor(navFindPartner, tabIndex == 0);
        updateNavTextColor(navBubble, tabIndex == 1);
        updateNavTextColor(navBroadcast, tabIndex == 2);

        if (tabIndex != 0) {
            Toast.makeText(getContext(), "切换到" + (tabIndex == 1 ? "泡泡墙" : "广播"), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateNavTextColor(FrameLayout navLayout, boolean isSelected) {
        TextView textView = (TextView) navLayout.getChildAt(0);
        if (textView != null) {
            textView.setTextColor(isSelected ? 0xFFFFFFFF : 0xFF000000);
        }
    }

    private void showAddTagDialog(boolean isWish) {
        // 检查是否已满3个
        if (isWish && wishTags.size() >= 3) {
            Toast.makeText(getContext(), "Wish 标签最多3个", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isWish && banTags.size() >= 3) {
            Toast.makeText(getContext(), "Ban 标签最多3个", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(isWish ? "添加 Wish 标签" : "添加 Ban 标签");

        final EditText input = new EditText(requireContext());
        input.setHint("请输入标签");
        builder.setView(input);

        builder.setPositiveButton("确认", (dialog, which) -> {
            String tag = input.getText().toString().trim();
            if (!tag.isEmpty()) {
                if (isWish) {
                    if (wishTags.size() >= 3) {
                        Toast.makeText(getContext(), "Wish 标签最多3个", Toast.LENGTH_SHORT).show();
                    } else if (!wishTags.contains(tag)) {
                        wishTags.add(tag);
                        addChipToGroup(chipGroupWish, tag, true);
                    } else {
                        Toast.makeText(getContext(), "标签已存在", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (banTags.size() >= 3) {
                        Toast.makeText(getContext(), "Ban 标签最多3个", Toast.LENGTH_SHORT).show();
                    } else if (!banTags.contains(tag)) {
                        banTags.add(tag);
                        addChipToGroup(chipGroupBan, tag, false);
                    } else {
                        Toast.makeText(getContext(), "标签已存在", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        builder.setNegativeButton("取消", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void addChipToGroup(ChipGroup chipGroup, String text, boolean isWish) {
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
        chip.setOnClickListener(v -> {
            // 点击标签删除
            chipGroup.removeView(chip);
            if (isWish) {
                wishTags.remove(text);
            } else {
                banTags.remove(text);
            }
            // 更新 + 按钮可见性
            updateAddButtonVisibility();
        });
        chipGroup.addView(chip);
        // 更新 + 按钮可见性
        updateAddButtonVisibility();
    }

    private void updateAddButtonVisibility() {
        // Wish + 按钮：满3个隐藏，否则显示
        flWishAdd.setVisibility(wishTags.size() >= 3 ? View.GONE : View.VISIBLE);
        // Ban + 按钮：满3个隐藏，否则显示
        flBanAdd.setVisibility(banTags.size() >= 3 ? View.GONE : View.VISIBLE);
    }

    private void submitPost() {
        String topicName = etTopicName.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String year = etYear.getText().toString().trim();
        String month = etMonth.getText().toString().trim();
        String day = etDay.getText().toString().trim();
        String partnerNumber = etPartnerNumber.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        // 检查必填项
        if (topicName.isEmpty() || address.isEmpty() || year.isEmpty() 
                || month.isEmpty() || day.isEmpty() || description.isEmpty()) {
            Toast.makeText(getContext(), "编辑不完整", Toast.LENGTH_SHORT).show();
            return;
        }

        // 显示发布成功提示
        Toast.makeText(getContext(), "发布成功", Toast.LENGTH_SHORT).show();

        // 返回到 SquareFindFragment（找搭子列表页）
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    private void clearForm() {
        etTopicName.setText("");
        etAddress.setText("");
        etYear.setText("");
        etMonth.setText("");
        etDay.setText("");
        etPartnerNumber.setText("");
        etDescription.setText("");
        chipGroupWish.removeAllViews();
        chipGroupBan.removeAllViews();
        wishTags.clear();
        banTags.clear();
        // 恢复 + 按钮可见性
        flWishAdd.setVisibility(View.VISIBLE);
        flBanAdd.setVisibility(View.VISIBLE);
    }

    private void setupCardFocus(CardView cardView, EditText editText) {
        cardView.setOnClickListener(v -> {
            editText.requestFocus();
            showKeyboard(editText);
        });
    }

    private void showKeyboard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }
    }
}
