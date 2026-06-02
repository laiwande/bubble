package com.bubble.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.bubble.R;
import com.bubble.model.User;
import com.bubble.network.ApiClient;
import com.bubble.network.ApiService;
import com.bubble.service.BatteryMonitorService;
import com.bubble.utils.AvatarUtils;
import com.bubble.utils.SharedPreferencesUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MeFragment extends Fragment {

    private TextView tvHello;
    private ImageView ivAvatar;
    private SharedPreferencesUtil sharedPreferencesUtil;
    private Switch switchBatteryMonitor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_me, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvHello = view.findViewById(R.id.tv_hello);
        ivAvatar = view.findViewById(R.id.iv_avatar_me);
        switchBatteryMonitor = view.findViewById(R.id.switch_battery_monitor);
        sharedPreferencesUtil = new SharedPreferencesUtil(requireContext());
        
        loadUserInfo();
        loadUserAvatar();
        setupBatteryMonitorSwitch();
    }

    private void loadUserInfo() {
        String token = sharedPreferencesUtil.getToken();
        ApiService apiService = ApiClient.getApiService();

        apiService.getUserInfo("Bearer " + token).enqueue(new Callback<com.bubble.model.Result<User>>() {
            @Override
            public void onResponse(Call<com.bubble.model.Result<User>> call, Response<com.bubble.model.Result<User>> response) {
                if (!isAdded() || tvHello == null) return;
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
                if (!isAdded() || tvHello == null) return;
                tvHello.setText("Connection Failed");
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 加载用户头像（使用 DiceBear 生成可爱卡通头像）
     */
    private void loadUserAvatar() {
        if (ivAvatar == null || getActivity() == null) return;
        
        Long userId = sharedPreferencesUtil.getUserId();
        String avatarSeed = (userId != null) ? String.valueOf(userId) : String.valueOf(System.currentTimeMillis());
        
        Glide.with(requireContext())
                .load(AvatarUtils.getAvatarUrl(avatarSeed))
                .placeholder(R.drawable.ic_me_user)
                .error(R.drawable.ic_me_user)
                .circleCrop()
                .into(ivAvatar);
    }

    /**
     * 设置电量监控开关
     */
    private void setupBatteryMonitorSwitch() {
        if (switchBatteryMonitor == null || getContext() == null) return;

        // 检查通知权限（Android 13+）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!NotificationManagerCompat.from(requireContext()).areNotificationsEnabled()) {
                switchBatteryMonitor.setEnabled(false);
                switchBatteryMonitor.setText(" (需开启通知权限)");
                return;
            }
        }

        // 恢复之前的状态
        boolean isServiceRunning = isBatteryServiceRunning();
        switchBatteryMonitor.setChecked(isServiceRunning);

        // 监听开关状态变化
        switchBatteryMonitor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startBatteryService();
                } else {
                    stopBatteryService();
                }
            }
        });
    }

    /**
     * 启动电量监控服务
     */
    private void startBatteryService() {
        if (getContext() == null) return;
        
        Intent serviceIntent = new Intent(getContext(), BatteryMonitorService.class);
        
        // Android 8.0+ 需要前台服务
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getContext().startForegroundService(serviceIntent);
        } else {
            getContext().startService(serviceIntent);
        }
        
        Toast.makeText(getContext(), "🔋 电量监控已启动", Toast.LENGTH_SHORT).show();
    }

    /**
     * 停止电量监控服务
     */
    private void stopBatteryService() {
        if (getContext() == null) return;
        
        Intent serviceIntent = new Intent(getContext(), BatteryMonitorService.class);
        getContext().stopService(serviceIntent);
        
        Toast.makeText(getContext(), "⏹️ 电量监控已停止", Toast.LENGTH_SHORT).show();
    }

    /**
     * 检查电量监控服务是否正在运行
     */
    private boolean isBatteryServiceRunning() {
        if (getContext() == null) false;
        
        try {
            Context context = requireContext();
            android.app.ActivityManager activityManager = 
                    (android.app.ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            
            for (android.app.ActivityManager.RunningServiceInfo service : 
                 activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if (BatteryMonitorService.class.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        } catch (Exception e) {
            // 忽略异常
        }
        return false;
    }
}
