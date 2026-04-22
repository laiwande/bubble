package com.bubble.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.bubble.R;
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

    private boolean isRegisterMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPreferencesUtil = new SharedPreferencesUtil(this);

        binding.btnLogin.setOnClickListener(v -> {
            if (isRegisterMode) {
                register();
            } else {
                login();
            }
        });
        binding.tvRegisterLink.setOnClickListener(v -> toggleMode());

        // Bottom navigation click listeners
        binding.bottomNav.findViewById(R.id.tab_bubble).setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, BubbleActivity.class));
        });

        binding.bottomNav.findViewById(R.id.tab_square).setOnClickListener(v -> {
            // Square tab - to be implemented
        });

        binding.bottomNav.findViewById(R.id.tab_add).setOnClickListener(v -> {
            // Add tab - to be implemented
        });

        binding.bottomNav.findViewById(R.id.tab_chat).setOnClickListener(v -> {
            // Chat tab - to be implemented
        });

        binding.bottomNav.findViewById(R.id.tab_me).setOnClickListener(v -> {
            // Me tab - to be implemented
        });
    }

    private void toggleMode() {
        isRegisterMode = !isRegisterMode;
        if (isRegisterMode) {
            binding.tilUsername.setVisibility(View.VISIBLE);
            binding.btnLogin.setText("Register");
            binding.tvRegisterPrefix.setText("已有帐户？ ");
            binding.tvRegisterLink.setText("登录");
        } else {
            binding.tilUsername.setVisibility(View.GONE);
            binding.btnLogin.setText("Login");
            binding.tvRegisterPrefix.setText("没有帐户？ ");
            binding.tvRegisterLink.setText("注册");
        }
    }

    private void login() {
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "请输入邮箱和密码", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> loginData = new HashMap<>();
        loginData.put("email", email);
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
                        startActivity(new Intent(LoginActivity.this, BubbleActivity.class));
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
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        String username = binding.etUsername.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
            Toast.makeText(this, "请输入邮箱、用户名和密码", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> registerData = new HashMap<>();
        registerData.put("email", email);
        registerData.put("password", password);
        registerData.put("username", username);

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
