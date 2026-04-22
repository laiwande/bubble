package com.bubble.ui;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bubble.R;

public class BottomNavigationView extends LinearLayout {

    private static final int COLOR_INACTIVE = Color.parseColor("#AEAEB5");
    private static final int COLOR_ACTIVE = Color.parseColor("#1A1A1A");

    private LinearLayout tabBubble, tabSquare, tabAdd, tabChat, tabMe;
    private TextView tvTabBubble, tvTabSquare, tvTabChat, tvTabMe;
    private ImageView ivTabBubble, ivTabSquare, ivTabAdd, ivTabChat, ivTabMe;
    private BottomNavigationListener listener;

    public BottomNavigationView(Context context) {
        super(context);
        init(context);
    }

    public BottomNavigationView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BottomNavigationView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.bottom_navigation, this, true);

        // Tab containers
        tabBubble = findViewById(R.id.tab_bubble);
        tabSquare = findViewById(R.id.tab_square);
        tabAdd = findViewById(R.id.tab_add);
        tabChat = findViewById(R.id.tab_chat);
        tabMe = findViewById(R.id.tab_me);

        // Icons
        ivTabBubble = findViewById(R.id.iv_tab_bubble);
        ivTabSquare = findViewById(R.id.iv_tab_square);
        ivTabAdd = findViewById(R.id.iv_tab_add);
        ivTabChat = findViewById(R.id.iv_tab_chat);
        ivTabMe = findViewById(R.id.iv_tab_me);

        // Text labels
        tvTabBubble = findViewById(R.id.tv_tab_bubble);
        tvTabSquare = findViewById(R.id.tv_tab_square);
        tvTabChat = findViewById(R.id.tv_tab_chat);
        tvTabMe = findViewById(R.id.tv_tab_me);

        setupClickListeners();
    }

    private void setupClickListeners() {
        tabBubble.setOnClickListener(v -> {
            setActiveTab(tabBubble);
            if (listener != null) listener.onBubbleTabClicked();
        });

        tabSquare.setOnClickListener(v -> {
            setActiveTab(tabSquare);
            if (listener != null) listener.onSquareTabClicked();
        });

        tabAdd.setOnClickListener(v -> {
            // + button does not highlight like other tabs
            if (listener != null) listener.onAddTabClicked();
        });

        tabChat.setOnClickListener(v -> {
            setActiveTab(tabChat);
            if (listener != null) listener.onChatTabClicked();
        });

        tabMe.setOnClickListener(v -> {
            setActiveTab(tabMe);
            if (listener != null) listener.onMeTabClicked();
        });
    }

    /**
     * Set active tab by resource id.
     */
    public void setActiveTab(int tabId) {
        LinearLayout target = null;
        if (tabId == R.id.tab_bubble) target = tabBubble;
        else if (tabId == R.id.tab_square) target = tabSquare;
        else if (tabId == R.id.tab_chat) target = tabChat;
        else if (tabId == R.id.tab_me) target = tabMe;
        if (target != null) setActiveTab(target);
    }

    /**
     * Set a specific tab as active. Resets all others to inactive.
     */
    public void setActiveTab(LinearLayout activeTab) {
        resetAllTabs();

        TextView activeText = getTextForTab(activeTab);
        if (activeText != null) {
            activeText.setTextColor(COLOR_ACTIVE);
        }
    }

    private void resetAllTabs() {
        tvTabBubble.setTextColor(COLOR_INACTIVE);
        tvTabSquare.setTextColor(COLOR_INACTIVE);
        tvTabChat.setTextColor(COLOR_INACTIVE);
        tvTabMe.setTextColor(COLOR_INACTIVE);
    }

    private TextView getTextForTab(LinearLayout tab) {
        if (tab == tabBubble) return tvTabBubble;
        if (tab == tabSquare) return tvTabSquare;
        if (tab == tabChat) return tvTabChat;
        if (tab == tabMe) return tvTabMe;
        return null;
    }

    public void setListener(BottomNavigationListener listener) {
        this.listener = listener;
    }

    /** Callback interface for bottom navigation events. */
    public interface BottomNavigationListener {
        void onBubbleTabClicked();
        void onSquareTabClicked();
        void onAddTabClicked();
        void onChatTabClicked();
        void onMeTabClicked();
    }
}
