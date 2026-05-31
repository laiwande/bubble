package com.bubble.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bubble.R;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ShopActivity extends AppCompatActivity {

    private ImageView ivBack;
    private TextView tvCoins;
    private ImageView ivCardPreviewBg;
    private ImageView ivCardPreviewSymbol;
    private View flCardPreview;
    private ImageView ivRefresh;
    private View resultCardOverlay;

    /** 10个符号图片资源ID */
    private static final int[] SYMBOL_RES = {
        R.drawable.symbol_01, R.drawable.symbol_02, R.drawable.symbol_03,
        R.drawable.symbol_04, R.drawable.symbol_05, R.drawable.symbol_06,
        R.drawable.symbol_07, R.drawable.symbol_08, R.drawable.symbol_09,
        R.drawable.symbol_10
    };

    private final int[] CARD_IDS = {
        R.id.card_01, R.id.card_02, R.id.card_03,
        R.id.card_04, R.id.card_05, R.id.card_06,
        R.id.card_07, R.id.card_08, R.id.card_09,
        R.id.card_10
    };

    private final Random random = new Random();

    /** 缓存已处理好的抠图 Bitmap，避免重复处理 */
    private final Map<Integer, Bitmap> goldLinesCache = new HashMap<>();

    /** 当前选中的卡片 View */
    private View selectedCard = null;
    /** 当前选中卡片的符号资源 */
    private int selectedSymbolRes = 0;
    /** 当前金币余额 */
    private int currentCoins = 15300;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        initViews();
        initCards();
        initListeners();
    }

    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
        tvCoins = findViewById(R.id.tv_coins);
        flCardPreview = findViewById(R.id.fl_card_preview);
        ivCardPreviewBg = findViewById(R.id.iv_card_preview_bg);
        ivCardPreviewSymbol = findViewById(R.id.iv_card_preview_symbol);
        ivRefresh = findViewById(R.id.iv_refresh);
        resultCardOverlay = findViewById(R.id.result_card_overlay);

        // 初始化金币显示
        tvCoins.setText(String.valueOf(currentCoins));

        // 点击分享按钮跳转到 CreateActivity
        ImageView ivShare = resultCardOverlay.findViewById(R.id.iv_share);
        if (ivShare != null) {
            ivShare.setOnClickListener(v -> {
                Intent intent = new Intent(this, CreateActivity.class);
                startActivity(intent);
            });
        }

        // 点击结果卡片区域关闭
        resultCardOverlay.setOnClickListener(v -> hideResultCard());
    }

    private void hideResultCard() {
        resultCardOverlay.setVisibility(View.GONE);
        flCardPreview.setVisibility(View.VISIBLE);
        // 恢复问号背景
        ivCardPreviewBg.setImageResource(R.drawable.card_lottery_bg);
    }

    /** 价格档位（10张卡片从左到右递增） */
    private static final int[] PRICES = {200, 400, 600, 800, 1000, 1200, 1400, 1600, 1800, 2000};
    private static final float SELECTED_OFFSET_DP = -40f;

    /** 初始化10张卡片：随机绑定符号图案 + 随机价格 + 点击事件 */
    private void initCards() {
        for (int i = 0; i < CARD_IDS.length; i++) {
            View card = findViewById(CARD_IDS[i]);
            if (card == null) continue;

            ImageView ivSymbol = card.findViewById(R.id.iv_symbol);
            int symbolIndex = random.nextInt(SYMBOL_RES.length);
            if (ivSymbol != null) {
                Bitmap processed = getGoldLinesBitmap(SYMBOL_RES[symbolIndex]);
                ivSymbol.setImageBitmap(processed);
            }

            // 设置价格：从左到右递增
            TextView tvPrice = card.findViewById(R.id.tv_card_price);
            int price = PRICES[i % PRICES.length];
            if (tvPrice != null) {
                tvPrice.setText(String.valueOf(price));
            }

            final View targetCard = card;
            final int symRes = SYMBOL_RES[symbolIndex];
            final int cardPrice = price;
            card.setOnClickListener(v -> onCardClick(targetCard, symRes, cardPrice));
        }
    }

    /**
     * 加载 symbol 图片（暂不抠图，直接显示原图以确认资源正常）
     */
    private Bitmap getGoldLinesBitmap(int resId) {
        Bitmap cached = goldLinesCache.get(resId);
        if (cached != null) return cached;

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap original = BitmapFactory.decodeResource(getResources(), resId, opts);
        if (original == null) return null;

        goldLinesCache.put(resId, original);
        return original;
    }

    private void initListeners() {
        ivBack.setOnClickListener(v -> finish());
        ivRefresh.setOnClickListener(v -> onRefresh());
    }

    private void onRefresh() {
        // 复位选中状态
        if (selectedCard != null) {
            selectedCard.animate().translationY(0f).setDuration(150).start();
            selectedCard = null;
        }
        hideResultCard();
        // 恢复问号背景
        ivCardPreviewBg.setImageResource(R.drawable.card_lottery_bg);
        initCards();
        Toast.makeText(this, "已刷新", Toast.LENGTH_SHORT).show();
    }

    private void onCardClick(View card, int symbolRes, int price) {
        float offsetPx = SELECTED_OFFSET_DP * getResources().getDisplayMetrics().density;

        if (selectedCard == card) {
            // 第二次点击同一张卡片 → 扣减金币并展示结果卡片
            if (currentCoins < price) {
                Toast.makeText(this, "金币不足！", Toast.LENGTH_SHORT).show();
                return;
            }
            currentCoins -= price;
            tvCoins.setText(String.valueOf(currentCoins));
            showResultCard();
            return;
        }

        // 复位之前选中的卡片
        if (selectedCard != null) {
            selectedCard.animate().translationY(0f).setDuration(150).start();
        }

        // 选中新卡片：上移
        selectedCard = card;
        selectedSymbolRes = symbolRes;
        card.animate().translationY(offsetPx).setDuration(200).start();

        // 首次点击：中间预览卡片保持问号 bg，不换底图
        ivCardPreviewBg.setImageResource(R.drawable.card_lottery_bg);
        ivCardPreviewSymbol.setVisibility(View.GONE);
    }

    private void showResultCard() {
        flCardPreview.setVisibility(View.GONE);
        resultCardOverlay.setVisibility(View.VISIBLE);
        resultCardOverlay.setAlpha(0f);
        resultCardOverlay.animate().alpha(1f).setDuration(300).start();

        // 生成随机编号
        TextView tvNumber = resultCardOverlay.findViewById(R.id.tv_card_number);
        if (tvNumber != null) {
            int num = 1000000 + random.nextInt(999999);
            tvNumber.setText("No. " + num);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (Bitmap bitmap : goldLinesCache.values()) {
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
        goldLinesCache.clear();
    }
}
