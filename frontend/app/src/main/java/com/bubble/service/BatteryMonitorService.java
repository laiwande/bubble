package com.bubble.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import appcompat.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.bubble.R;
import com.bubble.receiver.BatteryReceiver;

/**
 * 电量监控服务（前台服务）
 * 
 * 功能：
 * 1. 前台运行，显示通知栏"电量监控中..."
 * 2. 持续监听电池变化（低电量、充电状态等）
 * 3. 通过 BatteryReceiver 处理具体事件
 * 4. 支持由 Activity 启动/停止
 */
public class BatteryMonitorService extends Service {

    private static final String TAG = "BatteryMonitorService";
    private static final String CHANNEL_ID = "battery_monitor_channel";
    private static final int NOTIFICATION_ID = 1001;
    private static final String ACTION_STOP = "com.bubble.action.STOP_BATTERY_MONITOR";

    private BatteryReceiver batteryReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: 电量监控服务创建");

        // 创建通知渠道（Android 8.0+）
        createNotificationChannel();

        // 启动前台服务
        startForeground(NOTIFICATION_ID, createNotification());

        // 注册电量广播接收器
        registerBatteryReceiver();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: 服务启动");

        // 检查是否为停止命令
        if (intent != null && ACTION_STOP.equals(intent.getAction())) {
            Log.d(TAG, "收到停止命令，停止服务");
            stopSelf();
            return START_NOT_STICKY;
        }

        // 服务被杀死后自动重启
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // 不需要绑定
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: 电量监控服务销毁");

        // 注销广播接收器
        unregisterBatteryReceiver();
        
        // 取消前台服务通知
        stopForeground(true);
        stopSelf();
    }

    /**
     * 创建通知渠道（Android 8.0+ 必须）
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "电量监控",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("持续监控设备电量和充电状态");
            channel.setShowBadge(false);

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    /**
     * 创建前台服务通知
     */
    private Notification createNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("电量监控中")
                .setContentText("正在后台监控设备电量状态...")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setSilent(true);  // 静音，不发出提示音

        return builder.build();
    }

    /**
     * 注册电量广播接收器
     */
    private void registerBatteryReceiver() {
        if (batteryReceiver == null) {
            batteryReceiver = new BatteryReceiver();
        }
        
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_LOW);
        filter.addAction(Intent.ACTION_BATTERY_OKAY);
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        
        registerReceiver(batteryReceiver, filter);
        Log.d(TAG, "已注册电量广播接收器");
    }

    /**
     * 注销电量广播接收器
     */
    private void unregisterBatteryReceiver() {
        if (batteryReceiver != null) {
            try {
                unregisterReceiver(batteryReceiver);
                batteryReceiver = null;
                Log.d(TAG, "已注销电量广播接收器");
            } catch (Exception e) {
                Log.e(TAG, "注销广播接收器失败: " + e.getMessage());
            }
        }
    }
}
