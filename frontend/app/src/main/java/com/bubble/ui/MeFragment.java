package com.bubble.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bubble.R;
import com.bubble.model.User;
import com.bubble.network.ApiClient;
import com.bubble.network.ApiService;
import com.bubble.utils.SharedPreferencesUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MeFragment extends Fragment {

    private TextView tvHello;
    private SharedPreferencesUtil sharedPreferencesUtil;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_me, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvHello = view.findViewById(R.id.tv_hello);
        sharedPreferencesUtil = new SharedPreferencesUtil(requireContext());
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
                    Toast.makeText(getContext(), "Request failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<com.bubble.model.Result<User>> call, Throwable t) {
                tvHello.setText("Connection Failed");
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
