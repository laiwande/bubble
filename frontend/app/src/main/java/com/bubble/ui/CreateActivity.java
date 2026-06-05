package com.bubble.ui;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.bubble.R;
import com.bubble.databinding.ActivityCreateBinding;
import com.bubble.model.Bubble;
import com.bubble.model.Result;
import com.bubble.network.ApiClient;
import com.bubble.network.ApiService;
import com.bubble.utils.SharedPreferencesUtil;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateActivity extends AppCompatActivity {

    private ActivityCreateBinding binding;
    private String bubbleName = "";
    private String bubbleWallOriginalText = "";  // 存储原始未截断的文本
    
    // 性别滑块相关
    private boolean isFemaleChecked = false;
    private boolean isMaleChecked = false;
    private float pixelsPerProgress = 0; // 每1个progress对应的像素偏移量（用于数字跟随圆圈）

    // Allow / Ban / Bubble Label 标签
    private List<String> allowTags = new ArrayList<>();
    private List<String> banTags = new ArrayList<>();
    private List<String> bubbleLabelTags = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 启用 ViewBinding
        binding = ActivityCreateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        // 返回按钮
        binding.ivBack.setOnClickListener(v -> finish());
        
        // 初始化性别滑块
        initGenderSlider();

        // Card Note Input 点击展开
        binding.ivCardNoteInput.setOnClickListener(v -> showExpandContainer());

        // This Bubble Wall 输入框也支持点击展开
        binding.etNewField4.setOnClickListener(v -> showExpandContainer());

        // 标题文字也支持点击展开
        binding.tvBubbleWallText.setOnClickListener(v -> showExpandContainer());

        // 展开区域的返回键
        binding.ivExpandBack.setOnClickListener(v -> hideExpandContainer());

        // 点击遮罩层也可关闭
        binding.viewExpandOverlay.setOnClickListener(v -> hideExpandContainer());

        // 同步展开输入框内容到原输入框（仅在关闭时同步，弹出时不更新下方显示）
        binding.etExpandInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // 仅保存原始文本，不更新下方显示（下方显示在关闭弹窗时更新）
                bubbleWallOriginalText = s.toString();
            }
        });

        // Name 字段输入监听
        binding.etNameValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                bubbleName = s.toString().trim();
                updateUnderlineWidth(s);
            }
        });

    }

    private void initGenderSlider() {
        // 初始状态：两个复选框都不选，滑块在中间(50)，显示50
        binding.tvGenderValue.setText("50");
        setGenderValueColor(false); // 初始状态 → 灰色
        
        // 计算滑块偏移量（基于你在XML中调整好的progress=50位置）
        binding.sbGender.post(() -> {
            float range = binding.sbGender.getWidth()
                    - binding.sbGender.getPaddingLeft()
                    - binding.sbGender.getPaddingRight();
            pixelsPerProgress = range / 100f;
            // 初始化数字位置到圆圈正上方
            updateGenderValuePosition(50);
        });
        
        // 左侧女生复选框点击
        binding.ivFemaleCheckbox.setOnClickListener(v -> {
            isFemaleChecked = !isFemaleChecked;
            if (isFemaleChecked) {
                // 选中女生：取消男生选中，滑块移到最左侧(0)，显示100
                isMaleChecked = false;
                binding.ivMaleCheckbox.setImageResource(R.drawable.cb_male_unchecked);
                binding.ivFemaleCheckbox.setImageResource(R.drawable.cb_female_checked);
                binding.sbGender.setProgress(0);
                binding.tvGenderValue.setText("0");
                updateGenderValuePosition(0);
                setGenderValueColor(false); // 复选框选中 → 灰色
            } else {
                // 取消选中女生
                binding.ivFemaleCheckbox.setImageResource(R.drawable.cb_female_unchecked);
                binding.sbGender.setProgress(50);
                binding.tvGenderValue.setText("50");
                updateGenderValuePosition(50);
                setGenderValueColor(false); // 默认状态 → 灰色
            }
        });

        // 右侧男生复选框点击
        binding.ivMaleCheckbox.setOnClickListener(v -> {
            isMaleChecked = !isMaleChecked;
            if (isMaleChecked) {
                // 选中男生：取消女生选中，滑块移到最右侧(100)，显示100
                isFemaleChecked = false;
                binding.ivFemaleCheckbox.setImageResource(R.drawable.cb_female_unchecked);
                binding.ivMaleCheckbox.setImageResource(R.drawable.cb_male_checked);
                binding.sbGender.setProgress(100);
                binding.tvGenderValue.setText("100");
                updateGenderValuePosition(100);
                setGenderValueColor(false); // 复选框选中 → 灰色
            } else {
                // 取消选中男生
                binding.ivMaleCheckbox.setImageResource(R.drawable.cb_male_unchecked);
                binding.sbGender.setProgress(50);
                binding.tvGenderValue.setText("50");
                updateGenderValuePosition(50);
                setGenderValueColor(false); // 默认状态 → 灰色
            }
        });
        
        // 滑块拖动监听
        binding.sbGender.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // 滑块位置直接对应女生比例
                // 0 = 100%女生(最左), 100 = 0%女生 = 100%男生(最右)
                binding.tvGenderValue.setText(String.valueOf(progress));
                
                // 更新数字位置，让它跟随圆圈
                updateGenderValuePosition(progress);
                
                // 如果用户手动拖动滑块，取消两个复选框的选中状态
                if (fromUser) {
                    isFemaleChecked = false;
                    isMaleChecked = false;
                    binding.ivFemaleCheckbox.setImageResource(R.drawable.cb_female_unchecked);
                    binding.ivMaleCheckbox.setImageResource(R.drawable.cb_male_unchecked);
                    setGenderValueColor(true); // 拖动中 → #333333深色
                }
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // 开始拖动时取消复选框选中，颜色变深
                isFemaleChecked = false;
                isMaleChecked = false;
                binding.ivFemaleCheckbox.setImageResource(R.drawable.cb_female_unchecked);
                binding.ivMaleCheckbox.setImageResource(R.drawable.cb_male_unchecked);
                setGenderValueColor(true); // 开始拖动 → #333333深色
            }
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // 停止拖动 → 恢复灰色（非选中/非拖动状态）
                setGenderValueColor(false);
            }
        });
    }

    /**
     * 更新性别数值的位置，使其跟随SeekBar的thumb（圆圈）移动
     * @param progress 当前滑块进度值(0-100)
     */
    private void updateGenderValuePosition(int progress) {
        if (binding.sbGender.getWidth() <= 0) return;

        // 获取 thumb 在 gender_container 内的中心位置
        float thumbCenter = binding.genderRow.getLeft()
                + binding.sbGender.getLeft()
                + binding.sbGender.getThumb().getBounds().centerX();

        // 考虑 scaleX 计算文字视觉中心
        float scaleX = binding.tvGenderValue.getScaleX();
        float tvVisualCenter = binding.tvGenderValue.getLeft()
                + (binding.tvGenderValue.getWidth() * scaleX) / 2f;

        // X方向：文字视觉中心对齐 thumb 中心
        final float TWEAK_X = 28f;
        binding.tvGenderValue.setTranslationX(thumbCenter - tvVisualCenter + TWEAK_X);

        // Y方向：固定正偏移（负值会超出容器被裁剪导致文字消失）
        binding.tvGenderValue.setTranslationY(20f);
    }

    /**
     * 设置性别数值文字和滑块圆圈的颜色
     * @param isDragging 是否正在拖动（true=#464646深色, false=灰色）
     */
    private void setGenderValueColor(boolean isDragging) {
        int color = isDragging ? 0xFF464646 : 0xFF999999;
        binding.tvGenderValue.setTextColor(color);
        // 同步改变滑块thumb圆圈颜色
        binding.sbGender.setThumbTintList(ColorStateList.valueOf(color));
    }




    private void showExpandContainer() {
        // 同步原输入框内容到展开输入框（使用原始未截断的文本）
        binding.etExpandInput.setText(bubbleWallOriginalText);
        binding.etExpandInput.setSelection(binding.etExpandInput.getText().length());

        // 显示遮罩和展开容器
        binding.viewExpandOverlay.setVisibility(View.VISIBLE);
        binding.expandContainer.setVisibility(View.VISIBLE);

        // 设置动画（可选）
        binding.expandContainer.setAlpha(0f);
        binding.expandContainer.animate()
                .alpha(1f)
                .setDuration(200)
                .start();

        binding.viewExpandOverlay.setAlpha(0f);
        binding.viewExpandOverlay.animate()
                .alpha(1f)
                .setDuration(200)
                .start();
        
        // 聚焦到展开输入框
        binding.etExpandInput.requestFocus();
    }

    private void hideExpandContainer() {
        // 保存原始文本，并截断为预览格式显示
        String originalText = binding.etExpandInput.getText().toString();
        bubbleWallOriginalText = originalText;  // 保存原始文本
        String truncatedText = truncateForPreview(originalText);
        binding.etNewField4.setText(truncatedText);

        // 隐藏动画
        binding.expandContainer.animate()
                .alpha(0f)
                .setDuration(200)
                .withEndAction(() -> {
                    binding.expandContainer.setVisibility(View.GONE);
                    binding.viewExpandOverlay.setVisibility(View.GONE);
                })
                .start();

        binding.viewExpandOverlay.animate()
                .alpha(0f)
                .setDuration(200)
                .start();
    }

    private void setupClickListeners() {
        // Allow 添加按钮
        binding.ivAddAllow.setOnClickListener(v -> showAddTagDialog("Allow", allowTags, binding.chipGroupAllow));

        // Ban 添加按钮
        binding.ivAddBan.setOnClickListener(v -> showAddTagDialog("Ban", banTags, binding.chipGroupBanCreate));

        // Bubble Label 添加按钮
        binding.ivAddInputBubbleLabel.setOnClickListener(v -> showAddTagDialog("Bubble Label", bubbleLabelTags, binding.chipGroupBubbleLabel));

        // Create 按钮
        binding.btnCreate.setOnClickListener(v -> {
            if (validateForm()) {
                createBubble();
            }
        });
    }

    private boolean validateForm() {
        // 验证 Name 字段
        if (bubbleName.isEmpty()) {
            Toast.makeText(this, "请输入 Bubble 名称", Toast.LENGTH_SHORT).show();
            binding.etNameValue.requestFocus();
            return false;
        }

        if (bubbleName.length() > 20) {
            Toast.makeText(this, "Bubble 名称不能超过20个字符", Toast.LENGTH_SHORT).show();
            binding.etNameValue.requestFocus();
            return false;
        }

        return true;
    }

    private boolean isCreating = false; // 防重复点击

    private void createBubble() {
        if (isCreating) return;
        isCreating = true;
        binding.btnCreate.setEnabled(false);

        // 获取 token
        SharedPreferencesUtil spUtil = new SharedPreferencesUtil(this);
        String token = spUtil.getToken();
        if (token.isEmpty()) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            resetCreateButton();
            return;
        }

        // 构建请求体
        Map<String, Object> body = new HashMap<>();
        body.put("name", bubbleName);
        body.put("description", bubbleWallOriginalText);
        body.put("allowTags", allowTags);
        body.put("banTags", banTags);
        body.put("bubbleLabelTags", bubbleLabelTags);

        // 获取性别比例
        String genderRatio = binding.tvGenderValue.getText().toString();
        body.put("genderRatio", genderRatio);

        // 调用 API
        ApiService apiService = ApiClient.getApiService();
        apiService.createBubble("Bearer " + token, body).enqueue(new Callback<Result<Bubble>>() {
            @Override
            public void onResponse(Call<Result<Bubble>> call, Response<Result<Bubble>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 200) {
                    Toast.makeText(CreateActivity.this, "创建成功!", Toast.LENGTH_SHORT).show();
                    // 直接跳转到该 bubble 的聊天界面
                    Bubble createdBubble = response.body().getData();
                    if (createdBubble != null && createdBubble.getId() != null) {
                        Intent intent = new Intent(CreateActivity.this, ChatConcreteActivity.class);
                        intent.putExtra(ChatConcreteActivity.EXTRA_USER_ID, String.valueOf(createdBubble.getId()));
                        intent.putExtra(ChatConcreteActivity.EXTRA_USER_NAME, createdBubble.getName());
                        startActivity(intent);
                    }
                    finish();
                } else {
                    String msg = (response.body() != null) ? response.body().getMessage() : "创建失败";
                    Toast.makeText(CreateActivity.this, msg, Toast.LENGTH_SHORT).show();
                    resetCreateButton();
                }
            }

            @Override
            public void onFailure(Call<Result<Bubble>> call, Throwable t) {
                Log.e("CreateBubble", "创建失败", t);
                Toast.makeText(CreateActivity.this, "网络错误，请重试", Toast.LENGTH_SHORT).show();
                resetCreateButton();
            }
        });
    }

    private void resetCreateButton() {
        isCreating = false;
        binding.btnCreate.setEnabled(true);
    }

    public String getBubbleName() {
        return bubbleName;
    }

    private int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return (int) (dp * displayMetrics.density);
    }

    private void updateUnderlineWidth(Editable s) {
        // 获取输入框的右边界
        int editTextRight = binding.etNameValue.getRight();
        // 获取下划线的左边界
        int underlineLeft = binding.vNameUnderline.getLeft();
        // 最大宽度 = 输入框右边界 - 下划线左边界
        int maxWidth = editTextRight - underlineLeft;

        // 计算文字宽度
        float charWidth = binding.etNameValue.getPaint().measureText(s.toString());
        int minWidth = dpToPx(100); // 最小宽度100dp
        int neededWidth = (int) Math.max(minWidth, charWidth + dpToPx(10));

        // 取最小值：不超过输入框右边界
        int newWidth = Math.min(neededWidth, maxWidth);

        binding.vNameUnderline.getLayoutParams().width = newWidth;
        binding.vNameUnderline.requestLayout();
    }

    /**
     * 截断文本为预览格式：中文6字/英文4词 + "..."
     * 同时移除换行符，只保留第一行
     */
    private String truncateForPreview(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        // 先移除所有换行符，只保留第一行内容
        String singleLine = text.replaceAll("[\\r\\n]+", " ").trim();

        // 判断是中文还是英文为主
        int chineseCount = singleLine.replaceAll("[^\\u4e00-\\u9fa5]", "").length();
        int englishCount = singleLine.replaceAll("[^a-zA-Z]", "").length();

        String result;
        if (chineseCount >= englishCount) {
            // 中文为主：保留6个中文字符
            int chineseIndex = 0;
            int charCount = 0;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < singleLine.length(); i++) {
                char c = singleLine.charAt(i);
                if (c >= '\u4e00' && c <= '\u9fa5') {
                    chineseIndex++;
                }
                charCount++;
                sb.append(c);
                if (chineseIndex >= 10) {
                    break;
                }
            }
            if (chineseIndex >= 10 || charCount < singleLine.length()) {
                result = sb.toString() + "...";
            } else {
                result = singleLine;
            }
        } else {
            // 英文为主：保留4个单词
            String[] words = singleLine.trim().split("\\s+");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < Math.min(4, words.length); i++) {
                if (i > 0) sb.append(" ");
                sb.append(words[i]);
            }
            if (words.length > 4) {
                result = sb.toString() + "...";
            } else {
                result = singleLine;
            }
        }

        return result;
    }

    /**
     * 显示添加标签对话框
     */
    private void showAddTagDialog(String title, List<String> tagList, ChipGroup chipGroup) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("添加 " + title + " 标签");

        final android.widget.EditText input = new android.widget.EditText(this);
        input.setHint("请输入标签");
        android.widget.LinearLayout.LayoutParams lp = new android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(48, 16, 48, 16);
        input.setLayoutParams(lp);
        android.widget.LinearLayout container = new android.widget.LinearLayout(this);
        container.setOrientation(android.widget.LinearLayout.VERTICAL);
        container.addView(input);
        builder.setView(container);

        builder.setPositiveButton("确认", (dialog, which) -> {
            String tag = input.getText().toString().trim();
            if (!tag.isEmpty() && !tagList.contains(tag)) {
                tagList.add(tag);
                addChipToGroup(tag, tagList, chipGroup);
            } else if (tagList.contains(tag)) {
                Toast.makeText(this, "标签已存在", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    /**
     * 添加 Chip 到 ChipGroup
     */
    private void addChipToGroup(String tag, List<String> tagList, ChipGroup chipGroup) {
        Chip chip = new Chip(this);
        chip.setText(tag);
        chip.setTextSize(11);
        chip.setTextColor(0xFFFFFFFF);
        chip.setChipBackgroundColor(android.content.res.ColorStateList.valueOf(0xFF333333));
        boolean isAllow = chipGroup == binding.chipGroupAllow;
        boolean isBan = chipGroup == binding.chipGroupBanCreate;
        if (isAllow || isBan) {
            chip.setChipIconResource(isAllow ? R.drawable.ic_check_white : R.drawable.ic_square_wrong);
            chip.setChipIconTint(null);
            chip.setChipIconSize(isAllow ? 20 : 14);
        }

        // 找搭子风格间距
        chip.setChipStartPadding(4);
        chip.setChipEndPadding(4);
        chip.setTextStartPadding(2);
        chip.setTextEndPadding(2);
        chip.setIconStartPadding(1);
        chip.setChipMinHeight(24);

        chip.setOnClickListener(v -> {
            chipGroup.removeView(chip);
            tagList.remove(tag);
        });
        chipGroup.addView(chip);
    }
}
