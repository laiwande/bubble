package com.bubble.ui;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bubble.R;
import com.bubble.model.Conversation;
import com.bubble.model.Message;
import com.bubble.model.PageData;
import com.bubble.model.Result;
import com.bubble.network.ApiClient;
import com.bubble.network.ApiService;
import com.bubble.ui.adapter.ChatMessageAdapter;
import com.bubble.utils.SharedPreferencesUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatConcreteActivity extends AppCompatActivity {

    private ImageView btnBack;
    private ImageView btnSetting;
    private TextView tvChatName;
    private EditText etMessageInput;
    private ImageView btnSend;
    private ImageView btnPlus;
    private ImageView ivCamera;
    private RecyclerView rvMessages;
    private LinearLayout panelMore;

    private Long bubbleId;
    private String bubbleName;
    private Long conversationId;
    private Long currentUserId;

    private ChatMessageAdapter adapter;
    private SharedPreferencesUtil spUtil;
    private ApiService apiService;

    public static final String EXTRA_USER_ID = "chat_user_id";
    public static final String EXTRA_USER_NAME = "chat_user_name";

    private static final String TAG = "ChatConcrete";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_concrete_chat);

        String bubbleIdStr = getIntent().getStringExtra(EXTRA_USER_ID);
        if (bubbleIdStr != null) {
            bubbleId = Long.parseLong(bubbleIdStr);
        }
        bubbleName = getIntent().getStringExtra(EXTRA_USER_NAME);

        spUtil = new SharedPreferencesUtil(this);
        currentUserId = spUtil.getUserId();
        // 如果 userId 是默认值 -1，尝试从 JWT token 中提取
        if (currentUserId == -1L) {
            currentUserId = extractUserIdFromToken(spUtil.getToken());
            if (currentUserId != -1L) {
                spUtil.saveUserId(currentUserId);
                Log.d(TAG, "userId extracted from JWT: " + currentUserId);
            }
        }
        apiService = ApiClient.getApiService();

        initViews();
        setupListeners();
        setupRecyclerView();
        loadConversationAndMessages();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        btnSetting = findViewById(R.id.btn_setting);
        tvChatName = findViewById(R.id.tv_chat_name);
        etMessageInput = findViewById(R.id.et_message_input);
        btnSend = findViewById(R.id.btn_send);
        btnPlus = findViewById(R.id.btn_plus);
        ivCamera = findViewById(R.id.iv_camera);
        rvMessages = findViewById(R.id.rv_messages);
        panelMore = findViewById(R.id.panel_more);

        if (bubbleName != null) {
            tvChatName.setText(bubbleName);
        }
    }

    private void setupRecyclerView() {
        rvMessages.setLayoutManager(new LinearLayoutManager(this));
        rvMessages.setBackgroundColor(android.graphics.Color.TRANSPARENT);
        rvMessages.setHasFixedSize(true);
        rvMessages.setOverScrollMode(View.OVER_SCROLL_NEVER);

        adapter = new ChatMessageAdapter();
        adapter.setCurrentUserId(currentUserId);
        rvMessages.setAdapter(adapter);
    }

    private void loadConversationAndMessages() {
        if (bubbleId == null) return;

        String token = spUtil.getToken();
        apiService.getBubbleConversation("Bearer " + token, bubbleId).enqueue(new Callback<Result<Conversation>>() {
            @Override
            public void onResponse(Call<Result<Conversation>> call, Response<Result<Conversation>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 200) {
                    Conversation conversation = response.body().getData();
                    conversationId = conversation.getId();
                    loadMessages();
                } else {
                    Log.e(TAG, "获取会话失败");
                    Toast.makeText(ChatConcreteActivity.this, "加载聊天失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Result<Conversation>> call, Throwable t) {
                Log.e(TAG, "网络错误", t);
                Toast.makeText(ChatConcreteActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadMessages() {
        if (conversationId == null) return;

        String token = spUtil.getToken();
        apiService.getMessageList("Bearer " + token, conversationId, 1, 200)
                .enqueue(new Callback<Result<PageData<Message>>>() {
            @Override
            public void onResponse(Call<Result<PageData<Message>>> call, Response<Result<PageData<Message>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 200) {
                    PageData<Message> pageData = response.body().getData();
                    if (pageData != null) {
                        List<Message> msgList = pageData.getRecords();
                        // 后端按 create_time DESC 排序，需要反转成升序
                        java.util.Collections.reverse(msgList);
                        adapter.setMessages(msgList);
                        scrollToBottom();
                    }
                }
            }

            @Override
            public void onFailure(Call<Result<PageData<Message>>> call, Throwable t) {
                Log.e(TAG, "加载消息失败", t);
            }
        });
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> navigateToChatTab());

        btnSetting.setOnClickListener(v ->
                Toast.makeText(this, "设置", Toast.LENGTH_SHORT).show()
        );

        btnSend.setOnClickListener(v -> {
            String content = etMessageInput.getText().toString().trim();
            if (content.isEmpty()) {
                Toast.makeText(this, "不能发送空消息", Toast.LENGTH_SHORT).show();
                return;
            }
            sendMessage(content);
            etMessageInput.setText("");
        });

        btnPlus.setOnClickListener(v -> {
            if (panelMore.getVisibility() == View.GONE) {
                panelMore.setVisibility(View.VISIBLE);
            } else {
                panelMore.setVisibility(View.GONE);
            }
        });

        ivCamera.setOnClickListener(v ->
                Toast.makeText(this, "发送图片", Toast.LENGTH_SHORT).show()
        );

        etMessageInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    btnSend.setColorFilter(0xFF464646, PorterDuff.Mode.SRC_IN);
                } else {
                    btnSend.setColorFilter(0xFFCFCFCF, PorterDuff.Mode.SRC_IN);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void sendMessage(String content) {
        if (conversationId == null) return;

        String token = spUtil.getToken();
        Map<String, Object> body = new HashMap<>();
        body.put("conversationId", conversationId);
        body.put("content", content);
        body.put("msgType", "text");

        apiService.sendMessage("Bearer " + token, body).enqueue(new Callback<Result<Message>>() {
            @Override
            public void onResponse(Call<Result<Message>> call, Response<Result<Message>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 200) {
                    // 发送成功后立即刷新消息列表
                    loadMessages();
                } else {
                    String msg = (response.body() != null) ? response.body().getMessage() : "发送失败";
                    Toast.makeText(ChatConcreteActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Result<Message>> call, Throwable t) {
                Log.e(TAG, "发送失败", t);
                Toast.makeText(ChatConcreteActivity.this, "网络错误，发送失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void scrollToBottom() {
        if (adapter.getMessageCount() > 0) {
            rvMessages.scrollToPosition(adapter.getMessageCount() - 1);
        }
    }

    private Long extractUserIdFromToken(String token) {
        if (token == null || token.isEmpty()) return -1L;
        try {
            String[] parts = token.split("\\.");
            if (parts.length >= 2) {
                byte[] decoded = Base64.decode(parts[1], Base64.URL_SAFE);
                String payload = new String(decoded, "UTF-8");
                JSONObject json = new JSONObject(payload);
                String sub = json.optString("sub");
                if (sub != null && !sub.isEmpty()) {
                    return Long.parseLong(sub);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to extract userId from JWT", e);
        }
        return -1L;
    }

    private void navigateToChatTab() {
        Intent intent = new Intent(ChatConcreteActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("navigate_to", R.id.tab_chat);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        navigateToChatTab();
    }
}
