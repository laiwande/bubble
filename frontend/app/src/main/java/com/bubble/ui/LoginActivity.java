package com.bubble.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.bubble.databinding.ActivityLoginBinding;
import com.bubble.model.Result;
import com.bubble.network.ApiClient;
import com.bubble.network.ApiService;
import com.bubble.utils.SharedPreferencesUtil;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private SharedPreferencesUtil sharedPreferencesUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPreferencesUtil = new SharedPreferencesUtil(this);

        binding.btnLogin.setOnClickListener(v -> login());
        binding.tvRegisterLink.setOnClickListener(v -> register());
    }

    private void login() {
        String username = binding.etUsername.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> loginData = new HashMap<>();
        loginData.put("username", username);
        loginData.put("password", password);

        ApiService apiService = ApiClient.getApiService();
        apiService.login(loginData).enqueue(new Callback<Result<Map<String, String>>>() {
            @Override
            public void onResponse(Call<Result<Map<String, String>>> call, Response<Result<Map<String, String>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Result<Map<String, String>> result = response.body();
                    if (result.getCode() == 200) {
                        String token = result.getData().get("token");
                        sharedPreferencesUtil.saveToken(token);
                        Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Result<Map<String, String>>> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void register() {
        String username = binding.etUsername.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> registerData = new HashMap<>();
        registerData.put("username", username);
        registerData.put("password", password);
        registerData.put("nickname", username);

        ApiService apiService = ApiClient.getApiService();
        apiService.register(registerData).enqueue(new Callback<Result<Void>>() {
            @Override
            public void onResponse(Call<Result<Void>> call, Response<Result<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Result<Void> result = response.body();
                    if (result.getCode() == 200) {
                        Toast.makeText(LoginActivity.this, "注册成功，请登录", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Result<Void>> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
