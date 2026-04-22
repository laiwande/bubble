package com.bubble.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.bubble.R;
import com.bubble.model.User;
import com.bubble.network.ApiClient;
import com.bubble.network.ApiService;
import com.bubble.utils.SharedPreferencesUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MeActivity extends AppCompatActivity implements BottomNavigationView.BottomNavigationListener {

    private TextView tvHello;
    private SharedPreferencesUtil sharedPreferencesUtil;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me);

        sharedPreferencesUtil = new SharedPreferencesUtil(this);

        if (!sharedPreferencesUtil.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Initialize bottom navigation
        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setListener(this);
        bottomNavigationView.setActiveTab(R.id.tab_me);

        tvHello = findViewById(R.id.tv_hello);

        loadUserInfo();
    }

    private void loadUserInfo() {
        String token = sharedPreferencesUtil.getToken();
        ApiService apiService = ApiClient.getApiService();

        apiService.getUserInfo("Bearer " + token).enqueue(new Callback<com.bubble.model.Result<User>>() {
            @Override
            public void onResponse(Call<com.bubble.model.Result<User>> call, Response<com.bubble.model.Result<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    com.bubble.model.Result<User> result = response.body();
                    if (result.getCode() == 200) {
                        User user = result.getData();
                        tvHello.setText("Welcome, " + user.getNickname() + "!");
                    } else {
                        tvHello.setText("Error: " + result.getMessage());
                    }
                } else {
                    tvHello.setText("Network Error");
                    Toast.makeText(MeActivity.this, "Request failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<com.bubble.model.Result<User>> call, Throwable t) {
                tvHello.setText("Connection Failed");
                Toast.makeText(MeActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBubbleTabClicked() {
        startActivity(new Intent(MeActivity.this, BubbleActivity.class));
        finish();
    }

    @Override
    public void onSquareTabClicked() {
        Toast.makeText(MeActivity.this, "广场功能即将推出", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onChatTabClicked() {
        Toast.makeText(MeActivity.this, "聊天功能即将推出", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAddTabClicked() {
        Toast.makeText(MeActivity.this, "发布功能即将推出", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMeTabClicked() {
        // Already on me page, do nothing
    }
}