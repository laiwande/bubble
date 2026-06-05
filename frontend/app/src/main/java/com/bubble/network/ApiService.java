package com.bubble.network;

import com.bubble.model.Bubble;
import com.bubble.model.BubbleDetailData;
import com.bubble.model.BubblePost;
import com.bubble.model.Message;
import com.bubble.model.PageData;
import com.bubble.model.PartnerPost;
import com.bubble.model.Conversation;
import com.bubble.model.Result;
import com.bubble.model.User;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.Map;

public interface ApiService {

    // 测试接口
    @GET("test/hello")
    Call<Result<String>> hello();

    // 用户登录
    @POST("user/login")
    Call<Result<Map<String, String>>> login(@Body Map<String, String> loginData);

    // 发送验证码
    @POST("user/send-code")
    Call<Result<Void>> sendVerificationCode(@Body Map<String, String> data);

    // 用户注册
    @POST("user/register")
    Call<Result<Void>> register(@Body Map<String, String> registerData);

    // 获取用户信息
    @GET("user/info")
    Call<Result<User>> getUserInfo(@Header("Authorization") String token);

    // 更新用户信息
    @PUT("user/info")
    Call<Result<Void>> updateUserInfo(@Header("Authorization") String token, @Body User user);

    // 获取 Bubble 列表
    @GET("bubble/list")
    Call<Result<PageData<Bubble>>> getBubbleList(@Header("Authorization") String token, @Query("page") int page, @Query("size") int size, @Query("joined") boolean joined);

    // 获取 Bubble 详情
    @GET("bubble/{id}")
    Call<Result<Bubble>> getBubbleDetail(@Path("id") Long id);

    // 获取 Bubble 完整详情（含帖子等）
    @GET("bubble/{id}/full")
    Call<Result<BubbleDetailData>> getBubbleFullDetail(@Header("Authorization") String token, @Path("id") Long id);

    // 创建 Bubble
    @POST("bubble/create")
    Call<Result<Bubble>> createBubble(@Header("Authorization") String token, @Body Map<String, Object> data);

    // 加入 Bubble
    @POST("bubble/{id}/join")
    Call<Result<Void>> joinBubble(@Header("Authorization") String token, @Path("id") Long id);

    // 离开 Bubble
    @POST("bubble/{id}/leave")
    Call<Result<Void>> leaveBubble(@Header("Authorization") String token, @Path("id") Long id);

    // 获取帖子列表
    @GET("post/list/{bubbleId}")
    Call<Result<PageData<BubblePost>>> getPostList(
            @Header("Authorization") String token,
            @Path("bubbleId") Long bubbleId,
            @Query("page") int page,
            @Query("size") int size);

    // 创建帖子
    @POST("post/create")
    Call<Result<BubblePost>> createPost(@Header("Authorization") String token, @Body Map<String, Object> data);

    // 创建找搭子帖子
    @POST("partner/create")
    Call<Result<PartnerPost>> createPartnerPost(@Header("Authorization") String token, @Body Map<String, Object> data);

    // 获取找搭子帖子列表
    @GET("partner/list")
    Call<Result<PageData<PartnerPost>>> getPartnerPostList(@Query("page") int page, @Query("size") int size);

    // 点赞帖子
    @POST("post/{postId}/like")
    Call<Result<Void>> likePost(@Header("Authorization") String token, @Path("postId") Long postId);

    // 评论帖子
    @POST("post/{postId}/comment")
    Call<Result<Void>> commentPost(@Header("Authorization") String token, @Path("postId") Long postId, @Query("content") String content);

    // 获取或创建 Bubble 群聊会话
    @GET("message/conversation/bubble/{bubbleId}")
    Call<Result<Conversation>> getBubbleConversation(
            @Header("Authorization") String token,
            @Path("bubbleId") Long bubbleId);

    // 获取消息列表
    @GET("message/list/{conversationId}")
    Call<Result<PageData<Message>>> getMessageList(
            @Header("Authorization") String token,
            @Path("conversationId") Long conversationId,
            @Query("page") int page,
            @Query("size") int size);

    // 发送消息
    @POST("message/send")
    Call<Result<Message>> sendMessage(@Header("Authorization") String token, @Body Map<String, Object> data);

    // 获取未读消息数
    @GET("message/unread")
    Call<Result<Integer>> getUnreadCount(@Header("Authorization") String token);

    // 标记消息已读
    @POST("message/{messageId}/read")
    Call<Result<Void>> markAsRead(@Header("Authorization") String token, @Path("messageId") Long messageId);
}
