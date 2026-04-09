package com.bubble.model;

import com.google.gson.annotations.SerializedName;

public class Message {
    @SerializedName("id")
    private Long id;

    @SerializedName("conversationId")
    private Long conversationId;

    @SerializedName("senderId")
    private Long senderId;

    @SerializedName("content")
    private String content;

    @SerializedName("msgType")
    private String msgType;

    @SerializedName("extra")
    private String extra;

    @SerializedName("createTime")
    private String createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
