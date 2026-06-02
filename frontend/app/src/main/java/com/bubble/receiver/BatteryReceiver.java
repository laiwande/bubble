package com.bubble.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.widget.Toast;

/**
 * 电量广播接收器
 * 监听电量变化、充电状态等事件
 */
public class BatteryReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (Intent.ACTION_BATTERY_LOW.equals(action)) {
            // 电量低警告
            Toast.makeText(context, "⚠️ 电量不足，请尽快充电！", Toast.LENGTH_LONG).show();
            
        } else if (Intent.ACTION_BATTERY_OKAY.equals(action)) {
            // 电量恢复
            Toast.makeText(context, "✅ 电量已恢复正常", Toast.LENGTH_SHORT).show();
            
        } else if (Intent.ACTION_POWER_CONNECTED.equals(action)) {
            // 开始充电
            Toast.makeText(context, "🔌 已连接充电器", Toast.LENGTH_SHORT).show();
            
        } else if (Intent.ACTION_POWER_DISCONNECTED.equals(action)) {
            // 断开充电，显示当前电量
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            Toast.makeText(context, "🔋 当前电量: " + level + "%", Toast.LENGTH_SHORT).show();
        }
    }
}
