package com.bubble.model;

import com.google.gson.annotations.SerializedName;

public class Conversation {
    @SerializedName("id")
    private Long id;

    @SerializedName("type")
    private String type;

    @SerializedName("targetId")
    private Long targetId;

    @SerializedName("createTime")
    private String createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
