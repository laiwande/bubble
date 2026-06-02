package com.bubble.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bubble.R;

public class ChatConcreteActivity extends AppCompatActivity {

    private ImageView btnBack;
    private ImageView btnSetting;
    private TextView tvChatName;
    private EditText etMessageInput;
    private ImageView btnSend;
    private ImageView btnPlus;
    private ImageView ivCamera;

    private String chatUserId;
    private String chatUserName;

    public static final String EXTRA_USER_ID = "chat_user_id";
    public static final String EXTRA_USER_NAME = "chat_user_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_concrete_chat);

        chatUserId = getIntent().getStringExtra(EXTRA_USER_ID);
        chatUserName = getIntent().getStringExtra(EXTRA_USER_NAME);

        initViews();
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        btnSetting = findViewById(R.id.btn_setting);
        tvChatName = findViewById(R.id.tv_chat_name);
        etMessageInput = findViewById(R.id.et_message_input);
        btnSend = findViewById(R.id.btn_send);
        btnPlus = findViewById(R.id.btn_plus);
        ivCamera = findViewById(R.id.iv_camera);

        // 设置对方昵称
        if (chatUserName != null) {
            tvChatName.setText(chatUserName);
        }
    }

    private void setupListeners() {
        // 返回按钮
        btnBack.setOnClickListener(v -> finish());

        // 设置按钮（TODO: 后续实现）
        btnSetting.setOnClickListener(v ->
            Toast.makeText(this, "设置", Toast.LENGTH_SHORT).show()
        );

        // 发送按钮
        btnSend.setOnClickListener(v -> {
            String message = etMessageInput.getText().toString().trim();
            if (message.isEmpty()) {
                Toast.makeText(this, "不能发送空消息", Toast.LENGTH_SHORT).show();
                return;
            }
            sendMessage(message);
            etMessageInput.setText("");
        });

        // 加号按钮 - 切换展开/收起面板
        btnPlus.setOnClickListener(v -> {
            LinearLayout panelMore = findViewById(R.id.panel_more);
            if (panelMore.getVisibility() == View.GONE) {
                panelMore.setVisibility(View.VISIBLE);
            } else {
                panelMore.setVisibility(View.GONE);
            }
        });

        // 相机图标（TODO: 后续实现发送图片）
        ivCamera.setOnClickListener(v ->
            Toast.makeText(this, "发送图片", Toast.LENGTH_SHORT).show()
        );
    }

    private void sendMessage(String content) {
        // TODO: 调用后端API发送消息
        Toast.makeText(this, "发送: " + content, Toast.LENGTH_SHORT).show();
    }
}
