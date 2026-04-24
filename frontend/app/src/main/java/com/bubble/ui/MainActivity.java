package com.bubble.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.bubble.R;
import com.bubble.utils.SharedPreferencesUtil;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.BottomNavigationListener {

    private BottomNavigationView bottomNavigationView;

    // 5个Fragment实例
    private BubbleFragment bubbleFragment;
    private SquareFragment squareFragment;
    private AddFragment addFragment;
    private ChatFragment chatFragment;
    private MeFragment meFragment;

    // 当前显示的Fragment
    private Fragment currentFragment;

    // Tab ID → Fragment 映射
    private Map<Integer, Fragment> fragmentMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(this);

        // 检查登录状态
        if (!sharedPreferencesUtil.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // 初始化Fragment
        bubbleFragment = new BubbleFragment();
        squareFragment = new SquareFragment();
        addFragment = new AddFragment();
        chatFragment = new ChatFragment();
        meFragment = new MeFragment();

        fragmentMap.put(R.id.tab_bubble, bubbleFragment);
        fragmentMap.put(R.id.tab_square, squareFragment);
        fragmentMap.put(R.id.tab_add, addFragment);
        fragmentMap.put(R.id.tab_chat, chatFragment);
        fragmentMap.put(R.id.tab_me, meFragment);

        // 初始化底部导航
        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setListener(this);

        // 默认显示Bubble页面
        switchFragment(R.id.tab_bubble);
    }

    private void switchFragment(int tabId) {
        Fragment target = fragmentMap.get(tabId);
        if (target == null || target == currentFragment) return;

        var transaction = getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true);

        // 首次切换：需要先将所有 Fragment add 到容器
        if (currentFragment == null) {
            transaction.add(R.id.fragment_container, bubbleFragment);
            transaction.add(R.id.fragment_container, squareFragment);
            transaction.add(R.id.fragment_container, addFragment);
            transaction.add(R.id.fragment_container, chatFragment);
            transaction.add(R.id.fragment_container, meFragment);

            // 默认全部隐藏
            transaction.hide(bubbleFragment);
            transaction.hide(squareFragment);
            transaction.hide(addFragment);
            transaction.hide(chatFragment);
            transaction.hide(meFragment);
        } else {
            // 后续切换：只隐藏当前
            transaction.hide(currentFragment);
        }

        transaction.show(target).commit();
        currentFragment = target;
        bottomNavigationView.setActiveTab(tabId);
    }

    @Override
    public void onBubbleTabClicked() {
        switchFragment(R.id.tab_bubble);
    }

    @Override
    public void onSquareTabClicked() {
        switchFragment(R.id.tab_square);
    }

    @Override
    public void onAddTabClicked() {
        switchFragment(R.id.tab_add);
    }

    @Override
    public void onChatTabClicked() {
        switchFragment(R.id.tab_chat);
    }

    @Override
    public void onMeTabClicked() {
        switchFragment(R.id.tab_me);
    }
}
