package com.bubble.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import com.bubble.R;
import com.bubble.model.PartnerPost;
import com.bubble.model.Result;
import com.bubble.network.ApiClient;
import com.bubble.network.ApiService;
import com.bubble.utils.AvatarUtils;
import com.bubble.utils.SharedPreferencesUtil;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreatePostActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        initViews();
        initListeners();
    }

    private void initViews() {
        // 顶部标题栏
        ivMenu = findViewById(R.id.iv_menu);
        ivAvatarTop = findViewById(R.id.iv_avatar_top);
        ivAdd = findViewById(R.id.iv_add);

        // 顶部导航
        navFindPartner = findViewById(R.id.nav_find_partner);
        navBubble = findViewById(R.id.nav_bubble);
        navBroadcast = findViewById(R.id.nav_broadcast);

        // 卡片内容
        cardTopicName = findViewById(R.id.card_topic_name);
        cardAddress = findViewById(R.id.card_address);
        cardYear = findViewById(R.id.card_year);
        cardMonth = findViewById(R.id.card_month);
        cardDay = findViewById(R.id.card_day);
        cardPartnerNumber = findViewById(R.id.card_partner_number);
        cardDescription = findViewById(R.id.card_description);
        etTopicName = findViewById(R.id.et_topic_name);
        etAddress = findViewById(R.id.et_address);
        etYear = findViewById(R.id.et_year);
        etMonth = findViewById(R.id.et_month);
        etDay = findViewById(R.id.et_day);
        etPartnerNumber = findViewById(R.id.et_partner_number);
        etDescription = findViewById(R.id.et_description);

        // Wish/Ban
        flWishAdd = findViewById(R.id.fl_wish_add);
        flBanAdd = findViewById(R.id.fl_ban_add);
        chipGroupWish = findViewById(R.id.chip_group_wish);
        chipGroupBan = findViewById(R.id.chip_group_ban);

        // 提交按钮
        ivSubmit = findViewById(R.id.iv_submit);
        
        // 加载当前用户的可爱头像
        loadUserAvatar();
    }

    /**
     * 加载用户头像（使用 DiceBear 生成可爱卡通头像）
     */
    private void loadUserAvatar() {
        SharedPreferencesUtil spUtil = new SharedPreferencesUtil(this);
        Long userId = spUtil.getUserId();
        String avatarSeed = (userId != null) ? String.valueOf(userId) : String.valueOf(System.currentTimeMillis());
        
        AvatarUtils.loadCuteAvatar(ivAvatarTop, avatarSeed);
    }

    private void initListeners() {
        // 返回按钮
        ivMenu.setOnClickListener(v -> finish());

        // 头像点击 - 显示可爱头像
        ivAvatarTop.setOnClickListener(v -> {
            Toast.makeText(this, "个人中心", Toast.LENGTH_SHORT).show();
        });

        // 加号按钮 - 创建新帖子
        ivAdd.setOnClickListener(v -> {
            clearForm();
            Toast.makeText(this, "新建", Toast.LENGTH_SHORT).show();
        });

        // 导航标签切换 - 在创建帖子界面禁用
        // navFindPartner.setOnClickListener(v -> switchTab(0));
        // navBubble.setOnClickListener(v -> switchTab(1));
        // navBroadcast.setOnClickListener(v -> switchTab(2));

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
            Toast.makeText(this, "切换到" + (tabIndex == 1 ? "泡泡墙" : "广播"), Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "Wish 标签最多3个", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isWish && banTags.size() >= 3) {
            Toast.makeText(this, "Ban 标签最多3个", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(isWish ? "添加 Wish 标签" : "添加 Ban 标签");

        final EditText input = new EditText(this);
        input.setHint("请输入标签");
        builder.setView(input);

        builder.setPositiveButton("确认", (dialog, which) -> {
            String tag = input.getText().toString().trim();
            if (!tag.isEmpty()) {
                if (isWish) {
                    if (wishTags.size() >= 3) {
                        Toast.makeText(this, "Wish 标签最多3个", Toast.LENGTH_SHORT).show();
                    } else if (!wishTags.contains(tag)) {
                        wishTags.add(tag);
                        addChipToGroup(chipGroupWish, tag, true);
                    } else {
                        Toast.makeText(this, "标签已存在", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (banTags.size() >= 3) {
                        Toast.makeText(this, "Ban 标签最多3个", Toast.LENGTH_SHORT).show();
                    } else if (!banTags.contains(tag)) {
                        banTags.add(tag);
                        addChipToGroup(chipGroupBan, tag, false);
                    } else {
                        Toast.makeText(this, "标签已存在", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        builder.setNegativeButton("取消", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void addChipToGroup(ChipGroup chipGroup, String text, boolean isWish) {
        Chip chip = new Chip(this);
        chip.setText(text);
        chip.setTextSize(11);
        chip.setTextColor(Color.WHITE);
        chip.setChipBackgroundColor(ContextCompat.getColorStateList(this, R.color.black));
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
        String partnerNumberStr = etPartnerNumber.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        // 检查必填项
        if (topicName.isEmpty() || address.isEmpty() || year.isEmpty()
                || month.isEmpty() || day.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "编辑不完整", Toast.LENGTH_SHORT).show();
            return;
        }

        // 校验日期数值合法性
        int m, d;
        try {
            m = Integer.parseInt(month);
            d = Integer.parseInt(day);
            if (m < 1 || m > 12) throw new NumberFormatException();
            if (d < 1 || d > 31) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "请填写正确的日期", Toast.LENGTH_SHORT).show();
            return;
        }

        // 拼接活动日期（强制两位补零）
        String activityDate = year + "-" + String.format("%02d", m)
                + "-" + String.format("%02d", d);

        // 获取 token
        SharedPreferencesUtil spUtil = new SharedPreferencesUtil(this);
        String token = spUtil.getToken();
        if (token.isEmpty()) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }

        // 构建请求体
        Map<String, Object> body = new HashMap<>();
        body.put("topicName", topicName);
        body.put("address", address);
        body.put("activityDate", activityDate);
        body.put("description", description);
        if (!partnerNumberStr.isEmpty()) {
            body.put("partnerNumber", Integer.parseInt(partnerNumberStr));
        }
        // List<String> -> JSON 字符串
        Gson gson = new Gson();
        body.put("wishTags", gson.toJson(wishTags));
        body.put("banTags", gson.toJson(banTags));

        // 调用 API
        ApiService apiService = ApiClient.getApiService();
        apiService.createPartnerPost("Bearer " + token, body).enqueue(new Callback<Result<PartnerPost>>() {
            @Override
            public void onResponse(Call<Result<PartnerPost>> call, Response<Result<PartnerPost>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 200) {
                    PartnerPost post = response.body().getData();
                    Toast.makeText(CreatePostActivity.this, "发布成功", Toast.LENGTH_SHORT).show();

                    // 回传数据给 SquareFindFragment
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("new_post_id", post.getId());
                    resultIntent.putExtra("new_post_topicName", post.getTopicName());
                    resultIntent.putExtra("new_post_address", post.getAddress());
                    resultIntent.putExtra("new_post_activityDate", post.getActivityDate());
                    resultIntent.putExtra("new_post_partnerNumber", post.getPartnerNumber());
                    resultIntent.putExtra("new_post_description", post.getDescription());
                    // wishTags/banTags 后端返回的是 JSON 字符串，需要解析成 ArrayList 再传
                    java.lang.reflect.Type listType = new com.google.gson.reflect.TypeToken<ArrayList<String>>(){}.getType();
                    String wishJson = post.getWishTags();
                    String banJson = post.getBanTags();
                    resultIntent.putStringArrayListExtra("new_post_wishTags",
                            wishJson != null && !wishJson.isEmpty() ? gson.fromJson(wishJson, listType) : new ArrayList<>());
                    resultIntent.putStringArrayListExtra("new_post_banTags",
                            banJson != null && !banJson.isEmpty() ? gson.fromJson(banJson, listType) : new ArrayList<>());
                    resultIntent.putExtra("new_post_createTime", post.getCreateTime());
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    String msg;
                    if (response.body() != null) {
                        msg = response.body().getMessage();
                    } else {
                        // 尝试读取 error body
                        try {
                            msg = "HTTP " + response.code() + ": " + response.errorBody().string();
                        } catch (Exception e) {
                            msg = "HTTP " + response.code() + " (空响应)";
                        }
                    }
                    Toast.makeText(CreatePostActivity.this, "发布失败: " + msg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Result<PartnerPost>> call, Throwable t) {
                Toast.makeText(CreatePostActivity.this, "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }
    }
}
