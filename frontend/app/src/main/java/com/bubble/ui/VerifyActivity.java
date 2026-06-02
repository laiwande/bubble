package com.bubble.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.bubble.R;
import com.bubble.databinding.ActivityVerifyBinding;
import com.bubble.model.Result;
import com.bubble.network.ApiClient;
import com.bubble.network.ApiService;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyActivity extends AppCompatActivity {

    private ActivityVerifyBinding binding;

    private String email;
    private String password;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVerifyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 从Intent获取待注册信息
        email = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");
        username = getIntent().getStringExtra("username");

        binding.ivBack.setOnClickListener(v -> finish());

        binding.btnVerify.setOnClickListener(v -> verify());
    }

    private void verify() {
        String code = binding.etVerificationCode.getText().toString().trim();
        if (code.isEmpty()) {
            Toast.makeText(this, "请输入验证码", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> registerData = new HashMap<>();
        registerData.put("email", email);
        registerData.put("password", password);
        registerData.put("username", username);
        registerData.put("verificationCode", code);

        ApiService apiService = ApiClient.getApiService();
        apiService.register(registerData).enqueue(new Callback<Result<Void>>() {
            @Override
            public void onResponse(Call<Result<Void>> call, Response<Result<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Result<Void> result = response.body();
                    if (result.getCode() == 200) {
                        Toast.makeText(VerifyActivity.this, "注册成功，请登录", Toast.LENGTH_SHORT).show();
                        // 跳转到登录界面（登录模式）
                        Intent intent = new Intent(VerifyActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(VerifyActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(VerifyActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Result<Void>> call, Throwable t) {
                Toast.makeText(VerifyActivity.this, "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
