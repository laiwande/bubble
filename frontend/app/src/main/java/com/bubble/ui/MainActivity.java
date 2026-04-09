package com.bubble.ui;

import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity {

    private TextView tvHello;
    private SharedPreferencesUtil sharedPreferencesUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferencesUtil = new SharedPreferencesUtil(this);

        if (!sharedPreferencesUtil.isLoggedIn()) {
            finish();
            return;
        }

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
                    Toast.makeText(MainActivity.this, "Request failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<com.bubble.model.Result<User>> call, Throwable t) {
                tvHello.setText("Connection Failed");
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
