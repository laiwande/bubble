package com.bubble.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bubble.R;
import com.bubble.databinding.ActivityBubbleDetailBinding;
import com.bubble.model.Bubble;
import com.bubble.model.BubbleDetailData;
import com.bubble.model.Result;
import com.bubble.network.ApiClient;
import com.bubble.network.ApiService;
import com.bubble.utils.SharedPreferencesUtil;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BubbleDetailActivity extends AppCompatActivity {

    private ActivityBubbleDetailBinding binding;
    private ApiService apiService;
    private SharedPreferencesUtil spUtil;
    private Long bubbleId;
    private Bubble bubbleData;

    public static final String EXTRA_BUBBLE_ID = "BUBBLE_ID";
    public static final String EXTRA_BUBBLE_NAME = "BUBBLE_NAME";

    private static final String TAG = "BubbleDetail";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityBubbleDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        apiService = ApiClient.getApiService();
        spUtil = new SharedPreferencesUtil(this);

        bubbleId = getIntent().getLongExtra(EXTRA_BUBBLE_ID, -1L);
        String bubbleName = getIntent().getStringExtra(EXTRA_BUBBLE_NAME);

        initViews(bubbleName);
        setupClickListeners();

        if (bubbleId != -1L) {
            loadBubbleDetail(bubbleId);
        } else if (bubbleName != null) {
            // 从 CreateActivity 跳转过来时，只显示名称
            binding.tvBubbleCardName.setText(bubbleName);
            binding.tvBubbleCardContent.setText("🎸 【" + bubbleName + "】— 一起加入吧！🎤");
        }
    }

    private void initViews(String bubbleName) {
        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnSetting.setOnClickListener(v -> {
            // TODO: 打开设置界面
        });

        if (bubbleName != null && !bubbleName.isEmpty()) {
            binding.tvChatName.setText(bubbleName);
        }
    }

    private void setupClickListeners() {
        // Join 按钮点击
        binding.btnJoin.setOnClickListener(v -> {
            if (bubbleId == -1L) {
                Toast.makeText(this, "Bubble 信息未加载完成", Toast.LENGTH_SHORT).show();
                return;
            }
            joinAndNavigate();
        });

        // 白色圆角容器本身也可点击
        binding.whiteRoundedBox.setOnClickListener(v -> binding.btnJoin.performClick());
    }

    /**
     * 调用 /bubble/{id}/full 接口获取完整数据
     */
    private void loadBubbleDetail(Long id) {
        String token = spUtil.getToken();
        apiService.getBubbleFullDetail("Bearer " + token, id).enqueue(new Callback<Result<BubbleDetailData>>() {
            @Override
            public void onResponse(Call<Result<BubbleDetailData>> call, Response<Result<BubbleDetailData>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 200) {
                    BubbleDetailData data = response.body().getData();
                    if (data != null && data.getBubble() != null) {
                        bubbleData = data.getBubble();
                        displayBubbleData(bubbleData);
                    }
                } else {
                    Log.e(TAG, "加载Bubble详情失败: " + response.message());
                    Toast.makeText(BubbleDetailActivity.this, "加载详情失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Result<BubbleDetailData>> call, Throwable t) {
                Log.e(TAG, "网络错误", t);
                Toast.makeText(BubbleDetailActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 用 Bubble 数据渲染界面
     */
    private void displayBubbleData(Bubble bubble) {
        // 顶部名称
        if (bubble.getName() != null) {
            binding.tvChatName.setText(bubble.getName());
        }

        // 泡泡卡片名称
        binding.tvBubbleCardName.setText(bubble.getName());

        // 卡片内容（description）
        if (bubble.getDescription() != null && !bubble.getDescription().isEmpty()) {
            binding.tvBubbleCardContent.setText(bubble.getDescription());
        } else {
            binding.tvBubbleCardContent.setText("🎸 【" + bubble.getName() + "】— 一起加入吧！🎤");
        }

        // 年龄范围
        if (bubble.getAgeMin() != null && bubble.getAgeMax() != null) {
            binding.tvAgeValue.setText(bubble.getAgeMin() + " - " + bubble.getAgeMax());
        }

        // 性别比例（文字显示）
        updateGenderText(bubble.getGenderRatio());

        // 标签（只显示 type="label" 的，即创建时填写的 "your bubble label"）
        if (bubble.getLabels() != null && !bubble.getLabels().isEmpty()) {
            binding.layoutBubbleLabels.removeAllViews();
            for (String label : bubble.getLabels()) {
                if (label.startsWith("label:")) {
                    addLabelTag(label);
                }
            }
        }
    }

    /**
     * 更新性别文字显示
     * 0 → girls only，100 → boys only，中间值 → girls:boys = 50:50
     */
    private void updateGenderText(String genderRatio) {
        if (genderRatio == null || genderRatio.isEmpty()) {
            binding.tvGenderValue.setText("");
            return;
        }

        try {
            int value = Integer.parseInt(genderRatio);
            if (value == 0) {
                binding.tvGenderValue.setText("girls only");
            } else if (value == 100) {
                binding.tvGenderValue.setText("boys only");
            } else {
                int girls = 100 - value;
                int boys = value;
                binding.tvGenderValue.setText("girls : boys = " + girls + " : " + boys);
            }
        } catch (NumberFormatException e) {
            // 非数字（如"不限"），直接显示
            binding.tvGenderValue.setText(genderRatio);
        }
    }

    /**
     * 添加标签视图（参考找搭子卡片风格）
     */
    private void addLabelTag(String tag) {
        // 提取标签显示名（去掉 type: 前缀）
        String displayText = tag.contains(":") ? tag.substring(tag.indexOf(":") + 1) : tag;

        // 标签容器
        LinearLayout tagLayout = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                dpToPx(24)
        );
        if (binding.layoutBubbleLabels.getChildCount() > 0) {
            params.setMarginStart(dpToPx(8));
        }
        tagLayout.setLayoutParams(params);
        tagLayout.setOrientation(LinearLayout.HORIZONTAL);
        tagLayout.setGravity(Gravity.CENTER_VERTICAL);
        tagLayout.setBackgroundResource(R.drawable.bg_blacktab);
        tagLayout.setPadding(dpToPx(8), 0, dpToPx(10), 0);

        // 文字
        TextView textView = new TextView(this);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        textParams.setMarginStart(dpToPx(3));
        textView.setLayoutParams(textParams);
        textView.setText(displayText);
        textView.setTextColor(0xFFFFFFFF);
        textView.setTextSize(12);
        textView.setTypeface(null, android.graphics.Typeface.BOLD);
        textView.setMaxLines(1);
        textView.setIncludeFontPadding(false);
        tagLayout.addView(textView);

        binding.layoutBubbleLabels.addView(tagLayout);
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    /**
     * Join Bubble 成功后跳转到聊天界面
     */
    private void joinAndNavigate() {
        String token = spUtil.getToken();
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }

        apiService.joinBubble("Bearer " + token, bubbleId).enqueue(new Callback<Result<Void>>() {
            @Override
            public void onResponse(Call<Result<Void>> call, Response<Result<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 200) {
                    String bubbleName = (bubbleData != null) ? bubbleData.getName() : "";
                    Toast.makeText(BubbleDetailActivity.this, "加入成功", Toast.LENGTH_SHORT).show();
                    navigateToChat(bubbleId, bubbleName);
                } else {
                    String msg = (response.body() != null) ? response.body().getMessage() : "加入失败";
                    // 如果已经加入过，也跳转到聊天
                    if (msg.contains("已加入")) {
                        String bubbleName = (bubbleData != null) ? bubbleData.getName() : "";
                        navigateToChat(bubbleId, bubbleName);
                    } else {
                        Toast.makeText(BubbleDetailActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Result<Void>> call, Throwable t) {
                Log.e(TAG, "Join 网络错误", t);
                Toast.makeText(BubbleDetailActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 跳转到聊天界面
     */
    private void navigateToChat(Long bubbleId, String bubbleName) {
        Intent intent = new Intent(BubbleDetailActivity.this, ChatConcreteActivity.class);
        intent.putExtra(ChatConcreteActivity.EXTRA_USER_ID, String.valueOf(bubbleId));
        intent.putExtra(ChatConcreteActivity.EXTRA_USER_NAME, bubbleName != null ? bubbleName : "");
        startActivity(intent);
    }
}
